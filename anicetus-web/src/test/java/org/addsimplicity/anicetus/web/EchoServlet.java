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
package org.addsimplicity.anicetus.web;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 * @author Dan Pritchett
 * 
 */

public class EchoServlet extends HttpServlet {
	private static final long serialVersionUID = 16218783828419867L;

	@SuppressWarnings("unchecked")
	private void writeHeaders(HttpServletRequest req, JsonGenerator gen) throws IOException {
		gen.writeFieldName("HEADERS");

		gen.writeStartArray();

		Enumeration<String> niter = req.getHeaderNames();
		while (niter.hasMoreElements()) {
			String name = niter.nextElement();
			Enumeration<String> viter = req.getHeaders(name);

			gen.writeStartObject();
			gen.writeFieldName(name);

			gen.writeStartArray();
			while (viter.hasMoreElements()) {
				gen.writeString(viter.nextElement());
			}

			gen.writeEndArray();
			gen.writeEndObject();
		}

		gen.writeEndArray();
	}

	@SuppressWarnings("unchecked")
	private void writeParameters(HttpServletRequest req, JsonGenerator gen) throws IOException {
		gen.writeFieldName("PARAMS");

		Enumeration<String> names = req.getParameterNames();

		gen.writeStartArray();

		while (names.hasMoreElements()) {
			gen.writeStartObject();
			String name = names.nextElement();

			gen.writeFieldName(name);
			gen.writeString(req.getParameter(name));

			gen.writeEndObject();
		}

		gen.writeEndArray();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");

		CharArrayWriter out = new CharArrayWriter();
		try {
			JsonGenerator gen = (new JsonFactory()).createJsonGenerator(out);
			gen.writeStartObject();

			writeHeaders(req, gen);
			writeParameters(req, gen);

			gen.writeEndObject();

			gen.close();
			out.close();

			byte[] result = out.toString().getBytes();

			resp.setContentLength(result.length);

			resp.getOutputStream().write(result);
		}
		catch (IOException ioe) {
			resp.sendError(500, ioe.getMessage());
			ioe.printStackTrace();
			return;
		}
	}
}
