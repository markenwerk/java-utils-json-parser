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
 * A {@link JsonState} describes the current state of a {@link JsonParser} and
 * determines which methods may be called successfully next.
 *
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public enum JsonState {

	/**
	 * The {@link JsonParser} has encountered the beginning of a JSON array. It
	 * is possible to {@link JsonParser#beginArray() begin} an array or
	 * {@link JsonParser#skipValue() skip} the array.
	 */
	ARRAY_BEGIN,

	/**
	 * The {@link JsonParser} has encountered the end of a JSON array. It is
	 * possible to {@link JsonParser#endArray() end} the array.
	 */
	ARRAY_END,

	/**
	 * The {@link JsonParser} has encountered the beginning of a JSON object. It
	 * is possible to {@link JsonParser#beginObject() begin} an object or
	 * {@link JsonParser#skipValue() skip} the object.
	 */
	OBJECT_BEGIN,

	/**
	 * The {@link JsonParser} has encountered the name of a JSON object element.
	 * It is possible to {@link JsonParser#nextName() obtain} the name or
	 * {@link JsonParser#skipValue() skip} the name and the corresponding value.
	 */
	NAME,

	/**
	 * The {@link JsonParser} has encountered the end of a JSON object. It is
	 * possible to {@link JsonParser#endObject() end} the object.
	 */
	OBJECT_END,

	/**
	 * The {@link JsonParser} has encountered a JSON null. It is possible to
	 * {@link JsonParser#nextNull() obtain} the value or
	 * {@link JsonParser#skipValue() skip} the value.
	 */
	NULL,

	/**
	 * The {@link JsonParser} has encountered a JSON boolean. It is possible to
	 * {@link JsonParser#nextBoolean() obtain} the value or
	 * {@link JsonParser#skipValue() skip} the value.
	 */
	BOOLEAN,

	/**
	 * The {@link JsonParser} has encountered a JSON integer. It is possible to
	 * obtain the value as a {@link JsonParser#nextLong() <tt>long</tt>},
	 * {@link JsonParser#nextInteger() <tt>int</tt>},
	 * {@link JsonParser#nextShort() <tt>short</tt>},
	 * {@link JsonParser#nextCharacter() <tt>char</tt>},
	 * {@link JsonParser#nextByte() <tt>byte</tt>} or
	 * {@link JsonParser#skipValue() skip} the value.
	 */
	LONG,

	/**
	 * The {@link JsonParser} has encountered a JSON real. It is possible to
	 * obtain the value as a {@link JsonParser#nextDouble() <tt>double</tt>},
	 * {@link JsonParser#nextFloat() <tt>float</tt>} or
	 * {@link JsonParser#skipValue() skip} the value.
	 */
	DOUBLE,

	/**
	 * The {@link JsonParser} has encountered a JSON string. It is possible to
	 * {@link JsonParser#nextString() obtain} the value or
	 * {@link JsonParser#skipValue() skip} the value.
	 */
	STRING,

	/**
	 * The {@link JsonParser} has encountered the end of a JSON document. It is
	 * possible to {@link JsonParser#endDocumnet() end} the document.
	 */
	DOCUMENT_END
}
