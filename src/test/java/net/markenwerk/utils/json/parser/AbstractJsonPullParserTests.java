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

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link JsonPullParser}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public abstract class AbstractJsonPullParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullSource() {
		new JsonPullParser((JsonSource) null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyDocument() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource(""));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("\""));
		try {

			jsonParser.beginDocumnet();
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
	public void emptyArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_afterBom() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource(JsonSource.BYTE_ORDER_MARK + "[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_leadingWhitespace() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource(" \n\r\t []"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_trailingWhitespace() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[] \n\r\t "));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_unfinished() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("["));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("[}"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("[]X"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocumnet();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_END, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNull() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFalse() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[false]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
			Assert.assertEquals(false, jsonParser.nextBoolean());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleTrue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[true]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
			Assert.assertEquals(true, jsonParser.nextBoolean());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleEmptyString() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("", jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyString() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"foo\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyVeryLargeString() throws IOException, JsonSyntaxException {
		String value = createVeryLargeString();
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"" + value + "\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals(value, jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
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
	public void nonEmptyArray_singleNonEmptyStringWithEscapeSequences() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\\"\\\\\\/\\b\\f\\r\\n\\t\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\"\\/\b\f\r\n\t", jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithUnicodeEscapeSequences() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\uBEEF\\ubeef\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\ubeef\uBEEF", jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithSurrogateUnicodeEscapeSequences() throws IOException,
			JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\uD834\\uDD1E\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			Assert.assertEquals("\uD834\uDD1E", jsonParser.nextString());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringDangelingEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleNonEmptyStringUnterminatedEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleNonEmptyStringInvalidEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\x\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleNonEmptyStringDangelingUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\u"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleNonEmptyStringUnterminatedUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\u\",null]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleNonEmptyStringInvalidUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[\"\\uNOPE\"]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_singleLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[0]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(0, jsonParser.nextLong());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextLong());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[-42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(-42, jsonParser.nextLong());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInteger() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextInteger());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MAX_VALUE) + 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextInteger();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MIN_VALUE) - 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextInteger();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleShort() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextShort());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MAX_VALUE) + 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextShort();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MIN_VALUE) - 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextShort();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleCharacter() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextCharacter());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MAX_VALUE) + 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextCharacter();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MIN_VALUE) - 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextCharacter();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleByte() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
			Assert.assertEquals(42, jsonParser.nextByte());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MAX_VALUE) + 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextByte();

		} finally {
			jsonParser.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MIN_VALUE) - 1;
		JsonPullParser jsonParser = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.nextByte();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[0.0]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(0, jsonParser.nextDouble(), 0e-10);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42.23]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.23, jsonParser.nextDouble(), 0e-10);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[-42.23]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(-42.23, jsonParser.nextDouble(), 0e-10);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithPositiveExponent() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42.0e7]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.0e7, jsonParser.nextDouble(), 0e-10);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithNegativeExponent() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42.0e-7]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.0e-7, jsonParser.nextDouble(), 0e-10);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFloat() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[42.23]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonParser.currentState());
			Assert.assertEquals(42.23f, jsonParser.nextFloat(), 0e-10f);
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInvalidLiteal() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[x]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
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
	public void nonEmptyArray_multipleValues() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null,null]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_nestedArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[[]]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
			jsonParser.beginArray();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingValue() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null}"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null,"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null,]"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_unfinished() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{]"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_multipleValues() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null,\"bar\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("bar", jsonParser.nextName());
			Assert.assertEquals(JsonState.NULL, jsonParser.currentState());
			jsonParser.nextNull();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_nestedValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":{}}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			Assert.assertEquals("foo", jsonParser.nextName());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
			jsonParser.beginObject();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoName() throws IOException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{null}"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\""));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null,"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\",null}"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null,null}"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"foo\":null]"));
		try {

			jsonParser.beginDocumnet();
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
		JsonPullParser jsonParser = new JsonPullParser(
				getSource(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {
			//@formatter:off
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonParser.currentState());
			jsonParser.beginDocumnet();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
				jsonParser.beginObject();
				
				Assert.assertTrue(jsonParser.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
				Assert.assertEquals("foo", jsonParser.nextName());
				
				Assert.assertTrue(jsonParser.hasNext());
				Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonParser.currentState());
					jsonParser.beginArray();
					
					Assert.assertTrue(jsonParser.hasNext());
					Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
					Assert.assertEquals("bar\n", jsonParser.nextString());

					Assert.assertTrue(jsonParser.hasNext());
					Assert.assertEquals(JsonState.BOOLEAN, jsonParser.currentState());
					Assert.assertEquals(true, jsonParser.nextBoolean());
					
					Assert.assertTrue(jsonParser.hasNext());
					Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
						jsonParser.beginObject();
					
						Assert.assertFalse(jsonParser.hasNext());
						Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
						jsonParser.endObject();
						
					Assert.assertFalse(jsonParser.hasNext());
					Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
					jsonParser.endArray();
					
				Assert.assertTrue(jsonParser.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
				Assert.assertEquals("baz", jsonParser.nextName());
				
				Assert.assertTrue(jsonParser.hasNext());
				Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonParser.currentState());
					jsonParser.beginObject();
					
					Assert.assertTrue(jsonParser.hasNext());
					Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
					Assert.assertEquals("foo", jsonParser.nextName());
					
					Assert.assertTrue(jsonParser.hasNext());
					Assert.assertEquals(JsonState.LONG, jsonParser.currentState());
					Assert.assertEquals(42, jsonParser.nextLong());
					
					Assert.assertFalse(jsonParser.hasNext());
					Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
					jsonParser.endObject();

				Assert.assertFalse(jsonParser.hasNext());
				Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
				jsonParser.endObject();
				
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();
			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());
			//@formatter:on

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipSingleValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[null,\"keep\" ]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			jsonParser.nextString();
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonParser.currentState());
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipComplexValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("[[{\"skipped\":[true]}],\"keep\" ]"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.STRING, jsonParser.currentState());
			jsonParser.nextString();
			jsonParser.endArray();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootObject() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.skipValue();
			Assert.assertFalse(jsonParser.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonParser.currentState());
			jsonParser.endObject();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueBeforeName() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.endObject();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueAfterName() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.nextName();
			jsonParser.skipValue();
			Assert.assertTrue(jsonParser.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonParser.currentState());
			jsonParser.nextName();
			jsonParser.nextNull();
			jsonParser.endObject();
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}"));
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.skipValue();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonParser.currentState());
			jsonParser.endDocumnet();

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}[]"), true);
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocumnet();

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocumnet();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode_trailingWhitespace() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{}[] \n\r\t "), true);
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocumnet();

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocumnet();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void multipleDocumentMode_separatingWhitespace() throws IOException, JsonSyntaxException {
		JsonPullParser jsonParser = new JsonPullParser(getSource("{} \n\r\t []"), true);
		try {

			jsonParser.beginDocumnet();
			jsonParser.beginObject();
			jsonParser.endObject();
			jsonParser.endDocumnet();

			jsonParser.beginDocumnet();
			jsonParser.beginArray();
			jsonParser.endArray();
			jsonParser.endDocumnet();

			Assert.assertEquals(JsonState.SOURCE_END, jsonParser.currentState());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void syntaxError_position() throws IOException {

		JsonPullParser jsonParser = new JsonPullParser(getSource("  \n  \n  \n   Xfoobar"));

		try {

			jsonParser.beginDocumnet();
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
