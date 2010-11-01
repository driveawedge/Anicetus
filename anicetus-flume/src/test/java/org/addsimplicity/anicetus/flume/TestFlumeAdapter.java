package org.addsimplicity.anicetus.flume;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.addsimplicity.anicetus.TelemetryContext;
import org.addsimplicity.anicetus.entity.CompletionStatus;
import org.addsimplicity.anicetus.entity.TelemetryEvent;
import org.addsimplicity.anicetus.entity.TelemetrySession;
import org.addsimplicity.anicetus.entity.TelemetryTransaction;
import org.addsimplicity.anicetus.io.DeliveryAdapter;
import org.addsimplicity.anicetus.io.ExceptionHandler;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cloudera.flume.conf.thrift.FlumeMasterAdminServer;
import com.cloudera.flume.conf.thrift.FlumeMasterCommandThrift;

public class TestFlumeAdapter implements ExceptionHandler {
	static final String s_testFile = "/tmp/anicetus-flume-test.json";
	
	static TTransport masterTransport;
	static FlumeMasterAdminServer.Client masterServer;
	TelemetryContext m_mgr;
	ApplicationContext m_context;
	Exception m_caught;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		masterTransport = new TSocket("localhost", 35873);
		TProtocol proto = new TBinaryProtocol(masterTransport);
		masterServer = new FlumeMasterAdminServer.Client(proto);
		masterTransport.open();
	}

	@Before
	public void init() throws Exception {
		m_caught = null;
		m_context = new ClassPathXmlApplicationContext(
				new String[] { "org/addsimplicity/anicetus/flume/FlumeTestBinding.xml" });

		m_mgr = (TelemetryContext) m_context.getBean("manager");
		
		DeliveryAdapter adapter = (DeliveryAdapter)m_context.getBean("adapter");
		adapter.setExceptionHandler(this);
		
		configureNode(s_testFile);
	}
	
	@Override
	public void exceptionCaught(Throwable exception) {
		m_caught = (Exception)exception;
	}

	@Test
	public void testEvent() throws Exception {		
		TelemetryEvent evt = (TelemetryEvent)m_mgr.newEvent("test");
		evt.setMessage("testEvent");
		evt.put("X", "1");
		
		m_mgr.sendBeacon(evt);
		if (m_caught != null) {
			throw m_caught;
		}
	}
	
	@Test
	public void testSession() throws Exception {
		TelemetrySession sess = m_mgr.getSession();
		sess.setMessage("testSession");
		
		TelemetryTransaction trans = m_mgr.beginTransaction("flume-remote");
		trans.setMessage("calling remote");
		String params[] = {"a", "b"};
		trans.setParameters(params);
		trans.setStatus(CompletionStatus.Success);
		trans.complete();
		
		sess.setStatus(CompletionStatus.Success);
		
		m_mgr.endSession();
		if (m_caught != null) {
			throw m_caught;
		}
	}

	private void configureNode(String path) throws Exception {
		List<String> args = new ArrayList<String>();
		args.add("anicetus-test");
		args.add("rpcSource(35883)");
		args.add("text(\"" + path + "\", \"avrojson\")");
		FlumeMasterCommandThrift cmd = new FlumeMasterCommandThrift("config", args);
		
		masterServer.submit(cmd);
		
		// Sleep for 2 seconds to let configuration propogate...we hope.
		//
		Thread.sleep(2000);
	}
	
	List<JsonNode> parseLog(String path) throws Exception {
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(new File(path));
		ObjectMapper mapper = new ObjectMapper();
		
		List<JsonNode> result = new ArrayList<JsonNode>();
		
		while (true) {
			try {
				JsonNode node = mapper.readTree(parser);
				if (node == null) {
					break;
				}
				result.add(node);
			} catch (Exception e) {
				break;
			}
		}
		
		
		return result;
	}
}
