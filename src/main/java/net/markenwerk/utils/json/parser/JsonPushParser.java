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
 * A {@link JsonPushParser} is a stream based JSON parser. It reads characters from
 * a given {@link Reader} as far as necessary to calculate a {@link JsonState}
 * or to yield the next value.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public final class JsonPushParser implements Closeable {

	private final StringBuilder builder = new StringBuilder();

	private final JsonSource source;

	private final JsonHandler handler;

	/**
	 * Creates a new {@link JsonPushParser} for the given {@link String}.
	 *
	 * @param string
	 *           The {@link String} to read from.
	 * @param handler
	 *           The {@link JsonHandler} to report to.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null} or if the given
	 *            {@link JsonHandler} is {@literal null}.
	 */
	public JsonPushParser(String string, JsonHandler handler) throws IllegalArgumentException {
		this(new StringJsonSource(string), handler);
	}

	/**
	 * Creates a new {@link JsonPushParser} for the given {@code char[]}.
	 *
	 * @param characters
	 *           The {@code char[]} to read from.
	 * @param handler
	 *           The {@link JsonHandler} to report to.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null} or if the given
	 *            {@link JsonHandler} is {@literal null}.
	 */
	public JsonPushParser(char[] characters, JsonHandler handler) throws IllegalArgumentException {
		this(new CharacterArrayJsonSource(characters), handler);
	}

	/**
	 * Creates a new {@link JsonPushParser} for the given {@link Reader}.
	 * 
	 * @param reader
	 *           The {@link Reader} to read from.
	 * @param handler
	 *           The {@link JsonHandler} to report to.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null} or if the given
	 *            {@link JsonHandler} is {@literal null}.
	 */
	public JsonPushParser(Reader reader, JsonHandler handler) throws IllegalArgumentException {
		this(new ReaderJsonSource(reader), handler);
	}

	/**
	 * Creates a new {@link JsonPushParser} for the given {@link JsonSource}.
	 * 
	 * @param source
	 *           The {@link JsonSource} to read from.
	 * @param handler
	 *           The {@link JsonHandler} to report to.
	 * @throws IllegalArgumentException
	 *            If the given {@link String} is {@literal null} or if the given
	 *            {@link JsonHandler} is {@literal null}.
	 */
	public JsonPushParser(JsonSource source, JsonHandler handler) throws IllegalArgumentException {
		if (null == source) {
			throw new IllegalArgumentException("source is null");
		}
		if (null == handler) {
			throw new IllegalArgumentException("handler  is null");
		}
		this.source = source;
		this.handler = handler;
	}

	/**
	 * Handle the character sequence from the {@link JsonSource} and report to
	 * the {@link JsonHandler};
	 */
	public void handle() {
		try {
			handleDocument();
		} catch (JsonSyntaxException e) {
			handler.onException(e);
		} catch (IOException e) {
			handler.onException(e);
		}
	}

	private void handleDocument() throws JsonSyntaxException, IOException {
		handler.onBeginDocument();
		char firstCharacter = nextNonWhitespace("Invalid document start (expected '[' or '{')");
		if (firstCharacter == '{') {
			handleObjectFirst();
		} else if (firstCharacter == '[') {
			handleArrayFirst();
		} else {
			throw syntaxError("Invalid document start (expected '[' or '{')");
		}
		handler.onEndDocument();
	}

	private void handleArrayFirst() throws JsonSyntaxException, IOException {
		handler.onBeginArray();
		char nextCharacter = nextNonWhitespace("Unfinished array (expected value or ']')");
		if (nextCharacter == ']') {
			handler.onEndArray();
		} else {
			handleValue(nextCharacter, "Unfinished array (expected value or ']')");
			handleArrayFollowing();
		}
	}

	private void handleArrayFollowing() throws JsonSyntaxException, IOException {
		while (true) {
			char nextCharacter = nextNonWhitespace("Unfinished array (expected ',' or ']')");
			if (nextCharacter == ']') {
				handler.onEndArray();
				break;
			} else if (nextCharacter == ',') {
				handleValue("Unfinished array (expected value)");
			} else {
				throw syntaxError("Unfinished array (expected ',' or ']')");
			}
		}
	}

	private void handleObjectFirst() throws JsonSyntaxException, IOException {
		handler.onBeginObject();
		char nextCharacter = nextNonWhitespace("Unfinished object (expected key or '}')");
		if (nextCharacter == '}') {
			handler.onEndObject();
		} else if (nextCharacter == '"') {
			handleObjectValue();
			handleObjectFollowing();
		} else {
			throw syntaxError("Unfinished object (expected key or '}')");
		}
	}

	private void handleObjectFollowing() throws JsonSyntaxException, IOException {
		while (true) {
			char nextCharacter = nextNonWhitespace("Unfinished object (expected ',' or '}')");
			if (nextCharacter == '}') {
				handler.onEndObject();
				break;
			} else if (nextCharacter == ',') {
				nextCharacter = nextNonWhitespace("Unfinished object (expected '\"key\"')");
				if (nextCharacter == '"') {
					handleObjectValue();
				} else {
					throw syntaxError("Unfinished object (expected '\"key\"')");
				}
			} else {
				throw syntaxError("Unfinished object (expected ',' or '}')");
			}
		}
	}

	private void handleObjectValue() throws JsonSyntaxException, IOException {
		handler.onName(readNextString());
		char nextCharacter = nextNonWhitespace("Unfinished object value (expected ':')");
		if (nextCharacter == ':') {
			handleValue("Unfinished object value (expected value)");
		} else {
			throw syntaxError("Unfinished object value (expected ':')");
		}
	}

	private void handleValue(String errorMessage) throws JsonSyntaxException, IOException {
		handleValue(nextNonWhitespace(errorMessage), errorMessage);
	}

	private void handleValue(char firstCharacter, String errorMessage) throws JsonSyntaxException, IOException {
		if (firstCharacter == '{') {
			handleObjectFirst();
		} else if (firstCharacter == '[') {
			handleArrayFirst();
		} else if (firstCharacter == '"') {
			handler.onString(readNextString());
		} else {
			handleLiteral(firstCharacter);
		}
	}

	private char nextNonWhitespace(String errorMessage) throws JsonSyntaxException, IOException {
		while (source.makeAvailable(1)) {
			for (int i = 0, n = source.getAvailable(); i < n; i++) {
				char nextCharacter = source.nextCharacter();
				if (' ' != nextCharacter && '\n' != nextCharacter && '\t' != nextCharacter && '\r' != nextCharacter) {
					return nextCharacter;
				}
			}
		}
		throw syntaxError(errorMessage);
	}

	private String readNextString() throws JsonSyntaxException, IOException {
		builder.setLength(0);
		boolean buffered = false;
		while (source.makeAvailable(1)) {
			int offset = 0;
			int available = source.getAvailable();
			while (offset < available) {
				char nextCharacter = source.peekCharacter(offset);
				if ('"' == nextCharacter) {
					String stringValue;
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
		throw syntaxError("Unterminated string");
	}

	private char readEscaped() throws JsonSyntaxException, IOException {
		if (!source.makeAvailable(1)) {
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
		if (!source.makeAvailable(4)) {
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

	private void handleLiteral(char firstCharacter) throws JsonSyntaxException, IOException {
		builder.setLength(0);
		builder.append(firstCharacter);
		int offset = 0;
		while (source.makeAvailable(offset + 1)) {
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
				source.appendNextString(builder, offset - 1);
				handleLiteral(builder.toString());
				return;
			}
		}
		throw syntaxError("Invald literal");
	}

	private void handleLiteral(String literal) throws JsonSyntaxException {
		if ("null".equalsIgnoreCase(literal)) {
			handler.onNull();
		} else if ("false".equalsIgnoreCase(literal)) {
			handler.onBoolean(false);
		} else if ("true".equalsIgnoreCase(literal)) {
			handler.onBoolean(true);
		} else {
			handleNumber(literal);
		}
	}

	private void handleNumber(String literal) throws JsonSyntaxException {
		try {
			handler.onLong(Long.parseLong(literal));
		} catch (NumberFormatException ignored) {
			try {
				handler.onDouble(Double.parseDouble(literal));
			} catch (NumberFormatException e) {
				throw syntaxError("Invald literal");
			}
		}
	}

	private JsonSyntaxException syntaxError(String message) {
		return new JsonSyntaxException(message, source.getLine(), source.getColumn() - 1, source.getPast(5),
				source.getFuture(5));
	}

	public void close() throws IOException {
		source.close();
	}

	@Override
	public String toString() {
		return "JsonReader [line=" + source.getLine() + ", column=" + source.getColumn() + ", near='" + source.getPast(15)
				+ source.getFuture(15) + "']";
	}

}
