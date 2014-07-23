package gorskima.altair8800;

import static com.google.common.base.Preconditions.checkArgument;

public class Word {

	private final int value;

	public Word(int value) {
		checkArgument((value & 0xFFFFFF00) == 0x00000000, "Data overflow");
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public DoubleWord toDoubleWord() {
		return new DoubleWord(value);
	}
}
