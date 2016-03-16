package net.markenwerk.utils.json.parser;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

/**
 * JUnit test for {@link JsonPullParser} with an underlying {@link ReaderJsonSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class ReaderJsonPullParserTests extends AbstractJsonPullParserTests {

	
	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullReader() {
		new JsonPullParser((Reader) null);
	}
	
	@Override
	protected JsonSource getSource(String string) {
		return new ReaderJsonSource(new StringReader(string));
	}

}
