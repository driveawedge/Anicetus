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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

import org.addsimplicity.anicetus.TelemetryContext;
import org.addsimplicity.anicetus.entity.ExecInfo;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.TelemetrySession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Dan Pritchett
 * 
 */
public class HibernateTest {
	private static ApplicationContext m_context;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		m_context = new ClassPathXmlApplicationContext(
				new String[] { "org/addsimplicity/anicetus/hibernate/HibernateTestBinding.xml" });
	}

	private Session m_sess;

	@Test
	public void testEventCycles() throws Exception {
		TelemetryContext ctx = (TelemetryContext) m_context.getBean("telemetryContext");
		TelemetrySession tsess = ctx.getSession();
		tsess.setOperationName("testSimpleTransaction");

		SessionFactory fact = (SessionFactory) m_context.getBean("sessionFactory");

		m_sess = fact.openSession();

		SimpleBean b = new SimpleBean();
		b.setText("xyzzy");

		Transaction t = m_sess.beginTransaction();

		m_sess.save(b);

		t.commit();

		ctx.endSession();

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetrySession);

		TelemetrySession rsess = (TelemetrySession) obj;

		Collection<GlobalInfo> childs = rsess.getChildren();
		assertEquals("Child count", 1, childs.size());

		GlobalInfo c1 = childs.iterator().next();
		assertTrue("Child Type", c1 instanceof HibernateTelemetry);

		HibernateTelemetry telem = (HibernateTelemetry) c1;
		Collection<String> sqls = telem.getSQLStatements();
		assertEquals("SQL Statements", 2, sqls.size());

		for (String sql : sqls) {
			System.out.println(sql);
		}

		Collection<HibernateEntity> ents = telem.getHibernateEntities();
		assertEquals("Entities", 1, ents.size());

		HibernateEntity e = ents.iterator().next();

		assertEquals("Operation", HibernateOperation.Create, e.getOperation());

		System.out.println("Exec ms " + rsess.getDuration());
	}

	@Test
	public void testOtherOps() throws Exception {
		TelemetryContext ctx = (TelemetryContext) m_context.getBean("telemetryContext");
		TelemetrySession tsess = ctx.getSession();
		tsess.setOperationName("testSimpleTransaction");

		SessionFactory fact = (SessionFactory) m_context.getBean("sessionFactory");

		m_sess = fact.openSession();

		SimpleBean b = new SimpleBean();
		b.setText("xyzzy");

		Transaction t = m_sess.beginTransaction();

		m_sess.save(b);

		t.commit();

		t = m_sess.beginTransaction();

		b = (SimpleBean) m_sess.load(SimpleBean.class, Long.valueOf(b.getId()));
		assertNotNull("Bean found", b);

		b.setText("abbc");
		b.setDate(new Date(System.currentTimeMillis()));

		m_sess.save(b);

		t.commit();

		t = m_sess.beginTransaction();

		m_sess.delete(b);

		t.commit();

		ctx.endSession();

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetrySession);

		ExecInfo rsess = (ExecInfo) obj;

		Collection<GlobalInfo> childs = rsess.getChildren();
		assertEquals("Child count", 3, childs.size());

		Iterator<GlobalInfo> iter = childs.iterator();
		GlobalInfo gi = iter.next();
		assertTrue("Type", gi instanceof HibernateTelemetry);

		HibernateTelemetry ht = (HibernateTelemetry) gi;

		Collection<HibernateEntity> ents = ht.getHibernateEntities();
		assertEquals("Save 1 ents", 1, ents.size());
		assertEquals("Save 1 op", HibernateOperation.Create, ents.iterator().next().getOperation());

		for (String sql : ht.getSQLStatements()) {
			System.out.println(sql);
		}

		gi = iter.next();
		assertTrue("Type", gi instanceof HibernateTelemetry);

		ht = (HibernateTelemetry) gi;

		for (String sql : ht.getSQLStatements()) {
			System.out.println(sql);
		}

		ents = ht.getHibernateEntities();
		assertEquals("Save 2 ents", 1, ents.size());
		assertEquals("Save 2 op", HibernateOperation.Update, ents.iterator().next().getOperation());

		gi = iter.next();
		assertTrue("Type", gi instanceof HibernateTelemetry);

		ht = (HibernateTelemetry) gi;
		for (String sql : ht.getSQLStatements()) {
			System.out.println(sql);
		}

		ents = ht.getHibernateEntities();
		assertEquals("Delete ents", 1, ents.size());
		assertEquals("Delete op", HibernateOperation.Delete, ents.iterator().next().getOperation());

		Collection<String> tabs = ht.getTables();
		assertEquals("Tables", 1, tabs.size());
		assertEquals("Table Name", "BEANS", tabs.iterator().next());

	}

}
