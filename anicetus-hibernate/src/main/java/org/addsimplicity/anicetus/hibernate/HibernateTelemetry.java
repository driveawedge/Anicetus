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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.addsimplicity.anicetus.entity.EntityTypeRegistry;
import org.addsimplicity.anicetus.entity.ExecInfo;
import org.addsimplicity.anicetus.entity.GlobalInfo;

/**
 * The hibernate telemetry artifact tracks the actions performed by an
 * application against the database through the Hibernate framework. The state
 * captured provides information about what entities are involved, what
 * operations were performed, what tables were involved, and what sql was
 * executed. It also tracks timing information about the Hibernate transaction.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class HibernateTelemetry extends ExecInfo {

	static {
		EntityTypeRegistry.addSearchPackage(HibernateTelemetry.class.getPackage().getName());
		EntityTypeRegistry.addBeanType(HibernateEntity.class);
		EntityTypeRegistry.addClassShortName(HibernateTelemetry.class, "HT");
	}

	/**
	 * Construct a telemetry artifact without any parent.
	 */
	public HibernateTelemetry() {
	}

	/**
	 * Construct a telemetry artifact with the specified parent.
	 * 
	 * @param parent
	 *          The parent of the artifact.
	 */
	public HibernateTelemetry(GlobalInfo parent) {
		super(parent);
	}

	/**
	 * Add an entity to this artifact. The same entity may appear multiple times,
	 * once for each operation that is performed upon it. This is true even if the
	 * same operation is performed more than once on the entity.
	 * 
	 * @param entity
	 *          The entity to add to the telemetry.
	 */
	@SuppressWarnings("unchecked")
	public void addHibernateEntity(HibernateEntity entity) {
		List<HibernateEntity> entities = (List<HibernateEntity>) get(HibernateTelemetryFields.HibernateEntity.name());
		if (entities == null) {
			entities = new ArrayList<HibernateEntity>();
			put(HibernateTelemetryFields.HibernateEntity.name(), entities);
		}

		entities.add(entity);
	}

	/**
	 * Add a SQL statement to the telemetry. SQL statements are generated by
	 * Hibernate and loosely correlate to the entities that are added. Multiple
	 * SQL statements may appear for each entity operation however.
	 * 
	 * This method will also parse the table name from the statement and add them
	 * to the table lists.
	 * 
	 * @param statement
	 *          The SQL statement generated by Hibernate.
	 */
	@SuppressWarnings("unchecked")
	public void addSQLStatement(String statement) {
		List<String> stmts = (List<String>) get(HibernateTelemetryFields.SQLStatement.name());
		if (stmts == null) {
			stmts = new ArrayList<String>();
			put(HibernateTelemetryFields.SQLStatement.name(), stmts);
		}

		stmts.add(statement);

		String tables[] = getTableFromSQL(statement);
		if (tables != null) {
			for (String t : tables) {
				addTable(t);
			}
		}
	}

	/**
	 * Add a referenced table to the telemetry. Tables are added for any SQL
	 * statement they appear in.
	 * 
	 * @param table
	 *          The table name.
	 */
	@SuppressWarnings("unchecked")
	public void addTable(String table) {
		Set<String> tables = (Set<String>) get(HibernateTelemetryFields.Table.name());
		if (tables == null) {
			tables = new HashSet<String>();
			;
			put(HibernateTelemetryFields.Table.name(), tables);
		}

		tables.add(table);
	}

	/**
	 * Return an immutable collection of entities that were involved in the
	 * Hibernate transaction.
	 * 
	 * @return a collection of entities.
	 */
	@SuppressWarnings("unchecked")
	public Collection<HibernateEntity> getHibernateEntities() {
		return Collections
				.unmodifiableCollection((Collection<? extends HibernateEntity>) get(HibernateTelemetryFields.HibernateEntity
						.name()));
	}

	/**
	 * Return an immutable collection of SQL statements that were executed by
	 * Hibernate for this transaction.
	 * 
	 * @return a collection of SQL statements.
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getSQLStatements() {
		return Collections.unmodifiableCollection((Collection<? extends String>) get(HibernateTelemetryFields.SQLStatement
				.name()));
	}

	/**
	 * Return an immutable set of unique table names involved in this Hibernate
	 * transaction.
	 * 
	 * @return a distinct set of table names.
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getTables() {
		return Collections
				.unmodifiableCollection((Collection<? extends String>) get(HibernateTelemetryFields.Table.name()));
	}

	/**
	 * Set the entities involved in the Hibernate transaction.
	 * 
	 * @param entities
	 *          The entities involved in the Hibernate transaction.
	 */
	public void setHibernateEntities(Collection<HibernateEntity> entities) {
		put(HibernateTelemetryFields.HibernateEntity.name(), entities);
	}

	/**
	 * Set the collection of SQL statements involved in the Hibernate transaction.
	 * 
	 * @param statements
	 *          The SQL statements involved in the Hibernate transaction.
	 */
	public void setSQLStatements(Collection<String> statements) {
		put(HibernateTelemetryFields.SQLStatement.name(), statements);
	}

	/**
	 * Set the collection of tables involved in the Hibernate transaction.
	 * 
	 * @param tables
	 *          The collection of tables involved in the transaction.
	 */
	public void setTables(Collection<String> tables) {
		Set<String> tset = new HashSet<String>(tables.size());
		tset.addAll(tables);

		put(HibernateTelemetryFields.Table.name(), tset);
	}

	private String[] getTableFromSQL(String sql) {
		String result[] = null;

		StringTokenizer toks = new StringTokenizer(sql);

		String op = toks.nextToken();
		if (op.equalsIgnoreCase("select")) {
			result = parseSelect(toks);
		}
		else if (op.equalsIgnoreCase("update")) {
			result = parseUpdate(toks);
		}
		else if (op.equalsIgnoreCase("insert")) {
			result = parseInsertAndDelete(toks);
		}
		else if (op.equalsIgnoreCase("delete")) {
			result = parseInsertAndDelete(toks);
		}

		return result;
	}

	private String[] parseInsertAndDelete(StringTokenizer toks) {
		// Next token is "from" or "into"
		toks.nextToken();

		return new String[] { toks.nextToken() };
	}

	private String[] parseSelect(StringTokenizer toks) {
		// Tables live between "FROM" and either "WHERE" or end of statement.
		//
		while (toks.hasMoreTokens()) {
			if (toks.nextToken().equalsIgnoreCase("from")) {
				break;
			}
		}

		// Reassemble the string until you reach the "where" or end. We have to
		// split on commas
		// now.
		//
		StringBuffer sb = new StringBuffer();
		while (toks.hasMoreTokens()) {
			String t = toks.nextToken();
			if (t.equalsIgnoreCase("where")) {
				break;
			}
			sb.append(" ");
			sb.append(t);
		}

		StringTokenizer commas = new StringTokenizer(sb.toString(), ",");

		List<String> tables = new ArrayList<String>();

		while (commas.hasMoreTokens()) {
			String tab = commas.nextToken();
			int space = tab.indexOf(" ");
			if (space > 0) {
				tables.add(tab.substring(0, space));
			}
			else {
				tables.add(tab);
			}
		}

		if (tables.size() > 0) {
			String[] result = new String[tables.size()];
			tables.toArray(result);

			return result;
		}
		else {
			return null;
		}
	}

	private String[] parseUpdate(StringTokenizer toks) {
		return new String[] { toks.nextToken() };
	}
}
