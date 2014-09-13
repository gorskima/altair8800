package gorskima.altair8800.cpu;

import static gorskima.altair8800.cpu.Flag.P;
import static gorskima.altair8800.cpu.Flag.S;
import static gorskima.altair8800.cpu.Flag.Z;
import static gorskima.altair8800.cpu.Register.A;
import static gorskima.altair8800.cpu.Register.BC;
import static gorskima.altair8800.cpu.Register.HL;
import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gorskima.altair8800.Word;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class ALUTest {

	private Registers reg = new Registers();
	private ALU alu = new ALU(reg);

	@Test
	@Parameters
	public void testAdd(final int op1, final int op2, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p, final boolean c) {
		
		reg.setRegister(A, op1);
		alu.add(op2);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestAdd() {
		return $(
			$(102, 38, 140, true, false, false, false, false));
	}

	@Test
	@Parameters
	public void testAdc(final int op1, final boolean carry, final int op2, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p, final boolean c) {
		
		reg.setRegister(A, op1);
		reg.setFlag(Flag.C, carry);
		alu.adc(op2);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestAdc() {
		return $(
			$(75, true, 200, 20, false, false, true, true, true));
	}

	@Test
	@Parameters
	public void testSub(final int op1, final int op2, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p, final boolean c) {
		
		reg.setRegister(A, op1);
		alu.sub(op2);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestSub() {
		return $(
			$(80, 95, 241, true, false, true, false, true));
	}

	@Test
	@Parameters
	public void testSbc(final int op1, final boolean carry, final int op2, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p, final boolean c) {
		
		reg.setRegister(A, op1);
		reg.setFlag(Flag.C, carry);
		alu.sbc(op2);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestSbc() {
		return $(
			$(200, true, 100, 99, false, false, false, true, false));
	}

	@Test
	@Parameters
	public void testInc(final int op, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p) {
		
		reg.setRegister(A, op);
		alu.inc(A);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		// TODO check that C is unaffected
	}
	
	private Object[] parametersForTestInc() {
		return $(
			$(255, 0, false, true, true, true));
	}

	@Test
	@Parameters
	public void testDec(final int op, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p) {
		
		reg.setRegister(A, op);
		alu.dec(A);
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		// TODO check that C is unaffected
	}

	private Object[] parametersForTestDec() {
		return $(
			$(0, 255, true, false, true, true));
	}
	
	@Test
	@Parameters
	public void testCp(final int op1, final int op2,
			final boolean s, final boolean z, final boolean h, final boolean p, final boolean c) {
		
		reg.setRegister(A, op1);
		alu.cp(op2);
		assertThat(reg.getRegister(A), is(op1)); // always
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestCp() {
		return $(
			$(7, 5, false, false, false, false, false),
			$(8, 8, false, true, false, true, false));
	}

	@Test
	@Parameters
	public void testAnd(final int op1, final int op2, final int result,
			final boolean s, final boolean z, final boolean p) {
		
		reg.setRegister(A, op1);
		alu.and(new Word(op2));
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(true)); // always
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(false)); // always
	}

	private Object[] parametersForTestAnd() {
		return $(
			$(70, 200, 64, false, false, false),
			$(0b00001111, 0b11001100, 0b00001100, false, false, true),
			$(0b11100000, 0b00010101, 0b00000000, false, true, true),
			$(0b11000011, 0b10001000, 0b10000000, true, false, false));
	}
	
	@Test
	@Parameters
	public void testOr(final int op1, final int op2, final int result,
			final boolean s, final boolean z, final boolean p) {
		
		reg.setRegister(A, op1);
		alu.or(new Word(op2));
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(false)); // always
	}
	
	private Object[] parametersForTestOr() {
		return $(
			$(70, 200, 206, true, false, false),
			$(0b11000000, 0b10010000, 0b11010000, true, false, false),
			$(0b00001111, 0b11110000, 0b11111111, true, false, true),
			$(0b00000000, 0b00000000, 0b00000000, false, true, true));
	}

	@Test
	@Parameters
	public void testXor(final int op1, final int op2, final int result,
			final boolean s, final boolean z, final boolean p) {
		
		reg.setRegister(A, op1);
		alu.xor(new Word(op2));
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(P), is(p));
		assertThat(reg.testFlag(Flag.C), is(false)); // always
	}
	
	private Object[] parametersForTestXor() {
		return $(
			$(70, 200, 142, true, false, true),
			$(0b00100100, 0b10111100, 0b10011000, true, false, false),
			$(0b00001111, 0b11110000, 0b11111111, true, false, true),
			$(0b00000000, 0b00000000, 0b00000000, false, true, true));
	}

	@Test
	@Parameters
	public void testCpl(final int op, final int result) {
		reg.setRegister(A, op);
		alu.cpl();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.H), is(true)); // always
		// TODO check that S, Z, P and C are unaffected
	}
	
	private Object[] parametersForTestCpl() {
		return $(
			$(3, 252),
			$(100, 155));
	}
	
	@Test
	@Parameters
	public void testAdd16(final int op1, final int op2, final int result,
			final boolean c, final boolean h) {
		
		reg.setRegister(HL, op1);
		alu.add16(op2);
		assertThat(reg.getRegister(HL), is(result));
		assertThat(reg.testFlag(Flag.C), is(c));
		assertThat(reg.testFlag(Flag.H), is(h));
		// TODO check that S, Z and P are unaffected
	}
	
	private Object[] parametersForTestAdd16() {
		return $(
			$(40000, 30000, 4464, true, true));
	}
	
	@Test
	@Parameters
	public void testIncExtern(final int op, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p) {
		
		int incremented = alu.incExtern(op);
		assertThat(incremented, is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		// TODO check that C is unaffected
	}
	
	private Object[] parametersForTestIncExtern() {
		return $(
			$(8, 9, false, false, false, true));
	}
	
	@Test
	@Parameters
	public void testDecExtern(final int op, final int result,
			final boolean s, final boolean z, final boolean h, final boolean p) {
		
		int decremented = alu.decExtern(op);
		assertThat(decremented, is(result));
		assertThat(reg.testFlag(S), is(s));
		assertThat(reg.testFlag(Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(P), is(p));
		// TODO check that C is unaffected
	}
	
	private Object[] parametersForTestDecExtern() {
		return $(
			$(128, 127, false, false, true, false));
	}
	
	@Test
	public void testInc16() {
		reg.setRegister(HL, 40000);
		alu.inc16(HL);
		assertThat(reg.getRegister(HL), is(40001));
	}
	
	@Test
	public void testDec16() {
		reg.setRegister(BC, 40000);
		alu.dec16(BC);
		assertThat(reg.getRegister(BC), is(39999));
	}
	
	@Test
	@Parameters
	public void testRlca(final int op, final int result, final boolean c) {
		reg.setRegister(A, op);
		alu.rlca();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(Flag.C), is(c));
		// TODO check that S, Z and P are unaffected
	}
	
	private Object[] parametersForTestRlca() {
		return $(
			$(0b10000001, 0b00000011, true),
			$(0b00000011, 0b00000110, false));
	}
	
	@Test
	@Parameters
	public void testRrca(final int op, final int result, final boolean c) {
		reg.setRegister(A, op);
		alu.rrca();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(Flag.C), is(c));
		// TODO check that S, Z and P are unaffected
	}
	
	private Object[] parametersForTestRrca() {
		return $(
			$(0b00000010, 0b00000001, false),
			$(0b00001101, 0b10000110, true));
	}
	
	@Test
	@Parameters
	public void testRla(final int op, final boolean carry, final int result, final boolean c) {
		reg.setRegister(A, op);
		reg.setFlag(Flag.C, carry);
		alu.rla();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(Flag.C), is(c));
		// TODO check that S, Z and P are unaffected
	}
	
	private Object[] parametersForTestRla() {
		return $(
			$(0b10000101, false, 0b00001010, true),
			$(0b00001010, true, 0b00010101, false));
	}
	
	@Test
	@Parameters
	public void testRra(final int op, final boolean carry, final int result, final boolean c) {
		reg.setRegister(A, op); 
		reg.setFlag(Flag.C, carry);
		alu.rra();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.H), is(false)); // always
		assertThat(reg.testFlag(Flag.C), is(c));
		// TODO check that S, Z and P are unaffected
	}
	
	private Object[] parametersForTestRra() {
		return $(
			$(0b00000110, true, 0b10000011, false),
			$(0b10000011, false, 0b01000001, true));
	}
	
	@Test
	@Parameters
	public void testDaa(final int op, final int result, boolean s, boolean z, boolean h, boolean p, boolean c) {
		reg.setRegister(A, op);
		alu.daa();
		assertThat(reg.getRegister(A), is(result));
		assertThat(reg.testFlag(Flag.S), is(s));
		assertThat(reg.testFlag(Flag.Z), is(z));
		assertThat(reg.testFlag(Flag.H), is(h));
		assertThat(reg.testFlag(Flag.P), is(p));
		assertThat(reg.testFlag(Flag.C), is(c));
	}
	
	private Object[] parametersForTestDaa() {
		return $(
			$(0b00000000, 0b00000000, false, true, false, true, false), // 0
			$(0b10000000, 0b10000000, true, false, false, false, false), // 80
			$(0b00010001, 0b00010001, false, false, false, true, false), // 11
			$(0b00001001, 0b00001001, false, false, false, true, false), // 9
			$(0b00001010, 0b00010000, false, false, true, false, false), // 10
			$(0b10011010, 0b00000000, false, true, true, true, true), // 100
			$(0b10110000, 0b00010000, false, false, false, false, true), // 110
			$(0b10111011, 0b00100001, false, false, true, true, true), // 121
			$(0b00111011, 0b01000001, false, false, true, true, false));// 41
	}

}
