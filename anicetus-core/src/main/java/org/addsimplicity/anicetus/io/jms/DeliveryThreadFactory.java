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

import java.util.concurrent.ThreadFactory;

/**
 * The delivery thread factory creates threads for publishing telemetry
 * asynchronously. One of the primary reasons for this factory is to provide
 * naming that makes the threads created role clear.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
class DeliveryThreadFactory implements ThreadFactory {
	private static final String s_Prefix = "AnicetusTelemetry";
	private static int s_instanceCount = 0;

	private final ThreadGroup m_threadGroup = new ThreadGroup(s_Prefix + "Group-" + (s_instanceCount++));

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	public Thread newThread(Runnable r) {
		Thread t = new Thread(m_threadGroup, r);
		t.setName(s_Prefix + "-" + t.getId());
		return t;
	}

}
