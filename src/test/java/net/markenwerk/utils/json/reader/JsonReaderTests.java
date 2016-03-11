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
package net.markenwerk.utils.json.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link JsonReader}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class JsonReaderTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullReader() {
		new JsonReader(null);
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void emptyDocument() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read(""));
		try {

			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmpty_invalidStart() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("\""));
		try {

			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[]"));
		try {

			Assert.assertEquals(JsonToken.ARRAY_BEGIN, jsonReader.currentToken());
			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.ARRAY_END, jsonReader.currentToken());
			jsonReader.endArray();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void emptyArray_unfinished() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("["));
		try {

			jsonReader.beginArray();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNull() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFalse() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[false]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.BOOLEAN, jsonReader.currentToken());
			Assert.assertEquals(false, jsonReader.nextBoolean());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleTrue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[true]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.BOOLEAN, jsonReader.currentToken());
			Assert.assertEquals(true, jsonReader.nextBoolean());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleEmptyString() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			Assert.assertEquals("", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyString() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"foo\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			Assert.assertEquals("foo", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyVeryLargeString() throws MalformedJsonException, IOException {
		String value = createVeryLargeString();
		JsonReader jsonReader = new JsonReader(read("[\"" + value + "\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
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
	public void nonEmptyArray_singleNonEmptyStringWithEscapeSequences() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\\"\\\\\\/\\b\\f\\r\\n\\t\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			Assert.assertEquals("\"\\/\b\f\r\n\t", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithUnicodeEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\uBEEF\\ubeef\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			Assert.assertEquals("\ubeef\uBEEF", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithSurrogateUnicodeEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\uD834\\uDD1E\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			Assert.assertEquals("\uD834\uDD1E", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringDangelingEscapeSequences() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringUnterminatedEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\\"]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringInvalidEscapeSequences() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\x\"]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringDangelingUnicodeEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\u"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringUnterminatedUnicodeEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\u\",null]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleNonEmptyStringInvalidUnicodeEscapeSequences() throws MalformedJsonException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\uNOPE\"]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleLong() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[0]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(0, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveLong() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(42, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeLong() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[-42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(-42, jsonReader.nextLong());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInteger() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(42, jsonReader.nextInteger());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeInteger() throws MalformedJsonException, IOException {
		long value = ((long) Integer.MAX_VALUE) + 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextInteger();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallInteger() throws MalformedJsonException, IOException {
		long value = ((long) Integer.MIN_VALUE) - 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextInteger();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleShort() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(42, jsonReader.nextShort());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeShort() throws MalformedJsonException, IOException {
		long value = ((long) Short.MAX_VALUE) + 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextShort();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallShort() throws MalformedJsonException, IOException {
		long value = ((long) Short.MIN_VALUE) - 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextShort();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleCharacter() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(42, jsonReader.nextCharacter());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeCharacter() throws MalformedJsonException, IOException {
		long value = ((long) Character.MAX_VALUE) + 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextCharacter();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallCharacter() throws MalformedJsonException, IOException {
		long value = ((long) Character.MIN_VALUE) - 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextCharacter();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleByte() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
			Assert.assertEquals(42, jsonReader.nextByte());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooLargeByte() throws MalformedJsonException, IOException {
		long value = ((long) Byte.MAX_VALUE) + 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextByte();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = ArithmeticException.class)
	public void nonEmptyArray_singleTooSmallByte() throws MalformedJsonException, IOException {
		long value = ((long) Byte.MIN_VALUE) - 1;
		JsonReader jsonReader = new JsonReader(read("[" + value + "]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextByte();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleDouble() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[0.0]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(0, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDouble() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.23]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(42.23, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeDouble() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[-42.23]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(-42.23, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithPositiveExponent() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.0e7]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(42.0e7, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithNegativeExponent() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.0e-7]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(42.0e-7, jsonReader.nextDouble(), 0e-10);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFloat() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.23]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonToken.DOUBLE, jsonReader.currentToken());
			Assert.assertEquals(42.23f, jsonReader.nextFloat(), 0e-10f);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_singleInvalidLiteal() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[x]"));
		try {

			jsonReader.beginArray();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_multipleValues() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,null]"));
		try {

			Assert.assertEquals(JsonToken.ARRAY_BEGIN, jsonReader.currentToken());
			jsonReader.beginArray();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.ARRAY_END, jsonReader.currentToken());
			jsonReader.endArray();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_nestedArray() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[[]]"));
		try {

			Assert.assertEquals(JsonToken.ARRAY_BEGIN, jsonReader.currentToken());
			jsonReader.beginArray();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.ARRAY_BEGIN, jsonReader.currentToken());
			jsonReader.beginArray();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.ARRAY_END, jsonReader.currentToken());
			jsonReader.endArray();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.ARRAY_END, jsonReader.currentToken());
			jsonReader.endArray();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_unfinishedDangelingValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null"));
		try {

			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyArray_unfinishedDangelingComma() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,"));
		try {

			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{}"));
		try {

			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
			jsonReader.beginObject();
			Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
			jsonReader.endObject();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void emptyObject_unfinished() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{"));
		try {

			jsonReader.beginObject();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_singleValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null}"));
		try {

			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
			jsonReader.endObject();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_multipleValues() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null,\"bar\":null}"));
		try {

			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
			Assert.assertEquals("bar", jsonReader.nextName());
			Assert.assertEquals(JsonToken.NULL, jsonReader.currentToken());
			jsonReader.nextNull();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
			jsonReader.endObject();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_nestedValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":{}}"));
		try {

			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
			jsonReader.beginObject();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
			Assert.assertEquals("foo", jsonReader.nextName());
			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
			jsonReader.beginObject();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
			jsonReader.endObject();
			Assert.assertFalse(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
			jsonReader.endObject();
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyObject_unfinishedDangelingName() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\""));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyObject_unfinishedDangelingColon() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyObject_unfinishedDangelingValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = MalformedJsonException.class)
	public void nonEmptyObject_unfinishedDangelingComma() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null,"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentToken();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_complexValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(
				read(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {
			//@formatter:off
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
				jsonReader.beginObject();
				assertPath(jsonReader, ObjectKey.NO_HINT);
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
				Assert.assertEquals("foo", jsonReader.nextName());
				assertPath(jsonReader, "foo");
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonToken.ARRAY_BEGIN, jsonReader.currentToken());
					jsonReader.beginArray();
					assertPath(jsonReader, "foo", ArrayKey.NO_HINT);
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
					Assert.assertEquals("bar\n", jsonReader.nextString());
					assertPath(jsonReader, "foo", "0");

					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.BOOLEAN, jsonReader.currentToken());
					Assert.assertEquals(true, jsonReader.nextBoolean());
					assertPath(jsonReader, "foo", "1");
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
						jsonReader.beginObject();
						assertPath(jsonReader, "foo", "2", ObjectKey.NO_HINT);
					
						Assert.assertFalse(jsonReader.hasNext());
						Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
						jsonReader.endObject();
						assertPath(jsonReader, "foo", ArrayKey.NO_HINT);
						
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.ARRAY_END, jsonReader.currentToken());
					jsonReader.endArray();
					assertPath(jsonReader, ObjectKey.NO_HINT);
					
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
				Assert.assertEquals("baz", jsonReader.nextName());
				assertPath(jsonReader, "baz");
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonToken.OBJECT_BEGIN, jsonReader.currentToken());
					jsonReader.beginObject();
					assertPath(jsonReader, "baz", ObjectKey.NO_HINT);
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
					Assert.assertEquals("foo", jsonReader.nextName());
					assertPath(jsonReader, "baz", "foo");
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.LONG, jsonReader.currentToken());
					Assert.assertEquals(42, jsonReader.nextLong());
					assertPath(jsonReader, "baz", "foo");
					
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
					jsonReader.endObject();
					assertPath(jsonReader, ObjectKey.NO_HINT);

				Assert.assertFalse(jsonReader.hasNext());
				Assert.assertEquals(JsonToken.OBJECT_END, jsonReader.currentToken());
				jsonReader.endObject();
				assertPath(jsonReader);
				
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();
			//@formatter:on

		} finally {
			jsonReader.close();
		}
	}

	private void assertPath(JsonReader jsonReader, String... path) {
		Assert.assertEquals(jsonReader.getPath(), Arrays.asList(path));
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootArray() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[]"));
		try {

			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipSingleValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,\"keep\" ]"));
		try {

			jsonReader.beginArray();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			jsonReader.nextString();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_skipComplexValue() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("[[{\"skipped\":[true]}],\"keep\" ]"));
		try {

			jsonReader.beginArray();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.STRING, jsonReader.currentToken());
			jsonReader.nextString();
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyDocument_skipRootObject() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{}"));
		try {

			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.DOCUMENT_END, jsonReader.currentToken());
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_skipSingleValueBeforeName() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"skip\":null,\"keep\":null}"));
		try {

			jsonReader.beginObject();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
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
	public void nonEmptyObject_skipSingleValueAfterName() throws MalformedJsonException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"skip\":null,\"keep\":null}"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.skipValue();
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonToken.NAME, jsonReader.currentToken());
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.endObject();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	private Reader read(String string) {
		return new StringReader(string);
	}

}
