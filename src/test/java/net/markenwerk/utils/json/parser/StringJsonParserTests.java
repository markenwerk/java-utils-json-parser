package net.markenwerk.utils.json.parser;

import org.junit.Test;

/**
 * JUnit test for {@link JsonPullParser} with an underlying {@link StringJsonSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class StringJsonParserTests extends AbstractJsonParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullString() {
		new JsonPullParser((String) null);
	}

	@Override
	protected JsonSource getSource(String string) {
		return new StringJsonSource(string);
	}

}
