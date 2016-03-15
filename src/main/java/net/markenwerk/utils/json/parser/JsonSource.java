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

import java.io.Closeable;
import java.io.IOException;

/**
 * A {@link JsonSource} represents access to a sequence of characters to be used
 * used by a {@link JsonPullParser}. Characters may be consumed
 * {@link JsonSource#nextCharacter() one by one}, or, for convenience,
 * {@link JsonSource#nextString(int) multiple at a time}, or
 * {@link JsonSource#appendNextString(StringBuilder, int) appended } to a
 * {@link StringBuilder}.
 * 
 * <p>
 * Callers must or {@link JsonSource#makeAvailable(int) ensure}, that the
 * desired amount of characters is available, before consuming them.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public interface JsonSource extends Closeable {

	/**
	 * A {@code char} that represents the Unicode byte order mark. A leading
	 * byte order mark in a character sequence should be ignored by a
	 * {@link JsonSource}.
	 */
	public static final char BYTE_ORDER_MARK = '\uFEFF';

	/**
	 * Returns the amount of currently available characters.
	 * 
	 * @return The amount of currently available characters.
	 */
	public int getAvailable();

	/**
	 * Ensures that a given minimum of characters is available to be consumed.
	 * 
	 * @param minimum
	 *            The desired minimum.
	 * 
	 * @return Whether the desired amount of available characters could be
	 *         ensured or the end of the character sequence has been reached.
	 * @throws IOException
	 *             If something went wrong while ensuring that the desired
	 *             amount of available characters.
	 */
	public boolean makeAvailable(int minimum) throws IOException;

	/**
	 * Consumes and returns the next character in the character sequence.
	 * 
	 * @return The next character in the character sequence. Must be
	 *         non-negative.
	 */
	public char nextCharacter();

	/**
	 * Returns a future character in the character sequence, without consuming
	 * it.
	 * 
	 * <p>
	 * Callers must {@link JsonSource#makeAvailable(int) ensure}, that the
	 * desired amount of characters is available.
	 * 
	 * @param offset
	 *            The amount of characters to look ahead. Must be non-negative.
	 * 
	 * @return The future character in the character sequence.
	 */
	public char peekCharacter(int offset);

	/**
	 * Consumes and returns the next characters in the character sequence.
	 * 
	 * <p>
	 * Callers must {@link JsonSource#makeAvailable(int) ensure}, that the
	 * desired amount of characters is available.
	 * 
	 * @param length
	 *            The amount of characters to be consumed. Must be non-negative.
	 * 
	 * @return The next characters in the character sequence.
	 */
	public String nextString(int length);

	/**
	 * Consumes and appends the next characters in the character sequence to the
	 * given {@link StringBuilder}.
	 * 
	 * <p>
	 * Callers must {@link JsonSource#makeAvailable(int) ensure}, that the
	 * desired amount of characters is available.
	 * 
	 * @param builder
	 *            The string builder to add the characters to. Must be
	 *            non-negative.
	 * 
	 * @param length
	 *            The amount of characters to be consumed.
	 */
	public void appendNextString(StringBuilder builder, int length);

	/**
	 * Returns the line that corresponds to the current position of this
	 * {@link JsonSource} in the character sequence, as if the character source
	 * was a regular file.
	 * 
	 * <p>
	 * The line is equal to the number of consumed {@code \n}-characters + 1.
	 * 
	 * @return The line.
	 */
	public int getLine();

	/**
	 * Returns the column that corresponds to the current position of this
	 * {@link JsonSource} in the character sequence, as if the character source
	 * was a regular file.
	 * 
	 * <p>
	 * The column is equal to the number of consumed characters + 1 in the
	 * current {@link JsonSource#getLine() line}.
	 * 
	 * @return The line.
	 */
	public int getColumn();

	/**
	 * Returns some of the characters that already have been consumed, ending
	 * with the last consumed character, if still available, or an empty
	 * {@link String} otherwise.
	 * 
	 * @param maximum
	 *            The maximum amount of characters. Must be non-negative.
	 * 
	 * @return The past characters.
	 */
	public String getPast(int maximum);

	/**
	 * Returns some of the characters havn't been consumed yet, beginning with
	 * the next character to be consumed, if already available, or an empty
	 * {@link String} otherwise.
	 * 
	 * @param maximum
	 *            The maximum amount of characters. Must be non-negative.
	 * 
	 * @return The future characters.
	 */
	public String getFuture(int maximum);

}