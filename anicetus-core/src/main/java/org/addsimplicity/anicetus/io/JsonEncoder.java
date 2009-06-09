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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.addsimplicity.anicetus.entity.EntityTypeRegistry;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.GlobalInfoFields;
import org.addsimplicity.anicetus.entity.JsonConstants;
import org.addsimplicity.anicetus.entity.TelemetryContainer;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

/**
 * The encoder translates telemetry into a JSON string. The object graph is
 * fully exported. In addition to serializing telemetry artifacts, the
 * application can also set classes that are to be treated like beans when
 * serializing. If an object is not a telemetry object and is not a bean, then
 * its toString method is used to generate the value.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class JsonEncoder implements TelemetryEncoder {
	private ExceptionHandler m_exceptionHandler = new SystemErrorExceptionHandler();

	/**
	 * Encode the telemetry graph as JSON.
	 * 
	 * @param session
	 *          The telemetry graph to encode.
	 * @return the JSON string as a character array.
	 */
	public char[] encode(GlobalInfo session) {
		CharArrayWriter out = new CharArrayWriter();
		try {
			JsonGenerator gen = (new JsonFactory()).createJsonGenerator(out);
			writeEntity(session, gen, true);

			gen.close();
			out.close();

		}
		catch (IOException ioe) {
			m_exceptionHandler.exceptionCaught(ioe);
		}

		return out.toCharArray();
	}

	/**
	 * Return the exception handler currently in effect.
	 * 
	 * @return the exception handler.
	 */
	public ExceptionHandler getExceptionHandler() {
		return m_exceptionHandler;
	}

	/**
	 * Set the exception handler that will receive exceptions encountered during
	 * encoding.
	 * 
	 * @param exceptionHandler
	 *          The exception handler.
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		m_exceptionHandler = exceptionHandler;
	}

	private void writeBean(Object value, JsonGenerator gen) throws JsonGenerationException, IOException {
		gen.writeStartObject();

		Class<? extends Object> cls = value.getClass();

		gen.writeFieldName(JsonConstants.EntityType);
		gen.writeString(cls.getSimpleName());

		Method meths[] = cls.getMethods();

		for (Method m : meths) {
			String mname = m.getName();
			if (mname.startsWith("get")) {
				gen.writeFieldName(mname.substring(3));

				try {
					Object propVal = m.invoke(value);
					writeValue(propVal, gen);
				}
				catch (IllegalArgumentException e) {
					continue;
				}
				catch (IllegalAccessException e) {
					continue;
				}
				catch (InvocationTargetException e) {
					continue;
				}
			}
		}

		gen.writeEndObject();

	}

	private void writeEntity(GlobalInfo gi, JsonGenerator gen, boolean root) throws IOException, JsonGenerationException {
		gen.writeStartObject();

		gen.writeFieldName(JsonConstants.EntityType);
		String type = EntityTypeRegistry.getShortName(gi);
		if (type == null) {
			type = gi.getClass().getSimpleName();
		}
		gen.writeString(type);

		for (Map.Entry<String, Object> entry : gi.entrySet()) {
			String propName = entry.getKey();
			if (!root && propName.equals(GlobalInfoFields.ParentId.name())) {
				// Skip the parent GUID as it's implicit in the containment structure.
				continue;
			}
			String jsField = EntityTypeRegistry.getJsonKey(propName);
			String fieldName = jsField != null ? jsField : propName;
			gen.writeFieldName(fieldName);
			writeValue(entry.getValue(), gen);
		}

		if (gi instanceof TelemetryContainer) {
			TelemetryContainer cont = (TelemetryContainer) gi;
			if (cont.getChildren().size() > 0) {
				gen.writeFieldName("child");
				gen.writeStartArray();

				for (GlobalInfo g : cont.getChildren()) {
					writeEntity(g, gen, false);
				}
				gen.writeEndArray();
			}
		}
		gen.writeEndObject();
	}

	@SuppressWarnings("unchecked")
	private void writeValue(Object value, JsonGenerator gen) throws JsonGenerationException, IOException {
		if (value == null) {
			gen.writeNull();
		}
		else if (EntityTypeRegistry.isBeanType(value.getClass())) {
			writeBean(value, gen);
		}
		else if (value.getClass().isArray()) {
			Object a[] = (Object[]) value;
			gen.writeStartArray();

			for (int i = 0; i < a.length; i++) {
				writeValue(a[i], gen);
			}

			gen.writeEndArray();
		}
		else if (value instanceof Collection) {
			Collection<? extends Object> c = (Collection<? extends Object>) value;

			gen.writeStartArray();

			for (Object v : c) {
				writeValue(v, gen);
			}

			gen.writeEndArray();
		}
		else {
			gen.writeString(value.toString());
		}
	}
}
