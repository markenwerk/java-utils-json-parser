package net.markenwerk.utils.json.parser;

public enum JsonSyntaxError {

	//@formatter:off
	
	INVALID_DOCUMENT_START("Invalid document start", "Expected '[' or '{'"),

	INVALID_DOCUMENT_END("Invalid document end", "Expected EOF"),

	INVALID_ARRAY_FIRST("Invalid array", "Expected value or ']' after '['"),

	INVALID_ARRAY_FOLLOW("Invalid array", "Expected ',' or ']' after value"),

	INVALID_ARRAY_VALUE("Invalid array", "Expected value after ','"),

	INVALID_OBJECT_FIRST("Invalid object", "Expected name or '}' after '}'"),

	INVALID_OBJECT_FOLLOW("Invalid object", "Expected ',' or '}' after value"),

	INVALID_OBJECT_NAME("Invalid object", "Expected name after ','"),

	INVALID_OBJECT_SEPARATION("Invalid object", "Expected ':' after name"),

	INVALID_OBJECT_VALUE("Invalid object", "Expected value after ':'"),

	UNTERMINATED_STRING("Unterminated string", "Expected '\"'"),

	UNFINISHED_ESCAPE_SEQUENCE("Unfinished escapse sequence", "Expected at least one character after '\\'"),

	INVALID_ESCAPE_SEQUENCE("Invalid escape sequence", "Expected '\"', '\\', '/', 'b', 'f', 'r', 'n', 't' or 'uXXXX' after '\\'"),

	UNFINISHED_UNICODE_ESCAPE_SEQUENCE("Unfinished unicode escapse sequence", "Expected four characters after '\\u'"),

	INVALID_UNICODE_ESCAPE_SEQUENCE("Invalid unicode escapse sequence",	"Expected four hexadecimal characters after '\\u'"),

	INVALID_LITERAL("Invalid literal", "Expected 'null', 'false', 'true' or a number for a value that doesn't start with '[', '{' or '\"'");

	//@formatter:on

	private final String description;

	private final String expectation;

	private JsonSyntaxError(String description, String expectation) {
		this.description = description;
		this.expectation = expectation;
	}

	public String getDescription() {
		return description;
	}

	public String getExpectation() {
		return expectation;
	}

}
