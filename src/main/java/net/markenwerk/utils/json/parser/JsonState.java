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

/**
 * A {@link JsonState} describes the current state of a {@link JsonReader} and
 * determines which methods may be called successfully next.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public enum JsonState {

	/**
	 * The {@link JsonReader} has encountered the beginning of a JSON array. It
	 * is possible to {@link JsonReader#beginArray() begin} an array or
	 * {@link JsonReader#skipValue() skip} the array.
	 */
	ARRAY_BEGIN,

	/**
	 * The {@link JsonReader} has encountered the end of a JSON array. It is
	 * possible to {@link JsonReader#endArray() end} the array.
	 */
	ARRAY_END,

	/**
	 * The {@link JsonReader} has encountered the beginning of a JSON object. It
	 * is possible to {@link JsonReader#beginObject() begin} an object or
	 * {@link JsonReader#skipValue() skip} the object.
	 */
	OBJECT_BEGIN,

	/**
	 * The {@link JsonReader} has encountered the name of a JSON object element.
	 * It is possible to {@link JsonReader#nextName() obtain} the name or
	 * {@link JsonReader#skipValue() skip} the name and the corresponding value.
	 */
	NAME,

	/**
	 * The {@link JsonReader} has encountered the end of a JSON object. It is
	 * possible to {@link JsonReader#endObject() end} the object.
	 */
	OBJECT_END,

	/**
	 * The {@link JsonReader} has encountered a JSON null. It is possible to
	 * {@link JsonReader#nextNull() obtain} the value or
	 * {@link JsonReader#skipValue() skip} the value.
	 */
	NULL,

	/**
	 * The {@link JsonReader} has encountered a JSON boolean. It is possible to
	 * {@link JsonReader#nextBoolean() obtain} the value or
	 * {@link JsonReader#skipValue() skip} the value.
	 */
	BOOLEAN,

	/**
	 * The {@link JsonReader} has encountered a JSON integer. It is possible to
	 * obtain the value as a {@link JsonReader#nextLong() <tt>long</tt>},
	 * {@link JsonReader#nextInteger() <tt>int</tt>},
	 * {@link JsonReader#nextShort() <tt>short</tt>},
	 * {@link JsonReader#nextCharacter() <tt>char</tt>},
	 * {@link JsonReader#nextByte() <tt>byte</tt>} or
	 * {@link JsonReader#skipValue() skip} the value.
	 */
	LONG,

	/**
	 * The {@link JsonReader} has encountered a JSON real. It is possible to
	 * obtain the value as a {@link JsonReader#nextDouble() <tt>double</tt>},
	 * {@link JsonReader#nextFloat() <tt>float</tt>} or
	 * {@link JsonReader#skipValue() skip} the value.
	 */
	DOUBLE,

	/**
	 * The {@link JsonReader} has encountered a JSON string. It is possible to
	 * {@link JsonReader#nextString() obtain} the value or
	 * {@link JsonReader#skipValue() skip} the value.
	 */
	STRING,

	/**
	 * The {@link JsonReader} has encountered the end of a JSON document. It is
	 * possible to {@link JsonReader#endDocumnet() end} the document.
	 */
	DOCUMENT_END
}
