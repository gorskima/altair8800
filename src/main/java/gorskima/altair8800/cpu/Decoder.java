package gorskima.altair8800.cpu;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import gorskima.altair8800.Word;

public class Decoder {

	public enum RegisterType {
		r, dd, ss, qq
	};

	private final Map<RegisterType, Map<Integer, Register>> registerMap;
	private final Map<Integer, Flag> flagMap;

	public Decoder() {
		Map<Integer, Register> r = ImmutableMap.<Integer, Register>builder()
			.put(0, Register.B).put(1, Register.C).put(2, Register.D).put(3, Register.E)
			.put(4, Register.H).put(5, Register.L).put(7, Register.A)
			.build();

		Map<Integer, Register> dd = ImmutableMap.<Integer, Register>builder()
			.put(0, Register.BC).put(1, Register.DE).put(2, Register.HL).put(3, Register.SP)
			.build();

		Map<Integer, Register> qq = ImmutableMap.<Integer, Register>builder()
			.put(0, Register.BC).put(1, Register.DE).put(2, Register.HL).put(3, Register.AF)
			.build();

		registerMap = ImmutableMap.<Decoder.RegisterType, Map<Integer, Register>> builder()
			.put(RegisterType.r, r).put(RegisterType.dd, dd).put(RegisterType.ss, dd)
			.put(RegisterType.qq, qq).build();

		flagMap = ImmutableMap.<Integer, Flag> builder()
			.put(0, Flag.Z).put(1, Flag.C).put(2, Flag.P).put(3, Flag.S)
			.build();
	}

	public Register decodeRegister(final RegisterType type, final Word opCode) {
		if (type == RegisterType.r) {
			throw new IllegalArgumentException("Call decodeUpperR or decodeLowerR");
		}
		return decode(type, extractDoubleRegisterCode(opCode.toInt()));
	}

	public Register decodeUpperR(final Word opCode) {
		return decode(RegisterType.r, extractHigherRegisterCode(opCode.toInt()));
	}

	public Register decodeLowerR(final Word opCode) {
		return decode(RegisterType.r, opCode.toInt() & 0x07);
	}

	private Register decode(final RegisterType type, final int code) {
		return registerMap.get(type).get(code);
	}

	private int extractHigherRegisterCode(final int opCode) {
		return (opCode >> 3) & 0x07;
	}

	private int extractDoubleRegisterCode(final int opCode) {
		return (opCode >> 4) & 0x03;
	}

	public Condition decodeCondition(final Word opCode) {
		return new Condition(flagMap.get(extractFlagCode(opCode.toInt())), extractExpectedFlagValue(opCode.toInt()));
	}

	private int extractFlagCode(final int opCode) {
		return (opCode >> 4) & 0x03;
	}

	private boolean extractExpectedFlagValue(final int opCode) {
		return (opCode & 0x08) > 0;
	}

	public int decodePage(final Word opCode) {
		return opCode.toInt() & 0x38;
	}

}
