package net.markenwerk.utils.json.parser;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

/**
 * JUnit test for {@link JsonParser} with an underlying {@link ReaderSource}.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 */
public class ReaderJsonParserTests extends AbstractJsonParserTests {

	
	@SuppressWarnings({ "resource", "javadoc" })
	@Test(expected = IllegalArgumentException.class)
	public void create_nullReader() {
		new JsonParser((Reader) null);
	}
	
	@Override
	protected JsonSource getSource(String string) {
		return new ReaderSource(new StringReader(string));
	}

}
