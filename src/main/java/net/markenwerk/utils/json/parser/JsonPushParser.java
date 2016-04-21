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

import net.markenwerk.utils.json.common.exceptions.JsonHandlingException;
import net.markenwerk.utils.json.common.exceptions.JsonSyntaxException;
import net.markenwerk.utils.json.common.handler.JsonHandler;

/**
 * 
 * A {@link JsonPullParser} is an event based JSON parser. It
 * {@link JsonPullParser#currentState() reports} it consume the underlying JSON
 * document and reports the appropriate events to a {@link JsonHandler}.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public interface JsonPushParser extends Closeable {

	/**
	 * Handle the character sequence from the {@link JsonSource} and report to
	 * the {@link JsonHandler}.
	 * 
	 * <p>
	 * Calling this method closes the underlying {@link JsonSource}, but it can
	 * be {@link JsonSourcePushParser#close() closed manually}.
	 * 
	 * @param <Result>
	 *            The result type of the {@link JsonHandler}.
	 * 
	 * @param handler
	 *            The {@link JsonHandler} to report to.
	 * 
	 * @return The result that has been calculated by the given
	 *         {@link JsonHandler}.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link JsonHandler} is {@literal null}.
	 * @throws IOException
	 *             If reading from the underlying {@link Reader} failed.
	 * @throws JsonSyntaxException
	 *             If the {@link JsonSyntaxException} document contains a syntax
	 *             error.
	 * @throws JsonHandlingException
	 *             If the given {@link JsonHandler} failed to while handling an
	 *             event.
	 */
	public <Result> Result handle(JsonHandler<Result> handler) throws IllegalArgumentException, IOException,
			JsonSyntaxException, JsonHandlingException;

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