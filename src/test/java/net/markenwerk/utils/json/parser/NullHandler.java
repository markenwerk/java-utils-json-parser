package net.markenwerk.utils.json.parser;

import net.markenwerk.utils.json.commons.interfaces.JsonHandler;

@SuppressWarnings("javadoc")
public class NullHandler implements JsonHandler<Void> {

	@Override
	public void onDocumentBegin() {
	}

	@Override
	public void onDocumentEnd() {
	}

	@Override
	public void onArrayBegin() {
	}

	@Override
	public void onArrayEnd() {
	}

	@Override
	public void onObjectBegin() {
	}

	@Override
	public void onObjectEnd() {
	}

	@Override
	public void onName(String name) {
	}

	@Override
	public void onNext() {
	}

	@Override
	public void onNull() {
	}

	@Override
	public void onBoolean(boolean value) {
	}

	@Override
	public void onLong(long value) {
	}

	@Override
	public void onDouble(double value) {
	}

	@Override
	public void onString(String value) {
	}

	@Override
	public Void getResult() {
		return null;
	}

}
