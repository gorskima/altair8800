package gorskima.altair8800;

import static com.google.common.base.Preconditions.checkArgument;

public class DoubleWord {

	private final int value;

	public DoubleWord(int value) {
		checkArgument((value & 0xFFFF0000) == 0x00000000, "Data out of range");
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

	public DoubleWord increment() {
		return add(1);
	}

	public DoubleWord add(int n) {
		return new DoubleWord((value + n) & 0x0000FFFF);
	}

	public DoubleWord subtract(int n) {
		return new DoubleWord((value - n) & 0x0000FFFF);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof Word)) {
			return false;
		}

		DoubleWord otherWord = (DoubleWord) other;
		return this.value == otherWord.value;
	}

	@Override
	public int hashCode() {
		return value;
	}
}
