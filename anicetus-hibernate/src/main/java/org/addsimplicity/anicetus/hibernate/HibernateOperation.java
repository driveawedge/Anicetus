package org.addsimplicity.anicetus.hibernate;

/**
 * The operation enumeration captures the hibernate action taken. It follows the
 * basic CRUD definitions.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public enum HibernateOperation {
	/**
	 * A new entity is being added to the database.
	 */
	Create,

	/**
	 * An entity is being deleted from the database.
	 */
	Delete,

	/**
	 * An entity is being retrieved from the database.
	 */
	Load,

	/**
	 * An entity is being updated in the database.
	 */
	Update
}
