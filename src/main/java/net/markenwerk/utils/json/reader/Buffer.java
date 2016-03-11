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
package net.markenwerk.utils.json.reader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

final class Buffer {

	private static final char BYTE_ORDER_MARK = '\uFEFF';

	private final Reader reader;

	private final char[] buffer;

	private int position;

	private int available;

	private int line = 1;

	private int column = 1;

	private boolean firstCharRead;

	public Buffer(Reader reader, int size) {
		this.buffer = new char[size];
		this.reader = reader;
	}

	public boolean available(int minimum) {
		return minimum <= available;
	}

	public boolean ensure(int minimum) throws IOException {
		return minimum < available || fillBuffer(minimum);
	}

	private boolean fillBuffer(int minimum) throws IOException {
		assert minimum <= buffer.length;
		while (available < minimum) {
			int maximum = buffer.length - available;
			int writePosition = position + available;
			if (writePosition < buffer.length) {
				maximum = buffer.length - writePosition;
			} else {
				writePosition -= buffer.length;
			}
			int read = reader.read(buffer, writePosition, maximum);
			if (-1 == read) {
				return false;
			} else {
				available += read;
				if (!firstCharRead && 1 <= available) {
					char firstChar = nextCharacter();
					if (firstChar != BYTE_ORDER_MARK) {
						revertCharacter();
					}
					firstCharRead = true;
				}
			}
		}
		return true;
	}

	public char nextCharacter() {
		char result = buffer[position];
		position += 1;
		if (buffer.length == position) {
			position = 0;
		}
		available -= 1;
		if ('\n' == result) {
			line += 1;
			column = 0;
		} else {
			column += 1;
		}
		return result;
	}

	public void revertCharacter() {
		assert !available(buffer.length);
		position -= 1;
		if (position < 0) {
			position = buffer.length - 1;
		}
		available += 1;
	}

	public char peekCharacter(int offset) {
		assert available(offset);
		return buffer[(position + offset) % buffer.length];
	}

	public String nextString(int length) {
		assert available(length);
		char[] buffer = new char[length];
		for (int i = 0; i < length; i++) {
			buffer[i] = nextCharacter();
		}
		return new String(buffer);
	}

	public void appendNextString(StringBuilder builder, int length) {
		assert available(length);
		for (int i = 0; i < length; i++) {
			builder.append(nextCharacter());
		}
	}

	public MalformedJsonException syntaxError(String message, List<String> path) {
		return new MalformedJsonException(message, line, column, pastSnipper(), futureSnippet(), path);
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public String getSnippet() {
		StringBuilder builder = new StringBuilder();
		builder.append(pastSnipper());
		builder.append(futureSnippet());
		return builder.toString();
	}

	private String pastSnipper() {
		// int beforePos = Math.min(position, 20);
		// return getString(position - beforePos,
		// beforePos).replaceAll(Pattern.quote("\n"), "\\\\n");
		return "PAST";
	}

	private String futureSnippet() {
		// int afterPos = Math.min(limit - position, 20);
		// return getString(position, afterPos).replaceAll(Pattern.quote("\n"),
		// "\\\\n");
		return "FUTURE";
	}

	public void close() throws IOException {
		reader.close();
	}

}
