package gorskima.altair8800.cpu;

import gorskima.altair8800.DoubleWord;
import gorskima.altair8800.Word;

public class ALU {

	private final Registers registers;

	public ALU(final Registers registers) {
		this.registers = registers;
	}

	public ALU() {
		this(new Registers());
	}

	public void add(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);

		Adder adder = Adder.newAdder8();
		int result = adder.add(op1.toInt(), op2.toInt(), 0);
		registers.setRegister8(Register.A, new Word(result));

		setAdditionFlags(adder, result);
	}

	public void adc(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);
		int carry = getCarry();

		Adder adder = Adder.newAdder8();
		int result = adder.add(op1.toInt(), op2.toInt(), carry);
		registers.setRegister8(Register.A, new Word(result));

		setAdditionFlags(adder, result);
	}

	public void sub(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);

		Adder adder = Adder.newAdder8();
		int result = adder.sub(op1.toInt(), op2.toInt(), 0);
		registers.setRegister8(Register.A, new Word(result));

		setSubstractionFlags(adder, result);
	}

	public void sbc(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);
		int carry = getCarry();

		Adder adder = Adder.newAdder8();
		int result = adder.sub(op1.toInt(), op2.toInt(), carry);
		registers.setRegister8(Register.A, new Word(result));

		setSubstractionFlags(adder, result);
	}

	public void cp(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);

		Adder adder = Adder.newAdder8();
		int result = adder.sub(op1.toInt(), op2.toInt(), 0);

		setSubstractionFlags(adder, result);
	}

	private void setAdditionFlags(final Adder adder, final int result) {
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.H, adder.isHalfCarry());
		registers.setFlag(Flag.P, getParity(result));
		registers.setFlag(Flag.C, adder.isCarry());
	}

	private void setSubstractionFlags(final Adder adder, final int result) {
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.H, adder.isHalfBorrow());
		registers.setFlag(Flag.P, getParity(result));
		registers.setFlag(Flag.C, adder.isBorrow());
	}

	private void setIncrementFlags(final Adder adder, final int result) {
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.H, adder.isHalfCarry());
		registers.setFlag(Flag.P, getParity(result));
	}

	private void setDecrementFlags(final Adder adder, final int result) {
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.H, adder.isHalfBorrow());
		registers.setFlag(Flag.P, getParity(result));
	}

    public void and(final Word op2) {
        Word op1 = registers.getRegister8(Register.A);
        Word result = op1.and(op2);
        registers.setRegister8(Register.A, result);

        setLogicalFlags(result.toInt());
        registers.setFlag(Flag.H, true);
    }

    public void or(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);
		Word result = op1.or(op2);
		registers.setRegister8(Register.A, result);

		setLogicalFlags(result.toInt());
		registers.setFlag(Flag.H, false);
	}

	public void xor(final Word op2) {
		Word op1 = registers.getRegister8(Register.A);
		Word result = op1.xor(op2);
		registers.setRegister8(Register.A, result);

		setLogicalFlags(result.toInt());
		registers.setFlag(Flag.H, false);
	}

	private void setLogicalFlags(final int result) {
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.P, getParity(result));
		registers.setFlag(Flag.C, false);
	}

	// TODO not sure if CPL belongs to ALU, let's leave it here for now
	public void cpl() {
		Word op = registers.getRegister8(Register.A);
		registers.setRegister8(Register.A, op.invert());

		registers.setFlag(Flag.H, true);
	}

	private boolean getSign8(final int op) {
		return ((op >> 7) & 0x01) == 1;
	}

	private boolean isZero(final int op) {
		return op == 0;
	}

	private boolean getParity(int op) {
		boolean parity = true;
		while (op != 0) {
			parity = !parity;
			op = op & (op - 1);
		}
		return parity;
	}

	public void add16(final DoubleWord op2) {
		DoubleWord op1 = registers.getRegister16(Register.HL);
		Adder adder = Adder.newAdder16();
		int result = adder.add(op1.toInt(), op2.toInt(), 0);
		registers.setRegister16(Register.HL, new DoubleWord(result));

		registers.setFlag(Flag.C, adder.isCarry());
		registers.setFlag(Flag.H, adder.isHalfCarry());
	}
	
	// TODO temp. solution; move it out of ALU or combine with existing inc()
	public int incExtern(final int op1) {
		Adder adder = Adder.newAdder8();
		int result = adder.add(op1, 1, 0);
		setIncrementFlags(adder, result);
		return result;
	}

	// TODO temp. solution; move it out of ALU or combine with existing dec()
	public int decExtern(final int op1) {
		Adder adder = Adder.newAdder8();
		int result = adder.sub(op1, 1, 0);
		setDecrementFlags(adder, result);
		return result;
	}
	
	public void inc(final Register r) {
		Word op1 = registers.getRegister8(r);
		
		Adder adder = createAdderForRegister(r);
		int result = adder.add(op1.toInt(), 1, 0);
		registers.setRegister8(r, new Word(result));
		
		if (r.size == 1) {
			setIncrementFlags(adder, result);
		}
	}

	public void inc16(final Register r) {
		DoubleWord op1 = registers.getRegister16(r);

		Adder adder = createAdderForRegister(r);
		int result = adder.add(op1.toInt(), 1, 0);
		registers.setRegister16(r, new DoubleWord(result));
	}

	private Adder createAdderForRegister(Register r) {
		return r.size == 1 ? Adder.newAdder8() : Adder.newAdder16();
	}

	public void dec(final Register r) {
		Word op1 = registers.getRegister8(r);

		Adder adder = createAdderForRegister(r);
		int result = adder.sub(op1.toInt(), 1, 0);
		registers.setRegister8(r, new Word(result));

		if (r.size == 1) {
			setDecrementFlags(adder, result);
		}
	}

	public void dec16(final Register r) {
		DoubleWord op1 = registers.getRegister16(r);

		Adder adder = createAdderForRegister(r);
		int result = adder.sub(op1.toInt(), 1, 0);
		registers.setRegister16(r, new DoubleWord(result));
	}

	public void rlca() {
		int op = registers.getRegister8(Register.A).toInt();
		int result = ((op << 1) | (op >>> 7)) & 0xFF;
		registers.setRegister8(Register.A, new Word(result));
		registers.setFlag(Flag.C, ((op >>> 7) & 0x01) == 1);
		setCommonRotationFlags();
	}

	public void rrca() {
		int op = registers.getRegister8(Register.A).toInt();
		int result = ((op >>> 1) | (op << 7)) & 0xFF;
		registers.setRegister8(Register.A, new Word(result));
		registers.setFlag(Flag.C, (op & 0x01) == 1);
		setCommonRotationFlags();
	}

	public void rla() {
		int op = registers.getRegister8(Register.A).toInt();
		int c = getCarry();
		int result = ((op << 1) | c) & 0xFF;
		registers.setRegister8(Register.A, new Word(result));
		registers.setFlag(Flag.C, ((op >>> 7) & 0x01) == 1);
		setCommonRotationFlags();
	}

	public void rra() {
		int op = registers.getRegister8(Register.A).toInt();
		int c = getCarry();
		int result = (op >> 1) | (c << 7);
		registers.setRegister8(Register.A, new Word(result));
		registers.setFlag(Flag.C, (op & 0x01) == 1);
		setCommonRotationFlags();
	}

	private void setCommonRotationFlags() {
		registers.setFlag(Flag.H, false);
	}
	
	private int getCarry() {
		return registers.testFlag(Flag.C) ? 1 : 0;
	}

	public void daa() {
		// TODO it works, now make it pretty
		int result = registers.getRegister8(Register.A).toInt();
		if ((result & 0x0F) > 9) {
			Adder adder = Adder.newAdder8();
			result = adder.add(result, 0x06, 0);
			registers.setFlag(Flag.H, adder.isHalfCarry());
		}
		if (((result >>> 4) & 0x0F) > 9) {
			Adder adder = Adder.newAdder8();
			result = adder.add(result, 0x60, 0);
			registers.setFlag(Flag.C, adder.isCarry());
		}
		
		registers.setRegister8(Register.A, new Word(result));
		registers.setFlag(Flag.S, getSign8(result));
		registers.setFlag(Flag.Z, isZero(result));
		registers.setFlag(Flag.P, getParity(result));
	}

}
