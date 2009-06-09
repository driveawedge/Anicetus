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

package org.addsimplicity.anicetus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;

import org.addsimplicity.anicetus.entity.CompletionStatus;
import org.addsimplicity.anicetus.entity.ExecInfo;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.SubTypedInfo;
import org.addsimplicity.anicetus.entity.TelemetryEvent;
import org.addsimplicity.anicetus.entity.TelemetrySession;
import org.addsimplicity.anicetus.entity.TelemetryState;
import org.addsimplicity.anicetus.entity.TelemetryTransaction;
import org.addsimplicity.anicetus.io.DeliveryAdapter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * The TelemetryContext establishes the execution container for all telemetry.
 * The execution context is defined by the system, process, and potentially the
 * thread of execution for the session. A session is a logical concept that
 * represents a unit of activity performed by the application.
 * 
 * A session provides a container for other telemetry artifacts. Transactions
 * are one of the artifacts that are also containers for other artifacts. The
 * TelemetryContext provides a convenience method for creating other artifacts
 * that will be parented to the current container (either session or
 * transaction).
 * 
 * The TelemetryContext is responsible for sending telemetry to the bus. By
 * default, a session is sent anytime it ends. Applications may also send beacon
 * telemetry (i.e. state and events) directly to the telemetry bus using the
 * context convenience method.
 * 
 * The lifecycle of the session is controlled by startSession and endSession.
 * These methods are automatically called from the Spring container if Spring is
 * used to manage the lifecycle of the context.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryContext implements InitializingBean, DisposableBean {
	private final Stack<ExecInfo> m_context = new Stack<ExecInfo>();
	private DeliveryAdapter m_deliveryAdapter;
	private String m_operationName;
	private int m_processIdentifier = -1;
	private String m_reportingNode;
	private TelemetrySession m_session;

	/**
	 * Called after the Spring framework sets all properties. This method will
	 * start the session.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		startSession();
	}

	/**
	 * The beginTransaction method starts a new transaction as a child of the
	 * current session or transaction.
	 * 
	 * @param resourceId
	 *          The application defined resource identifier.
	 * @return an initialized transaction.
	 */
	public TelemetryTransaction beginTransaction(String resourceId) {
		final TelemetryTransaction trans = new TelemetryTransaction(m_context.peek());
		trans.setResourceId(resourceId);
		setReporting(trans);

		m_context.push(trans);

		return trans;
	}

	/**
	 * The destroy method is called by the Spring framework when the context is
	 * being disposed. An open session will be closed and published before the
	 * bean is disposed.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		if (m_context.size() > 0) {
			endSession();
		}
	}

	/**
	 * The current session is ended. Any open transactions are closed with their
	 * completion status set to unknown. The session is published on the telemetry
	 * bus.
	 */
	public void endSession() {
		closeDanglingTrans();
		m_session.complete();
		m_context.pop();

		m_deliveryAdapter.sendTelemetry(m_session);
		startSession();
	}

	/**
	 * The current transaction is closed. The completion status will be set to
	 * unknown if it has not already been set.
	 */
	public void endTransaction() {
		if (m_context.size() <= 1) {
			return; // Something is unbalanced but do we throw exceptions in reporting
			// flows?
		}

		final TelemetryTransaction trans = (TelemetryTransaction) m_context.pop();
		if (trans.getStatus() == null) {
			trans.setStatus(CompletionStatus.Unknown);
		}

		trans.complete();
	}

	/**
	 * Return the currently set delivery adapter.
	 * 
	 * @return the current delivery adapter.
	 */
	public DeliveryAdapter getDeliveryAdapter() {
		return m_deliveryAdapter;
	}

	/**
	 * Return the operation name. The operation name is set on the session when it
	 * is created.
	 * 
	 * @return the operation name.
	 */
	public String getOperationName() {
		return m_operationName;
	}

	/**
	 * Return the processor identifier. The process identifier is extracted from
	 * the system property anicetus.processid. The process identifier is used in
	 * conjunction with the thread identifier to construct the session execution
	 * context.
	 * 
	 * @return the processor identifier.
	 */
	public int getProcessIdentifier() {
		return m_processIdentifier;
	}

	/**
	 * Return the reporting node. The reporting node is set to the host name of
	 * the default interface on the system where the application is run. The
	 * reporting node is set on the session.
	 * 
	 * @return the reporting node.
	 */
	public String getReportingNode() {
		return m_reportingNode;
	}

	/**
	 * Return the current active session.
	 * 
	 * @return the current active session.
	 */
	public TelemetrySession getSession() {
		return m_session;
	}

	/**
	 * Create an event, child of the current context (session or transaction).
	 * 
	 * @param type
	 *          The application defined type of the event.
	 * @return the newly created event.
	 */
	public SubTypedInfo newEvent(String type) {
		final SubTypedInfo evt = new TelemetryEvent(m_context.peek());
		evt.setType(type);
		setReporting(evt);

		return evt;
	}

	/**
	 * Create a state, child of the current context (session or transaction).
	 * 
	 * @return the newly created state.
	 */
	public TelemetryState newState() {
		final TelemetryState state = new TelemetryState(m_context.peek());
		setReporting(state);

		return state;
	}

	/**
	 * Return the current execution context (session or transaction).
	 * 
	 * @return the session or transaction context in effect.
	 */
	public ExecInfo peekTransaction() {
		return m_context.peek();
	}

	/**
	 * Pop the current transaction off the top of the stack. The session will not
	 * be popped from the stack.
	 * 
	 * @return the current transaction or null if no transactions are left.
	 */
	public ExecInfo popTransaction() {
		return m_context.size() > 1 ? m_context.pop() : null;
	}

	/**
	 * Push a transaction to the top of the execution context. The standard
	 * reporting information will be added to the transaction.
	 * 
	 * @param transaction
	 *          The transaction to push.
	 */
	public void pushTransaction(ExecInfo transaction) {
		setReporting(transaction);

		m_context.push(transaction);
	}

	/**
	 * Send a telemetry beacon that is independent of the current execution
	 * context. The beacon will be sent immediately. The basic information about
	 * the current application environment will be set.
	 * 
	 * @param beacon
	 *          The beacon to be sent.
	 */
	public void sendBeacon(GlobalInfo beacon) {
		fillBaseInfo(beacon);
		m_deliveryAdapter.sendTelemetry(beacon);
	}

	/**
	 * Set the delivery adpater that will be used to publish events on the
	 * telemetry bus.
	 * 
	 * @param deliveryAdapter
	 *          The delivery adapter instance.
	 */
	public void setDeliveryAdapter(DeliveryAdapter deliveryAdapter) {
		m_deliveryAdapter = deliveryAdapter;
	}

	/**
	 * Set the operation name for this context. This will be set on sessions when
	 * they are created.
	 * 
	 * @param operationName
	 */
	public void setOperationName(String operationName) {
		m_operationName = operationName;
	}

	/**
	 * Set the process identifier for this context. The process identifier should
	 * be the operating system identifier for the JVM process.
	 * 
	 * @param processIdentifier
	 *          The JVM process identifier.
	 */
	public void setProcessIdentifier(int processIdentifier) {
		m_processIdentifier = processIdentifier;
	}

	/**
	 * Set the node identifier for this context. This is typically the host name
	 * or IP address.
	 * 
	 * @param reportingNode
	 *          The node network identifier.
	 */
	public void setReportingNode(String reportingNode) {
		m_reportingNode = reportingNode;
	}

	/**
	 * Start a new session. The session is filled with the current execution
	 * environment information.
	 */
	public void startSession() {
		m_session = createSession();
		m_context.clear();
		m_context.push(m_session);
		m_session.setOperationName(m_operationName);
		sniffHost();
		m_session.setReportingNode(m_reportingNode);
		sniffProcessId();
		m_session.setExecutionContext(makeExecContext());
	}

	private void closeDanglingTrans() {
		while (m_context.size() > 1) {
			endTransaction();
		}
	}

	private void fillBaseInfo(GlobalInfo info) {
		if (info.getReportingNode() == null) {
			info.setReportingNode(m_reportingNode);
		}

		if (info.getExecutionContext() == null) {
			info.setExecutionContext(makeExecContext());
		}
	}

	private String makeExecContext() {
		return (m_processIdentifier >= 0 ? m_processIdentifier : "UNKNOWN") + "." + Thread.currentThread().getId();
	}

	private void setReporting(GlobalInfo target) {
		target.setReportingNode(m_context.peek().getReportingNode());
	}

	private void sniffHost() {
		if (m_reportingNode == null) {
			try {
				final InetAddress local = InetAddress.getLocalHost();
				m_reportingNode = local.getCanonicalHostName();
			}
			catch (final UnknownHostException e) {
				// Not sure what else to do. Just local host it.
				//
				m_reportingNode = "127.0.0.1";
			}
		}
	}

	private void sniffProcessId() {
		if (m_processIdentifier < 0) {
			final String pid = System.getProperty("anicetus.processid");
			if (pid != null) {
				try {
					m_processIdentifier = Integer.parseInt(pid);
				}
				catch (final NumberFormatException e) {
					// Set it to zero. This will cause something other than 'UNKNOWN'
					// which is a hint
					// they set the property to an unparseable string.
					//
					m_processIdentifier = 0;
				}
			}
		}
	}

	protected TelemetrySession createSession() {
		return new TelemetrySession();
	}
}
