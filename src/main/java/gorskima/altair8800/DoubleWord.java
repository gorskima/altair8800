package gorskima.altair8800;

import static com.google.common.base.Preconditions.checkArgument;

public class DoubleWord {

	private final int value;

	public DoubleWord(int value) {
		checkArgument((value & 0xFFFF0000) == 0x00000000, "Data overflow");
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public Word toWord() {
		return getLowerByte();
	}

	public Word getUpperByte() {
		return new Word(value >> 8);
	}

	public Word getLowerByte() {
		return new Word(value & 0x000000FF);
	}
}
