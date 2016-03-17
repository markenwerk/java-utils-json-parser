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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.markenwerk.utils.json.parser.events.ArrayBeginJsonEvent;
import net.markenwerk.utils.json.parser.events.ArrayEndJsonEvent;
import net.markenwerk.utils.json.parser.events.BooleanJsonEvent;
import net.markenwerk.utils.json.parser.events.CollectingJsonEventHandler;
import net.markenwerk.utils.json.parser.events.DocumentBeginJsonEvent;
import net.markenwerk.utils.json.parser.events.DocumentEndJsonEvent;
import net.markenwerk.utils.json.parser.events.DoubleJsonEvent;
import net.markenwerk.utils.json.parser.events.JsonEvent;
import net.markenwerk.utils.json.parser.events.JsonEventJsonHandler;
import net.markenwerk.utils.json.parser.events.LongJsonEvent;
import net.markenwerk.utils.json.parser.events.NameJsonEvent;
import net.markenwerk.utils.json.parser.events.NullJsonEvent;
import net.markenwerk.utils.json.parser.events.ObjectBeginJsonEvent;
import net.markenwerk.utils.json.parser.events.ObjectEndJsonEvent;
import net.markenwerk.utils.json.parser.events.StringJsonEvent;

/**
 * JUnit test for {@link JsonPushParser}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public abstract class AbstractJsonPushParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullSource() {
		new JsonPushParser((JsonSource) null);
	}

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullHandler() throws IllegalArgumentException, JsonSyntaxException, IOException {
		new JsonPushParser(getSource("")).handle(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyDocument() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource(""));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("\""));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new ArrayEndJsonEvent(), events.get(2));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(3));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_afterBom() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource(JsonSource.BYTE_ORDER_MARK + "[]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new ArrayEndJsonEvent(), events.get(2));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(3));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyArray_unfinished() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("["));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[}"));
		try {

			jsonParser.handle(new NullHandler());

			throw new RuntimeException("Expected JsonSyntaxException");
		} catch (JsonSyntaxException exception) {

			Assert.assertEquals(JsonSyntaxError.INVALID_ARRAY_FIRST, exception.getError());

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNull() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new NullJsonEvent(), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleFalse() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[false]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new BooleanJsonEvent(false), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleTrue() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[true]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new BooleanJsonEvent(true), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleEmptyString() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent(""), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyString() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"foo\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent("foo"), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyVeryLargeString() throws IOException, JsonSyntaxException {
		String value = createVeryLargeString();
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"" + value + "\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent(value), events.get(2));

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\\"\\\\\\/\\b\\f\\r\\n\\t\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent("\"\\/\b\f\r\n\t"), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithUnicodeEscapeSequences() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\uBEEF\\ubeef\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent("\ubeef\uBEEF"), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringWithSurrogateUnicodeEscapeSequences() throws IOException,
			JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\uD834\\uDD1E\"]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new StringJsonEvent("\uD834\uDD1E"), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNonEmptyStringDangelingEscapeSequences() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\\"]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\x\"]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\u"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\u\",null]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[\"\\uNOPE\"]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[0]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new LongJsonEvent(0), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveLong() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[42]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new LongJsonEvent(42), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeLong() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[-42]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new LongJsonEvent(-42), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleDouble() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[0.0]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DoubleJsonEvent(0.0), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDouble() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[42.23]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DoubleJsonEvent(42.23), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleNegativeDouble() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[-42.23]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DoubleJsonEvent(-42.23), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithPositiveExponent() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[42.0e7]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DoubleJsonEvent(42.0e7), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singlePositiveDoubleWithNegativeExponent() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[42.0e-7]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DoubleJsonEvent(42.0e-7), events.get(2));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_singleInvalidLiteal() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[x]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null,null]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new NullJsonEvent(), events.get(2));
			Assert.assertEquals(new NullJsonEvent(), events.get(3));
			Assert.assertEquals(new ArrayEndJsonEvent(), events.get(4));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(5));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_nestedArray() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[[]]"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(2));
			Assert.assertEquals(new ArrayEndJsonEvent(), events.get(3));
			Assert.assertEquals(new ArrayEndJsonEvent(), events.get(4));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(5));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyArray_unfinishedDangelingValue() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null}"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null,"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("[null,]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{}"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new ObjectEndJsonEvent(), events.get(2));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(3));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void emptyObject_unfinished() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("{"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null}"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new NameJsonEvent("foo"), events.get(2));
			Assert.assertEquals(new NullJsonEvent(), events.get(3));
			Assert.assertEquals(new ObjectEndJsonEvent(), events.get(4));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(5));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_multipleValues() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null,\"bar\":null}"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new NameJsonEvent("foo"), events.get(2));
			Assert.assertEquals(new NullJsonEvent(), events.get(3));
			Assert.assertEquals(new NameJsonEvent("bar"), events.get(4));
			Assert.assertEquals(new NullJsonEvent(), events.get(5));
			Assert.assertEquals(new ObjectEndJsonEvent(), events.get(6));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(7));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_nestedValue() throws IOException, JsonSyntaxException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":{}}"));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(1));
			Assert.assertEquals(new NameJsonEvent("foo"), events.get(2));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(3));
			Assert.assertEquals(new ObjectEndJsonEvent(), events.get(4));
			Assert.assertEquals(new ObjectEndJsonEvent(), events.get(5));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(6));

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void nonEmptyObject_unfinishedNoName() throws IOException {
		JsonPushParser jsonParser = new JsonPushParser(getSource("{null}"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\""));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null,"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\",null}"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null,null}"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(getSource("{\"foo\":null]"));
		try {

			jsonParser.handle(new NullHandler());

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
		JsonPushParser jsonParser = new JsonPushParser(
				getSource(" \n { \"foo\" : [ \"bar\\n\" , true , { \n } ] , \"baz\" : { \"foo\" \t : \t 42 } } \n "));
		try {

			List<JsonEvent> events = jsonParser.handle(new JsonEventJsonHandler<List<JsonEvent>>(
					new CollectingJsonEventHandler()));

			//@formatter:off
			Assert.assertEquals(new DocumentBeginJsonEvent(), events.get(0));
			Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(1));
				Assert.assertEquals(new NameJsonEvent("foo"), events.get(2));
				Assert.assertEquals(new ArrayBeginJsonEvent(), events.get(3));
					Assert.assertEquals(new StringJsonEvent("bar\n"), events.get(4));
					Assert.assertEquals(new BooleanJsonEvent(true), events.get(5));
					Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(6));
						Assert.assertEquals(new ObjectEndJsonEvent(), events.get(7));
					Assert.assertEquals(new ArrayEndJsonEvent(), events.get(8));
				Assert.assertEquals(new NameJsonEvent("baz"), events.get(9));
				Assert.assertEquals(new ObjectBeginJsonEvent(), events.get(10));
					Assert.assertEquals(new NameJsonEvent("foo"), events.get(11));
					Assert.assertEquals(new LongJsonEvent(42), events.get(12));
					Assert.assertEquals(new ObjectEndJsonEvent(), events.get(13));
				Assert.assertEquals(new ObjectEndJsonEvent(), events.get(14));
			Assert.assertEquals(new DocumentEndJsonEvent(), events.get(15));
			//@formatter:on

		} finally {
			jsonParser.close();
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void syntaxError_position() throws IOException {

		JsonPushParser jsonParser = new JsonPushParser(getSource("  \n  \n  \n   Xfoobar"));

		try {

			jsonParser.handle(new NullHandler());

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
