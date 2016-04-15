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
 * A {@link JsonSyntaxError} describes the kinds of syntax error a
 * {@link JsonPullParser} or a {@link JsonPushParser} may encounter.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public enum JsonSyntaxError {

	// @formatter:off
	
	/**
	 * Describes an invalid document, where the initial character is neither
	 * <tt>{</tt> nor <tt>[</tt>.
	 */
	INVALID_DOCUMENT_START("Invalid document start", "Expected '[' or '{'"),

	/**
	 * Describes an invalid document, where the the last <tt>}</tt> or
	 * <tt>]</tt> is followed by further non-whitespace characters.
	 */
	INVALID_DOCUMENT_END("Invalid document end", "Expected EOF"),

	/**
	 * Describes an invalid JSON array, where the initial <tt>[</tt> isn't
	 * followed by a JSON value or a <tt>]</tt>.
	 */
	INVALID_ARRAY_FIRST("Invalid array", "Expected value or ']' after '['"),

	/**
	 * Describes an invalid JSON array, where a value isn't followed by a JSON
	 * <tt>,</tt> or a <tt>]</tt>.
	 */
	INVALID_ARRAY_FOLLOW("Invalid array", "Expected ',' or ']' after value"),

	/**
	 * Describes an invalid JSON array, where a <tt>,</tt> isn't followed by a
	 * JSON value.
	 */
	INVALID_ARRAY_VALUE("Invalid array", "Expected value after ','"),

	/**
	 * Describes an invalid JSON object, where the initial <tt>{</tt> isn't
	 * followed by a JSON name or a <tt>}</tt>.
	 */
	INVALID_OBJECT_FIRST("Invalid object", "Expected name or '}' after '}'"),

	/**
	 * Describes an invalid JSON object, where a value isn't followed by a
	 * <tt>.</tt> or a <tt>}</tt>.
	 */
	INVALID_OBJECT_FOLLOW("Invalid object", "Expected ',' or '}' after value"),

	/**
	 * Describes an invalid JSON object, where a <tt>,</tt> isn't followed by a
	 * JSON name.
	 */
	INVALID_OBJECT_NAME("Invalid object", "Expected name after ','"),

	/**
	 * Describes an invalid JSON object, where a JSON name isn't followed by a
	 * <tt>:</tt>.
	 */
	INVALID_OBJECT_SEPARATION("Invalid object", "Expected ':' after name"),

	/**
	 * Describes an invalid JSON object, where a <tt>:</tt> isn't followed by a
	 * JSON value.
	 */
	INVALID_OBJECT_VALUE("Invalid object", "Expected value after ':'"),

	/**
	 * Describes an invalid JSON string, that is not ended by a <tt>"</tt>.
	 */
	UNTERMINATED_STRING("Unterminated string", "Expected '\"'"),

	/**
	 * Describes an invalid JSON string, where a <tt>\</tt> isn't followed by at
	 * least one character.
	 */
	UNFINISHED_ESCAPE_SEQUENCE("Unfinished escapse sequence", "Expected at least one character after '\\'"),

	/**
	 * Describes an invalid JSON string, where a <tt>\\</tt> isn't followed by a
	 * valid escape sequence.
	 */
	INVALID_ESCAPE_SEQUENCE("Invalid escape sequence", "Expected '\"', '\\', '/', 'b', 'f', 'r', 'n', 't' or 'uXXXX' after '\\'"),

	/**
	 * Describes an invalid JSON string, where a <tt>\\u</tt> isn't followed by
	 * four character.
	 */
	UNFINISHED_UNICODE_ESCAPE_SEQUENCE("Unfinished unicode escapse sequence", "Expected four characters after '\\u'"),

	/**
	 * Describes an invalid JSON string, where a <tt>\\u</tt> isn't followed by
	 * four hex characters.
	 */
	INVALID_UNICODE_ESCAPE_SEQUENCE("Invalid unicode escapse sequence", "Expected four hexadecimal characters after '\\u'"),

	/**
	 * Describes an invalid JSON value, that does't begin with <tt>[</tt>,
	 * <tt>{</tt> or <tt>"</tt> and isn't a valid JSON literal.
	 */
	INVALID_LITERAL("Invalid literal", "Expected 'null', 'false', 'true' or a number for a value that doesn't start with '[', '{' or '\"'");

	// @formatter:on

	private final String description;

	private final String expectation;

	private JsonSyntaxError(String description, String expectation) {
		this.description = description;
		this.expectation = expectation;
	}

	/**
	 * Returns a description of this {@link JsonSyntaxError}.
	 * 
	 * @return A description of this {@link JsonSyntaxError}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a description of an unfulfilled expectation, that caused this
	 * {@link JsonSyntaxError}.
	 * 
	 * @return a description of an unfulfilled expectation, that caused this
	 *         {@link JsonSyntaxError}.
	 */
	public String getExpectation() {
		return expectation;
	}

}
