package org.addsimplicity.anicetus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.addsimplicity.anicetus.entity.CompletionStatus;
import org.addsimplicity.anicetus.entity.ExecInfo;
import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.SubTypedInfo;
import org.addsimplicity.anicetus.entity.TelemetryEvent;
import org.addsimplicity.anicetus.entity.TelemetrySession;
import org.addsimplicity.anicetus.entity.TelemetryState;
import org.addsimplicity.anicetus.entity.TelemetryTransaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class JMSTest {
	TelemetryContext m_mgr;

	ApplicationContext m_context;

	@Before
	public void init() {
		m_context = new ClassPathXmlApplicationContext(new String[] { "org/addsimplicity/anicetus/JMSTestBinding.xml" });

		m_mgr = (TelemetryContext) m_context.getBean("manager");
	}

	@Test
	public void testEventBeacon() throws Exception {
		SubTypedInfo ev = new TelemetryEvent();
		ev.put("XYZZY", "Zork");

		m_mgr.sendBeacon(ev);

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetryEvent);

		SubTypedInfo recv = (SubTypedInfo) obj;

		assertEquals("Custom field", "Zork", recv.get("XYZZY"));
		assertNotNull("Host", recv.getReportingNode());
		assertNotNull("Context", recv.getExecutionContext());

	}

	@Test
	public void testSimpleSession() throws Exception {
		ExecInfo s = m_mgr.getSession();
		try {
			Thread.sleep(5);
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		s.setStatus(CompletionStatus.Success);
		m_mgr.endSession();

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetrySession);

		GlobalInfo rsess = (GlobalInfo) obj;
		assertEquals("EntityId", s.getEntityId(), rsess.getEntityId());
	}

	@Test
	public void testStateBeacon() throws Exception {
		TelemetryState st = new TelemetryState();
		st.put("XYZZY", "Zork");

		m_mgr.sendBeacon(st);

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetryState);

		TelemetryState recv = (TelemetryState) obj;

		assertEquals("Custom field", "Zork", recv.get("XYZZY"));
		assertNotNull("Host", recv.getReportingNode());
		assertNotNull("Context", recv.getExecutionContext());
	}

	@Test
	public void testStateNEvent() throws Exception {
		ExecInfo s = m_mgr.getSession();

		m_mgr.newEvent("TestEvent");
		m_mgr.newState();

		s.setStatus(CompletionStatus.PartialSuccess);
		m_mgr.endSession();

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetrySession);

		ExecInfo rsess = (ExecInfo) obj;

		Collection<GlobalInfo> childs = rsess.getChildren();
		assertEquals("Children", 2, childs.size());

		Iterator<GlobalInfo> iter = childs.iterator();

		GlobalInfo ch = iter.next();

		assertTrue("Type", ch instanceof TelemetryEvent);

		ch = iter.next();

		assertTrue("Type", ch instanceof TelemetryState);
	}

	@Test
	public void testTransaction() throws Exception {
		ExecInfo s = m_mgr.getSession();

		TelemetryTransaction t = m_mgr.beginTransaction("test:uri");
		String params[] = { "a", "b" };
		t.setParameters(params);

		s.setStatus(CompletionStatus.PartialSuccess);
		m_mgr.endSession();

		JmsTemplate tmpl = (JmsTemplate) m_context.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();
		assertNotNull("Object received", obj);
		assertTrue("Type", obj instanceof TelemetrySession);

		ExecInfo rsess = (ExecInfo) obj;

		assertEquals("Status", CompletionStatus.PartialSuccess, rsess.getStatus());

		Collection<GlobalInfo> childs = rsess.getChildren();
		assertEquals("Children", 1, childs.size());

		Iterator<GlobalInfo> iter = childs.iterator();

		GlobalInfo ch = iter.next();

		assertTrue("Type", ch instanceof TelemetryTransaction);

		TelemetryTransaction rt = (TelemetryTransaction) ch;

		String p[] = (String[]) rt.getParameters();

		assertEquals("Length", params.length, p.length);
		assertEquals("Parent", rsess.getEntityId(), rt.getParentId());
	}
}
