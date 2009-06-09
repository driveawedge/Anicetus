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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ExecInfo provides an execution container for telemetry that has a duration
 * and potentially child telemetry.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public abstract class ExecInfo extends GlobalInfo implements TelemetryContainer {
	private final List<GlobalInfo> m_children = new ArrayList<GlobalInfo>();
	private long m_timerStart = System.nanoTime();

	/**
	 * Create an ExecInfo without a parent.
	 */
	protected ExecInfo() {
		super();
	}

	/**
	 * Create an ExecInfo with the specified telemetry as the parent.
	 * 
	 * @param parent
	 *          The telemetry entity that will be considered the parent of this
	 *          entity.
	 */
	protected ExecInfo(GlobalInfo parent) {
		super(parent);
	}

	/**
	 * Add a child to this container. The entity will be marked as having this
	 * container as the parent.
	 * 
	 * @param child
	 *          The entity to add to this container.
	 * @see org.addsimplicity.anicetus.entity.TelemetryContainer#addChild(org.addsimplicity.anicetus.entity.GlobalInfo)
	 */
	public void addChild(GlobalInfo child) {
		child.setParentId(getEntityId());
		m_children.add(child);
	}

	/**
	 * Complete the current execution container. The duration of the execution is
	 * set automatically based the difference of when the event was created and
	 * the time this method is called. If the completion status has not been set,
	 * it will be set to Unknown.
	 */
	public void complete() {
		final long now = System.nanoTime();
		final long duration = now - m_timerStart;
		setDuration(duration);

		if (getStatus() == null) {
			setStatus(CompletionStatus.Unknown);
		}
	}

	/**
	 * Return the children of the execution container.
	 */
	public Collection<GlobalInfo> getChildren() {
		return Collections.unmodifiableCollection(m_children);
	}

	/**
	 * Get the duration of the execution of the container. Note that this field
	 * will not be set until the complete method has been called.
	 * 
	 * @return the execution time in nanoseconds.
	 */
	public long getDuration() {
		return (Long) get(ExecInfoFields.Duration.name());
	}

	/**
	 * Get the operation name. For web oriented applications or services this will
	 * be the action name.
	 * 
	 * @return the operation name.
	 */
	public String getOperationName() {
		return (String) get(ExecInfoFields.OperationName.name());
	}

	/**
	 * Return the completion status of the operation.
	 * 
	 * @return the completion status.
	 */
	public CompletionStatus getStatus() {
		return (CompletionStatus) get(ExecInfoFields.Status.name());
	}

	/**
	 * Set the execution time in nanoseconds. Note that the preferred method for
	 * setting the duration is to use the complete method and let the duration be
	 * computed.
	 * 
	 * @param duration
	 *          The execution time in milliseconds.
	 */
	public void setDuration(long duration) {
		put(ExecInfoFields.Duration.name(), duration);
	}

	/**
	 * Set the operation name. For web oriented applications or services this will
	 * be the action name.
	 * 
	 * @param name
	 *          The operation name.
	 */
	public void setOperationName(String name) {
		put(ExecInfoFields.OperationName.name(), name);
	}

	/**
	 * Set the completion status of the execution.
	 * 
	 * @param status
	 *          The completion status of the execution.
	 */
	public void setStatus(CompletionStatus status) {
		put(ExecInfoFields.Status.name(), status);
	}

	/**
	 * Reset the start time of the operation. By default it is set to the object
	 * creation time.
	 */
	public void startTimer() {
		m_timerStart = System.nanoTime();
	}
}
