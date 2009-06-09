package org.addsimplicity.anicetus;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;

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

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		m_delegate.write(cbuf, off, len);
	}

	JsonNode getObjectGraph() throws Exception {
		System.out.println(m_delegate.toCharArray());
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(new CharArrayReader(m_delegate
				.toCharArray()));
		JsonTypeMapper mapper = new JsonTypeMapper();

		return mapper.read(parser);
	}
	
	List<JsonNode> getAllObjects() throws Exception {
		System.out.println(m_delegate.toCharArray());
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createJsonParser(new CharArrayReader(m_delegate
				.toCharArray()));
		JsonTypeMapper mapper = new JsonTypeMapper();

		List<JsonNode> result = new ArrayList<JsonNode>();
		
		while (true) {
			JsonNode node = mapper.read(parser);
			if (node == null) {
				break;
			}
			result.add(node);
		}
		
		return result;
	}

}
