package gorskima.altair8800;

import static com.google.common.base.Preconditions.checkArgument;

public class Word {

	private final int value;

	public Word(int value) {
		checkArgument((value & 0xFFFFFF00) == 0x00000000, "Data out of range");
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public DoubleWord toDoubleWord() {
		return new DoubleWord(value);
	}

	public DoubleWord withUpperByte(Word word) {
		return new DoubleWord(word.toInt() << 8 | value);
	}

	public boolean testBitmask(Word mask) {
		return (value & mask.toInt()) == mask.toInt();
	}

	public Word setBits(Word word) {
		return new Word(value | word.toInt());
	}

	public Word unsetBits(Word word) {
		return new Word(value & ~word.toInt());
	}

	public Word invert() {
		return new Word(~value & 0xFF);
	}

    public Word and(Word other) {
        return new Word(value & other.value);
    }

	public Word or(Word other) {
		return new Word(value | other.value);
	}

	public Word xor(Word other) {
		return new Word(value ^ other.value);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof Word)) {
			return false;
		}

		Word otherWord = (Word) other;
		return this.value == otherWord.value;
	}

	@Override
	public int hashCode() {
		return value;
	}
}
