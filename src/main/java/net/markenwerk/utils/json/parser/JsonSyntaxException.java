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
 * A {@link JsonSyntaxException} indicates that a {@link JsonPullParser} or a
 * {@link JsonPushParser} encountered a JSON syntax error while parsing a JSON
 * document.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class JsonSyntaxException extends RuntimeException {

	private static final long serialVersionUID = 4648280170907086815L;

	private final JsonSyntaxError error;

	private final int line;

	private final int column;

	private final String past;

	private final String future;

	/**
	 * Creates a new {@link JsonSyntaxException} for the given components.
	 * 
	 * @param error
	 *            The syntax error.
	 * @param line
	 *            The line of the syntax error.
	 * @param column
	 *            The column of the syntax error
	 * @param past
	 *            A few characters from the parsed character stream, that came
	 *            before the syntax error.
	 * @param future
	 *            a few characters from the parsed character stream, that come
	 *            after the syntax error.
	 */
	public JsonSyntaxException(JsonSyntaxError error, int line, int column, String past, String future) {
		this.error = error;
		this.line = line;
		this.column = column;
		this.past = past;
		this.future = future;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(error.getDescription());
		builder.append(" (");
		builder.append(error.getExpectation());
		builder.append(") at line ");
		builder.append(line);
		builder.append(", column ");
		builder.append(column);
		builder.append(", after '");
		builder.append(past);
		builder.append("', before '");
		builder.append(future);
		builder.append("'");
		return builder.toString();
	}

	/**
	 * Returns the syntax error.
	 * 
	 * @return The syntax error.
	 */
	public JsonSyntaxError getError() {
		return error;
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
	 * Returns a few characters from the parsed character stream, that came
	 * before the syntax error, if still cached.
	 * 
	 * @return The snippet from the character stream.
	 */
	public String getPast() {
		return past;
	}

	/**
	 * Returns a few characters from the parsed character stream, that come
	 * after the syntax error, if already cached.
	 * 
	 * @return The snippet from the character stream.
	 */
	public String getFuture() {
		return future;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
