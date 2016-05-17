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
 * A {@link StringJsonSource} is a {@link JsonSource} that is backed by a given
 * {@link Reader} and buffers a small portion of the read characters in a
 * {@code char[]}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class ReaderJsonSource implements JsonSource {

	private static final int MINIMUM_BUFFER_SIZE = 128;

	private static final int DEFAULT_BUFFER_SIZE = 512;

	private final Reader reader;

	private final char[] buffer;

	private int position;

	private int end;

	private int line = 1;

	private int column = 1;

	private boolean firstCharacterRead;

	private int lastNewLinePosition;

	/**
	 * Creates a new {@link StringJsonSource} with the
	 * {@link ReaderJsonSource#DEFAULT_BUFFER_SIZE default} buffer size.
	 * 
	 * @param reader
	 *            The {@link Reader} to be used.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null}.
	 */
	public ReaderJsonSource(Reader reader) {
		this(reader, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Creates a new {@link StringJsonSource}.
	 * 
	 * @param reader
	 *            The {@link Reader} to be used.
	 * @param size
	 *            The buffer size to be used.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null} or if the
	 *             given size is smaller than the
	 *             {@link ReaderJsonSource#MINIMUM_BUFFER_SIZE minimum} buffer
	 *             size.
	 */
	public ReaderJsonSource(Reader reader, int size) {
		if (null == reader) {
			throw new IllegalArgumentException("The given reader is null");
		}
		if (size < MINIMUM_BUFFER_SIZE) {
			throw new IllegalArgumentException("The given size is too small: " + size);
		}
		this.reader = reader;
		this.buffer = new char[size];
	}

	@Override
	public int getAvailable() {
		return end - position;
	}

	@Override
	public int makeAvailable() throws IOException {
		int available = getAvailable();
		if (0 != available) {
			return available;
		} else {
			fillBuffer(1);
			return getAvailable();
		}
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
