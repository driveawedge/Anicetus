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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.springframework.beans.factory.DisposableBean;

/**
 * The file delivery adapater will publish events to a file or stream. This
 * adapter is provided to assist with development debugging where running a bus
 * may not be necessary or convenient.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class FileDeliveryAdapter implements DeliveryAdapter, DisposableBean {
	private ExceptionHandler m_exceptionHandler = new SystemErrorExceptionHandler();

	private TelemetryEncoder m_translator = new JsonEncoder();

	private Writer m_writer;
	private boolean m_ownStream;

	/**
	 * Close the current file stream, if it was opened by the delivery adapter.
	 * 
	 * @throws IOException
	 *           if an error occurs while attempting to close the stream.
	 */
	public void close() throws IOException {
		if (m_writer != null && m_ownStream) {
			m_writer.close();
			m_writer = null;
		}
	}

	/**
	 * The destroy method is called by the Spring framework when this bean is
	 * being disposed.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		close();
	}

	/**
	 * Get the current exception handler that is in effect.
	 * 
	 * @return the exception handler.
	 */
	public ExceptionHandler getExceptionHandler() {
		return m_exceptionHandler;
	}

	/**
	 * Get the current session translator that is in effect.
	 * 
	 * @return the current translator.
	 */
	public TelemetryEncoder getTranslator() {
		return m_translator;
	}

	/**
	 * Get the current stream where telemetry is being written.
	 * 
	 * @return the current stream.
	 */
	public Writer getWriter() {
		return m_writer;
	}

	/**
	 * Send the telemetry to the file. It will be translated using the session
	 * translator and written immediately to the file, blocking the caller until
	 * it has completed.
	 * 
	 * @param telemetry
	 *          The telemetry artifact to write to the file.
	 * @see org.addsimplicity.anicetus.io.DeliveryAdapter#sendTelemetry(org.addsimplicity.anicetus.entity.GlobalInfo)
	 */
	public void sendTelemetry(GlobalInfo telemetry) {
		try {
			m_writer.write(m_translator.encode(telemetry));
		}
		catch (final IOException ioe) {
			m_exceptionHandler.exceptionCaught(ioe);
		}
	}

	/**
	 * Set the exception handler that will receive exceptions that occur.
	 * 
	 * @param handler
	 *          The handler to receive exceptions.
	 */
	public void setExceptionHandler(ExceptionHandler handler) {
		m_exceptionHandler = handler;
	}

	/**
	 * Set the file where events are written. The file will be written, with
	 * append mode. Any previously opened file will be closed.
	 * 
	 * @param fileName
	 *          The name of the file.
	 * @throws IOException
	 *           if the file does not exist and cannot be created or opened for
	 *           write.
	 */
	public void setFile(String fileName) throws IOException {
		close();
		m_writer = new FileWriter(fileName, true);
		m_ownStream = true;
	}

	/**
	 * Set the translator that will be used for this session. The default
	 * translator formats the telemetry as JSON.
	 * 
	 * @param translator
	 *          The translator to use.
	 * @see org.addsimplicity.anicetus.io.JsonEncoder
	 */
	public void setTranslator(TelemetryEncoder translator) {
		m_translator = translator;
	}

	/**
	 * Set the stream to use for writing telemetry. Note that this stream will not
	 * be closed by the delivery adapter. The life cycle of the stream is owned by
	 * the caller.
	 * 
	 * @param writer
	 *          The stream to be used for writing telemetry.
	 */
	public void setWriter(Writer writer) {
		m_writer = writer;
		m_ownStream = false;
	}

}
