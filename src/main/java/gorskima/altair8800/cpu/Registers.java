package gorskima.altair8800.cpu;

import static gorskima.altair8800.cpu.Register.F;
import static gorskima.altair8800.cpu.Register.PC;

import gorskima.altair8800.DoubleWord;
import gorskima.altair8800.Word;

public class Registers {

	private static final Word UNINITIALIZED_BYTE = new Word(0);

	private Word[] mem = new Word[12];

	public Registers() {
		for (int addr = 0; addr < mem.length; addr++) {
			mem[addr] = UNINITIALIZED_BYTE;
		}
	}

	public int getRegister(final Register r) {
		if (r.size == 1) {
			return getRegister8(r).toInt();
		} else {
			return getRegister16(r).toInt();
		}
	}

	public void setRegister(final Register r, final int value) {
		if (r.size == 1) {
			setRegister8(r, new Word(value));
		} else {
			setRegister16(r, new DoubleWord(value));
		}
	}

	private Word getRegister8(final Register r) {
		int addr = r.offset;
		return mem[addr];
	}

	private void setRegister8(final Register r, final Word value) {
		int addr = r.offset;
		mem[addr] = value;
	}

	private DoubleWord getRegister16(final Register r) {
		int addr = r.offset;
		Word upperByte = mem[addr];
		Word lowerByte = mem[addr + 1];
		return lowerByte.withUpperByte(upperByte);
	}

	private void setRegister16(final Register r, final DoubleWord doubleWord) {
		int addr = r.offset;
		mem[addr] = doubleWord.getUpperByte();
		mem[addr + 1] = doubleWord.getLowerByte();
	}

	public void incPC() {
		DoubleWord pc = getRegister16(PC);
		setRegister16(PC, pc.increment());
	}

	public boolean testFlag(final Flag flag) {
		return getRegister8(F).testBitmask(new Word(flag.mask));
	}

	public void setFlag(final Flag flag, final boolean value) {
		int f = getRegister8(F).toInt();

		if (value) {
			setRegister8(F, new Word(f | flag.mask));
		} else {
			setRegister8(F, new Word(f & ~flag.mask));
		}
	}

}
