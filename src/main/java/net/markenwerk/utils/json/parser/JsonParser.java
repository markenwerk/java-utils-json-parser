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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link JsonParser} is a stream based JSON parser. It reads characters from
 * a given {@link Reader} as far as necessary to calculate a {@link JsonState}
 * or to yield the next value.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class JsonParser implements Closeable {

	private final StringBuilder builder = new StringBuilder();

	private final Stack<Context> stack = new Stack<Context>();

	private final Stack<Key> path = new Stack<Key>();

	private final JsonSource source;

	private JsonState state;

	private boolean booleanValue;

	private long longValue;

	private double doubleValue;

	private String stringValue;

	/**
	 * Creates a new {@link JsonParser} for the given {@link String}.
	 * 
	 * @param string
	 *           The {@link String} to read from.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null}.
	 */
	public JsonParser(String string) throws IllegalArgumentException {
		this(new StringSource(string));
	}

	/**
	 * Creates a new {@link JsonParser} for the given {@link Reader}.
	 * 
	 * @param reader
	 *           The {@link Reader} to read from.
	 * @throws IllegalArgumentException
	 *            If the given {@link Reader} is {@literal null}.
	 */
	public JsonParser(Reader reader) throws IllegalArgumentException {
		this(new ReaderSource(reader));
	}

	/**
	 * Creates a new {@link JsonParser} for the given {@link JsonSource}.
	 * 
	 * @param source
	 *           The {@link JsonSource} to read from.
	 * @throws IllegalArgumentException
	 *            If the given {@link JsonSource} is {@literal null}.
	 */
	public JsonParser(JsonSource source) throws IllegalArgumentException {
		if (null == source) {
			throw new IllegalArgumentException("source is null");
		}
		this.source = source;
		stack.push(Context.EMPTY_DOCUMENT);
	}

	/**
	 * Reads, if necessary, from the underlying Reader and describes the current
	 * {@link JsonState} of this {@link JsonParser}, which describes the next
	 * type of value or structural element of the JSON document.
	 * 
	 * @return The current {@link JsonState}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public JsonState currentState() throws JsonSyntaxException, IOException {
		if (null == state) {
			state = nextState();
		}
		return state;
	}

	private JsonState nextState() throws JsonSyntaxException, IOException {
		switch (stack.peek()) {
		case EMPTY_DOCUMENT:
			stack.replace(Context.NONEMPTY_DOCUMENT);
			try {
				JsonState initialState = prepareNextValue("Invalid document start (expected '[' or '{')");
				if (JsonState.ARRAY_BEGIN != initialState && JsonState.OBJECT_BEGIN != initialState) {
					throw syntaxError("Invalid document start (expected '[' or '{')");
				}
				return initialState;
			} catch (JsonSyntaxException e) {
				throw syntaxError("Invalid document start (expected '[' or '{')");
			}
		case EMPTY_ARRAY:
			return prepareArrayFirst();
		case NONEMPTY_ARRAY:
			return prepareArrayFollowing();
		case EMPTY_OBJECT:
			return prepareObjectFirst();
		case NONEMPTY_OBJECT:
			return prepareObjectFollowing();
		case DANGLING_NAME:
			return prepareObjectValue();
		case NONEMPTY_DOCUMENT:
			try {
				prepareNextValue("Invalid document end (expected EOF)");
				throw syntaxError("Expected EOF");
			} catch (JsonSyntaxException e) {
				return JsonState.DOCUMENT_END;
			}
		case CLOSED:
			throw new IllegalStateException("JsonReader is closed");
		default:
			throw new AssertionError();
		}
	}

	private void consume(JsonState expected) throws JsonSyntaxException, IllegalStateException, IOException {
		currentState();
		if (state != expected) {
			throw new IllegalStateException("Current state is " + state + " (expected " + expected + ")");
		}
		state = null;
	}

	private JsonState prepareArrayFirst() throws JsonSyntaxException, IOException {
		char character = nextNonWhitespace("Unfinished array (expected value or '}')");
		switch (character) {
		case ']':
			pop();
			return JsonState.ARRAY_END;
		default:
			path.peek().hint(null);
			stack.replace(Context.NONEMPTY_ARRAY);
			return prepareNextValue(character, "Unfinished array (expected value)");
		}
	}

	private JsonState prepareArrayFollowing() throws JsonSyntaxException, IOException {
		switch (nextNonWhitespace("Unfinished array (expected ',' or ']')")) {
		case ']':
			pop();
			return JsonState.ARRAY_END;
		case ',':
			path.peek().hint(null);
			return prepareNextValue("Unfinished array (expected value)");
		default:
			throw syntaxError("Unfinished array (expected ',' or ']')");
		}
	}

	private JsonState prepareObjectFirst() throws JsonSyntaxException, IOException {
		switch (nextNonWhitespace("Unfinished object (expected key or '}')")) {
		case '}':
			pop();
			return JsonState.OBJECT_END;
		case '"':
			prepareNextString();
			path.peek().hint(stringValue);
			stack.replace(Context.DANGLING_NAME);
			return JsonState.NAME;
		default:
			throw syntaxError("Unfinished object (expected key or '}')");
		}
	}

	private JsonState prepareObjectFollowing() throws JsonSyntaxException, IOException {
		switch (nextNonWhitespace("Unfinished object (expected ',' or '}')")) {
		case '}':
			pop();
			return JsonState.OBJECT_END;
		case ',':
			switch (nextNonWhitespace("Unfinished object (expected '\"key\"')")) {
			case '"':
				prepareNextString();
				path.peek().hint(stringValue);
				stack.replace(Context.DANGLING_NAME);
				return JsonState.NAME;
			default:
				throw syntaxError("Unfinished object (expected '\"key\"')");
			}
		default:
			throw syntaxError("Unfinished object (expected ',' or '}')");
		}
	}

	private void pop() {
		stack.pop();
		path.pop();
		if (!path.isEmpty()) {
			path.peek().unhint();
		}
	}

	private JsonState prepareObjectValue() throws JsonSyntaxException, IOException {
		switch (nextNonWhitespace("Unfinished object value (expected ':')")) {
		case ':':
			stack.replace(Context.NONEMPTY_OBJECT);
			return prepareNextValue("Unfinished object value (expected value)");
		default:
			throw syntaxError("Unfinished object value (expected ':')");
		}
	}

	private JsonState prepareNextValue(String errorMessage) throws JsonSyntaxException, IOException {
		return prepareNextValue(nextNonWhitespace(errorMessage), errorMessage);
	}

	private JsonState prepareNextValue(char firstCharacter, String errorMessage)
			throws JsonSyntaxException, IOException {
		switch (firstCharacter) {
		case '{':
			path.push(new ObjectKey());
			stack.push(Context.EMPTY_OBJECT);
			return JsonState.OBJECT_BEGIN;
		case '[':
			path.push(new ArrayKey());
			stack.push(Context.EMPTY_ARRAY);
			return JsonState.ARRAY_BEGIN;
		case '"':
			return prepareNextString();
		default:
			return prepareNextLiteral(firstCharacter);
		}
	}

	private char nextNonWhitespace(String errorMessage) throws JsonSyntaxException, IOException {
		while (source.ensure(1)) {
			char c = source.nextCharacter();
			switch (c) {
			case '\t':
			case ' ':
			case '\n':
			case '\r':
				continue;
			default:
				return c;
			}
		}
		throw syntaxError(errorMessage);
	}

	private JsonState prepareNextString() throws JsonSyntaxException, IOException {
		builder.setLength(0);
		while (source.ensure(1)) {
			int offset = 0;
			while (source.available(offset + 1)) {
				switch (source.peekCharacter(offset++)) {
				case '"':
					source.appendNextString(builder, offset - 1);
					source.nextCharacter();
					stringValue = builder.toString();
					return JsonState.STRING;
				case '\\':
					source.appendNextString(builder, offset - 1);
					source.nextCharacter();
					builder.append(readEscaped());
					offset = 0;
					break;
				default:
				}
			}
			source.appendNextString(builder, offset);
		}
		throw syntaxError("Unterminated string");
	}

	private char readEscaped() throws JsonSyntaxException, IOException {
		if (!source.ensure(1)) {
			throw syntaxError("Unterminated escape sequence");
		} else {
			switch (source.nextCharacter()) {
			case '"':
				return '"';
			case '\\':
				return '\\';
			case '/':
				return '/';
			case 'b':
				return '\b';
			case 'f':
				return '\f';
			case 'r':
				return '\r';
			case 'n':
				return '\n';
			case 't':
				return '\t';
			case 'u':
				return readUnicodeEscaped();
			default:
				throw syntaxError("Invalid escape sequence");
			}
		}
	}

	private char readUnicodeEscaped() throws JsonSyntaxException, IOException {
		if (!source.ensure(4)) {
			throw syntaxError("Unterminated unicode escape sequence");
		} else {
			try {
				String hex = source.nextString(4);
				return (char) Integer.parseInt(hex, 16);
			} catch (NumberFormatException e) {
				throw syntaxError("Invalid unicode escape sequence");
			}
		}
	}

	private JsonState prepareNextLiteral(char firstCharacter) throws JsonSyntaxException, IOException {
		int offset = 0;
		while (source.ensure(offset + 1)) {
			switch (source.peekCharacter(offset++)) {
			case '}':
			case ']':
			case ',':
			case ' ':
			case '\b':
			case '\f':
			case '\r':
			case '\n':
			case '\t':
				return decodeLiteral(firstCharacter + source.nextString(offset - 1));
			}
		}
		throw syntaxError("Invald literal");
	}

	private JsonState decodeLiteral(String literal) throws JsonSyntaxException {
		if ("null".equalsIgnoreCase(literal)) {
			return JsonState.NULL;
		} else if ("false".equalsIgnoreCase(literal)) {
			booleanValue = false;
			return JsonState.BOOLEAN;
		} else if ("true".equalsIgnoreCase(literal)) {
			booleanValue = true;
			return JsonState.BOOLEAN;
		} else {
			return decodeNumber(literal);
		}
	}

	private JsonState decodeNumber(String literal) throws JsonSyntaxException {
		try {
			longValue = Long.parseLong(literal);
			return JsonState.LONG;
		} catch (NumberFormatException ignored) {
			try {
				doubleValue = Double.parseDouble(literal);
				return JsonState.DOUBLE;
			} catch (NumberFormatException e) {
				throw syntaxError("Invald literal");
			}
		}
	}

	private JsonSyntaxException syntaxError(String message) {
		return new JsonSyntaxException(message, source.getLine(), source.getColumn(), source.getPast(15),
				source.getFuture(15), getPath());
	}

	/**
	 * Returns the path in the JSON document (e.g <tt>/foo/0/bar</tt> for
	 * <tt>{"foo":[{"bar":ERROR}]}</tt>).
	 * 
	 * @return The path.
	 */
	public List<String> getPath() {
		List<String> path = new ArrayList<String>(this.path.size());
		for (Key key : this.path) {
			path.add(0, key.toString());
		}
		return Collections.unmodifiableList(path);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#ARRAY_BEGIN} and consumes the
	 * beginning of a JSON array. The next {@link JsonState} describes either the
	 * first value of this JSON array or the end of this JSON array.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#ARRAY_BEGIN}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void beginArray() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.ARRAY_BEGIN);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#ARRAY_END} and consumes the end of
	 * the JSON array. The next {@link JsonState} describes either the next
	 * sibling value of this JSON array or the end of the JSON document.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#ARRAY_END}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void endArray() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.ARRAY_END);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#OBJECT_BEGIN} and consumes the
	 * beginning of a JSON object. The next {@link JsonState} describes either
	 * the {@link JsonParser#nextName()} of the first value of this JSON object
	 * or the end of this JSON object.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#OBJECT_BEGIN}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void beginObject() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.OBJECT_BEGIN);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#OBJECT_END} and consumes the end of
	 * the JSON object. The next {@link JsonState} describes either the next
	 * sibling value of this JSON object or the end of the JSON document.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#OBJECT_END}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void endObject() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.OBJECT_END);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOCUMENT_END} and consumes the end
	 * of the JSON document. The next {@link JsonState} will be {@literal null}.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#DOCUMENT_END}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void endDocumnet() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOCUMENT_END);
	}

	/**
	 * Returns whether the current JSON array or JSON object has more elements.
	 * 
	 * @return Whether the current JSON array or JSON object has more elements.
	 * 
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public boolean hasNext() throws JsonSyntaxException, IOException {
		currentState();
		return JsonState.OBJECT_END != state && JsonState.ARRAY_END != state;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#NULL} and consumes the
	 * {@literal null} The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#NULL}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void nextNull() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.NULL);
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#BOOLEAN} and consumes and returns
	 * the corresponding value. The next {@link JsonState} describes either the
	 * next sibling value of this JSON value or the end of surrounding JSON array
	 * or JSON object.
	 * 
	 * @return The {@code boolean} value.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#BOOLEAN}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public boolean nextBoolean() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.BOOLEAN);
		return booleanValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code byte}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code byte} value.
	 * 
	 * @throws ArithmeticException
	 *            If the value is too large or too small to fit into a
	 *            {@code byte}.
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public byte nextByte() throws ArithmeticException, IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Byte.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a byte");
		}
		if (longValue > Byte.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a byte");
		}
		return (byte) longValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code char}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code char} value.
	 * 
	 * @throws ArithmeticException
	 *            If the value is too large or too small to fit into a
	 *            {@code char}.
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public char nextCharacter() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Character.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a character");
		}
		if (longValue > Character.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a character");
		}
		return (char) longValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code short}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code short} value.
	 * 
	 * @throws ArithmeticException
	 *            If the value is too large or too small to fit into a
	 *            {@code short}.
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public short nextShort() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Short.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a short");
		}
		if (longValue > Short.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a short");
		}
		return (short) longValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value as a {@code int}. The next {@link JsonState} describes
	 * either the next sibling value of this JSON value or the end of surrounding
	 * JSON array or JSON object.
	 * 
	 * @return The {@code int} value.
	 * 
	 * @throws ArithmeticException
	 *            If the value is too large or too small to fit into a
	 *            {@code int}.
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public int nextInteger() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Integer.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be an integer");
		}
		if (longValue > Integer.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be an integer");
		}
		return (int) longValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#LONG} and consumes and returns the
	 * corresponding value. The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @return The {@code long} value.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#LONG}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public long nextLong() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		return longValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOUBLE} and consumes and returns the
	 * corresponding value as a {@code float}. The next {@link JsonState}
	 * describes either the next sibling value of this JSON value or the end of
	 * surrounding JSON array or JSON object.
	 * 
	 * @return The {@code float} value.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#DOUBLE}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public float nextFloat() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOUBLE);
		return (float) doubleValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#DOUBLE} and consumes and returns the
	 * corresponding value. The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @return The {@code double} value.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#DOUBLE}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public double nextDouble() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOUBLE);
		return doubleValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#STRING} and consumes and returns the
	 * corresponding value. The next {@link JsonState} describes either the next
	 * sibling value of this JSON value or the end of surrounding JSON array or
	 * JSON object.
	 * 
	 * @return The string value.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not
	 *            {@link JsonState#STRING}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public String nextString() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.STRING);
		return stringValue;
	}

	/**
	 * Ensures that the {@link JsonParser#currentState() current}
	 * {@link JsonState} is {@link JsonState#NAME} and consumes and returns the
	 * corresponding name of a JSON object entry. The next {@link JsonState}
	 * describes the corresponding value.
	 * 
	 * @return The name.
	 * 
	 * @throws IllegalStateException
	 *            If the current {@link JsonState} is not {@link JsonState#NAME}.
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public String nextName() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.NAME);
		return stringValue;
	}

	/**
	 * Skips the current JSON value. The next {@link JsonState} describes either
	 * the next sibling value of this JSON value or the end of surrounding JSON
	 * array or JSON object.
	 * 
	 * @throws JsonSyntaxException
	 *            If the read {@link JsonSyntaxException} document contains a
	 *            syntax error.
	 * @throws IOException
	 *            If reading from the underlying {@link Reader} failed.
	 */
	public void skipValue() throws JsonSyntaxException, IOException {
		switch (currentState()) {
		case ARRAY_END:
		case OBJECT_END:
		case DOCUMENT_END:
			return;
		case NAME:
			nextName();
		default:
		}
		int depth = 0;
		do {
			switch (currentState()) {
			case ARRAY_BEGIN:
			case OBJECT_BEGIN:
				depth++;
				break;
			case ARRAY_END:
			case OBJECT_END:
				depth--;
				break;
			default:
			}
			state = null;
		} while (0 != depth);
		currentState();
	}

	public void close() throws IOException {
		stack.clear();
		stack.push(Context.CLOSED);
		source.close();
	}

	@Override
	public String toString() {
		return "JsonReader [line=" + source.getLine() + ", column=" + source.getColumn() + ", near='" + source.getPast(15)
				+ source.getFuture(15) + "']";
	}

}
