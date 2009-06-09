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
package org.addsimplicity.anicetus.web;

/**
 * The header type maps the directional headers provided by the telemetry. The
 * HTTP protocol has both request and response headers, reflected by the
 * enumerated type.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
enum HeaderType {
	/**
	 * Headers associated with the request.
	 */
	Request,

	/**
	 * Headers associated with the response.
	 */
	Response
}
