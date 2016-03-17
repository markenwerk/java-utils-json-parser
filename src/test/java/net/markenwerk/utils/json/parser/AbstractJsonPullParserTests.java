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
		JsonPullParser jsonReader = new JsonPullParser(getSource(""));
		try {

			jsonReader.beginDocumnet();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_START, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmpty_invalidStart() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("\""));
		try {

			jsonReader.beginDocumnet();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_DOCUMENT_START, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_afterBom() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource(JsonSource.BYTE_ORDER_MARK + "[]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_unfinished() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("["));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FIRST, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_wrongFinish() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FIRST, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNull() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFalse() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[false]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.BOOLEAN, jsonReader.currentState());
			Assert.assertEquals(false, jsonReader.nextBoolean());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleTrue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[true]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.BOOLEAN, jsonReader.currentState());
			Assert.assertEquals(true, jsonReader.nextBoolean());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleEmptyString() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyString() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"foo\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("foo", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyVeryLargeString() throws IOException, JsonSyntaxException {
		String value = createVeryLargeString();
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"" + value + "\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals(value, jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
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
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\\"\\\\\\/\\b\\f\\r\\n\\t\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("\"\\/\b\f\r\n\t", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithUnicodeEscapeSequences() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\uBEEF\\ubeef\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("\ubeef\uBEEF", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithSurrogateUnicodeEscapeSequences() throws IOException,
			JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\uD834\\uDD1E\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("\uD834\uDD1E", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringDangelingEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringUnterminatedEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNTERMINATED_STRING, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringInvalidEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\x\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringDangelingUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\u"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringUnterminatedUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\u\",null]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.UNFINISHED_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringInvalidUnicodeEscapeSequences() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[\"\\uNOPE\"]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextString();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_UNICODE_ESCAPE_SEQUENCE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[0]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(0, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(42, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeLong() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[-42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(-42, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInteger() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(42, jsonReader.nextInteger());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MAX_VALUE) + 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextInteger();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallInteger() throws IOException, JsonSyntaxException {
		long value = ((long) Integer.MIN_VALUE) - 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextInteger();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleShort() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(42, jsonReader.nextShort());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MAX_VALUE) + 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextShort();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallShort() throws IOException, JsonSyntaxException {
		long value = ((long) Short.MIN_VALUE) - 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextShort();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleCharacter() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(42, jsonReader.nextCharacter());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MAX_VALUE) + 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextCharacter();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallCharacter() throws IOException, JsonSyntaxException {
		long value = ((long) Character.MIN_VALUE) - 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextCharacter();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleByte() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
			Assert.assertEquals(42, jsonReader.nextByte());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MAX_VALUE) + 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextByte();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallByte() throws IOException, JsonSyntaxException {
		long value = ((long) Byte.MIN_VALUE) - 1;
		JsonPullParser jsonReader = new JsonPullParser(getSource("[" + value + "]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextByte();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[0.0]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(0, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42.23]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(42.23, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeDouble() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[-42.23]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(-42.23, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithPositiveExponent() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42.0e7]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(42.0e7, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithNegativeExponent() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42.0e-7]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(42.0e-7, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFloat() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[42.23]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(42.23f, jsonReader.nextFloat(), 0e-10f);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInvalidLiteal() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[x]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_LITERAL, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_multipleValues() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null,null]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
			jsonReader.beginArray();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_nestedArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[[]]"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
			jsonReader.beginArray();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
			jsonReader.beginArray();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingValue() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FOLLOW, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_wrongFinish() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FOLLOW, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingComma() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null,"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_VALUE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_dengelingCommaWrongFinish() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null,}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_VALUE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
			jsonReader.beginObject();
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_unfinished() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_wrongFinish() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_singleValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_multipleValues() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null,\"bar\":null}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			Assert.assertEquals("bar", jsonReader.nextName());
			Assert.assertEquals(JsonState.NULL, jsonReader.currentState());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_nestedValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":{}}"));
		try {

			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
			jsonReader.beginObject();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoName() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FIRST, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingName() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\""));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_SEPARATION, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingColon() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_VALUE, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingValue() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FOLLOW, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedDangelingComma() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null,"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_NAME, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoValue() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\",null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_SEPARATION, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedKeyAfterComma() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null,null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_NAME, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_wrongFinish() throws IOException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"foo\":null]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_OBJECT_FOLLOW, exception.getError());

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_complexValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(
				getSource(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {
			//@formatter:off
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_BEGIN, jsonReader.currentState());
			jsonReader.beginDocumnet();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
				jsonReader.beginObject();
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
				Assert.assertEquals("foo", jsonReader.nextName());
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
					jsonReader.beginArray();
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
					Assert.assertEquals("bar\n", jsonReader.nextString());

					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.BOOLEAN, jsonReader.currentState());
					Assert.assertEquals(true, jsonReader.nextBoolean());
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
						jsonReader.beginObject();
					
						Assert.assertFalse(jsonReader.hasNext());
						Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
						jsonReader.endObject();
						
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
					jsonReader.endArray();
					
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
				Assert.assertEquals("baz", jsonReader.nextName());
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
					jsonReader.beginObject();
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
					Assert.assertEquals("foo", jsonReader.nextName());
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
					Assert.assertEquals(42, jsonReader.nextLong());
					
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
					jsonReader.endObject();

				Assert.assertFalse(jsonReader.hasNext());
				Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
				jsonReader.endObject();
				
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();
			//@formatter:on

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootArray() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipSingleValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[null,\"keep\" ]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			jsonReader.nextString();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.skipValue();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipComplexValue() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("[[{\"skipped\":[true]}],\"keep\" ]"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginArray();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			jsonReader.nextString();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootObject() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.skipValue();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
			jsonReader.endObject();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueBeforeName() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.endObject();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueAfterName() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{\"skip\":null,\"keep\":null}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.endObject();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocumentBeforeEnd() throws IOException, JsonSyntaxException {
		JsonPullParser jsonReader = new JsonPullParser(getSource("{}"));
		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();
			jsonReader.endObject();
			jsonReader.skipValue();
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void syntaxError_position() throws IOException {

		JsonPullParser jsonReader = new JsonPullParser(getSource("  \n  \n  \n   Xfoobar"));

		try {

			jsonReader.beginDocumnet();
			jsonReader.beginObject();

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException e) {

			Assert.assertEquals(4, e.getLine());
			Assert.assertEquals(4, e.getColumn());

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	protected abstract JsonSource getSource(String string);

}
