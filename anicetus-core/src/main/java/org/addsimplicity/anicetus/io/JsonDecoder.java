/**
 * Copyright 2008-2009 Dan Pritchett
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.addsimplicity.anicetus.io;

import java.io.CharArrayReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.addsimplicity.anicetus.entity.EntityTypeRegistry;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.JsonConstants;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

/**
 * The decoder translates a JSON encoded telemetry into a Java object. In
 * addition to decoding the telemetry maps, the decoder will also attempt to
 * decode simple Java beans. It relies upon the application adding packages for
 * searching for types to detect a bean. If it encounters a type it doesn't
 * recognize, it decodes it into a TelemetryState which is just a simple map.
 * 
 * When converting strings into types, the decoder looks for getter methods of
 * the appropriate name. The return type of the getter is used to drive the
 * conversion. If it is a primitive, the appropriate primitive converter is
 * used. If it is not a primitive, it will look for static fromString and
 * valueOf methods that take String as a single argument. The methods are
 * searched in that order to provide for custom decoders on Enum types.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class JsonDecoder implements TelemetryDecoder {
	private static final Map<Class<?>, Class<?>> s_primitiveBox = new HashMap<Class<?>, Class<?>>();

	static {
		EntityTypeRegistry.addSearchPackage(GlobalInfo.class.getPackage().getName());
	}

	static {
		s_primitiveBox.put(Boolean.TYPE, Boolean.class);
		s_primitiveBox.put(Character.TYPE, Character.class);
		s_primitiveBox.put(Byte.TYPE, Byte.class);
		s_primitiveBox.put(Short.TYPE, Short.class);
		s_primitiveBox.put(Integer.TYPE, Integer.class);
		s_primitiveBox.put(Long.TYPE, Long.class);
		s_primitiveBox.put(Float.TYPE, Float.class);
		s_primitiveBox.put(Double.TYPE, Double.class);
	}

	private final Map<String, Class<? extends Object>> m_typeCache = new HashMap<String, Class<? extends Object>>();

	private ExceptionHandler m_exceptionHandler = new SystemErrorExceptionHandler();

	/**
	 * Convert a character array that represents a JSON encoded object. The entire
	 * object graph will be decoded and returned as the appropriate root telemetry
	 * artifact.
	 * 
	 * @param jsonEncoded
	 *          The encoded JSON object as a character array.
	 */
	public GlobalInfo decode(char[] jsonEncoded) {
		CharArrayReader in = new CharArrayReader(jsonEncoded);
		JsonFactory fact = new JsonFactory();
		try {
			JsonParser parser = fact.createJsonParser(in);

			JsonTypeMapper mapper = new JsonTypeMapper();

			JsonNode node = mapper.read(parser);

			Object root = getTypedObject(node);
			if (root instanceof GlobalInfo) {
				fillType(node, root);
			}
			else {
				return null;
			}

			return (GlobalInfo) root;
		}
		catch (IOException ioe) {
			m_exceptionHandler.exceptionCaught(ioe);
			return null;
		}

	}

	/**
	 * Return the exception handler in effect for the decoder.
	 * 
	 * @return the exception handler in effect.
	 */
	public ExceptionHandler getExceptionHandler() {
		return m_exceptionHandler;
	}

	/**
	 * Set the exception handler that will receive any exception that occurs
	 * during decoding.
	 * 
	 * @param exceptionHandler
	 *          The exception handler.
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		m_exceptionHandler = exceptionHandler;
	}

	private void fillType(JsonNode node, Object type) {
		Iterator<String> names = node.getFieldNames();
		while (names.hasNext()) {
			String name = names.next();
			JsonNode field = node.getFieldValue(name);
			String pname = EntityTypeRegistry.getPropKey(name);
			if (pname != null) {
				name = pname;
			}
			if (field.isArray()) {
				Method adder = getAdderMethod(name, type.getClass());
				if (field.getElementValue(0).isObject() || adder != null) {
					for (int e = 0; e < field.size(); e++) {
						JsonNode ele = field.getElementValue(e);
						Object child = getTypedObject(ele);
						if (child != null) {
							fillType(ele, child);
							try {
								adder.invoke(type, child);
							}
							catch (IllegalAccessException iae) {
								m_exceptionHandler.exceptionCaught(iae);
							}
							catch (InvocationTargetException ite) {
								m_exceptionHandler.exceptionCaught(ite);
							}
						}
						else {
							try {
								adder.invoke(type, ele.getTextValue());
							}
							catch (IllegalAccessException iae) {
								m_exceptionHandler.exceptionCaught(iae);
							}
							catch (InvocationTargetException ite) {
								m_exceptionHandler.exceptionCaught(ite);
							}
						}
					}
				}
				else {
					String ar[] = new String[field.size()];
					for (int e = 0; e < field.size(); e++) {
						ar[e] = field.getElementValue(e).getTextValue();
					}

					if (type instanceof GlobalInfo) {
						((GlobalInfo) type).put(name, ar);
					}
					else {
						setTypedProperty(type, name, ar);
					}
				}
			}
			else if (field.isObject()) {
				Object value = getTypedObject(field);
				if (value != null) {
					fillType(field, value);
				}
				else {
					m_exceptionHandler.exceptionCaught(new MissingPropertyException(name, type));
				}

				if (type instanceof GlobalInfo) {
					((GlobalInfo) type).put(name, value);
				}
				else {
					setTypedProperty(type, name, value);
				}
			}
			else {
				Object value = getTypedValue(name, type.getClass(), field.getTextValue());
				if (type instanceof GlobalInfo) {
					((GlobalInfo) type).put(name, value);
				}
				else {
					setTypedProperty(type, name, value);
				}
			}
		}
	}

	private Method getAdderMethod(String name, Class<?> type) {
		String mname = makeArraySetter(name);
		for (Method m : type.getMethods()) {
			if (mname.equals(m.getName())) {
				return m;
			}
		}

		return null;
	}

	private Object getTypedObject(JsonNode node) {
		JsonNode tnode = node.getFieldValue(JsonConstants.EntityType);
		if (tnode == null) {
			return null;
		}
		String tname = tnode.getTextValue();

		Class<? extends Object> cls = null;

		// Have we seen this type before? If so, grab the class. But note that
		// it may be null. The type cache does negative caching as well.
		//
		if (m_typeCache.containsKey(tname)) {
			cls = m_typeCache.get(tname);
		}
		else {
			// Is this a distinguished name?
			//
			cls = EntityTypeRegistry.getClassFromName(tname);

			if (cls == null) {
				// Okay, try the raw name and see if it just resolves.
				//
				try {
					cls = Class.forName(tname);
				}
				catch (ClassNotFoundException cfe) {
					// We can fall out here. Only interested in null.
				}
			}

			// If we didn't get it there, then we have to walk through each package
			// and try.
			//
			if (cls == null) {
				for (String p : EntityTypeRegistry.getSearchPackages()) {
					StringBuffer fqcn = new StringBuffer(p);
					fqcn.append(".");
					fqcn.append(tname);

					try {
						cls = Class.forName(fqcn.toString());
					}
					catch (ClassNotFoundException cfe) {
						// Again, looking for null
					}

					if (cls != null) {
						break;
					}
				}
			}

			// No matter what the answer right here, we cache the result.
			//
			m_typeCache.put(tname, cls);
		}

		Object result = null;
		if (cls != null) {
			try {
				result = cls.newInstance();
			}
			catch (IllegalAccessException iae) {
				m_exceptionHandler.exceptionCaught(iae);
			}
			catch (InstantiationException ie) {
				m_exceptionHandler.exceptionCaught(ie);
			}
		}
		else {
			m_exceptionHandler.exceptionCaught(new ClassNotFoundException(tname));
		}

		return result;
	}

	private Object getTypedValue(String name, Class<? extends Object> cls, String value) {
		try {
			Method m = cls.getMethod(makeGetter(name), (Class[]) null);

			Class<?> ret = m.getReturnType();
			if (ret.equals(String.class)) {
				return value;
			}

			if (ret.isPrimitive()) {
				ret = s_primitiveBox.get(ret);
			}

			// Okay, now we want to see if we can invoke a converter. Only two
			// supported:
			// valueOf(String s)
			// fromString(String s)
			//

			Method conv = null;
			try {
				conv = ret.getMethod("fromString", String.class);
			}
			catch (NoSuchMethodException nse) {
				// weird way to say not found...
			}

			if (conv == null) {
				// Here we'll just fall out to the outside exception and return a
				// string.
				//
				conv = ret.getMethod("valueOf", String.class);
			}

			if (conv != null) {
				try {
					return conv.invoke(null, value);
				}
				catch (InvocationTargetException ite) {
					m_exceptionHandler.exceptionCaught(ite);
				}
				catch (IllegalAccessException ie) {
					m_exceptionHandler.exceptionCaught(ie);
				}
			}

			return value;

		}
		catch (NoSuchMethodException e) {
			return value;
		}
	}

	private String makeArraySetter(String name) {
		StringBuilder sb = new StringBuilder("add");
		sb.append(name.substring(0, 1).toUpperCase());
		sb.append(name.substring(1));

		return sb.toString();
	}

	private String makeGetter(String name) {
		StringBuilder sb = new StringBuilder("get");
		sb.append(name.substring(0, 1).toUpperCase());
		sb.append(name.substring(1));

		return sb.toString();
	}

	private void setTypedProperty(Object type, String name, Object value) {
		String setter = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

		Class<?> cls = type.getClass();

		try {
			Method m = cls.getMethod(setter, value.getClass());
			m.invoke(type, value);
		}
		catch (NoSuchMethodException nsme) {
			// Again, what could we do?
		}
		catch (InvocationTargetException ite) {
			// Ignore
		}
		catch (IllegalAccessException iae) {
			// Ignore
		}
	}

}
