package org.addsimplicity.anicetus;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class InMemoryAdapter extends Writer {
	private CharArrayWriter m_delegate;

	void clear() {
		m_delegate = new CharArrayWriter();
	}

	@Override
	public void close() throws IOException {
		m_delegate.close();
	}

	@Override
	public void flush() throws IOException {
		m_delegate.flush();
	}

	List<JsonNode> getAllObjects() throws Exception {
		System.out.println(m_delegate.toCharArray());
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(new CharArrayReader(
				m_delegate.toCharArray()));
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

	JsonNode getObjectGraph() throws Exception {
		System.out.println(m_delegate.toCharArray());
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(new CharArrayReader(
				m_delegate.toCharArray()));
		ObjectMapper mapper = new ObjectMapper();

		return mapper.readTree(parser);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		m_delegate.write(cbuf, off, len);
	}

}
