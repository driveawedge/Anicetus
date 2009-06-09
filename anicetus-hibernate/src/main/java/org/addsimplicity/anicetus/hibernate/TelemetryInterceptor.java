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

import java.io.Serializable;

import org.addsimplicity.anicetus.TelemetryContext;
import org.addsimplicity.anicetus.entity.CompletionStatus;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * The telemetry interceptor uses the Hibernate interceptor interface to capture
 * operations that are performed by Hibernate and create a telemetry artifact.
 * The telemetry is created at the start of a transaction so that it can gather
 * all of the entities and operations that are done during the transaction. The
 * operation timer is started at the beginning of the commit so the time
 * captured is database processing time. Note that telemetry is not captured if
 * a Hibernate transaction is not used.
 * 
 * The interceptor relies upon Spring to retrieve the context used for managing
 * the state of the transactional stack.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryInterceptor extends EmptyInterceptor implements BeanFactoryAware {
	private static final long serialVersionUID = 1L;

	private transient BeanFactory m_beanFactory;

	private String m_managerName = "telemetryContext";

	/**
	 * This method is called by Hibernate after the begin transaction operation. A
	 * new telemetry artifact is created at the point that this method is called.
	 * 
	 */
	@Override
	public void afterTransactionBegin(Transaction tx) {
		TelemetryContext ctx = (TelemetryContext) m_beanFactory.getBean(m_managerName);
		HibernateTelemetry telem = new HibernateTelemetry(ctx.peekTransaction());
		ctx.pushTransaction(telem);
	}

	/**
	 * This method is called by Hibernate when the transaction completes. The
	 * telemetry status is set based on the transaction committed status.
	 */
	@Override
	public void afterTransactionCompletion(Transaction tx) {
		TelemetryContext ctx = (TelemetryContext) m_beanFactory.getBean(m_managerName);
		HibernateTelemetry telem = (HibernateTelemetry) ctx.popTransaction();
		telem.complete();

		if (tx.wasCommitted()) {
			telem.setStatus(CompletionStatus.Success);
		}
		else {
			telem.setStatus(CompletionStatus.Failure);
		}
	}

	/**
	 * This method is called by Hibernate before the transaction begins to commit
	 * to the database.
	 */
	@Override
	public void beforeTransactionCompletion(Transaction tx) {
		getTelemetry().startTimer();
	}

	/**
	 * Get the key used to retrieve the TelemetryContext from the Spring context.
	 * 
	 * @return the key to the TelemetryContext.
	 */
	public String getManagerName() {
		return m_managerName;
	}

	/**
	 * This method is called by Hibernate prior to an entity being deleted.
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		HibernateEntity he = new HibernateEntity(entity.getClass().getName(), id.toString(), HibernateOperation.Delete);
		getTelemetry().addHibernateEntity(he);
	}

	/**
	 * This method is called by Hibernate prior to an entity being updated.
	 */
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		HibernateEntity he = new HibernateEntity(entity.getClass().getName(), id.toString(), HibernateOperation.Update);
		getTelemetry().addHibernateEntity(he);
		return false;
	}

	/**
	 * This method is called prior to an object being loaded from the database.
	 */
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		HibernateEntity he = new HibernateEntity(entity.getClass().getName(), id.toString(), HibernateOperation.Load);
		getTelemetry().addHibernateEntity(he);
		return false;
	}

	/**
	 * This method is called by Hibernate each time a SQL statement is prepared.
	 */
	@Override
	public String onPrepareStatement(String sql) {
		getTelemetry().addSQLStatement(sql);

		return sql;
	}

	/**
	 * This method is called by Hibernate each time an entity is being inserted.
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		HibernateEntity he = new HibernateEntity(entity.getClass().getName(), id != null ? id.toString() : "NEW",
				HibernateOperation.Create);
		getTelemetry().addHibernateEntity(he);
		return false;
	}

	/**
	 * This method is called by Spring to set the context used to create the bean.
	 */
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		m_beanFactory = factory;
	}

	/**
	 * Set the Spring key that is used to retrieve the TelemetryContext from the
	 * Spring container.
	 * 
	 * @param managerName
	 *          The key to the TelemetryContext
	 */
	public void setManagerName(String managerName) {
		m_managerName = managerName;
	}

	private HibernateTelemetry getTelemetry() {
		TelemetryContext ctx = (TelemetryContext) m_beanFactory.getBean(m_managerName);

		return (HibernateTelemetry) ctx.peekTransaction();
	}

}
