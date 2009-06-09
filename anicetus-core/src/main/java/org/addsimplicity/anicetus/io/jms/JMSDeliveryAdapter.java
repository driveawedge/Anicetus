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
package org.addsimplicity.anicetus.io.jms;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.io.DeliveryAdapter;
import org.addsimplicity.anicetus.io.ExceptionHandler;
import org.addsimplicity.anicetus.io.SystemErrorExceptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * The JMS delivery adapter publishes telemetry to a JMS topic or queue. The
 * delivery is done using Spring's JMS templates. Telemetry is published on a
 * separate thread, asynchronously to the primary application flows. This is
 * done to minimize any overhead that may be introduced into the main
 * application processing.
 * 
 * Telemetry artifacts are queued and delivered by worker threads. The
 * application can control the number of worker threads as well as the size of
 * the queue. Additionally, the behavior if the queue overruns can be
 * controlled. Telemetry can either be discarded if there is no further queue
 * space or the calling thread will be used to delivery the artifact.
 * 
 * Messages are converted to the desired format using a Spring message converter
 * implementation.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class JMSDeliveryAdapter implements DeliveryAdapter, InitializingBean {
	class DeliveryTask implements Runnable {
		GlobalInfo m_telemetry;

		public DeliveryTask(GlobalInfo telemetry) {
			super();
			m_telemetry = telemetry;
		}

		public void run() {
			try {
				m_template.convertAndSend(m_telemetry);
			}
			catch (Throwable t) {
				m_handler.exceptionCaught(t);
			}
		}

	}

	private ConnectionFactory m_connectionFactory;

	private Destination m_destination;
	private ExceptionHandler m_handler = new SystemErrorExceptionHandler();
	private MessageConverter m_messageConverter;
	private JmsTemplate m_template;
	private int m_maxDeliveryThreads = 2;
	private int m_maxDeliveryQueue = Integer.MAX_VALUE;
	private RejectedExecutionHandler m_rejectionHandler = new ThreadPoolExecutor.DiscardPolicy();
	private ExecutorService m_executor;

	/**
	 * Called by Spring once all properties have been set. This method will
	 * establish the connection to the JMS broker.
	 */
	public void afterPropertiesSet() throws Exception {
		BlockingQueue<Runnable> q = new LinkedBlockingQueue<Runnable>(m_maxDeliveryQueue);
		m_executor = new ThreadPoolExecutor(1, m_maxDeliveryThreads, 60l, TimeUnit.SECONDS, q, new DeliveryThreadFactory(),
				m_rejectionHandler);

		m_template = new JmsTemplate(m_connectionFactory);
		m_template.setDefaultDestination(m_destination);
		m_template.setMessageConverter(m_messageConverter);
	}

	/**
	 * Get the current connection factory used to connect to the JMS broker.
	 * 
	 * @return the connection factory.
	 */
	public ConnectionFactory getConnectionFactory() {
		return m_connectionFactory;
	}

	/**
	 * Get the current destination.
	 * 
	 * @return the current destination.
	 */
	public Destination getDestination() {
		return m_destination;
	}

	/**
	 * Get the current policy for queue overruns.
	 * 
	 * @return the current overrun policy.
	 */
	public boolean getDiscardOverrun() {
		return m_rejectionHandler instanceof ThreadPoolExecutor.DiscardPolicy;
	}

	/**
	 * Get the maximum size of the delivery queue.
	 * 
	 * @return the maximum queue size.
	 */
	public int getMaxDeliveryQueue() {
		return m_maxDeliveryQueue;
	}

	/**
	 * Get the maximum number of delivery threads.
	 * 
	 * @return the maximum number of delivery threads
	 */
	public int getMaxDeliveryThreads() {
		return m_maxDeliveryThreads;
	}

	/**
	 * Get the current message converter.
	 * 
	 * @return the message converter.
	 */
	public MessageConverter getMessageConverter() {
		return m_messageConverter;
	}

	/**
	 * Send the telemetry to the JMS topic. The telemetry is queued for delivery
	 * and this method will return immediately unless discarding messages is
	 * disabled and the queue is full.
	 * 
	 * @param telemetry
	 *          The telemetry to send.
	 * 
	 * @seeorg.addsimplicity.aniticus.support.DeliveryAdapter#sendSession(org. 
	 *                                                                         addsimplicity
	 *                                                                         .
	 *                                                                         aniticus
	 *                                                                         .
	 *                                                                         entity
	 *                                                                         .
	 *                                                                         Session
	 *                                                                         )
	 */
	public void sendTelemetry(GlobalInfo telemetry) {
		m_executor.submit(new DeliveryTask(telemetry));
	}

	/**
	 * Set the JMS connection factory that will be used to connect to the broker.
	 * 
	 * @param connectionFactory
	 *          The factory used to connect to the broker.
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		m_connectionFactory = connectionFactory;
	}

	/**
	 * Set the destination topic or queue for delivering telemetry.
	 * 
	 * @param destination
	 *          The topic for publishing messages.
	 */
	public void setDestination(Destination destination) {
		m_destination = destination;
	}

	/**
	 * Setting discard to true will cause telemetry to be dropped if the delivery
	 * queue is full. This is the default setting.
	 * 
	 * @param discard
	 *          True to discard telemetry if the queue is full.
	 */
	public void setDiscardOverrun(boolean discard) {
		if (discard) {
			m_rejectionHandler = new ThreadPoolExecutor.DiscardPolicy();
		}
		else {
			m_rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
		}
	}

	/**
	 * The exception handler that will be invoked if a delivery error occurs. Note
	 * that the handler may be called on a different thread from the thread that
	 * sets it.
	 * 
	 * @param handler
	 *          The exception handler.
	 * 
	 * @see org.addsimplicity.aniticus.support.DeliveryAdapter#setExceptionHandler(org.addsimplicity.aniticus.support.ExceptionHandler)
	 */
	public void setExceptionHandler(ExceptionHandler handler) {
		m_handler = handler;
	}

	/**
	 * The delivery queue holds telemetry to be delivered. By default the queue
	 * size is unlimited.
	 * 
	 * @param maxDeliveryQueue
	 *          The maximum number of telemetry events that will be queued.
	 */
	public void setMaxDeliveryQueue(int maxDeliveryQueue) {
		m_maxDeliveryQueue = maxDeliveryQueue;
	}

	/**
	 * Delivery threads publish events to the JMS topic. By default a maximum of 2
	 * threads are used.
	 * 
	 * @param maxDeliveryThreads
	 *          The maximum threads that will be used for delivering telemetry.
	 */
	public void setMaxDeliveryThreads(int maxDeliveryThreads) {
		m_maxDeliveryThreads = maxDeliveryThreads;
	}

	/**
	 * The message converter is responsible for translating the telemetry artifact
	 * to the JMS message structure.
	 * 
	 * @param messageConverter
	 *          The message converter.
	 */
	public void setMessageConverter(MessageConverter messageConverter) {
		m_messageConverter = messageConverter;
	}

}
