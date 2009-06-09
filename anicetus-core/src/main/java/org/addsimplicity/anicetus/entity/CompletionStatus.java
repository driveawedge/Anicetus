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

import java.util.HashMap;
import java.util.Map;

/**
 * The completion status defines the outcome of a session or transaction.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public enum CompletionStatus {
	/**
	 * The execution completed successfully.
	 */
	Success('S'),

	/**
	 * The execution failed.
	 */
	Failure('F'),

	/**
	 * The execution completed, but only part of the task goals were completed.
	 */
	PartialSuccess('P'),

	/**
	 * The completion status was not set by the application.
	 */
	Unknown('U');

	private static class Holder {
		private static Map<Character, CompletionStatus> s_holder = new HashMap<Character, CompletionStatus>();
	}

	/**
	 * Translate the single letter status abbreviation to the enumeration
	 * instance.
	 * 
	 * @param abbrev
	 *          The single letter abbreviation.
	 * @return the enumeration that maps to the status or null if the status is
	 *         invalid.
	 */
	public static CompletionStatus fromAbbreviation(char abbrev) {
		return Holder.s_holder.get(abbrev);
	}

	public static CompletionStatus fromString(String val) {
		return fromAbbreviation(val.charAt(0));
	}

	private char m_abbrev;

	private CompletionStatus(char abbrev) {
		m_abbrev = abbrev;
		Holder.s_holder.put(abbrev, this);
	}

	/**
	 * Get the abbreviation from the enumeration.
	 * 
	 * @return the abbreviation.
	 */
	public char getAbbreviation() {
		return m_abbrev;
	}

	@Override
	public String toString() {
		char carray[] = new char[1];
		carray[0] = getAbbreviation();

		return new String(carray);
	}
}
