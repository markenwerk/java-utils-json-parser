package net.markenwerk.utils.json.parser.events;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("javadoc")
public class CollectingJsonEventHandler implements JsonEventHandler<List<JsonEvent>> {

	private final List<JsonEvent> events = new LinkedList<JsonEvent>();

	@Override
	public void onEvent(JsonEvent event) {
		events.add(event);
	}

	@Override
	public List<JsonEvent> getResult() {
		return events;
	}

}
