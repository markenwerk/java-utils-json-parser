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

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonReader implements Closeable {

	private static final String NULL = "null";

	private static final String FALSE = "false";

	private static final String TRUE = "true";

	private final StringBuilder builder = new StringBuilder();

	private final Stack<Context> stack = new Stack<Context>();

	private final Stack<Key> path = new Stack<Key>();

	private final Buffer buffer;

	private JsonToken token;

	private boolean booleanValue;

	private long longValue;

	private double doubleValue;

	private String stringValue;

	/**
	 * Creates a new {@link JsonReader} for the given {@link Reader}.
	 * 
	 * @param reader
	 *            The {@link Reader} to read from.
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null}.
	 */
	public JsonReader(Reader reader) throws IllegalArgumentException {
		if (null == reader) {
			throw new IllegalArgumentException("reader is null");
		}
		this.buffer = new Buffer(reader, 512);
		stack.push(Context.EMPTY_DOCUMENT);
	}

	public void beginArray() throws MalformedJsonException, IOException {
		consume(JsonToken.ARRAY_BEGIN);
	}

	public void endArray() throws MalformedJsonException, IOException {
		consume(JsonToken.ARRAY_END);
	}

	public void beginObject() throws MalformedJsonException, IOException {
		consume(JsonToken.OBJECT_BEGIN);
	}

	public void endObject() throws MalformedJsonException, IOException {
		consume(JsonToken.OBJECT_END);
	}

	public void endDocumnet() throws MalformedJsonException, IOException {
		consume(JsonToken.DOCUMENT_END);
	}

	public boolean hasNext() throws MalformedJsonException, IOException {
		currentToken();
		return JsonToken.OBJECT_END != token && JsonToken.ARRAY_END != token;
	}

	public JsonToken currentToken() throws MalformedJsonException, IOException {
		if (null == token) {
			token = prepareToken();
		}
		return token;
	}

	private JsonToken prepareToken() throws MalformedJsonException, IOException {
		switch (stack.peek()) {
		case EMPTY_DOCUMENT:
			stack.replace(Context.NONEMPTY_DOCUMENT);
			try {
				JsonToken firstToken = prepareNextValue("Invalid document start (expected '[' or '{')");
				if (JsonToken.ARRAY_BEGIN != firstToken && JsonToken.OBJECT_BEGIN != firstToken) {
					throw syntaxError("Invalid document start (expected '[' or '{')");
				}
				return firstToken;
			} catch (MalformedJsonException e) {
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
			} catch (MalformedJsonException e) {
				return JsonToken.DOCUMENT_END;
			}
		case CLOSED:
			throw new IllegalStateException("JsonReader is closed");
		default:
			throw new AssertionError();
		}
	}

	private void consume(JsonToken expected) throws MalformedJsonException, IOException {
		currentToken();
		if (token != expected) {
			throw new IllegalStateException("Current token is " + token + " (expected " + expected + ")");
		}
		token = null;
	}

	private JsonToken prepareArrayFirst() throws MalformedJsonException, IOException {
		switch (nextNonWhitespace("Unfinished array (expected value or '}')")) {
		case ']':
			pop();
			return JsonToken.ARRAY_END;
		default:
			buffer.revertCharacter();
			path.peek().hint(null);
			stack.replace(Context.NONEMPTY_ARRAY);
			return prepareNextValue("Unfinished array (expected value)");
		}
	}

	private JsonToken prepareArrayFollowing() throws MalformedJsonException, IOException {
		char nonWhitespace = nextNonWhitespace("Unfinished array (expected ',' or ']')");
		switch (nonWhitespace) {
		case ']':
			pop();
			return JsonToken.ARRAY_END;
		case ',':
			path.peek().hint(null);
			return prepareNextValue("Unfinished array (expected value)");
		default:
			throw syntaxError("Unfinished array (expected ',' or ']' but found '" + nonWhitespace + "')");
		}
	}

	private JsonToken prepareObjectFirst() throws MalformedJsonException, IOException {
		switch (nextNonWhitespace("Unfinished object (expected key or '}')")) {
		case '}':
			pop();
			return JsonToken.OBJECT_END;
		case '"':
			prepareNextString();
			path.peek().hint(stringValue);
			stack.replace(Context.DANGLING_NAME);
			return JsonToken.NAME;
		default:
			throw syntaxError("Unfinished object (expected key or '}')");
		}
	}

	private JsonToken prepareObjectFollowing() throws MalformedJsonException, IOException {
		switch (nextNonWhitespace("Unfinished object (expected ',' or '}')")) {
		case '}':
			pop();
			return JsonToken.OBJECT_END;
		case ',':
			switch (nextNonWhitespace("Unfinished object (expected '\"key\"')")) {
			case '"':
				prepareNextString();
				path.peek().hint(stringValue);
				stack.replace(Context.DANGLING_NAME);
				return JsonToken.NAME;
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

	private JsonToken prepareObjectValue() throws MalformedJsonException, IOException {
		switch (nextNonWhitespace("Unfinished object value (expected ':')")) {
		case ':':
			stack.replace(Context.NONEMPTY_OBJECT);
			return prepareNextValue("Unfinished object value (expected value)");
		default:
			throw syntaxError("Unfinished object value (expected ':')");
		}
	}

	private JsonToken prepareNextValue(String errorMessage) throws MalformedJsonException, IOException {
		int c = nextNonWhitespace(errorMessage);
		switch (c) {
		case '{':
			path.push(new ObjectKey());
			stack.push(Context.EMPTY_OBJECT);
			return JsonToken.OBJECT_BEGIN;
		case '[':
			path.push(new ArrayKey());
			stack.push(Context.EMPTY_ARRAY);
			return JsonToken.ARRAY_BEGIN;
		case '"':
			return prepareNextString();
		default:
			buffer.revertCharacter();
			return prepareNextLiteral();
		}
	}

	private char nextNonWhitespace(String errorMessage) throws MalformedJsonException, IOException {
		while (buffer.ensure(1)) {
			char c = buffer.nextCharacter();
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

	private JsonToken prepareNextString() throws MalformedJsonException, IOException {
		builder.setLength(0);
		while (buffer.ensure(1)) {
			int offset = 0;
			while (buffer.available(offset + 1)) {
				switch (buffer.peekCharacter(offset++)) {
				case '"':
					buffer.appendNextString(builder, offset - 1);
					buffer.nextCharacter();
					stringValue = builder.toString();
					return JsonToken.STRING;
				case '\\':
					buffer.appendNextString(builder, offset - 1);
					buffer.nextCharacter();
					builder.append(readEscaped());
					offset = 0;
					break;
				default:
				}
			}
			buffer.appendNextString(builder, offset);
		}
		throw syntaxError("Unterminated string");
	}

	private char readEscaped() throws MalformedJsonException, IOException {
		if (!buffer.ensure(1)) {
			throw syntaxError("Unterminated escape sequence");
		} else {
			switch (buffer.nextCharacter()) {
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

	private char readUnicodeEscaped() throws MalformedJsonException, IOException {
		if (!buffer.ensure(4)) {
			throw syntaxError("Unterminated unicode escape sequence");
		} else {
			try {
				String hex = buffer.nextString(4);
				return (char) Integer.parseInt(hex, 16);
			} catch (NumberFormatException e) {
				throw syntaxError("Invalid unicode escape sequence");
			}
		}
	}

	private JsonToken prepareNextLiteral() throws MalformedJsonException, IOException {
		int offset = 0;
		while (buffer.ensure(offset + 1)) {
			switch (buffer.peekCharacter(offset++)) {
			case '{':
			case '}':
			case '[':
			case ']':
			case ':':
			case ',':
			case ' ':
			case '\t':
			case '\b':
			case '\f':
			case '\r':
			case '\n':
				return decodeLiteral(buffer.nextString(offset - 1));
			}
		}
		throw syntaxError("Invald literal");
	}

	private JsonToken decodeLiteral(String literal) throws MalformedJsonException {
		if (NULL.equalsIgnoreCase(literal)) {
			return JsonToken.NULL;
		} else if (FALSE.equalsIgnoreCase(literal)) {
			booleanValue = false;
			return JsonToken.BOOLEAN;
		} else if (TRUE.equalsIgnoreCase(literal)) {
			booleanValue = true;
			return JsonToken.BOOLEAN;
		} else {
			return decodeNumber(literal);
		}
	}

	private JsonToken decodeNumber(String literal) throws MalformedJsonException {
		try {
			longValue = Long.parseLong(literal);
			return JsonToken.LONG;
		} catch (NumberFormatException ignored) {
			try {
				doubleValue = Double.parseDouble(literal);
				return JsonToken.DOUBLE;
			} catch (NumberFormatException e) {
				throw syntaxError("Invald literal");
			}
		}
	}

	private MalformedJsonException syntaxError(String message) {
		return buffer.syntaxError(message, getPath());
	}

	public List<String> getPath() {
		List<String> path = new ArrayList<String>(this.path.size());
		for (Key key : this.path) {
			path.add(0, key.toString());
		}
		return Collections.unmodifiableList(path);
	}

	public void nextNull() throws MalformedJsonException, IOException {
		consume(JsonToken.NULL);
	}

	public boolean nextBoolean() throws MalformedJsonException, IOException {
		consume(JsonToken.BOOLEAN);
		return booleanValue;
	}

	public byte nextByte() throws MalformedJsonException, IOException {
		consume(JsonToken.LONG);
		if (longValue < Byte.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a byte");
		}
		if (longValue > Byte.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a byte");
		}
		return (byte) longValue;
	}

	public char nextCharacter() throws MalformedJsonException, IOException {
		consume(JsonToken.LONG);
		if (longValue < Character.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a character");
		}
		if (longValue > Character.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a character");
		}
		return (char) longValue;
	}

	public short nextShort() throws MalformedJsonException, IOException {
		consume(JsonToken.LONG);
		if (longValue < Short.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be a short");
		}
		if (longValue > Short.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be a short");
		}
		return (short) longValue;
	}

	public int nextInteger() throws MalformedJsonException, IOException {
		consume(JsonToken.LONG);
		if (longValue < Integer.MIN_VALUE) {
			throw new ArithmeticException("Value is too small to be an integer");
		}
		if (longValue > Integer.MAX_VALUE) {
			throw new ArithmeticException("Value is too large to be an integer");
		}
		return (int) longValue;
	}

	public long nextLong() throws MalformedJsonException, IOException {
		consume(JsonToken.LONG);
		return longValue;
	}

	public float nextFloat() throws MalformedJsonException, IOException {
		consume(JsonToken.DOUBLE);
		return (float) doubleValue;
	}

	public double nextDouble() throws MalformedJsonException, IOException {
		consume(JsonToken.DOUBLE);
		return doubleValue;
	}

	public String nextString() throws MalformedJsonException, IOException {
		consume(JsonToken.STRING);
		return stringValue;
	}

	public String nextName() throws MalformedJsonException, IOException {
		consume(JsonToken.NAME);
		return stringValue;
	}

	public void skipValue() throws MalformedJsonException, IOException {
		if (JsonToken.NAME == currentToken()) {
			nextName();
		}
		int depth = 0;
		do {
			switch (currentToken()) {
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
			System.out.println("skipping " + token);
			token = null;
		} while (0 != depth);
		currentToken();
		System.out.println("skipped till " + token);
	}

	public void close() throws IOException {
		stack.clear();
		stack.push(Context.CLOSED);
		buffer.close();
	}

	@Override
	public String toString() {
		return "JsonReader [line=" + buffer.getLine() + ", column=" + buffer.getColumn() + ", near='"
				+ buffer.getSnippet() + "']";
	}

}
