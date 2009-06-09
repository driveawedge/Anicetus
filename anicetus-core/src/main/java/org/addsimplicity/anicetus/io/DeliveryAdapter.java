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

import org.addsimplicity.anicetus.entity.GlobalInfo;

/**
 * The delivery adapter interface is used to send telemetry events to the
 * telemetry bus.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public interface DeliveryAdapter {

	/**
	 * Send the specified telemtry to the bus. The telemetry will be serialized
	 * per the bus requirements and sent to the bus. It is not a requirement of
	 * the interface that the telemetry has been sent before this method returns.
	 * 
	 * @param telemetry
	 *          The telemetry artifact to be sent.
	 */
	void sendTelemetry(GlobalInfo telemetry);

	/**
	 * Set the exception handler that will be called if the sendTelemetry method
	 * fails for any reason.
	 * 
	 * @param handler
	 *          The exception handler.
	 */
	void setExceptionHandler(ExceptionHandler handler);

}
