package net.markenwerk.utils.json.parser;

import org.junit.Test;

/**
 * JUnit test for {@link JsonPullParser} with an underlying
 * {@link CharacterArrayJsonSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class CharacterArrayJsonPullParserTests extends AbstractJsonPullParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullString() {
		new JsonPullParser((char[]) null);
	}

	@Override
	protected JsonSource getSource(String string) {
		return new CharacterArrayJsonSource(string.toCharArray());
	}

}
