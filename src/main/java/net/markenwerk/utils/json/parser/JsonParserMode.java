/*
 * Copyright (c) 2016 Torsten Krause, Markenwerk GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.markenwerk.utils.json.parser;

/**
 * A {@link JsonParserMode} is a configuration flag that modifies the execution
 * of a {@link DefaultJsonPullParser} or a {@link DefaultJsonPushParser}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public enum JsonParserMode {

	/**
	 * Allows a {@link JsonSource} to contain successive JSON documents. May be
	 * useful to stream through a log file that contains appended JSON documents
	 * or to process a server response where the server sends multiple JSON
	 * documents through a persistent connection.
	 */
	MULTI_DOCUMENT_MODE,

	/**
	 * Requires that the JSON document contains either a JSON array or a JSON
	 * object, but no other JSON value, as required in the now obsolete <a
	 * href="https://tools.ietf.org/html/rfc4627">RFC 4627</a>
	 */
	STRICT_STRUCT_MODE

}
