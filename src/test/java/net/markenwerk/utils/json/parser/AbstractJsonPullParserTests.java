/*
 * Copyright (c) 2015, 2016 Torsten Krause, Markenwerk GmbH
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

import org.junit.Assert;
import org.junit.Test;

import net.markenwerk.utils.json.common.JsonValueException;

/**
 * JUnit test for {@link DefaultJsonPullParser}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public abstract class AbstractJsonPullParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullSource() {
		new DefaultJsonPullParser((JsonSource) null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyDocument() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(""));
		try {

			jsonParser.beginDocument();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_START, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmpty_invalidStart() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("null"), JsonParserMode.STRICT_STRUCT_MODE);
		try {

			jsonParser.beginDocument();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_START, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_null() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("null"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_false() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("false"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
			Assert.assertEquals(false, jsonParser.nextBoolean());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_true() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("true"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
			Assert.assertEquals(true, jsonParser.nextBoolean());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleLong() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("0"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(0, jsonParser.nextLong());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singlePositiveLong() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextLong());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleNegativeLong() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("-42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(-42, jsonParser.nextLong());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleInteger() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextInteger());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooLargeInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MAX_VALUE) + 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextInteger();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooSmallInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MIN_VALUE) - 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextInteger();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleShort() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextShort());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooLargeShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MAX_VALUE) + 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextShort();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooSmallShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MIN_VALUE) - 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextShort();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleCharacter() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextCharacter());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooLargeCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MAX_VALUE) + 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextCharacter();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooSmallCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MIN_VALUE) - 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextCharacter();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleByte() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextByte());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooLargeByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MAX_VALUE) + 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextByte();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonValueException.class)
	public void literal_singleTooSmallByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MIN_VALUE) - 1;
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(Long.toString(value)));
		try {

			jsonParser.beginDocument();
			jsonParser.nextByte();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleDouble() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("0.0"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(0, jsonParser.nextDouble(), 0e-10);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singlePositiveDouble() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42.23"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.23, jsonParser.nextDouble(), 0e-10);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleNegativeDouble() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("-42.23"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(-42.23, jsonParser.nextDouble(), 0e-10);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singlePositiveDoubleWithPositiveExponent() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42.0e7"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.0e7, jsonParser.nextDouble(), 0e-10);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singlePositiveDoubleWithNegativeExponent() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42.0e-7"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.0e-7, jsonParser.nextDouble(), 0e-10);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleFloat() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("42.23"));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.23f, jsonParser.nextFloat(), 0e-10f);
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void literal_singleInvalidLiteal() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("x"));
		try {

			jsonParser.beginDocument();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_LITERAL, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_empty() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("", jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_nonEmpty() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"foo\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_veryLarge() throws IOException, JsonSyntaxException {
		String value = createVeryLargeString();
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"" + value + "\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals(value, jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_readEmpty() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("", readString(jsonParser.readString()));
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_readNonEmpty() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"foo\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("foo", readString(jsonParser.readString()));
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_readVeryLarge() throws IOException, JsonSyntaxException {
		String value = createVeryLargeString();
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"" + value + "\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals(value, readString(jsonParser.readString()));
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	private String readString(Reader reader) throws IOException {
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[64];
		int length;
		while (-1 != (length = reader.read(buffer))) {
			builder.append(buffer, 0, length);
		}
		reader.close();
		return builder.toString();
	}

	private String createVeryLargeString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			builder.append((char) ('a' + i % 26));
		}
		String value = builder.toString();
		return value;
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_withEscapeSequences() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\\"\\\\\\/\\b\\f\\r\\n\\t\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\"\\/\b\f\r\n\t", jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_withUnicodeEscapeSequences() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\uBEEF\\ubeef\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\ubeef\uBEEF", jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_withSurrogateUnicodeEscapeSequence() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\uD834\\uDD1E\""));
		try {

			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\uD834\uDD1E", jsonParser.nextString());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_dangelingEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\"));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_unterminatedEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\\""));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNTERMINATED_STRING, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_invalidEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\x\""));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_dangelingUnicodeEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\u"));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_unterminatedUnicodeEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\u\""));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void string_invalidUnicodeEscapeSequence() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("\"\\uNOPE\""));
		try {

			jsonParser.beginDocument();
			jsonParser.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_afterBom() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(JsonSource.BYTE_ORDER_MARK + "[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_leadingWhitespace() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource(" \n\r\t []"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_trailingWhitespace() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[] \n\r\t "));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_unfinished() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("["));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_wrongFinish() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_trailingNonWhitespace() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[]X"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocument();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_END, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());
			
		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_multipleValues() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null,null]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_nestedArray() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[[]]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingValue() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FOLLOW, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_wrongFinish() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FOLLOW, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingComma() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null,"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_VALUE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_dengelingCommaWrongFinish() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null,]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_VALUE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_unfinished() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_wrongFinish() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_singleValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_multipleValues() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null,\"bar\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("bar", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_nestedValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":{}}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoName() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingName() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\""));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_SEPARATION, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingColon() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_VALUE, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingValue() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FOLLOW, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingComma() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null,"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_NAME, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoValue() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\",null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_SEPARATION, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedKeyAfterComma() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null,null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_NAME, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_wrongFinish() throws IOException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"foo\":null]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FOLLOW, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_complexValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(
				getSource(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {
			//@formatter:off
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocument();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
				jsonParser.beginObject();
				
				Assert.assertTrue(jsonParser.hasNextElement());
				Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
				Assert.assertEquals("foo", jsonParser.nextName());
				
				Assert.assertTrue(jsonParser.hasNextElement());
				Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
					jsonParser.beginArray();
					
					Assert.assertTrue(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
					Assert.assertEquals("bar\n", jsonParser.nextString());

					Assert.assertTrue(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
					Assert.assertEquals(true, jsonParser.nextBoolean());
					
					Assert.assertTrue(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
						jsonParser.beginObject();
					
						Assert.assertFalse(jsonParser.hasNextElement());
						Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
						jsonParser.endObject();
						
					Assert.assertFalse(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
					jsonParser.endArray();
					
				Assert.assertTrue(jsonParser.hasNextElement());
				Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
				Assert.assertEquals("baz", jsonParser.nextName());
				
				Assert.assertTrue(jsonParser.hasNextElement());
				Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
					jsonParser.beginObject();
					
					Assert.assertTrue(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
					Assert.assertEquals("foo", jsonParser.nextName());
					
					Assert.assertTrue(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
					Assert.assertEquals(42, jsonParser.nextLong());
					
					Assert.assertFalse(jsonParser.hasNextElement());
					Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
					jsonParser.endObject();

				Assert.assertFalse(jsonParser.hasNextElement());
				Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
				jsonParser.endObject();
				
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());
			//@formatter:on

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootArray() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[]"));
		try {

			jsonParser.beginDocument();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipSingleValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[null,\"keep\" ]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			jsonParser.nextString();
			jsonParser.endArray();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipBeforeEnd() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipComplexValue() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("[[{\"skipped\":[true]}],\"keep\" ]"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			jsonParser.nextString();
			jsonParser.endArray();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootObject() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocument();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipBeforeEnd() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.skipValue();
			Assert.assertFalse(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueBeforeName() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.endObject();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueAfterName() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNextElement());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.endObject();
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipBeforeEnd() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.skipValue();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocument();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}[]"),
				JsonParserMode.MULTI_DOCUMENT_MODE);
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocument();

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocument();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode_trailingWhitespace() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{}[] \n\r\t "),
				JsonParserMode.MULTI_DOCUMENT_MODE);
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocument();

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocument();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode_separatingWhitespace() throws IOException, JsonSyntaxException {
		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("{} \n\r\t []"),
				JsonParserMode.MULTI_DOCUMENT_MODE);
		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocument();

			jsonParser.beginDocument();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocument();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void syntaxError_position() throws IOException {

		DefaultJsonPullParser jsonParser = new DefaultJsonPullParser(getSource("  \n  \n  \n   X"));

		try {

			jsonParser.beginDocument();
			jsonParser.beginObject();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException e) {

			Assert.assertEquals(4, e.getLine());
			Assert.assertEquals(4, e.getColumn());

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	protected abstract JsonSource getSource(String string);

}
