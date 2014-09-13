package gorskima.altair8800.cpu;

import gorskima.altair8800.DoubleWord;
import gorskima.altair8800.Word;


public class Memory {

	private static final int DEFAULT_SIZE = 1 << 16;
	private static final Word UNINITIALIZED_BYTE = new Word(0);

	private Word[] mem = new Word[DEFAULT_SIZE];

	public Memory() {
		for (int addr = 0; addr < mem.length; addr++) {
			mem[addr] = UNINITIALIZED_BYTE;
		}
	}

	public Word readWord8(final DoubleWord addr) {
		return mem[addr.toInt()];
	}

	public void writeWord8(final DoubleWord addr, final Word word) {
		mem[addr.toInt()] = word;
	}

	public DoubleWord readWord16(final DoubleWord addr) {
		Word lower = mem[addr.toInt()];
		Word upper = mem[addr.toInt() + 1];
		return lower.withUpperByte(upper);
	}

	public void writeWord16(final DoubleWord addr, final DoubleWord doubleWord) {
		mem[addr.toInt()] = doubleWord.getLowerByte();
		mem[addr.toInt() + 1] = doubleWord.getUpperByte();
	}

	/*
	 * Deprecated methods, used only in old tests
	 */

	int readWord8(final int addr) {
		return readWord8(new DoubleWord(addr)).toInt();
	}

	void writeWord8(final int addr, final int word) {
		writeWord8(new DoubleWord(addr), new Word(word));
	}

	int readWord16(final int addr) {
		return readWord16(new DoubleWord(addr)).toInt();
	}

	void writeWord16(final int addr, final int doubleWord) {
		writeWord16(new DoubleWord(addr), new DoubleWord(doubleWord));
	}
	
}
