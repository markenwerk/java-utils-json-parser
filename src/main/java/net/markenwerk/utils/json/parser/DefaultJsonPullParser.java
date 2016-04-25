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
import java.util.Arrays;
import java.util.List;

import net.markenwerk.utils.json.common.InvalidJsonValueException;
import net.markenwerk.utils.json.common.JsonSyntaxError;
import net.markenwerk.utils.json.common.JsonSyntaxException;

/**
 * A {@link DefaultJsonPullParser} is a {@link JsonPullParser} that consumes a
 * {@link JsonSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class DefaultJsonPullParser implements JsonPullParser {

	private final StringBuilder builder = new StringBuilder();

	private final Stack<Context> stack = new Stack<Context>();

	private final JsonSource source;

	private final boolean multiDocumentMode;

	private final boolean strictStructMode;

	private JsonState state;

	private boolean booleanValue;

	private long longValue;

	private double doubleValue;

	/**
	 * Creates a new {@link DefaultJsonPullParser}.
	 *
	 * @param string
	 *            The {@link String} to read from.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link String} is {@literal null}.
	 */
	public DefaultJsonPullParser(String string) throws IllegalArgumentException {
		this(new StringJsonSource(string), (JsonParserMode[]) null);
	}

	/**
	 * Creates a new {@link DefaultJsonPullParser}.
	 *
	 * @param characters
	 *            The {@code char[]} to read from.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@code char[]} is {@literal null}.
	 */
	public DefaultJsonPullParser(char[] characters) throws IllegalArgumentException {
		this(new CharacterArrayJsonSource(characters), (JsonParserMode[]) null);
	}

	/**
	 * Creates a new {@link DefaultJsonPullParser}.
	 * 
	 * @param reader
	 *            The {@link Reader} to read from.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link Reader} is {@literal null}.
	 */
	public DefaultJsonPullParser(Reader reader) throws IllegalArgumentException {
		this(new ReaderJsonSource(reader), (JsonParserMode[]) null);
	}

	/**
	 * Creates a new {@link DefaultJsonPullParser}.
	 * 
	 * @param source
	 *            The {@link JsonSource} to read from.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link JsonSource} is {@literal null}.
	 */
	public DefaultJsonPullParser(JsonSource source) throws IllegalArgumentException {
		this(source, (JsonParserMode[]) null);

	}

	/**
	 * Creates a new {@link DefaultJsonPullParser}.
	 * 
	 * @param source
	 *            The {@link JsonSource} to read from.
	 * @param modes
	 *            Selection of {@link JsonParserMode JsonParserModes} to be used
	 *            during parsing.
	 * 
	 * @throws IllegalArgumentException
	 *             If the given {@link JsonSource} is {@literal null}.
	 */
	public DefaultJsonPullParser(JsonSource source, JsonParserMode... modes) throws IllegalArgumentException {
		if (null == source) {
			throw new IllegalArgumentException("source is null");
		}
		this.source = source;
		if (null != modes) {
			List<JsonParserMode> modesList = Arrays.asList(modes);
			this.multiDocumentMode = modesList.contains(JsonParserMode.MULTI_DOCUMENT_MODE);
			this.strictStructMode = modesList.contains(JsonParserMode.STRICT_STRUCT_MODE);
		} else {
			this.multiDocumentMode = false;
			this.strictStructMode = false;
		}
		stack.push(Context.BEFORE_PARSE);
	}

	private JsonState nextState() throws JsonSyntaxException, IOException {
		switch (stack.peek()) {
		case BEFORE_PARSE:
			stack.push(Context.EMPTY_DOCUMENT);
			return JsonState.DOCUMENT_BEGIN;
		case AFTER_PARSE:
			return JsonState.SOURCE_END;
		case EMPTY_DOCUMENT:
			return prepareDocument();
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
			stack.pop();
			if (hasNextNonWhitespace()) {
				if (multiDocumentMode) {
					stack.replace(Context.BEFORE_PARSE);
					return JsonState.DOCUMENT_END;
				} else {
					throw syntaxError(JsonSyntaxError.INVALID_DOCUMENT_END);
				}
			} else {
				stack.replace(Context.AFTER_PARSE);
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

	private JsonState prepareDocument() throws JsonSyntaxException, IOException {
		stack.replace(Context.NONEMPTY_DOCUMENT);
		char firstCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_DOCUMENT_START);
		if ('[' == firstCharacter) {
			stack.push(Context.EMPTY_ARRAY);
			return JsonState.ARRAY_BEGIN;
		} else if ('{' == firstCharacter) {
			stack.push(Context.EMPTY_OBJECT);
			return JsonState.OBJECT_BEGIN;
		} else {
			if (strictStructMode) {
				throw syntaxError(JsonSyntaxError.INVALID_DOCUMENT_START);
			} else if ('"' == firstCharacter) {
				return JsonState.STRING;
			} else {
				return prepareNextLiteral(firstCharacter);
			}
		}
	}

	private JsonState prepareArrayFirst() throws JsonSyntaxException, IOException {
		char nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_ARRAY_FIRST);
		if (']' == nextCharacter) {
			stack.pop();
			return JsonState.ARRAY_END;
		} else {
			stack.replace(Context.NONEMPTY_ARRAY);
			return prepareNextValue(nextCharacter, JsonSyntaxError.INVALID_ARRAY_FIRST);
		}
	}

	private JsonState prepareArrayFollowing() throws JsonSyntaxException, IOException {
		char nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_ARRAY_FOLLOW);
		if (',' == nextCharacter) {
			return prepareNextValue(JsonSyntaxError.INVALID_ARRAY_VALUE);
		} else if (']' == nextCharacter) {
			stack.pop();
			return JsonState.ARRAY_END;
		} else {
			throw syntaxError(JsonSyntaxError.INVALID_ARRAY_FOLLOW);
		}
	}

	private JsonState prepareObjectFirst() throws JsonSyntaxException, IOException {
		char nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_OBJECT_FIRST);
		if ('"' == nextCharacter) {
			stack.replace(Context.DANGLING_NAME);
			return JsonState.NAME;
		} else if ('}' == nextCharacter) {
			stack.pop();
			return JsonState.OBJECT_END;
		} else {
			throw syntaxError(JsonSyntaxError.INVALID_OBJECT_FIRST);
		}
	}

	private JsonState prepareObjectFollowing() throws JsonSyntaxException, IOException {
		char nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_OBJECT_FOLLOW);
		if (',' == nextCharacter) {
			nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_OBJECT_NAME);
			if ('"' == nextCharacter) {
				stack.replace(Context.DANGLING_NAME);
				return JsonState.NAME;
			} else {
				throw syntaxError(JsonSyntaxError.INVALID_OBJECT_NAME);
			}
		} else if ('}' == nextCharacter) {
			stack.pop();
			return JsonState.OBJECT_END;
		} else {
			throw syntaxError(JsonSyntaxError.INVALID_OBJECT_FOLLOW);
		}
	}

	private JsonState prepareObjectValue() throws JsonSyntaxException, IOException {
		char nextCharacter = nextNonWhitespace(JsonSyntaxError.INVALID_OBJECT_SEPARATION);
		if (':' == nextCharacter) {
			stack.replace(Context.NONEMPTY_OBJECT);
			return prepareNextValue(JsonSyntaxError.INVALID_OBJECT_VALUE);
		} else {
			throw syntaxError(JsonSyntaxError.INVALID_OBJECT_SEPARATION);
		}
	}

	private JsonState prepareNextValue(JsonSyntaxError error) throws JsonSyntaxException, IOException {
		return prepareNextValue(nextNonWhitespace(error), error);
	}

	private JsonState prepareNextValue(char firstCharacter, JsonSyntaxError error) throws JsonSyntaxException,
			IOException {
		if ('[' == firstCharacter) {
			stack.push(Context.EMPTY_ARRAY);
			return JsonState.ARRAY_BEGIN;
		} else if ('{' == firstCharacter) {
			stack.push(Context.EMPTY_OBJECT);
			return JsonState.OBJECT_BEGIN;
		} else if ('"' == firstCharacter) {
			return JsonState.STRING;
		} else if (']' == firstCharacter) {
			throw syntaxError(error);
		} else if ('}' == firstCharacter) {
			throw syntaxError(error);
		} else {
			return prepareNextLiteral(firstCharacter);
		}
	}

	private char nextNonWhitespace(JsonSyntaxError error) throws JsonSyntaxException, IOException {
		while (source.makeAvailable(1)) {
			for (int i = 0, n = source.getAvailable(); i < n; i++) {
				char nextCharacter = source.nextCharacter();
				if (' ' != nextCharacter && '\t' != nextCharacter && '\n' != nextCharacter && '\r' != nextCharacter) {
					return nextCharacter;
				}
			}
		}
		throw syntaxError(error);
	}

	private boolean hasNextNonWhitespace() throws JsonSyntaxException, IOException {
		while (0 != source.makeAvailable()) {
			for (int i = 0, n = source.getAvailable(); i < n; i++) {
				char nextCharacter = source.peekCharacter(0);
				if (' ' != nextCharacter && '\t' != nextCharacter && '\n' != nextCharacter && '\r' != nextCharacter) {
					return true;
				} else {
					source.nextCharacter();
				}
			}
		}
		return false;
	}

	private String getNextString() throws JsonSyntaxException, IOException {
		builder.setLength(0);
		boolean buffered = false;
		while (source.makeAvailable(1)) {
			int offset = 0;
			int available = source.getAvailable();
			while (offset < available) {
				char nextCharacter = source.peekCharacter(offset);
				if ('"' == nextCharacter) {
					final String stringValue;
					if (buffered) {
						source.appendNextString(builder, offset);
						stringValue = builder.toString();
					} else {
						stringValue = source.nextString(offset);
					}
					source.nextCharacter();
					return stringValue;
				} else if ('\\' == nextCharacter) {
					buffered = true;
					source.appendNextString(builder, offset);
					source.nextCharacter();
					builder.append(readEscaped());
					offset = -1;
					break;
				}
				offset++;
			}
			if (-1 != offset) {
				buffered = true;
				source.appendNextString(builder, offset);
			}
		}
		throw syntaxError(JsonSyntaxError.UNTERMINATED_STRING);
	}

	private Reader readNextString() throws JsonSyntaxException, IOException {

		return new Reader() {

			private boolean endReached = false;

			@Override
			public int read(char[] buffer, int offset, int maxLength) throws IOException {
				if (endReached) {
					return -1;
				}
				int amount = 0;
				while (amount < maxLength) {
					int readValue = read();
					if (-1 == readValue) {
						break;
					} else {
						buffer[offset + amount] = (char) readValue;
					}
					amount++;
				}
				return amount;
			}

			@Override
			public int read() throws IOException {
				if (!source.makeAvailable(1)) {
					JsonSyntaxException e = syntaxError(JsonSyntaxError.UNTERMINATED_STRING);
					throw new IOException(e.getMessage(), e);
				}
				char character = source.nextCharacter();
				if ('"' == character) {
					endReached = true;
					return -1;
				} else if ('\\' == character) {
					try {
						return readEscaped();
					} catch (JsonSyntaxException e) {
						throw new IOException(e.getMessage(), e);
					}
				} else {
					return character;
				}
			}

			@Override
			public void close() throws IOException {
				while (!endReached) {
					read();
				}
			}
		};

	}

	private void skipNextString() throws JsonSyntaxException, IOException {
		while (source.makeAvailable(1)) {
			int available = source.getAvailable();
			for (int i = 0; i < available; i++) {
				char nextCharacter = source.nextCharacter();
				if ('"' == nextCharacter) {
					return;
				}
			}
		}
		throw syntaxError(JsonSyntaxError.UNTERMINATED_STRING);
	}

	private char readEscaped() throws JsonSyntaxException, IOException {
		if (!source.makeAvailable(1)) {
			throw syntaxError(JsonSyntaxError.UNFINISHED_ESCAPE_SEQUENCE);
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
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 't':
				return '\t';
			case 'u':
				return readUnicodeEscaped();
			default:
				throw syntaxError(JsonSyntaxError.INVALID_ESCAPE_SEQUENCE);
			}
		}
	}

	private char readUnicodeEscaped() throws JsonSyntaxException, IOException {
		if (!source.makeAvailable(4)) {
			throw syntaxError(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE);
		} else {
			String hex = source.nextString(4);
			try {
				return (char) Integer.parseInt(hex, 16);
			} catch (NumberFormatException e) {
				if (hex.contains("\"")) {
					throw syntaxError(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE);
				} else {
					throw syntaxError(JsonSyntaxError.INVALID_UNICODE_ESCAPE_SEQUENCE);
				}
			}
		}
	}

	private JsonState prepareNextLiteral(char firstCharacter) throws JsonSyntaxException, IOException {
		builder.setLength(0);
		builder.append(firstCharacter);
		while (0 != source.makeAvailable()) {
			switch (source.peekCharacter(0)) {
			case ']':
			case '}':
			case ',':
			case ' ':
			case '\b':
			case '\f':
			case '\n':
			case '\r':
			case '\t':
				return decodeLiteral(builder.toString());
			default:
				builder.append(source.nextCharacter());
			}
		}
		return decodeLiteral(builder.toString());
	}

	private JsonState decodeLiteral(String literal) throws JsonSyntaxException {
		if ("null".equals(literal)) {
			return JsonState.NULL;
		} else if ("false".equals(literal)) {
			booleanValue = false;
			return JsonState.BOOLEAN;
		} else if ("true".equals(literal)) {
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
				throw syntaxError(JsonSyntaxError.INVALID_LITERAL);
			}
		}
	}

	private JsonSyntaxException syntaxError(JsonSyntaxError error) {
		return new JsonSyntaxException(error, source.getLine(), source.getColumn() - 1, source.getPast(15),
				source.getFuture(15));
	}

	@Override
	public boolean hasNextElement() throws JsonSyntaxException, IOException {
		currentState();
		return JsonState.OBJECT_END != state && JsonState.ARRAY_END != state;
	}

	@Override
	public JsonState currentState() throws JsonSyntaxException, IOException {
		if (null == state) {
			state = nextState();
		}
		return state;
	}

	@Override
	public void beginDocument() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOCUMENT_BEGIN);
	}

	@Override
	public void endDocument() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOCUMENT_END);
	}

	@Override
	public void beginArray() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.ARRAY_BEGIN);
	}

	@Override
	public void endArray() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.ARRAY_END);
	}

	@Override
	public void beginObject() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.OBJECT_BEGIN);
	}

	@Override
	public void endObject() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.OBJECT_END);
	}

	@Override
	public void nextNull() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.NULL);
	}

	@Override
	public boolean nextBoolean() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.BOOLEAN);
		return booleanValue;
	}

	@Override
	public byte nextByte() throws InvalidJsonValueException, IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Byte.MIN_VALUE) {
			throw new InvalidJsonValueException("Value is too small to be a byte: " + longValue);
		} else if (longValue > Byte.MAX_VALUE) {
			throw new InvalidJsonValueException("Value is too large to be a byte: " + longValue);
		} else {
			return (byte) longValue;
		}
	}

	@Override
	public char nextCharacter() throws InvalidJsonValueException, IllegalStateException, JsonSyntaxException,
			IOException {
		consume(JsonState.LONG);
		if (longValue < Character.MIN_VALUE) {
			throw new InvalidJsonValueException("Value is too small to be a character: " + longValue);
		} else if (longValue > Character.MAX_VALUE) {
			throw new InvalidJsonValueException("Value is too large to be a character: " + longValue);
		} else {
			return (char) longValue;
		}
	}

	@Override
	public short nextShort() throws InvalidJsonValueException, IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Short.MIN_VALUE) {
			throw new InvalidJsonValueException("Value is too small to be a short: " + longValue);
		} else if (longValue > Short.MAX_VALUE) {
			throw new InvalidJsonValueException("Value is too large to be a short: " + longValue);
		} else {
			return (short) longValue;
		}
	}

	@Override
	public int nextInteger() throws InvalidJsonValueException, IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		if (longValue < Integer.MIN_VALUE) {
			throw new InvalidJsonValueException("Value is too small to be an integer: " + longValue);
		} else if (longValue > Integer.MAX_VALUE) {
			throw new InvalidJsonValueException("Value is too large to be an integer: " + longValue);
		} else {
			return (int) longValue;
		}
	}

	@Override
	public long nextLong() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.LONG);
		return longValue;
	}

	@Override
	public float nextFloat() throws InvalidJsonValueException, IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOUBLE);
		if (doubleValue < Float.MIN_VALUE) {
			throw new InvalidJsonValueException("Value is too small to be a float: " + longValue);
		} else if (doubleValue > Float.MAX_VALUE) {
			throw new InvalidJsonValueException("Value is too large to be a float: " + longValue);
		} else {
			return (float) doubleValue;
		}
	}

	@Override
	public double nextDouble() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.DOUBLE);
		return doubleValue;
	}

	@Override
	public String nextString() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.STRING);
		return getNextString();
	}

	@Override
	public Reader readString() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.STRING);
		return readNextString();
	}

	@Override
	public String nextName() throws IllegalStateException, JsonSyntaxException, IOException {
		consume(JsonState.NAME);
		return getNextString();
	}

	@Override
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
			case STRING:
			case NAME:
				skipNextString();
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
	public int getLine() {
		return source.getLine();
	}

	@Override
	public int getColumn() {
		return source.getColumn();
	}

	@Override
	public String toString() {
		return "JsonReader [line=" + source.getLine() + ", column=" + source.getColumn() + ", near='"
				+ source.getPast(15) + source.getFuture(15) + "']";
	}

}
