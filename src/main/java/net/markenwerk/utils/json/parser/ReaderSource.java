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

	private final int sizeMask;

	private int position;

	private int available;

	private int line = 1;

	private int column = 1;

	private boolean firstCharacterRead;

	/**
	 * Creates a new {@link StringSource} for the given {@link Reader}.
	 * 
	 * @param reader
	 *           The {@link Reader} to be used.
	 * @throws IllegalArgumentException
	 *            If the given {@link Reader} is {@literal null}.
	 */
	public ReaderSource(Reader reader) {
		this(reader, 8);
	}

	/**
	 * Creates a new {@link StringSource} for the given {@link Reader}.
	 * 
	 * @param reader
	 *           The {@link Reader} to be used.
	 * @param size
	 *           The logarithm of the buffer size to be used.
	 * @throws IllegalArgumentException
	 *            If the given {@link Reader} is {@literal null} or if the given
	 *            size is smaller than the
	 *            {@link ReaderSource#MINIMUM_BUFFER_SIZE minimum} buffer size or
	 *            larger than the {@link ReaderSource#MAXIMUM_BUFFER_SIZE
	 *            maximum} buffer size.
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
		// buffer of size 2^^size
		this.buffer = new char[1 << size];
		// sizeMask like 00...01...11
		this.sizeMask = buffer.length - 1;
	}

	@Override
	public boolean available(int minimum) {
		return minimum <= available;
	}

	@Override
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

	@Override
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

	@Override
	public char peekCharacter(int offset) {
		assert available(offset);
		return buffer[(position + offset) & sizeMask];
	}

	@Override
	public String nextString(int length) {
		assert available(length);
		char[] buffer = new char[length];
		for (int i = 0; i < length; i++) {
			buffer[i] = nextCharacter();
		}
		return new String(buffer);
	}

	@Override
	public void appendNextString(StringBuilder builder, int length) {
		assert available(length);
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
		return column;
	}

	@Override
	public String getPast(int maximum) {
		// int beforePos = Math.min(position, 20);
		// return getString(position - beforePos,
		// beforePos).replaceAll(Pattern.quote("\n"), "\\\\n");
		return "PAST";
	}

	@Override
	public String getFuture(int maximum) {
		// int afterPos = Math.min(limit - position, 20);
		// return getString(position, afterPos).replaceAll(Pattern.quote("\n"),
		// "\\\\n");
		return "FUTURE";
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

}
