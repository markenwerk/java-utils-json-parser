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
package net.markenwerk.utils.json.parser.events;

import net.markenwerk.utils.json.handler.JsonHandler;
import net.markenwerk.utils.json.handler.JsonHandlingException;

@SuppressWarnings("javadoc")
public final class JsonEventJsonHandler<Result> implements JsonHandler<Result> {

	private final JsonEventHandler<Result> handler;

	public JsonEventJsonHandler(JsonEventHandler<Result> handler) {
		if (null == handler) {
			throw new IllegalArgumentException("handler is null");
		}
		this.handler = handler;
	}

	@Override
	public void onDocumentBegin() {
		handler.onEvent(new DocumentBeginJsonEvent());
	}

	@Override
	public void onDocumentEnd() {
		handler.onEvent(new DocumentEndJsonEvent());
	}

	@Override
	public void onArrayBegin() {
		handler.onEvent(new ArrayBeginJsonEvent());
	}

	@Override
	public void onArrayEnd() {
		handler.onEvent(new ArrayEndJsonEvent());
	}

	@Override
	public void onObjectBegin() {
		handler.onEvent(new ObjectBeginJsonEvent());
	}

	@Override
	public void onObjectEnd() {
		handler.onEvent(new ObjectEndJsonEvent());
	}

	@Override
	public void onName(String name) {
		handler.onEvent(new NameJsonEvent(name));
	}

	@Override
	public void onNext() throws JsonHandlingException {
	}

	@Override
	public void onNull() {
		handler.onEvent(new NullJsonEvent());
	}

	@Override
	public void onBoolean(boolean value) {
		handler.onEvent(new BooleanJsonEvent(value));
	}

	@Override
	public void onLong(long value) {
		handler.onEvent(new LongJsonEvent(value));
	}

	@Override
	public void onDouble(double value) {
		handler.onEvent(new DoubleJsonEvent(value));
	}

	@Override
	public void onString(String value) {
		handler.onEvent(new StringJsonEvent(value));
	}

	@Override
	public Result getResult() {
		return handler.getResult();
	}

}
