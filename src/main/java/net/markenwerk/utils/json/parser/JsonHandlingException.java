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
 * A JsonHandlingException indicates that the handling of an event in in
 * {@link JsonHandler} failed.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.1.0
 */
public class JsonHandlingException extends RuntimeException {

	private static final long serialVersionUID = -290339970597461477L;

	/**
	 * Creates a new {@link JsonHandlingException} with the given message and
	 * cause.
	 *
	 * @param message
	 *            The message.
	 * @param cause
	 *            The cause of this {@link JsonHandlingException}.
	 */
	public JsonHandlingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new {@link JsonHandlingException} with the given message.
	 *
	 * @param message
	 *            The message.
	 */
	public JsonHandlingException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link JsonHandlingException} with the given cause.
	 *
	 * @param cause
	 *            The cause of this {@link JsonHandlingException}.
	 */
	public JsonHandlingException(Throwable cause) {
		super(null == cause ? null : cause.getMessage(), cause);
	}

}
