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
import java.io.Reader;

/**
 * A {@link StringSource} is a {@link JsonSource} that is backed by a given
 * {@link Reader} and buffers a small portion of the read characters in a
 * {@code char[]}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class ReaderSource implements JsonSource {

	private static final int MINIMUM_BUFFER_SIZE = 5;

	private static final int MAXIMUM_BUFFER_SIZE = 16;

	private final Reader reader;

	private final char[] buffer;

	private int position;

	private int end;

	private int line = 1;

	private int column = 1;

	private boolean firstCharacterRead;

	private int lastNewLinePosition;

	/**
	 * Creates a new {@link StringSource} for the given {@link Reader}.
	 * 
	 * @param reader
	 *            The {@link Reader} to be used.
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null}.
	 */
	public ReaderSource(Reader reader) {
		this(reader, 10);
	}

	/**
	 * Creates a new {@link StringSource} for the given {@link Reader}.
	 * 
	 * @param reader
	 *            The {@link Reader} to be used.
	 * @param size
	 *            The logarithm of the buffer size to be used.
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null} or if the
	 *             given size is smaller than the
	 *             {@link ReaderSource#MINIMUM_BUFFER_SIZE minimum} buffer size
	 *             or larger than the {@link ReaderSource#MAXIMUM_BUFFER_SIZE
	 *             maximum} buffer size.
	 */
	public ReaderSource(Reader reader, int size) {
		if (null == reader) {
			throw new IllegalArgumentException("reader is null");
		}
		if (size < MINIMUM_BUFFER_SIZE) {
			throw new IllegalArgumentException("sizes is too small");
		}
		if (size > MAXIMUM_BUFFER_SIZE) {
			throw new IllegalArgumentException("sizes is too large");
		}
		this.reader = reader;
		this.buffer = new char[1 << size];
	}

	@Override
	public int getAvailable() {
		return end - position;
	}

	@Override
	public boolean makeAvailable(int minimum) throws IOException {
		return minimum <= end - position || fillBuffer(minimum);
	}

	private boolean fillBuffer(int minimum) throws IOException {
		if (0 != position && 0 != end) {
			System.arraycopy(buffer, position, buffer, 0, getAvailable());
			column += (position - lastNewLinePosition);
			lastNewLinePosition = 0;
			end -= position;
		}
		position = 0;
		while (getAvailable() < minimum) {
			int read = reader.read(buffer, end, buffer.length - end);
			if (-1 == read) {
				return false;
			} else {
				end += read;
			}
		}
		if (!firstCharacterRead && end >= 1) {
			if (buffer[0] == JsonSource.BYTE_ORDER_MARK) {
				position++;
			}
			firstCharacterRead = true;
		}
		return true;
	}

	@Override
	public char nextCharacter() {
		char result = buffer[position++];
		if ('\n' == result) {
			lastNewLinePosition = position;
			column = 1;
			line += 1;
		}
		return result;
	}

	@Override
	public char peekCharacter(int offset) {
		return buffer[position + offset];
	}

	@Override
	public String nextString(int length) {
		for (int i = 0; i < length; i++) {
			nextCharacter();
		}
		return new String(buffer, position - length, length);
	}

	@Override
	public void appendNextString(StringBuilder builder, int length) {
		for (int i = 0; i < length; i++) {
			builder.append(nextCharacter());
		}
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column + (position - lastNewLinePosition);
	}

	@Override
	public String getPast(int maximum) {
		if (0 == position) {
			return "";
		} else {
			int stillAvailable = Math.min(position, maximum);
			return new String(buffer, position - stillAvailable, stillAvailable);
		}
	}

	@Override
	public String getFuture(int maximum) {
		int alreadyAvailable = Math.min(end - position, maximum);
		return new String(buffer, position, alreadyAvailable);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

}
