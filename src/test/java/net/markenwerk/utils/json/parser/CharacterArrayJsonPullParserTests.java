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

import org.junit.Test;

/**
 * JUnit test for {@link JsonSourcePullParser} with an underlying
 * {@link CharacterArrayJsonSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class CharacterArrayJsonPullParserTests extends AbstractJsonPullParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullString() {
		new JsonSourcePullParser((char[]) null);
	}

	@Override
	protected JsonSource getSource(String string) {
		return new CharacterArrayJsonSource(string.toCharArray());
	}

}
