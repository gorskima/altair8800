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

	public int readWord8(final int addr) {
		return mem[addr].toInt();
	}
	
	public void writeWord8(final int addr, final int word) {
		mem[addr] = new Word(word);
	}
	
	public int readWord16(final int addr) {
		Word lower = mem[addr];
		Word upper = mem[addr + 1];
		return lower.withUpperByte(upper).toInt();
	}
	
	public void writeWord16(final int addr, final int word) {
		DoubleWord doubleWord = new DoubleWord(word);
		mem[addr] = doubleWord.getLowerByte();
		mem[addr + 1] = doubleWord.getUpperByte();
	}
	
}
