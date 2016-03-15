package net.markenwerk.utils.json.parser;

import org.junit.Test;

/**
 * JUnit test for {@link JsonParser} with an underlying {@link StringSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class StringJsonParserTests extends AbstractJsonParserTests {

	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullString() {
		new JsonParser((String) null);
	}

	@Override
	protected JsonSource getSource(String string) {
		return new StringSource(string);
	}

}
