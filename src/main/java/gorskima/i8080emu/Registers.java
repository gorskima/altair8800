package gorskima.i8080emu;

import static gorskima.i8080emu.Register.F;
import static gorskima.i8080emu.Register.PC;

import com.google.common.base.Preconditions;

public class Registers {

	private int[] mem = new int[12];

	public int getRegister(final Register r) {
		if (r.size == 1) {
			return getRegister8(r);
		} else {
			return getRegister16(r);
		}
	}

	public void setRegister(final Register r, final int value) {
		if (r.size == 1) {
			setRegister8(r, value);
		} else {
			setRegister16(r, value);
		}
	}

	private int getRegister8(final Register r) {
		int addr = r.offset;
		return mem[addr];
	}

	private void setRegister8(final Register r, final int value) {
		Preconditions.checkArgument((value & 0xFFFFFF00) == 0, "Value may use only 1 least significant byte");
		int addr = r.offset;
		mem[addr] = value;
	}

	private int getRegister16(final Register r) {
		int addr = r.offset;
		int h = mem[addr];
		int l = mem[addr + 1];
		return ((h << 8) + l);
	}

	private void setRegister16(final Register r, final int value) {
		Preconditions.checkArgument((value & 0xFFFF0000) == 0, "Value may use only 2 least significant bytes");
		int addr = r.offset;
		int h = value >> 8;
		int l = value & 0xFF;
		mem[addr] = h;
		mem[addr + 1] = l;
	}

	public void incPC() {
		int pc = getRegister16(PC);
		int newPc = (pc + 1) & 0xFFFF;
		setRegister16(PC, newPc);
	}

	public boolean testFlag(final Flag flag) {
		return (getRegister(F) & flag.mask) > 0;
	}

	public void setFlag(final Flag flag, final boolean value) {
		int f = getRegister8(F);

		if (value) {
			setRegister8(F, f | flag.mask);
		} else {
			setRegister8(F, f & ~flag.mask);
		}
	}

}
