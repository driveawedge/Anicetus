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

/**
 * JsonConstants defines the keys that are found in the JSON protocol per the
 * Anicetus protocol definition.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class JsonConstants {
	/**
	 * Completion Status - String - a_status - One of S (Success), P (Partial
	 * Success), U (Unknown), or F (Failure).
	 */
	public static final String CompletionStatus = "a_status";

	/**
	 * Entity Identifier - String - a_entityid - RFC4122 formatted string.
	 */
	public static final String EntityIdentifier = "a_entityid";

	/**
	 * Entity Type - String - a_type - Either the type string of an Anicetus type
	 * or an application extended type.
	 */
	public static final String EntityType = "a_type";

	/**
	 * Execution Context - String - a_exectx - Process identifier and optional
	 * thread identifer. Typically in the form of pid.tid.
	 */
	public static final String ExecutionContext = "a_exectx";

	/**
	 * Execution Time - 64-bit Integer - a_execns - Execution time in
	 * nano-seconds.
	 */
	public static final String ExecutionTime = "a_execns";

	/**
	 * Message - String - a_msg - Optionally provided application message.
	 */
	public static final String Message = "a_msg";

	/**
	 * Operation Name - String - a_operation - Application defined operation name.
	 */
	public static final String OperationName = "a_operation";

	/**
	 * Parameters - String Array - a_params - Optional parameters for the
	 * operation.
	 */
	public static final String Parameters = "a_params";

	/**
	 * Parent Entity - String - a_parentid - RFC4122 formatted string.
	 */
	public static final String ParentEntity = "a_parentid";

	/**
	 * Reporting Node - String - a_node - Hostname, IP address, or applicaiton
	 * assigned logical name of node.
	 */
	public static final String ReportingNode = "a_node";

	/**
	 * Resource Identifier - String - a_resourceid - URL of the external resource.
	 */
	public static final String ResourceIdentifier = "a_resourceid";

	/**
	 * Event Type - String - a_evtype - An application defined type.
	 */
	public static final String SubType = "a_subtype";

	/**
	 * Time Stamp - 64-bit Integer - a_timestamp - The number of milliseconds
	 * since January 1, 1970
	 */
	public static final String TimeStamp = "a_timestamp";
}
