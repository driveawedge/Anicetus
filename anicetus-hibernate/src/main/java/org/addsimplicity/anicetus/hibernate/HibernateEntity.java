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
package org.addsimplicity.anicetus.hibernate;

/**
 * A hibernate entity tracks the operation performed on a type. The entity
 * includes the primary key of the entity so each individual entity is tracked
 * directly.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class HibernateEntity {
	private String m_type;
	private String m_identifier;
	private HibernateOperation m_operation;

	/**
	 * Construct an empty hibernate entity.
	 */
	public HibernateEntity() {

	}

	/**
	 * Construct an entity of the specified type, instance, and operation.
	 * 
	 * @param type
	 *          The name of the type. Typically the fully qualified class name.
	 * @param identifier
	 *          The primary key of the instance.
	 * @param operation
	 *          The operation being performed upon the instance.
	 */
	public HibernateEntity(String type, String identifier, HibernateOperation operation) {
		super();
		m_type = type;
		m_identifier = identifier;
		m_operation = operation;
	}

	/**
	 * Get the primary key of the entity.
	 * 
	 * @return the primary key.
	 */
	public String getIdentifier() {
		return m_identifier;
	}

	/**
	 * Get the operation performed upon the entity.
	 * 
	 * @return the operation performed upon the entity.
	 */
	public HibernateOperation getOperation() {
		return m_operation;
	}

	/**
	 * Get the name of the type of the entity.
	 * 
	 * @return the type of the entity.
	 */
	public String getType() {
		return m_type;
	}

	/**
	 * Set the primary key of the entity.
	 * 
	 * @param identifier
	 *          The primary key of the entity.
	 */
	public void setIdentifier(String identifier) {
		m_identifier = identifier;
	}

	/**
	 * Set the operation performed upon the entity.
	 * 
	 * @param operation
	 *          The operation performed upon the entity.
	 * 
	 */
	public void setOperation(HibernateOperation operation) {
		m_operation = operation;
	}

	/**
	 * Set the type of the entity. Typically the fully qualified class name.
	 * 
	 * @param type
	 *          The type name.
	 */
	public void setType(String type) {
		m_type = type;
	}

}
