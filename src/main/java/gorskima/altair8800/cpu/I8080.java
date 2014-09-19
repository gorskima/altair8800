package gorskima.altair8800.cpu;

import gorskima.altair8800.DoubleWord;
import gorskima.altair8800.Word;
import gorskima.altair8800.cpu.Decoder.RegisterType;

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

    // At 2 MHz it will overflow in 146 years, so let's ignore it for now ;)
    private long cycles;

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
			Word n = registers.getRegister8(srcReg);
			registers.setRegister8(destReg, n);
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
			Word n = fetchWord8();
			Register destReg = decoder.decodeUpperR(opCode);
			registers.setRegister8(destReg, n);
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
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
			Register destReg = decoder.decodeUpperR(opCode);
			registers.setRegister8(destReg, n);
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
			Word n = registers.getRegister8(srcReg);
			DoubleWord addr = registers.getRegister16(Register.HL);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (HL),n
		case 0x36: {
			Word n = fetchWord8();
			DoubleWord addr = registers.getRegister16(Register.HL);
			memory.writeWord8(addr, n);
			return 10;
		}

		// LD A,(BC)
		case 0x0A: {
			DoubleWord addr = registers.getRegister16(Register.BC);
			Word n = memory.readWord8(addr);
			registers.setRegister8(Register.A, n);
			return 7;
		}

		// LD A,(DE)
		case 0x1A: {
			DoubleWord addr = registers.getRegister16(Register.DE);
			Word n = memory.readWord8(addr);
			registers.setRegister8(Register.A, n);
			return 7;
		}

		// LD A,(nn)
		case 0x3A: {
			DoubleWord addr = fetchWord16();
			Word n = memory.readWord8(addr);
			registers.setRegister8(Register.A, n);
			return 13;
		}

		// LD (BC),A
		case 0x02: {
			DoubleWord addr = registers.getRegister16(Register.BC);
			Word n = registers.getRegister8(Register.A);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (DE),A
		case 0x12: {
			DoubleWord addr = registers.getRegister16(Register.DE);
			Word n = registers.getRegister8(Register.A);
			memory.writeWord8(addr, n);
			return 7;
		}

		// LD (nn),A
		case 0x32: {
			DoubleWord addr = fetchWord16();
			Word n = registers.getRegister8(Register.A);
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
			DoubleWord nn = fetchWord16();
			Register destReg = decoder.decodeRegister(RegisterType.dd, opCode);
			registers.setRegister16(destReg, nn);
			return 10;
		}

		// LD HL,(nn)
		case 0x2A: {
			DoubleWord addr = fetchWord16();
			DoubleWord nn = memory.readWord16(addr);
			registers.setRegister16(Register.HL, nn);
			return 16;
		}

		// LD (nn),HL
		case 0x22: {
			DoubleWord addr = fetchWord16();
			DoubleWord nn = registers.getRegister16(Register.HL);
			memory.writeWord16(addr, nn);
			return 16;
		}

		// LD SP,HL
		case 0xF9: {
			DoubleWord nn = registers.getRegister16(Register.HL);
			registers.setRegister16(Register.SP, nn);
			return 5;
		}

		// PUSH qq
		case 0xC5:
		case 0xD5:
		case 0xE5:
		case 0xF5: {
			Register srcReg = decoder.decodeRegister(RegisterType.qq, opCode);
			pushOnStack(registers.getRegister16(srcReg));
			return 11;
		}

		// POP qq
		case 0xC1:
		case 0xD1:
		case 0xE1:
		case 0xF1: {
			Register dstReg = decoder.decodeRegister(RegisterType.qq, opCode);
			registers.setRegister16(dstReg, popFromStack());
			return 10;
		}
		
		/*
		 * Exchange, Block Transfer, and Search Group
		 */

		// EX DE,HL
		case 0xEB: {
			DoubleWord de = registers.getRegister16(Register.DE);
			DoubleWord hl = registers.getRegister16(Register.HL);
			registers.setRegister16(Register.DE, hl);
			registers.setRegister16(Register.HL, de);
			return 5;
		}

		// EX (SP),HL
		case 0xE3: {
			DoubleWord hl = registers.getRegister16(Register.HL);
			DoubleWord sp = registers.getRegister16(Register.SP);
			DoubleWord nn = memory.readWord16(sp);
			registers.setRegister16(Register.HL, nn);
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
			Word n = registers.getRegister8(srcReg);
			alu.add(n);
			return 4;
		}

		// ADD A,n
		case 0xC6: {
			Word n = fetchWord8();
			alu.add(n);
			return 7;
		}

		// ADD A,(HL)
		case 0x86: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.adc(n);
			return 4;
		}

		// ADC A,n
		case 0xCE: {
			Word n = fetchWord8();
			alu.adc(n);
			return 7;
		}

		// ADD A,(HL)
		case 0x8E: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.sub(n);
			return 4;
		}

		// SUB n
		case 0xD6: {
			Word n = fetchWord8();
			alu.sub(n);
			return 7;
		}

		// SUB (HL)
		case 0x96: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.sbc(n);
			return 4;
		}

		// SBC A,n
		case 0xDE: {
			Word n = fetchWord8();
			alu.sbc(n);
			return 7;
		}

		// SBC A,(HL)
		case 0x9E: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.and(n);
			return 4;
		}

		// AND n
		case 0xE6: {
			Word n = fetchWord8();
			alu.and(n);
			return 7;
		}

		// AND (HL)
		case 0xA6: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.or(n);
			return 4;
		}

		// OR n
		case 0xF6: {
			Word n = fetchWord8();
			alu.or(n);
			return 7;
		}

		// OR (HL)
		case 0xB6: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.xor(n);
			return 4;
		}

		// XOR n
		case 0xEE: {
			Word n = fetchWord8();
			alu.xor(n);
			return 7;
		}

		// XOR (HL)
		case 0xAE: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			Word n = registers.getRegister8(srcReg);
			alu.cp(n);
			return 4;
		}

		// CP n
		case 0xFE: {
			Word n = fetchWord8();
			alu.cp(n);
			return 7;
		}

		// CP (HL)
		case 0xBE: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word n = memory.readWord8(addr);
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
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word oldValue = memory.readWord8(addr);
			Word newValue = alu.incExtern(oldValue);
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
			DoubleWord addr = registers.getRegister16(Register.HL);
			Word oldValue = memory.readWord8(addr);
			Word newValue = alu.decExtern(oldValue);
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
		case 0x00:
		// unofficial opCodes:
		case 0x08:
		case 0x10:
		case 0x18:
		case 0x20:
		case 0x28:
		case 0x30:
		case 0x38: {
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
			DoubleWord value = registers.getRegister16(reg);
			alu.add16(value);
			return 10;
		}

		// INC ss
		case 0x03:
		case 0x13:
		case 0x23:
		case 0x33: {
			Register reg = decoder.decodeRegister(RegisterType.ss, opCode);
			alu.inc16(reg);
			return 5;
		}

		// DEC ss
		case 0x0B:
		case 0x1B:
		case 0x2B:
		case 0x3B: {
			Register reg = decoder.decodeRegister(RegisterType.ss, opCode);
			alu.dec16(reg);
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
		case 0xC3:
		// unofficial opCodes:
		case 0xCB: {
			DoubleWord nn = fetchWord16();
			registers.setRegister16(Register.PC, nn);
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
			DoubleWord nn = fetchWord16();
			Condition cond = decoder.decodeCondition(opCode);
			if (isConditionMet(cond)) {
				registers.setRegister16(Register.PC, nn);
			}
			return 10;
		}

		// JP (HL)
		case 0xE9: {
			DoubleWord addr = registers.getRegister16(Register.HL);
			registers.setRegister16(Register.PC, addr);
			return 5;
		}
		
		/*
		 * Call and return group
		 */

		// CALL nn
		case 0xCD:
		// unofficial opCodes:
		case 0xDD:
		case 0xED:
		case 0xFD: {
			DoubleWord addr = fetchWord16();
			pushOnStack(registers.getRegister16(Register.PC));
			registers.setRegister16(Register.PC, addr);
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
			DoubleWord addr = fetchWord16();
			Condition condition = decoder.decodeCondition(opCode);
			if (isConditionMet(condition)) {
				pushOnStack(registers.getRegister16(Register.PC));
				registers.setRegister16(Register.PC, addr);
                return 17;
			}
			return 11;
		}

		// RET
		case 0xC9:
		// unofficial opCodes:
		case 0xD9: {
			registers.setRegister16(Register.PC, popFromStack());
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
				registers.setRegister16(Register.PC, popFromStack());
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
			pushOnStack(registers.getRegister16(Register.PC));
			registers.setRegister16(Register.PC, new DoubleWord(addr));
			return 11;
		}

		/*
		 * Input and output group
		 */

		// IN A,(n)
		case 0xDB: {
			Word portId = fetchWord8();
			// TODO clean up
			int n = ioPorts[portId.toInt()] != null ? ioPorts[portId.toInt()].read() : 0;
			registers.setRegister8(Register.A, new Word(n));
			return 10;
		}

		// OUT (n),A
		case 0xD3: {
			Word portId = fetchWord8();
			Word n = registers.getRegister8(Register.A);
			ioPorts[portId.toInt()].write(n.toInt());
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

	// TODO clean up
	private int fetchOpCode() {
		return interruptAccepted ? interruptOpCode : fetchWord8().toInt();
	}

	private Word fetchWord8() {
		DoubleWord pc = registers.getRegister16(Register.PC);
		Word word = memory.readWord8(pc);
		registers.incPC();
		return word;
	}

	private DoubleWord fetchWord16() {
		DoubleWord pc = registers.getRegister16(Register.PC);
		DoubleWord word = memory.readWord16(pc);
		registers.incPC();
		registers.incPC();
		return word;
	}

	private boolean isConditionMet(final Condition cond) {
		return registers.testFlag(cond.getFlag()) == cond.getExpectedValue();
	}

	private void pushOnStack(DoubleWord nn) {
		DoubleWord sp = registers.getRegister16(Register.SP);
		// TODO clean up
		registers.setRegister16(Register.SP, new DoubleWord(sp.toInt() - 2));
		memory.writeWord16(new DoubleWord(sp.toInt() - 2), nn);
	}

	private DoubleWord popFromStack() {
		DoubleWord sp = registers.getRegister16(Register.SP);
		registers.setRegister16(Register.SP, new DoubleWord(sp.toInt() + 2));
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

    public long getCycles() {
        return cycles;
    }
}
