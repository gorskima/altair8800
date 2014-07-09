package gorskima.i8080emu;

import gorskima.i8080emu.Decoder.RegisterType;

public class I8080 {
	
	private static final int IO_PORTS = 256;

	private final Registers registers;
	private final ALU alu;
	private final Decoder decoder = new Decoder();
	private final Memory memory;
	private final IOPort ioPorts[] = new IOPort[IO_PORTS];

	private boolean halt = false;
	
	private boolean interruptsEnabled = false; // INTE
	private boolean interruptAwaiting = false; // INT
	private boolean interruptAccepted = false; // INTA
	private int interruptOpCode;
    private int cycles;

    // TODO add new constructor without registers
	public I8080(final Registers registers, final Memory memory) {
		this.registers = registers;
		this.memory = memory;
		this.alu = new ALU(registers);
	}

	public void attachDevice(int portId, IOPort device) {
		if (portId >= IO_PORTS) {
			throw new IllegalArgumentException("IO port number must be smaller than " + IO_PORTS);
		}
		this.ioPorts[portId] = device;
	}
	
	public void step() {
		int opCode = fetchOpCode();
		cycles += executeSingleInstruction(opCode);
		checkInterrupts(opCode);
	}
	private int executeSingleInstruction(int opCode) {
		switch (opCode) {

		/*
		 * 8-bit load group
		 */

		// LD r,r'
		case 0x7F: // LD A,r
		case 0x78:
		case 0x79:
		case 0x7A:
		case 0x7B:
		case 0x7C:
		case 0x7D:
		case 0x47: // LD B,r
		case 0x40:
		case 0x41:
		case 0x42:
		case 0x43:
		case 0x44:
		case 0x45:
		case 0x4F: // LD C,r
		case 0x48:
		case 0x49:
		case 0x4A:
		case 0x4B:
		case 0x4C:
		case 0x4D:
		case 0x57: // LD D,r
		case 0x50:
		case 0x51:
		case 0x52:
		case 0x53:
		case 0x54:
		case 0x55:
		case 0x5F: // LD E,r
		case 0x58:
		case 0x59:
		case 0x5A:
		case 0x5B:
		case 0x5C:
		case 0x5D:
		case 0x67: // LD H,r
		case 0x60:
		case 0x61:
		case 0x62:
		case 0x63:
		case 0x64:
		case 0x65:
		case 0x6F: // LD L,r
		case 0x68:
		case 0x69:
		case 0x6A:
		case 0x6B:
		case 0x6C:
		case 0x6D: {
			Register destReg = decoder.decodeUpperR(opCode);
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			registers.setRegister(destReg, n);
			return 5;
		}

		// LD r,n
		case 0x3E:
		case 0x06:
		case 0x0E:
		case 0x16:
		case 0x1E:
		case 0x26:
		case 0x2E: {
			int n = fetchWord8();
			Register destReg = decoder.decodeUpperR(opCode);
			registers.setRegister(destReg, n);
			return 7;
		}

		// LD r,(HL)
		case 0x7E:
		case 0x46:
		case 0x4E:
		case 0x56:
		case 0x5E:
		case 0x66:
		case 0x6E: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			Register destReg = decoder.decodeUpperR(opCode);
			registers.setRegister(destReg, n);
			return 7;
		}

		// LD (HL),r
		case 0x70:
		case 0x71:
		case 0x72:
		case 0x73:
		case 0x74:
		case 0x75:
		case 0x77: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			int addr = registers.getRegister(Register.HL);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (HL),n
		case 0x36: {
			int n = fetchWord8();
			int addr = registers.getRegister(Register.HL);
			memory.writeWord8(addr, n);
			return 10;
		}

		// LD A,(BC)
		case 0x0A: {
			int addr = registers.getRegister(Register.BC);
			int n = memory.readWord8(addr);
			registers.setRegister(Register.A, n);
			return 7;
		}

		// LD A,(DE)
		case 0x1A: {
			int addr = registers.getRegister(Register.DE);
			int n = memory.readWord8(addr);
			registers.setRegister(Register.A, n);
			return 7;
		}

		// LD A,(nn)
		case 0x3A: {
			int addr = fetchWord16();
			int n = memory.readWord8(addr);
			registers.setRegister(Register.A, n);
			return 13;
		}

		// LD (BC),A
		case 0x02: {
			int addr = registers.getRegister(Register.BC);
			int n = registers.getRegister(Register.A);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (DE),A
		case 0x12: {
			int addr = registers.getRegister(Register.DE);
			int n = registers.getRegister(Register.A);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (nn),A
		case 0x32: {
			int addr = fetchWord16();
			int n = registers.getRegister(Register.A);
			memory.writeWord8(addr, n);
			return 13;
		}

		/*
		 * 16-bit load group
		 */

		// LD dd,nn
		case 0x01:
		case 0x11:
		case 0x21:
		case 0x31: {
			int nn = fetchWord16();
			Register destReg = decoder.decodeRegister(RegisterType.dd, opCode);
			registers.setRegister(destReg, nn);
			return 10;
		}

		// LD HL,(nn)
		case 0x2A: {
			int addr = fetchWord16();
			int nn = memory.readWord16(addr);
			registers.setRegister(Register.HL, nn);
			return 16;
		}

		// LD (nn),HL
		case 0x22: {
			int addr = fetchWord16();
			int nn = registers.getRegister(Register.HL);
			memory.writeWord16(addr, nn);
			return 16;
		}

		// LD SP,HL
		case 0xF9: {
			int nn = registers.getRegister(Register.HL);
			registers.setRegister(Register.SP, nn);
			return 5;
		}

		// PUSH qq
		case 0xC5:
		case 0xD5:
		case 0xE5:
		case 0xF5: {
			Register srcReg = decoder.decodeRegister(RegisterType.qq, opCode);
			pushOnStack(registers.getRegister(srcReg));
			return 11;
		}

		// POP qq
		case 0xC1:
		case 0xD1:
		case 0xE1:
		case 0xF1: {
			Register dstReg = decoder.decodeRegister(RegisterType.qq, opCode);
			registers.setRegister(dstReg, popFromStack());
			return 10;
		}
		
		/*
		 * Exchange, Block Transfer, and Search Group
		 */

		// EX DE,HL
		case 0xEB: {
			int de = registers.getRegister(Register.DE);
			int hl = registers.getRegister(Register.HL);
			registers.setRegister(Register.DE, hl);
			registers.setRegister(Register.HL, de);
			return 5;
		}

		// EX (SP),HL
		case 0xE3: {
			int hl = registers.getRegister(Register.HL);
			int sp = registers.getRegister(Register.SP);
			int nn = memory.readWord16(sp);
			registers.setRegister(Register.HL, nn);
			memory.writeWord16(sp, hl);
			return 18;
		}

		/*
		 * 8-bit arithmetic group
		 */

		// ADD A,r
		case 0x80:
		case 0x81:
		case 0x82:
		case 0x83:
		case 0x84:
		case 0x85:
		case 0x87: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.add(n);
			return 4;
		}

		// ADD A,n
		case 0xC6: {
			int n = fetchWord8();
			alu.add(n);
			return 7;
		}

		// ADD A,(HL)
		case 0x86: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.add(n);
			return 7;
		}

		// ADC A,r
		case 0x88:
		case 0x89:
		case 0x8A:
		case 0x8B:
		case 0x8C:
		case 0x8D:
		case 0x8F: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.adc(n);
			return 4;
		}

		// ADC A,n
		case 0xCE: {
			int n = fetchWord8();
			alu.adc(n);
			return 7;
		}

		// ADD A,(HL)
		case 0x8E: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.adc(n);
			return 7;
		}

		// SUB r
		case 0x90:
		case 0x91:
		case 0x92:
		case 0x93:
		case 0x94:
		case 0x95:
		case 0x97: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.sub(n);
			return 4;
		}

		// SUB n
		case 0xD6: {
			int n = fetchWord8();
			alu.sub(n);
			return 7;
		}

		// SUB (HL)
		case 0x96: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.sub(n);
			return 7;
		}

		// SBC A,r
		case 0x98:
		case 0x99:
		case 0x9A:
		case 0x9B:
		case 0x9C:
		case 0x9D:
		case 0x9F: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.sbc(n);
			return 4;
		}

		// SBC A,n
		case 0xDE: {
			int n = fetchWord8();
			alu.sbc(n);
			return 7;
		}

		// SBC A,(HL)
		case 0x9E: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.sbc(n);
			return 7;
		}

		// AND r
		case 0xA0:
		case 0xA1:
		case 0xA2:
		case 0xA3:
		case 0xA4:
		case 0xA5:
		case 0xA7: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.and(n);
			return 4;
		}

		// AND n
		case 0xE6: {
			int n = fetchWord8();
			alu.and(n);
			return 7;
		}

		// AND (HL)
		case 0xA6: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.and(n);
			return 7;
		}

		// OR r
		case 0xB0:
		case 0xB1:
		case 0xB2:
		case 0xB3:
		case 0xB4:
		case 0xB5:
		case 0xB7: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.or(n);
			return 4;
		}

		// OR n
		case 0xF6: {
			int n = fetchWord8();
			alu.or(n);
			return 7;
		}

		// OR (HL)
		case 0xB6: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.or(n);
			return 7;
		}

		// XOR r
		case 0xA8:
		case 0xA9:
		case 0xAA:
		case 0xAB:
		case 0xAC:
		case 0xAD:
		case 0xAF: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.xor(n);
			return 4;
		}

		// XOR n
		case 0xEE: {
			int n = fetchWord8();
			alu.xor(n);
			return 7;
		}

		// XOR (HL)
		case 0xAE: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.xor(n);
			return 7;
		}

		// CP r
		case 0xB8:
		case 0xB9:
		case 0xBA:
		case 0xBB:
		case 0xBC:
		case 0xBD:
		case 0xBF: {
			Register srcReg = decoder.decodeLowerR(opCode);
			int n = registers.getRegister(srcReg);
			alu.cp(n);
			return 4;
		}

		// CP n
		case 0xFE: {
			int n = fetchWord8();
			alu.cp(n);
			return 7;
		}

		// CP (HL)
		case 0xBE: {
			int addr = registers.getRegister(Register.HL);
			int n = memory.readWord8(addr);
			alu.cp(n);
			return 7;
		}

		// INC r
		case 0x04:
		case 0x0C:
		case 0x14:
		case 0x1C:
		case 0x24:
		case 0x2C:
		case 0x3C: {
			Register r = decoder.decodeUpperR(opCode);
			alu.inc(r);
			return 5;
		}

		// INC (HL)
		case 0x34: {
			int addr = registers.getRegister(Register.HL);
			int oldValue = memory.readWord8(addr);
			int newValue = alu.incExtern(oldValue);
			memory.writeWord8(addr, newValue);
			return 10;
		}

		// DEC r
		case 0x05:
		case 0x0D:
		case 0x15:
		case 0x1D:
		case 0x25:
		case 0x2D:
		case 0x3D: {
			Register r = decoder.decodeUpperR(opCode);
			alu.dec(r);
			return 5;
		}

		// DEC (HL)
		case 0x35: {
			int addr = registers.getRegister(Register.HL);
			int oldValue = memory.readWord8(addr);
			int newValue = alu.decExtern(oldValue);
			memory.writeWord8(addr, newValue);
			return 10;
		}

		/*
		 * General purpose arithmetic and CPU control
		 */

		// DAA
		case 0x27: {
			alu.daa();
			return 4;
		}

		// CPL
		case 0x2F: {
			alu.cpl();
			return 4;
		}

		// CCF
		case 0x3F: {
			boolean c = registers.testFlag(Flag.C);
			registers.setFlag(Flag.C, !c);
			registers.setFlag(Flag.H, c);
			return 4;
		}

		// SCF
		case 0x37: {
			registers.setFlag(Flag.C, true);
			registers.setFlag(Flag.H, false);
			return 4;
		}

		// NOP
		case 0x00: {
			// do nothing :)
			return 4;
		}

		// HALT
		case 0x76: {
			// TODO implement "unhalting" on int, reset etc.
			halt = true;
			return 7;
		}

		// DI
		case 0xF3: {
			interruptsEnabled = false;
			return 4;
		}

		// EI
		case 0xFB: {
			interruptsEnabled = true;
			return 4;
		}
		
		/*
		 * 16-Bit Arithmetic Group
		 */

		// ADD HL,ss
		case 0x09:
		case 0x19:
		case 0x29:
		case 0x39: {
			Register reg = decoder.decodeRegister(RegisterType.ss, opCode);
			int value = registers.getRegister(reg);
			alu.add16(value);
			return 10;
		}

		// INC ss
		case 0x03:
		case 0x13:
		case 0x23:
		case 0x33: {
			Register reg = decoder.decodeRegister(RegisterType.ss, opCode);
			alu.inc(reg);
			return 5;
		}

		// DEC ss
		case 0x0B:
		case 0x1B:
		case 0x2B:
		case 0x3B: {
			Register reg = decoder.decodeRegister(RegisterType.ss, opCode);
			alu.dec(reg);
			return 5;
		}
		
		/*
		 * Rotate and Shift Grouop
		 */

		// RLCA
		case 0x07: {
			alu.rlca();
			return 4;
		}

		// RLA
		case 0x17: {
			alu.rla();
			return 4;
		}

		// RRCA
		case 0x0F: {
			alu.rrca();
			return 4;
		}

		// RRA
		case 0x1F: {
			alu.rra();
			return 4;
		}
		
		/*
		 * Jump group
		 */

		// JP nn
		case 0xC3: {
			int nn = fetchWord16();
			registers.setRegister(Register.PC, nn);
			return 10;
		}

		// JP cc,nn
		case 0xC2:
		case 0xCA:
		case 0xD2:
		case 0xDA:
		case 0xE2:
		case 0xEA:
		case 0xF2:
		case 0xFA: {
			int nn = fetchWord16();
			Condition cond = decoder.decodeCondition(opCode);
			if (isConditionMet(cond)) {
				registers.setRegister(Register.PC, nn);
			}
			return 10;
		}

		// JP (HL)
		case 0xE9: {
			int addr = registers.getRegister(Register.HL);
			registers.setRegister(Register.PC, addr);
			return 5;
		}
		
		/*
		 * Call and return group
		 */

		// CALL nn
		case 0xCD: {
			int addr = fetchWord16();
			pushOnStack(registers.getRegister(Register.PC));
			registers.setRegister(Register.PC, addr);
			return 17;
		}

		// CALL cc,nn
		case 0xC4:
		case 0xCC:
		case 0xD4:
		case 0xDC:
		case 0xE4:
		case 0xEC:
		case 0xF4:
		case 0xFC: {
			int addr = fetchWord16();
			Condition condition = decoder.decodeCondition(opCode);
			if (isConditionMet(condition)) {
				pushOnStack(registers.getRegister(Register.PC));
				registers.setRegister(Register.PC, addr);
                return 17;
			}
			return 11;
		}

		// RET
		case 0xC9: {
			registers.setRegister(Register.PC, popFromStack());
			return 10;
		}

		// RET cc
		case 0xC0:
		case 0xC8:
		case 0xD0:
		case 0xD8:
		case 0xE0:
		case 0xE8:
		case 0xF0:
		case 0xF8: {
			Condition condition = decoder.decodeCondition(opCode);
			if (isConditionMet(condition)) {
				registers.setRegister(Register.PC, popFromStack());
                return 11;
			}
            return 5;
		}

		// RST p
		case 0xC7:
		case 0xCF:
		case 0xD7:
		case 0xDF:
		case 0xE7:
		case 0xEF:
		case 0xF7:
		case 0xFF: {
			int addr = decoder.decodePage(opCode);
			pushOnStack(registers.getRegister(Register.PC));
			registers.setRegister(Register.PC, addr);
			return 11;
		}

		/*
		 * Input and output group
		 */

		// IN A,(n)
		case 0xDB: {
			int portId = fetchWord8();
			int n = ioPorts[portId] != null ? ioPorts[portId].read() : 0;
			registers.setRegister(Register.A, n);
			return 10;
		}

		// OUT (n),A
		case 0xD3: {
			int portId = fetchWord8();
			int n = registers.getRegister(Register.A);
			ioPorts[portId].write(n);
			return 10;
		}

		default:
			throw new IllegalArgumentException(String.format("OpCode 0x%x not supported", opCode));
		}
	}

	private void checkInterrupts(int opCode) {
		if (interruptsEnabled && interruptAwaiting && !isEIorDI(opCode)) {
			interruptsEnabled = false;
			interruptAwaiting = false;
			interruptAccepted = true;
		} else if (interruptAccepted) {
			interruptAccepted = false;
		}
	}
	private boolean isEIorDI(int opCode) {
		return opCode == 0xFB || opCode == 0xF3;
	}

	private int fetchOpCode() {
		return interruptAccepted ? interruptOpCode : fetchWord8();
	}

	private int fetchWord8() {
		int pc = registers.getRegister(Register.PC);
		int word = memory.readWord8(pc);
		registers.incPC();
		return word;
	}

	private int fetchWord16() {
		int pc = registers.getRegister(Register.PC);
		int word = memory.readWord16(pc);
		registers.incPC();
		registers.incPC();
		return word;
	}

	private boolean isConditionMet(final Condition cond) {
		return registers.testFlag(cond.getFlag()) == cond.getExpectedValue();
	}

	private void pushOnStack(int nn) {
		int sp = registers.getRegister(Register.SP);
		registers.setRegister(Register.SP, sp - 2);
		memory.writeWord16(sp - 2, nn);
	}

	private int popFromStack() {
		int sp = registers.getRegister(Register.SP);
		registers.setRegister(Register.SP, sp + 2);
		return memory.readWord16(sp);
	}

	public Memory getMemory() {
		return memory;
	}

	public Registers getRegisters() {
		return registers;
	}
	
	public boolean isInterruptsEnabled() {
		return interruptsEnabled;
	}

	public boolean isHalt() {
		return halt;
	}

	public void interrupt(int opCode) {
		interruptOpCode = opCode;
		interruptAwaiting = true;
	}

    public int getCycles() {
        return cycles;
    }
}
