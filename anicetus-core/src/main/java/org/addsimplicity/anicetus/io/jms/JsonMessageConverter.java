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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.addsimplicity.anicetus.entity.ExecInfoFields;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.GlobalInfoFields;
import org.addsimplicity.anicetus.io.JsonDecoder;
import org.addsimplicity.anicetus.io.JsonEncoder;
import org.addsimplicity.anicetus.io.TelemetryDecoder;
import org.addsimplicity.anicetus.io.TelemetryEncoder;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * The JSON message converter translates a telemetry artifact to a JMS text
 * message with the payload formatted as a JSON string. The full object graph is
 * translated to JSON. In addition, JMS headers are set from the telemetry
 * artifact. The headers set are:
 * 
 * JMSCorrelationID - Telemetry entity identifier.
 * 
 * ReportingNode - The reporting host.
 * 
 * OperationName - Set if present.
 * 
 * Status - Set if present.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class JsonMessageConverter implements MessageConverter {
	private TelemetryEncoder m_translator = new JsonEncoder();
	private TelemetryDecoder m_decoder = new JsonDecoder();

	/**
	 * Convert a JMS text message with a JSON payload to a telemetry artifact.
	 * 
	 * @param msg
	 *          The JMS text message.
	 * @return the telemetry artifact.
	 * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
	 */
	public Object fromMessage(Message msg) throws JMSException, MessageConversionException {
		if (msg instanceof TextMessage) {
			return m_decoder.decode(((TextMessage) msg).getText().toCharArray());
		}
		else {
			throw new JMSException("Message of type " + msg.getClass().getName() + " is not supported. Only TextMessage");
		}

	}

	/**
	 * Get the current decoder.
	 * 
	 * @return the current decoder.
	 */
	public TelemetryDecoder getDecoder() {
		return m_decoder;
	}

	/**
	 * Get the current encoder.
	 * 
	 * @return the telemetry encoder.
	 */
	public TelemetryEncoder getEncoder() {
		return m_translator;
	}

	/**
	 * Set the decoder. By default the JsonDecoder is used.
	 * 
	 * @param decoder
	 *          The decoder to use for decoding messages.
	 */
	public void setDecoder(TelemetryDecoder decoder) {
		m_decoder = decoder;
	}

	/**
	 * Set the telmetry encoder. By default the JsonEncoder is used.
	 * 
	 * @param translator
	 *          The encoder.
	 */
	public void setEncoder(TelemetryEncoder translator) {
		m_translator = translator;
	}

	/**
	 * Translate the telemetry to a JMS message. A JMS text message is used to
	 * contain the translated payload.
	 * 
	 * @param obj
	 *          The telemetry artifact.
	 * @param jsmSess
	 *          The JMS session.
	 * @return a text message containing the translated payload.
	 * 
	 * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object,
	 *      javax.jms.Session)
	 */
	public Message toMessage(Object obj, Session jmsSess) throws JMSException, MessageConversionException {
		TextMessage m = jmsSess.createTextMessage();

		GlobalInfo telemetry = (GlobalInfo) obj;
		m.setJMSCorrelationID(telemetry.getEntityId().toString());
		m.setStringProperty(GlobalInfoFields.ReportingNode.name(), telemetry.getReportingNode());

		if (telemetry.containsKey(ExecInfoFields.OperationName.name())) {
			m.setStringProperty(ExecInfoFields.OperationName.name(), (String) telemetry.get(ExecInfoFields.OperationName
					.name()));
		}

		if (telemetry.containsKey(ExecInfoFields.Status.name())) {
			m.setStringProperty(ExecInfoFields.Status.name(), telemetry.get(ExecInfoFields.Status.name()).toString());
		}

		char[] body = m_translator.encode(telemetry);

		m.setText(new String(body));

		return m;
	}

}
