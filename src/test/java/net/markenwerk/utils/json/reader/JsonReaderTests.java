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
	@Test(expected = JsonSyntaxException.class)
	public void emptyDocument() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read(""));
		try {

			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmpty_invalidStart() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("\""));
		try {

			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[]"));
		try {

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

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void emptyArray_unfinished() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("["));
		try {

			jsonReader.beginArray();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNull() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null]"));
		try {

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
	public void nonEmptyArray_singleFalse() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[false]"));
		try {

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
	public void nonEmptyArray_singleTrue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[true]"));
		try {

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
	public void nonEmptyArray_singleEmptyString() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\"]"));
		try {

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
	public void nonEmptyArray_singleNonEmptyString() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"foo\"]"));
		try {

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
	public void nonEmptyArray_singleNonEmptyVeryLargeString() throws JsonSyntaxException, IOException {
		String value = createVeryLargeString();
		JsonReader jsonReader = new JsonReader(read("[\"" + value + "\"]"));
		try {

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
	public void nonEmptyArray_singleNonEmptyStringWithEscapeSequences() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\\"\\\\\\/\\b\\f\\r\\n\\t\"]"));
		try {

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
	public void nonEmptyArray_singleNonEmptyStringWithUnicodeEscapeSequences() throws JsonSyntaxException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\uBEEF\\ubeef\"]"));
		try {

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
	public void nonEmptyArray_singleNonEmptyStringWithSurrogateUnicodeEscapeSequences() throws JsonSyntaxException,
			IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\uD834\\uDD1E\"]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
			Assert.assertEquals("\uD834\uDD1E", jsonReader.nextString());
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringDangelingEscapeSequences() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringUnterminatedEscapeSequences() throws JsonSyntaxException,
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
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringInvalidEscapeSequences() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[\"\\x\"]"));
		try {

			jsonReader.beginArray();
			jsonReader.nextString();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringDangelingUnicodeEscapeSequences() throws JsonSyntaxException,
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
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringUnterminatedUnicodeEscapeSequences() throws JsonSyntaxException,
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
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleNonEmptyStringInvalidUnicodeEscapeSequences() throws JsonSyntaxException,
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
	public void nonEmptyArray_singleLong() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[0]"));
		try {

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
	public void nonEmptyArray_singlePositiveLong() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

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
	public void nonEmptyArray_singleNegativeLong() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[-42]"));
		try {

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
	public void nonEmptyArray_singleInteger() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

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
	public void nonEmptyArray_singleTooLargeInteger() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleTooSmallInteger() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleShort() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

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
	public void nonEmptyArray_singleTooLargeShort() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleTooSmallShort() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleCharacter() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

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
	public void nonEmptyArray_singleTooLargeCharacter() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleTooSmallCharacter() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleByte() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42]"));
		try {

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
	public void nonEmptyArray_singleTooLargeByte() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleTooSmallByte() throws JsonSyntaxException, IOException {
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
	public void nonEmptyArray_singleDouble() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[0.0]"));
		try {

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
	public void nonEmptyArray_singlePositiveDouble() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.23]"));
		try {

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
	public void nonEmptyArray_singleNegativeDouble() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[-42.23]"));
		try {

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
	public void nonEmptyArray_singlePositiveDoubleWithPositiveExponent() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.0e7]"));
		try {

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
	public void nonEmptyArray_singlePositiveDoubleWithNegativeExponent() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.0e-7]"));
		try {

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
	public void nonEmptyArray_singleFloat() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[42.23]"));
		try {

			jsonReader.beginArray();
			Assert.assertEquals(JsonState.DOUBLE, jsonReader.currentState());
			Assert.assertEquals(42.23f, jsonReader.nextFloat(), 0e-10f);
			jsonReader.endArray();
			jsonReader.endDocumnet();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_singleInvalidLiteal() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[x]"));
		try {

			jsonReader.beginArray();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_multipleValues() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,null]"));
		try {

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
	public void nonEmptyArray_nestedArray() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[[]]"));
		try {

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

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_unfinishedDangelingValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null"));
		try {

			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyArray_unfinishedDangelingComma() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,"));
		try {

			jsonReader.beginArray();
			jsonReader.nextNull();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{}"));
		try {

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

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void emptyObject_unfinished() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{"));
		try {

			jsonReader.beginObject();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_singleValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null}"));
		try {

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
	public void nonEmptyObject_multipleValues() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null,\"bar\":null}"));
		try {

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
	public void nonEmptyObject_nestedValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":{}}"));
		try {

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

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyObject_unfinishedDangelingName() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\""));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyObject_unfinishedDangelingColon() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyObject_unfinishedDangelingValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@SuppressWarnings("javadoc")
	@Test(expected = JsonSyntaxException.class)
	public void nonEmptyObject_unfinishedDangelingComma() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"foo\":null,"));
		try {

			jsonReader.beginObject();
			jsonReader.nextName();
			jsonReader.nextNull();
			jsonReader.currentState();

		} finally {
			jsonReader.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_complexValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(
				read(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {
			//@formatter:off
			Assert.assertTrue(jsonReader.hasNext());
			Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
				jsonReader.beginObject();
				assertPath(jsonReader, ObjectKey.NO_HINT);
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
				Assert.assertEquals("foo", jsonReader.nextName());
				assertPath(jsonReader, "foo");
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.ARRAY_BEGIN, jsonReader.currentState());
					jsonReader.beginArray();
					assertPath(jsonReader, "foo", ArrayKey.NO_HINT);
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.STRING, jsonReader.currentState());
					Assert.assertEquals("bar\n", jsonReader.nextString());
					assertPath(jsonReader, "foo", "0");

					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.BOOLEAN, jsonReader.currentState());
					Assert.assertEquals(true, jsonReader.nextBoolean());
					assertPath(jsonReader, "foo", "1");
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
						jsonReader.beginObject();
						assertPath(jsonReader, "foo", "2", ObjectKey.NO_HINT);
					
						Assert.assertFalse(jsonReader.hasNext());
						Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
						jsonReader.endObject();
						assertPath(jsonReader, "foo", ArrayKey.NO_HINT);
						
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonState.ARRAY_END, jsonReader.currentState());
					jsonReader.endArray();
					assertPath(jsonReader, ObjectKey.NO_HINT);
					
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
				Assert.assertEquals("baz", jsonReader.nextName());
				assertPath(jsonReader, "baz");
				
				Assert.assertTrue(jsonReader.hasNext());
				Assert.assertEquals(JsonState.OBJECT_BEGIN, jsonReader.currentState());
					jsonReader.beginObject();
					assertPath(jsonReader, "baz", ObjectKey.NO_HINT);
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.NAME, jsonReader.currentState());
					Assert.assertEquals("foo", jsonReader.nextName());
					assertPath(jsonReader, "baz", "foo");
					
					Assert.assertTrue(jsonReader.hasNext());
					Assert.assertEquals(JsonState.LONG, jsonReader.currentState());
					Assert.assertEquals(42, jsonReader.nextLong());
					assertPath(jsonReader, "baz", "foo");
					
					Assert.assertFalse(jsonReader.hasNext());
					Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
					jsonReader.endObject();
					assertPath(jsonReader, ObjectKey.NO_HINT);

				Assert.assertFalse(jsonReader.hasNext());
				Assert.assertEquals(JsonState.OBJECT_END, jsonReader.currentState());
				jsonReader.endObject();
				assertPath(jsonReader);
				
			Assert.assertEquals(JsonState.DOCUMENT_END, jsonReader.currentState());
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
	public void nonEmptyDocument_skipRootArray() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[]"));
		try {

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
	public void nonEmptyArray_skipSingleValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[null,\"keep\" ]"));
		try {

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
	public void nonEmptyArray_skipComplexValue() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("[[{\"skipped\":[true]}],\"keep\" ]"));
		try {

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
	public void nonEmptyDocument_skipRootObject() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{}"));
		try {

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
	public void nonEmptyObject_skipSingleValueBeforeName() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"skip\":null,\"keep\":null}"));
		try {

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
	public void nonEmptyObject_skipSingleValueAfterName() throws JsonSyntaxException, IOException {
		JsonReader jsonReader = new JsonReader(read("{\"skip\":null,\"keep\":null}"));
		try {

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

	private Reader read(String string) {
		return new StringReader(string);
	}

}
