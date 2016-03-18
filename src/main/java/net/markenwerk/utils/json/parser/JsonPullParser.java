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
import java.io.Reader;

/**
 * A {@link JsonPullParser} is an event based JSON parser. It
 * {@link JsonPullParser#currentState() reports} it's current {@link JsonState}
 * and, depending on that {@link JsonState}, allows different ways to consume
 * the underlying JSON document.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public interface JsonPullParser extends Closeable {

	/**
	 * Returns whether the current JSON array or JSON object has more elements.
	 * 
	 * @return Whether the current JSON array or JSON object has more elements.
	 * 
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public boolean hasNext() throws JsonSyntaxException, IOException;

	/**
	 * Reads, if necessary, from the underlying Reader and describes the current
	 * {@link JsonState} of this {@link JsonSourcePullParser}, which describes
	 * the next type of value or structural element of the JSON document.
	 * 
	 * @return The current {@link JsonState}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public JsonState currentState() throws JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOCUMENT_BEGIN} and consumes the
	 * begin of the JSON document. The next {@link JsonState} will be
	 * {@link JsonState#ARRAY_BEGIN} or {@link JsonState#OBJECT_BEGIN}.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#DOCUMENT_BEGIN}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void beginDocumnet() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOCUMENT_END} and consumes the end
	 * of the JSON document. The next {@link JsonState} will be
	 * {@link JsonState#SOURCE_END}.
	 * 
	 * <p>
	 * If this {@link JsonSourcePullParser} allows multiple documents in the
	 * same {@link JsonSource}, The next {@link JsonState} will be
	 * {@link JsonState#DOCUMENT_BEGIN} or {@link JsonState#SOURCE_END}.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#DOCUMENT_END}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void endDocumnet() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#ARRAY_BEGIN} and consumes the
	 * beginning of a JSON array. The next {@link JsonState} describes either
	 * the first value of this JSON array or the end of this JSON array.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#ARRAY_BEGIN}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void beginArray() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#ARRAY_END} and consumes the end of
	 * the JSON array. The next {@link JsonState} describes either the next
	 * sibling value of this JSON array or the end of the JSON document.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#ARRAY_END}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void endArray() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#OBJECT_BEGIN} and consumes the
	 * beginning of a JSON object. The next {@link JsonState} describes either
	 * the {@link JsonSourcePullParser#nextName()} of the first value of this
	 * JSON object or the end of this JSON object.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#OBJECT_BEGIN}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void beginObject() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#OBJECT_END} and consumes the end of
	 * the JSON object. The next {@link JsonState} describes either the next
	 * sibling value of this JSON object or the end of the JSON document.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#OBJECT_END}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void endObject() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#NULL} and consumes the
	 * {@literal null} The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#NULL}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void nextNull() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#BOOLEAN} and consumes and returns
	 * the corresponding value. The next {@link JsonState} describes either the
	 * next sibling value of this JSON value or the end of surrounding JSON
	 * array or JSON object.
	 * 
	 * @return The {@code boolean} value.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#BOOLEAN}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public boolean nextBoolean() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code byte}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code byte} value.
	 * 
	 * @throws ArithmeticException
	 *             If the value is too large or too small to fit into a
	 *             {@code byte}.
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public byte nextByte() throws ArithmeticException, IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code char}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code char} value.
	 * 
	 * @throws ArithmeticException
	 *             If the value is too large or too small to fit into a
	 *             {@code char}.
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public char nextCharacter() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code short}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code short} value.
	 * 
	 * @throws ArithmeticException
	 *             If the value is too large or too small to fit into a
	 *             {@code short}.
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public short nextShort() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code int}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code int} value.
	 * 
	 * @throws ArithmeticException
	 *             If the value is too large or too small to fit into a
	 *             {@code int}.
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public int nextInteger() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value. The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @return The {@code long} value.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public long nextLong() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOUBLE} and consumes and returns
	 * the corresponding value as a {@code float}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code float} value.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#DOUBLE}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public float nextFloat() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOUBLE} and consumes and returns
	 * the corresponding value. The next {@link JsonState} describes either the
	 * next sibling value of this JSON value or the end of surrounding JSON
	 * array or JSON object.
	 * 
	 * @return The {@code double} value.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#DOUBLE}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public double nextDouble() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#STRING} and consumes and returns
	 * the corresponding value. The next {@link JsonState} describes either the
	 * next sibling value of this JSON value or the end of surrounding JSON
	 * array or JSON object.
	 * 
	 * @return The string value.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#STRING}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public String nextString() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Ensures that the {@link JsonSourcePullParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#NAME} and consumes and returns the
	 * corresponding name of a JSON object entry. The next {@link JsonState}
	 * describes the corresponding value.
	 * 
	 * @return The name.
	 * 
	 * @throws IllegalStateException
	 *             If the current {@link JsonState} is not
	 *             {@link JsonState#NAME}.
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public String nextName() throws IllegalStateException, JsonSyntaxException, IOException;

	/**
	 * Skips the current JSON value. The next {@link JsonState} describes either
	 * the next sibling value of this JSON value or the end of surrounding JSON
	 * array or JSON object.
	 * 
	 * @throws JsonSyntaxException
	 *             If the read {@link JsonSyntaxException} document contains a
	 *             syntax error.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 */
	public void skipValue() throws JsonSyntaxException, IOException;

	/**
	 * Returns the {@link JsonSource#getLine() line} that corresponds to the
	 * current position of the underlying {@link JsonSource} in the character
	 * sequence, as if the character source was a regular file.
	 * 
	 * @return The line.
	 */
	public int getLine();

	/**
	 * Returns the {@link JsonSource#getColumn() column} that corresponds to the
	 * current position of the underlying {@link JsonSource} in the character
	 * sequence, as if the character source was a regular file.
	 * 
	 * @return The line.
	 */
	public int getColumn();

}