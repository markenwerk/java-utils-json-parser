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

import java.util.List;

import net.markenwerk.commons.iterables.InfixedIterable;

/**
 * A {@link JsonSyntaxException} indicates that a {@link JsonParser} encountered
 * a JSON syntax error while parsing a JSON document.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class JsonSyntaxException extends Exception {

	private static final long serialVersionUID = 4648280170907086815L;

	private final String message;

	private final int line;

	private final int column;

	private final String past;

	private final String future;

	private final List<String> path;

	JsonSyntaxException(String message, int line, int column, String past, String future, List<String> path) {
		this.message = message;
		this.line = line;
		this.column = column;
		this.past = past;
		this.future = future;
		this.path = path;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(message);
		builder.append(" at line ");
		builder.append(line);
		builder.append(", column ");
		builder.append(column);
		builder.append(", after '");
		builder.append(past);
		builder.append("', before '");
		builder.append(future);
		builder.append("', at /");
		for (String part : new InfixedIterable<String>(path, "/")) {
			builder.append(part);
		}
		return builder.toString();
	}

	/**
	 * Returns a description of the actual syntax error.
	 * 
	 * @return The error description.
	 */
	public String getErrorMessage() {
		return message;
	}

	/**
	 * Returns the line in the JSON document, where the syntax error occurred.
	 * 
	 * @return The line.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns the column in the JSON document, where the syntax error occurred.
	 * 
	 * @return The columns.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns a few characters in the parsed character stream, that came before
	 * the syntax error, if still cached.
	 * 
	 * @return The snippet from the character stream.
	 */
	public String getPast() {
		return past;
	}

	/**
	 * Returns a few characters in the parsed character stream, that come after
	 * the syntax error, if already cached.
	 * 
	 * @return The snippet from the character stream.
	 */
	public String getFuture() {
		return future;
	}

	/**
	 * Returns the path in the JSON document (e.g <tt>/foo/0/bar</tt> for
	 * <tt>{"foo":[{"bar":ERROR}]}</tt>), where the syntax error occurred.
	 * 
	 * @return The path.
	 */
	public List<String> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
