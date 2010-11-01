package org.addsimplicity.anicetus.flume;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.entity.GlobalInfoFields;
import org.addsimplicity.anicetus.entity.TelemetryContainer;
import org.addsimplicity.anicetus.io.DeliveryAdapter;
import org.addsimplicity.anicetus.io.ExceptionHandler;
import org.addsimplicity.anicetus.io.SystemErrorExceptionHandler;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.cloudera.flume.handlers.thrift.Priority;
import com.cloudera.flume.handlers.thrift.ThriftFlumeEvent;
import com.cloudera.flume.handlers.thrift.ThriftFlumeEventServer;

public class FlumeDeliveryAdapter implements DeliveryAdapter, DisposableBean, InitializingBean {
	private static final Set<String> s_blockKeys = new HashSet<String>();

	static {
		s_blockKeys.add(GlobalInfoFields.ReportingNode.name());
		s_blockKeys.add(GlobalInfoFields.Message.name());
		s_blockKeys.add(GlobalInfoFields.TimeStamp.name());
	}
	private static Charset FLUME_CHARSET = Charset.forName("UTF-8");

	private ExceptionHandler m_exceptionHandler = new SystemErrorExceptionHandler();

	private TTransport m_transport;
	private ThriftFlumeEventServer.Client m_eventClient;
	
	private String m_flumeHost;
	private int m_flumePort;

	@Override
	public void afterPropertiesSet() throws Exception {
		m_transport = new TSocket(m_flumeHost, m_flumePort);
		TProtocol proto = new TBinaryProtocol(m_transport);
		m_eventClient = new ThriftFlumeEventServer.Client(proto);
		m_transport.open();
	}

	@Override
	public void destroy() throws Exception {
		m_transport.close();
	}

	public ExceptionHandler getExceptionHandler() {
		return m_exceptionHandler;
	}

	public String getFlumeHost() {
		return m_flumeHost;
	}

	public int getFlumePort() {
		return m_flumePort;
	}

	@Override
	public void sendTelemetry(GlobalInfo telemetry) {
		String smsg = telemetry.getMessage();
		if (smsg == null) {
			smsg = "(null)";
		}
		byte [] msg = smsg.getBytes(FLUME_CHARSET);
		ByteBuffer body = ByteBuffer.wrap(msg);

		Map<String, ByteBuffer> fields = new HashMap<String, ByteBuffer>();

		fields.put(
				"TelemetryType",
				ByteBuffer.wrap(telemetry.getClass().getName()
						.getBytes(FLUME_CHARSET)));

		for (Map.Entry<String, Object> entry : telemetry.entrySet()) {
			String key = entry.getKey();
			if (s_blockKeys.contains(key)) {
				continue;
			}

			Class<?> type = entry.getValue().getClass();
			if (!type.isArray()) {
				fields.put(
						key,
						ByteBuffer.wrap(entry.getValue().toString()
								.getBytes(FLUME_CHARSET)));
			} else {
				fields.put(key, ByteBuffer.wrap(toStringArray((Object[]) entry
						.getValue())));
			}
		}

		ThriftFlumeEvent evt = new ThriftFlumeEvent(telemetry.getTimeStamp(),
				Priority.INFO, body, 0, telemetry.getReportingNode(), fields);
		
		try {
			m_eventClient.append(evt);
		} catch (TException e) {
			m_exceptionHandler.exceptionCaught(e);
		}
		
		if (telemetry instanceof TelemetryContainer) {
			for (GlobalInfo t : ((TelemetryContainer)telemetry).getChildren()) {
				sendTelemetry(t);
			}
		}
	}

	@Override
	public void setExceptionHandler(ExceptionHandler handler) {
		m_exceptionHandler = handler;
	}

	public void setFlumeHost(String flumeHost) {
		m_flumeHost = flumeHost;
	}

	public void setFlumePort(int flumePort) {
		m_flumePort = flumePort;
	}

	private byte[] toStringArray(Object values[]) {
		StringBuilder sb = new StringBuilder();

		for (Object v : values) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(v.toString());
		}

		return sb.toString().getBytes(FLUME_CHARSET);
	}

}
