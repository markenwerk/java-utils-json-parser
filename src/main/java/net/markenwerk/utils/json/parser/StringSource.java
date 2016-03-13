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

import java.io.IOException;

/**
 * A {@link StringSource} is a {@link JsonSource} that is backed by a given
 * {@link String}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class StringSource implements JsonSource {

	private String string;

	private int position;

	private int line;

	private int column;

	/**
	 * Creates a new {@link StringSource} for the given {@link String}.
	 * 
	 * @param string
	 *           The {@link String} to be used.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null}.
	 */
	public StringSource(String string) throws IllegalArgumentException {
		if (null == string) {
			throw new IllegalArgumentException("string is null");
		}
		this.string = string;
		if (0 != string.length() && BYTE_ORDER_MARK == string.charAt(0)) {
			position++;
			column++;
		}
	}

	@Override
	public boolean available(int minimum) {
		return position + minimum <= string.length();
	}

	@Override
	public boolean ensure(int minimum) throws IOException {
		return position + minimum <= string.length();
	}

	@Override
	public char nextCharacter() {
		char result = string.charAt(position++);
		if ('\n' == result) {
			line += 1;
			column = 0;
		} else {
			column += 1;
		}
		return result;
	}

	@Override
	public char peekCharacter(int offset) {
		return string.charAt(position + offset);
	}

	@Override
	public String nextString(int length) {
		char[] buffer = new char[length];
		for (int i = 0; i < length; i++) {
			buffer[i] = nextCharacter();
		}
		return new String(buffer);
	}

	@Override
	public void appendNextString(StringBuilder builder, int length) {
		for (int i = 0; i < length; i++) {
			builder.append(nextCharacter());
		}
	}

	@Override
	public String getPast(int maximum) {
		if (0 != position) {
			return string.substring(Math.max(0, position - 1 - maximum), position - 1);
		} else {
			return "";
		}
	}

	@Override
	public String getFuture(int maximum) {
		return string.substring(position, Math.min(position + maximum, string.length()));
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public void close() throws IOException {
	}

}
