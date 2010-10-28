package org.addsimplicity.anicetus.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Unit test for simple App.
 */
@SuppressWarnings("unchecked")
public class SessionTest {
	private static final int s_PORT = 9191;
	private static Set<String> s_exclHeaders = new HashSet<String>();

	private static Set<String> s_exclParams = new HashSet<String>();

	static {
		s_exclHeaders.add("X-Anicetus-Parent-GUID");
	}

	static {
		s_exclParams.add("notpassed");
	}

	Server m_server;
	ServletHolder m_servletHolder;

	private JsonNode parseResponse(HttpEntity entity) throws IOException {
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(entity.getContent());

		ObjectMapper mapper = new ObjectMapper();

		return mapper.readTree(parser);
	}

	@Before
	public void startServletEngine() {
		m_server = new Server();

		Connector conn = new SocketConnector();
		conn.setPort(s_PORT);
		m_server.setConnectors(new Connector[] { conn });

		Context ctx = new Context(m_server, "/");

		ctx.getInitParams()
				.put("contextConfigLocation",
						"classpath:org/addsimplicity/anicetus/web/JMSLaunch.xml,classpath:org/addsimplicity/anicetus/web/SessionTestBinding.xml");

		ContextLoaderListener ctxLoader = new ContextLoaderListener();
		ctx.addEventListener(ctxLoader);

		RequestContextListener listener = new RequestContextListener();
		ctx.addEventListener(listener);

		ctx.addFilter(TelemetryServletFilter.class, "/", 1);

		m_servletHolder = ctx.addServlet(EchoServlet.class, "/");
	}

	@Test
	public void testBasicRequest() throws Exception {
		m_server.start();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:" + s_PORT + "/?a=1&b=2");
		get.setHeader("Content-Type", "text/plain");

		HttpResponse resp = client.execute(get);

		HttpEntity entity = resp.getEntity();

		JsonNode node = parseResponse(entity);

		ApplicationContext curCtx = WebApplicationContextUtils
				.getWebApplicationContext(m_servletHolder.getServlet()
						.getServletConfig().getServletContext());

		JmsTemplate tmpl = (JmsTemplate) curCtx.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();

		assertNotNull(obj);
		assertTrue(obj instanceof TelemetryHttpSession);

		TelemetryHttpSession hsess = (TelemetryHttpSession) obj;

		validateHeaders(node, resp, hsess);
		validateParams(node, hsess);

		m_server.stop();
	}

	@Test
	public void testParentAndBlock() throws Exception {
		m_server.start();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:" + s_PORT
				+ "/?a=1&b=2&notpassed=3");
		get.setHeader("Content-Type", "text/plain");
		UUID parent = UUID.randomUUID();
		get.setHeader("X-Anicetus-Parent-GUID", parent.toString());

		HttpResponse resp = client.execute(get);

		HttpEntity entity = resp.getEntity();

		JsonNode node = parseResponse(entity);

		ApplicationContext curCtx = WebApplicationContextUtils
				.getWebApplicationContext(m_servletHolder.getServlet()
						.getServletConfig().getServletContext());

		JmsTemplate tmpl = (JmsTemplate) curCtx.getBean("consumeTempl");
		Object obj = tmpl.receiveAndConvert();

		assertNotNull(obj);
		assertTrue(obj instanceof TelemetryHttpSession);

		TelemetryHttpSession hsess = (TelemetryHttpSession) obj;

		assertEquals("parent", parent, hsess.getParentId());

		validateHeaders(node, resp, hsess);
		validateParams(node, hsess);

		m_server.stop();

	}

	private void validateHeaders(JsonNode node, HttpResponse resp,
			TelemetryHttpSession hsess) throws Exception {
		JsonNode hnode = node.get("HEADERS");

		Iterator<JsonNode> hiter = hnode.getElements();
		while (hiter.hasNext()) {
			JsonNode h = hiter.next();
			Iterator<String> names = h.getFieldNames();
			while (names.hasNext()) {
				String n = names.next();
				if (s_exclHeaders.contains(n)) {
					continue;
				}
				Iterator<JsonNode> viter = h.get(n).getElements();
				String svals[] = hsess.getHeaderValues(n, HeaderType.Request);
				assertNotNull(n, svals);
				for (int v = 0; viter.hasNext(); v++) {
					String hval = svals[v];
					String nval = viter.next().getTextValue();
					assertEquals(n + "[" + v + "]", nval, hval);
				}
			}
		}

		for (Header rh : resp.getAllHeaders()) {
			String name = rh.getName();
			if (name.equals("Server")) {
				continue;
			}
			String value = rh.getValue();

			assertEquals(name, value,
					hsess.getHeader(name, HeaderType.Response));
		}

	}

	private void validateParams(JsonNode node, TelemetryHttpSession hsess)
			throws Exception {
		JsonNode pnode = node.get("PARAMS");

		Iterator<JsonNode> piter = pnode.getElements();
		while (piter.hasNext()) {
			JsonNode p = piter.next();
			Iterator<String> names = p.getFieldNames();
			while (names.hasNext()) {
				String n = names.next();
				if (s_exclParams.contains(n)) {
					continue;
				}
				String val = p.get(n).getValueAsText();

				assertEquals(n, val, hsess.getParameter(n));
			}
		}
	}
}
