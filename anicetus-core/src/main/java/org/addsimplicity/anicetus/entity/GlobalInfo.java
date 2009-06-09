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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * GlobalInfo serves as the base object for all telemetry artifacts. Telemetry
 * artifacts are maps that have type safe helper methods for setting and
 * accessing properties. The map interface is preserved however to allow
 * applications to add any arbitrary information to the telemetry object without
 * having to extend it.
 * 
 * This class will always set the entity identifier to a random UUID and the
 * timestamp to the current time in milliseconds.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public abstract class GlobalInfo implements Map<String, Object> {
	private final Map<String, Object> m_delegateMap = new HashMap<String, Object>();

	/**
	 * Construct a telemetry artifact without a parent.
	 */
	protected GlobalInfo() {
		init();
	}

	/**
	 * Construct a telemetry object as a child of the specified parent. If the
	 * parent implements TelemetryContainer, this object will be added as a child.
	 * Note that any object can serve as a parent from a closure perspective,
	 * which means that in the telemetry stream this object will refer to the
	 * parent object by GUID, regardless of whether the parent can contain this
	 * object in the Java structure.
	 * 
	 * @param parent
	 *          The parent of this object.
	 */
	protected GlobalInfo(GlobalInfo parent) {
		init();
		setParentId(parent.getEntityId());

		if (parent instanceof TelemetryContainer) {
			((TelemetryContainer) parent).addChild(this);
		}
	}

	/**
	 * Clear all entries in the map.
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		m_delegateMap.clear();
	}

	/**
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return m_delegateMap.containsKey(key);
	}

	/**
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return m_delegateMap.containsValue(value);
	}

	/**
	 * 
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, Object>> entrySet() {
		return m_delegateMap.entrySet();
	}

	/**
	 * Equality is defined as entityId equivalence.
	 * 
	 * @return true for two objects with the same entityId property.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (this.getClass() != o.getClass()) {
			return false;
		}

		GlobalInfo other = (GlobalInfo) o;

		return get(GlobalInfoFields.EntityId.name()).equals(other.get(GlobalInfoFields.EntityId.name()));
	}

	/**
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return m_delegateMap.get(key);
	}

	/**
	 * Return the entity identifier for this artifact. Entity identifiers are
	 * typically generated using the random UUID method.
	 * 
	 * @return the entity identifier.
	 * @see java.util.UUID#randomUUID()
	 */
	public UUID getEntityId() {
		return (UUID) get(GlobalInfoFields.EntityId.name());
	}

	/**
	 * Return the execution context. The execution context is typically defined as
	 * the process and thread where this artifact was created.
	 * 
	 * @return the execution context.
	 */
	public String getExecutionContext() {
		return (String) get(GlobalInfoFields.ExecutionContext.name());
	}

	/**
	 * Return the message associated with the artifact. Artifacts support an
	 * optional message string that may be set by the application.
	 * 
	 * @return the message.
	 */
	public String getMessage() {
		return (String) get(GlobalInfoFields.Message.name());
	}

	/**
	 * Return the entity id of the parent of this artifact.
	 * 
	 * @return the parent entity identifier or null if this artifact has no
	 *         parent.
	 */
	public UUID getParentId() {
		return (UUID) get(GlobalInfoFields.ParentId.name());
	}

	/**
	 * Return the node where this artifact was created. This is typically a host
	 * name or IP address.
	 * 
	 * @return the reporting node.
	 */
	public String getReportingNode() {
		return (String) get(GlobalInfoFields.ReportingNode.name());
	}

	/**
	 * Return the time stamp when this artifact was created. This is the number of
	 * milliseconds since January 1, 1970 00:00:00 GMT.
	 * 
	 * @return the creation time stamp.
	 */
	public long getTimeStamp() {
		return (Long) get(GlobalInfoFields.TimeStamp.name());
	}

	/**
	 * The hash code is implemented as the entity identifier hash code.
	 * 
	 * @see java.util.UUID#hashCode()
	 */
	@Override
	public int hashCode() {
		return ((UUID) get(GlobalInfoFields.EntityId.name())).hashCode();
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return m_delegateMap.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		return m_delegateMap.keySet();
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(String key, Object value) {
		return m_delegateMap.put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends Object> t) {
		m_delegateMap.putAll(t);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return m_delegateMap.remove(key);
	}

	/**
	 * Set the entity identifier for this artifact. This value is automatically
	 * set when new artifacts are created. This method is primarily used for
	 * deserializing existing streams.
	 * 
	 * @param id
	 *          The GUID to be used as the entity identifer.
	 */
	public void setEntityId(UUID id) {
		put(GlobalInfoFields.EntityId.name(), id);
	}

	/**
	 * Set the message to the formatted exception. The exception is converted to a
	 * string, including the stack trace and set as the message.
	 * 
	 * @param exception
	 *          The exception to format.
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void setExceptionAsMessage(Throwable exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		pw.close();
		sw.flush();

		setMessage(sw.toString());
	}

	/**
	 * Set the execution context for this artifact. This is typically the process
	 * identifer and optionally the thread identifier.
	 * 
	 * @param context
	 *          The execution context.
	 */
	public void setExecutionContext(String context) {
		put(GlobalInfoFields.ExecutionContext.name(), context);
	}

	/**
	 * Set a message on this artifact. Messages are application defined text
	 * related to the artifact.
	 * 
	 * @param message
	 *          The message.
	 */
	public void setMessage(String message) {
		put(GlobalInfoFields.Message.name(), message);
	}

	/**
	 * Set the GUID of the parent of this artifact.
	 * 
	 * @param parent
	 *          The parent entity identifier.
	 */
	public void setParentId(UUID parent) {
		put(GlobalInfoFields.ParentId.name(), parent);
	}

	/**
	 * Set the reporting node where this artifact is created. This is typically
	 * the host name or IP address.
	 * 
	 * @param node
	 *          The reporting node identifier.
	 */
	public void setReportingNode(String node) {
		put(GlobalInfoFields.ReportingNode.name(), node);
	}

	/**
	 * Set the time stamp when this artifact was created. The time stamp is set
	 * automatically when an artifact is created. This method is primarily for
	 * deserializing previously created artifacts. Time stamps are expressed as
	 * the number of milliseconds since January 1, 1970 00:00:00 GMT.
	 * 
	 * @param timeStamp
	 *          The time stamp when the artifact was created.
	 * @see java.lang.System#currentTimeMillis()
	 */
	public void setTimeStamp(long timeStamp) {
		put(GlobalInfoFields.TimeStamp.name(), timeStamp);
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		return m_delegateMap.size();
	}

	/**
	 * @see java.util.Map#values()
	 */
	public Collection<Object> values() {
		return m_delegateMap.values();
	}

	private void init() {
		put(GlobalInfoFields.EntityId.name(), UUID.randomUUID());
		put(GlobalInfoFields.TimeStamp.name(), System.currentTimeMillis());
	}
}
