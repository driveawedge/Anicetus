package org.addsimplicity.anicetus.hibernate;

import java.sql.Date;

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

/**
 * @author Dan Pritchett
 * 
 */
public class SimpleBean {
	private long m_id;
	private Date m_date;
	private String m_text;

	public SimpleBean() {
		super();
	}

	public Date getDate() {
		return m_date;
	}

	public long getId() {
		return m_id;
	}

	public String getText() {
		return m_text;
	}

	public void setDate(Date date) {
		m_date = date;
	}

	public void setId(long id) {
		m_id = id;
	}

	public void setText(String text) {
		m_text = text;
	}

}
