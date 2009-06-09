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
package org.addsimplicity.anicetus.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * The type registry provides the mappings between the Java implementation and
 * the JSON protocol specified for Anicetus. The reason there is divergence
 * between the the Java types and the JSON protocol is the protocol is designed
 * for conciseness while it is desirable to have the Java types provide better
 * readability.
 * 
 * There are two kinds of mappings that are managed. First, the type mappings
 * from Java types to the abbreviated type strings. The second is the mapping
 * from the keys used in the Java maps to the keys used in the JSON protocol for
 * properties.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class EntityTypeRegistry {
	private static final Map<Class<? extends GlobalInfo>, String> s_type2Abbrev = new HashMap<Class<? extends GlobalInfo>, String>();
	private static final Map<String, Class<? extends GlobalInfo>> s_abbrev2Type = new HashMap<String, Class<? extends GlobalInfo>>();
	private static final Map<String, String> s_prop2JsonMap = new HashMap<String, String>();
	private static final Map<String, String> s_json2PropMap = new HashMap<String, String>();

	private static final Set<Class<?>> s_beanTypes = new HashSet<Class<?>>();
	private static final Set<String> s_searchPackages = new LinkedHashSet<String>();

	public static void addBeanType(Class<?> cls) {
		s_beanTypes.add(cls);
		s_searchPackages.add(cls.getPackage().getName());
	}

	/**
	 * Add a mapping between a Java type and a JSON protocol type name.
	 * 
	 * @param cls
	 *          The class of the Java type.
	 * @param name
	 *          The JSON name for the entity.
	 */
	public static void addClassShortName(Class<? extends GlobalInfo> cls, String name) {
		s_type2Abbrev.put(cls, name);
		s_abbrev2Type.put(name, cls);
	}

	/**
	 * Add a mapping between a Java key and a JSON property name.
	 * 
	 * @param propKey
	 *          The Java key.
	 * @param jsonKey
	 *          The JSON key.
	 */
	public static void addJsonPropertyMapping(String propKey, String jsonKey) {
		s_prop2JsonMap.put(propKey, jsonKey);
		s_json2PropMap.put(jsonKey, propKey);
	}

	public static void addSearchPackage(String pkg) {
		s_searchPackages.add(pkg);
	}

	/**
	 * Get a class name given a JSON short name.
	 * 
	 * @param name
	 *          The name extracted from the JSON type specifier.
	 * @return the Java class or null if no mapping exists.
	 */
	public static Class<? extends GlobalInfo> getClassFromName(String name) {
		return s_abbrev2Type.get(name);
	}

	/**
	 * Get the JSON key for a Java property name.
	 * 
	 * @param propKey
	 *          The Java property name.
	 * @return the JSON key or null if no mapping exists.
	 */
	public static String getJsonKey(String propKey) {
		return s_prop2JsonMap.get(propKey);
	}

	/**
	 * Get the property name from a JSON property name.
	 * 
	 * @param jsonKey
	 *          The JSON key from the protocol.
	 * @return the Java property name or null if no key exists.
	 */
	public static String getPropKey(String jsonKey) {
		return s_json2PropMap.get(jsonKey);
	}

	@SuppressWarnings("unchecked")
	public static Collection<String> getSearchPackages() {
		return Collections.unmodifiableSet(s_searchPackages);
	}

	/**
	 * Get the JSON type name from a Java type.
	 * 
	 * @param inst
	 *          The Java type.
	 * @return the JSON type name or null if no mapping exists.
	 */
	public static String getShortName(GlobalInfo inst) {
		return s_type2Abbrev.get(inst.getClass());
	}

	public static boolean isBeanType(Class<?> cls) {
		return s_beanTypes.contains(cls);
	}
}
