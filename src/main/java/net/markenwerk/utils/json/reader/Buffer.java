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

	private final int sizeMask;

	private int position;

	private int available;

	private int line = 1;

	private int column = 1;

	private boolean firstCharacterRead;

	public Buffer(Reader reader) {
		this(reader, 8);
	}

	public Buffer(Reader reader, int size) {
		// buffer of size 2^^size
		this.buffer = new char[1 << size];
		// sizeMask like 00...01...11
		this.sizeMask = buffer.length - 1;
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
				// read no further than end of buffer
				maximum = buffer.length - writePosition;
			} else {
				// write position is left of read position
				// calculate actual write position and read
				// no further than read mark
				writePosition -= buffer.length;
			}
			int read = reader.read(buffer, writePosition, maximum);
			if (-1 == read) {
				return false;
			} else {
				available += read;
			}
		}
		if (!firstCharacterRead && available >= 1) {
			if (buffer[0] == BYTE_ORDER_MARK) {
				position++;
				column++;
			}
			firstCharacterRead = true;
		}
		return true;
	}

	public char nextCharacter() {
		char result = buffer[position];
		position = (position + 1) & sizeMask;
		available -= 1;
		if ('\n' == result) {
			line += 1;
			column = 0;
		} else {
			column += 1;
		}
		return result;
	}

	public char peekCharacter(int offset) {
		assert available(offset);
		return buffer[(position + offset) & sizeMask];
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

	public JsonSyntaxException syntaxError(String message, List<String> path) {
		return new JsonSyntaxException(message, line, column, pastSnipper(), futureSnippet(), path);
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
