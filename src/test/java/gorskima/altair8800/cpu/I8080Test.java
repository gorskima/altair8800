package gorskima.altair8800.cpu;

import static gorskima.altair8800.cpu.Flag.P;
import static gorskima.altair8800.cpu.Flag.Z;
import static gorskima.altair8800.cpu.Register.A;
import static gorskima.altair8800.cpu.Register.B;
import static gorskima.altair8800.cpu.Register.BC;
import static gorskima.altair8800.cpu.Register.C;
import static gorskima.altair8800.cpu.Register.D;
import static gorskima.altair8800.cpu.Register.DE;
import static gorskima.altair8800.cpu.Register.E;
import static gorskima.altair8800.cpu.Register.HL;
import static gorskima.altair8800.cpu.Register.L;
import static gorskima.altair8800.cpu.Register.PC;
import static gorskima.altair8800.cpu.Register.SP;
import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import gorskima.altair8800.Word;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class I8080Test {

	private Registers reg = new Registers();
	private Memory mem = new Memory();
	private I8080 cpu = new I8080(reg, mem);

	@Test(expected = IllegalArgumentException.class)
	public void testAttachingDeviceWithTooBigPortId() {
		cpu.attachDevice(256, mock(IOPort.class));
	}

	@Test
	public void test_LD_r_r() {
		reg.setRegister(B, 58);
		mem.writeWord8(0, 0x78); // LD A,B

		cpu.step();

        assertThat(reg.getRegister(A), is(58));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_LD_r_n() {
		mem.writeWord8(0, 0x06); // LD B,123
		mem.writeWord8(1, 123);

        cpu.step();

        assertThat(reg.getRegister(B), is(123));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_r_HL() {
		reg.setRegister(HL, 350);
		mem.writeWord8(0, 0x4E); // LD C,(HL)
		mem.writeWord8(350, 100);

        cpu.step();

        assertThat(reg.getRegister(C), is(100));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_HL_r() {
		reg.setRegister(D, 9);
		reg.setRegister(HL, 105);
		mem.writeWord8(0, 0x72); // LD (HL),D

        cpu.step();

		assertThat(mem.readWord8(105), is(9));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_HL_n() {
		reg.setRegister(HL, 200);
		mem.writeWord8(0, 0x36); // LD (HL),55
		mem.writeWord8(1, 55);
		cpu.step();
		assertThat(mem.readWord8(200), is(55));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_LD_A_BC() {
		reg.setRegister(BC, 2000);
		mem.writeWord16(0, 0x0A); // LD A,(BC)
		mem.writeWord8(2000, 33);

		cpu.step();

        assertThat(reg.getRegister(A), is(33));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_A_DE() {
		reg.setRegister(DE, 5000);
		mem.writeWord16(0, 0x1A); // LD A,(DE)
		mem.writeWord8(5000, 123);

        cpu.step();

		assertThat(reg.getRegister(A), is(123));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_A_nn() {
		mem.writeWord8(0, 0x3A); // LD A,(25000)
		mem.writeWord16(1, 25000);
		mem.writeWord8(25000, 7);

        cpu.step();

        assertThat(reg.getRegister(A), is(7));
        assertThat(cpu.getCycles(), is(13L));
	}

	@Test
	public void test_LD_BC_A() {
		reg.setRegister(A, 59);
		reg.setRegister(BC, 13300);
		mem.writeWord8(0, 0x02); // LD (BC),A

        cpu.step();

		assertThat(mem.readWord8(13300), is(59));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_DE_A() {
		reg.setRegister(A, 17);
		reg.setRegister(DE, 5000);
		mem.writeWord8(0, 0x12); // LD (DE),A

        cpu.step();

		assertThat(mem.readWord8(5000), is(17));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_LD_nn_A() {
		reg.setRegister(A, 15);
		mem.writeWord8(0, 0x32); // LD (43000),A
		mem.writeWord16(1, 43000);

        cpu.step();

        assertThat(mem.readWord8(43000), is(15));
        assertThat(cpu.getCycles(), is(13L));
	}

	@Test
	public void test_LD_dd_nn() {
		mem.writeWord8(0, 0x11); // LD DE,12345
		mem.writeWord16(1, 12345);

        cpu.step();

        assertThat(reg.getRegister(DE), is(12345));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_LD_HL_nn() {
		mem.writeWord8(0, 0x2A); // LD HL,(nn)
		mem.writeWord16(1, 0x5577);
		mem.writeWord16(0x5577, 0x1234);

		cpu.step();

        assertThat(reg.getRegister(HL), is(0x1234));
        assertThat(cpu.getCycles(), is(16L));
	}

	@Test
	public void test_LD_nn_HL() {
		reg.setRegister(HL, 0x1520);
		mem.writeWord8(0, 0x22); // LD (nn),HL
		mem.writeWord16(1, 0x0040);

		cpu.step();

        assertThat(mem.readWord16(0x0040), is(0x1520));
        assertThat(cpu.getCycles(), is(16L));
	}

	@Test
	public void test_LD_SP_HL() {
		reg.setRegister(HL, 25000);
		mem.writeWord8(0, 0xF9); // LD SP,HL

		cpu.step();

        assertThat(reg.getRegister(SP), is(25000));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_PUSH_qq() {
		reg.setRegister(SP, 0xFFFF);
		reg.setRegister(BC, 0x2277);
		mem.writeWord8(0, 0xC5); // PUSH BC

        cpu.step();

        assertThat(reg.getRegister(SP), is(0xFFFD));
		assertThat(mem.readWord8(0xFFFD), is(0x77));
		assertThat(mem.readWord8(0xFFFE), is(0x22));
        assertThat(cpu.getCycles(), is(11L));
	}

	@Test
	public void test_POP_qq() {
		reg.setRegister(SP, 0xFFF0);
		mem.writeWord8(0, 0xE1); // PUSH HL
		mem.writeWord8(0xFFF0, 0x34);
		mem.writeWord8(0xFFF1, 0x12);

        cpu.step();

		assertThat(reg.getRegister(HL), is(0x1234));
		assertThat(reg.getRegister(SP), is(0xFFF2));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_ADD_HL_ss() {
		reg.setRegister(HL, 10000);
		mem.writeWord16(0, 0x29); // ADD HL,HL

		cpu.step();

        assertThat(reg.getRegister(HL), is(20000));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_ADC_A_r() {
		reg.setRegister(A, 50);
		reg.setRegister(E, 20);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0x8B); // ADC A,E

        cpu.step();

        assertThat(reg.getRegister(A), is(71));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_ADC_A_HL() {
		reg.setRegister(A, 200);
		reg.setRegister(HL, 1000);
		reg.setFlag(Flag.C, true);
		mem.writeWord16(0, 0x8E); // ADD A,(HL)
		mem.writeWord8(1000, 54);

        cpu.step();

        assertThat(reg.getRegister(A), is(255));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_SUB_HL() {
		reg.setRegister(A, 100);
		reg.setRegister(HL, 500);
		mem.writeWord8(0, 0x96); // SUB (HL)
		mem.writeWord8(500, 77);

		cpu.step();

        assertThat(reg.getRegister(A), is(23));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_SBC_A_r() {
		reg.setRegister(A, 51);
		reg.setRegister(C, 20);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0x99); // SBC A,C

		cpu.step();

        assertThat(reg.getRegister(A), is(30));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_SBC_A_HL() {
		reg.setRegister(A, 75);
		reg.setRegister(HL, 35000);
		reg.setFlag(Flag.C, true);
		mem.writeWord16(0, 0x9E); // SBC A,(HL)
		mem.writeWord8(35000, 70);

        cpu.step();

        assertThat(reg.getRegister(A), is(4));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_AND_r() {
		reg.setRegister(A, 0x83);
		reg.setRegister(D, 0x05);
		mem.writeWord8(0, 0xA2); // AND D

		cpu.step();

        assertThat(reg.getRegister(A), is(0x01));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_AND_HL() {
		reg.setRegister(A, 0xF0);
		reg.setRegister(HL, 300);
		mem.writeWord8(0, 0xA6); // AND (HL)
		mem.writeWord8(300, 0x33);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x30));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_OR_HL() {
		reg.setRegister(A, 0x70);
		reg.setRegister(HL, 500);
		mem.writeWord8(0, 0xB6); // OR (HL)
		mem.writeWord8(500, 0x05);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x75));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_XOR_r() {
		reg.setRegister(A, 0xFF);
		reg.setRegister(B, 0x0F);
		mem.writeWord8(0, 0xA8); // XOR B

        cpu.step();

        assertThat(reg.getRegister(A), is(0xF0));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_XOR_HL() {
		reg.setRegister(A, 0x07);
		reg.setRegister(HL, 200);
		mem.writeWord8(0, 0xAE); // XOR (HL)
		mem.writeWord8(200, 0x09);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x0E));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_CP_r() {
		reg.setRegister(A, 125);
		reg.setRegister(C, 125);
		mem.writeWord8(0, 0xB9); // CP C

        cpu.step();

        assertThat(reg.testFlag(Flag.Z), is(true));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_CP_HL() {
		reg.setRegister(A, 67);
		reg.setRegister(HL, 790);
		mem.writeWord8(0, 0xBE); // CP (HL)
		mem.writeWord8(790, 67);

        cpu.step();

        assertThat(reg.testFlag(Flag.Z), is(true));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_INC_HL() {
		reg.setRegister(HL, 1000);
		mem.writeWord8(0, 0x34); // INC (HL)
		mem.writeWord8(1000, 23);

        cpu.step();

        assertThat(mem.readWord8(1000), is(24));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_DEC_HL() {
		reg.setRegister(HL, 1000);
		mem.writeWord8(0, 0x35); // DEC (HL)
		mem.writeWord8(1000, 23);

        cpu.step();

        assertThat(mem.readWord8(1000), is(22));
        assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_CCF() {
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0x3F); // CCF

        cpu.step();

        assertThat(reg.testFlag(Flag.C), is(false));
		assertThat(reg.testFlag(Flag.H), is(true));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_SCF() {
		reg.setFlag(Flag.C, false);
		mem.writeWord8(0, 0x37); // SCF

        cpu.step();

        assertThat(reg.testFlag(Flag.C), is(true));
		assertThat(reg.testFlag(Flag.H), is(false));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_INC_ss() {
		reg.setRegister(DE, 25000);
		mem.writeWord8(0, 0x13); // INC DE

        cpu.step();

        assertThat(reg.getRegister(DE), is(25001));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_DEC_ss() {
		reg.setRegister(SP, 25000);
		mem.writeWord8(0, 0x3B); // DEC SP

        cpu.step();

        assertThat(reg.getRegister(SP), is(24999));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_CALL_cc_nn() {
		reg.setRegister(SP, 0xFFFF);
		reg.setFlag(P, true);
		mem.writeWord8(0, 0xEC); // CC PE,20000
		mem.writeWord16(1, 20000);
		cpu.step();
		assertThat(reg.getRegister(PC), is(20000));
		assertThat(reg.getRegister(SP), is(0xFFFD));
		assertThat(mem.readWord16(0xFFFD), is(3));
        assertThat(cpu.getCycles(), is(17L));
	}

	@Test
	public void test_CALL_cc_nn_conditionNotFulfilled() {
		reg.setRegister(SP, 0xFFFF);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0xD4); // CC NC,20000
		mem.writeWord16(1, 15000);

        cpu.step();

        assertThat(reg.getRegister(PC), is(3));
		assertThat(reg.getRegister(SP), is(0xFFFF));
        assertThat(cpu.getCycles(), is(11L));
	}

	@Test
	public void test_RET_cc() {
		reg.setRegister(SP, 0xFFFD);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0xD8); // RET C
		mem.writeWord16(0xFFFD, 5000);

        cpu.step();

        assertThat(reg.getRegister(PC), is(5000));
		assertThat(reg.getRegister(SP), is(0xFFFF));
        assertThat(cpu.getCycles(), is(11L));
	}

	@Test
	public void test_RET_cc_conditionNotFulfilled() {
		reg.setRegister(SP, 0xFFFD);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0xD0); // RET NC
		mem.writeWord16(0xFFFD, 5000);

        cpu.step();

        assertThat(reg.getRegister(PC), is(1));
		assertThat(reg.getRegister(SP), is(0xFFFD));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_RST_p() {
		mem.writeWord8(0, 0xEF); // RST 28h (RST 5)
		reg.setRegister(SP, 0xFFFF);

        cpu.step();

        assertThat(reg.getRegister(PC), is(0x28));
		assertThat(reg.getRegister(SP), is(0xFFFD));
		assertThat(mem.readWord16(0xFFFD), is(1));
        assertThat(cpu.getCycles(), is(11L));
	}

	@Test
	public void test_JP_HL() {
		reg.setRegister(HL, 1000);
		mem.writeWord8(0, 0xE9);

        cpu.step();

        assertThat(reg.getRegister(PC), is(1000));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_RLCA() {
		reg.setRegister(A, 0x83);
		mem.writeWord8(0, 0x07); // RLCA

        cpu.step();

        assertThat(reg.getRegister(A), is(0x07));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_RLA() {
		reg.setRegister(A, 0x83);
		mem.writeWord8(0, 0x17); // RLA

        cpu.step();

        assertThat(reg.getRegister(A), is(0x06));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_RRCA() {
		reg.setRegister(A, 0x83);
		mem.writeWord8(0, 0x0F); // RRCA

        cpu.step();

        assertThat(reg.getRegister(A), is(0xC1));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_RRA() {
		reg.setRegister(A, 0x83);
		mem.writeWord8(0, 0x1F); // RRA

        cpu.step();

        assertThat(reg.getRegister(A), is(0x41));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_EX_DE_HL() {
		reg.setRegister(DE, 12345);
		reg.setRegister(HL, 33333);
		mem.writeWord8(0, 0xEB); // EX DE,HL

        cpu.step();

        assertThat(reg.getRegister(DE), is(33333));
		assertThat(reg.getRegister(HL), is(12345));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_EX_SP_HL() {
		reg.setRegister(HL, 0x7012);
		reg.setRegister(SP, 0x8856);
		mem.writeWord8(0, 0xE3); // EX (SP),HL
		mem.writeWord8(0x8856, 0x11);
		mem.writeWord8(0x8857, 0x22);

        cpu.step();

        assertThat(reg.getRegister(HL), is(0x2211));
		assertThat(reg.getRegister(SP), is(0x8856));
		assertThat(mem.readWord8(0x8856), is(0x12));
		assertThat(mem.readWord8(0x8857), is(0x70));
        assertThat(cpu.getCycles(), is(18L));
	}

	@Test
	public void test_ADD_A_r() {
		reg.setRegister(A, 33);
		reg.setRegister(D, 77);
		mem.writeWord8(0, 0x82); // ADD A,D

        cpu.step();

        assertThat(reg.getRegister(A), is(110));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_ADD_A_n() {
		reg.setRegister(A, 15);
		mem.writeWord8(0, 0xC6); // ADD A,40
		mem.writeWord8(1, 40);

        cpu.step();

        assertThat(reg.getRegister(A), is(55));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_ADD_A_HL() {
		reg.setRegister(A, 60);
		reg.setRegister(HL, 1000);
		mem.writeWord8(0, 0x86); // ADD A,(HL)
		mem.writeWord8(1000, 62);

        cpu.step();

        assertThat(reg.getRegister(A), is(122));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_ADC_A_n() {
		reg.setRegister(A, 13);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0xCE); // ADC A,90
		mem.writeWord8(1, 90);

        cpu.step();

        assertThat(reg.getRegister(A), is(104));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_SUB_r() {
		reg.setRegister(A, 100);
		reg.setRegister(L, 48);
		mem.writeWord8(0, 0x95); // SUB L

        cpu.step();

        assertThat(reg.getRegister(A), is(52));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_SUB_n() {
		reg.setRegister(A, 75);
		mem.writeWord8(0, 0xD6); // SUB 15
		mem.writeWord8(1, 15);
		cpu.step();
		assertThat(reg.getRegister(A), is(60));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_SBC_A_n() {
		reg.setRegister(A, 101);
		reg.setFlag(Flag.C, true);
		mem.writeWord8(0, 0xDE); // SBC A,13
		mem.writeWord8(1, 13);

        cpu.step();

        assertThat(reg.getRegister(A), is(87));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_AND_n() {
		reg.setRegister(A, 0x81);
		mem.writeWord8(0, 0xE6); // AND E6h
		mem.writeWord8(1, 0x7F);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x01));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_OR_r() {
		reg.setRegister(A, 0x0F);
		reg.setRegister(E, 0xE1);
		mem.writeWord8(0, 0xB3); // OR E

        cpu.step();

        assertThat(reg.getRegister(A), is(0xEF));
        assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_OR_n() {
		reg.setRegister(A, 0x10);
		mem.writeWord8(0, 0xF6); // OR 23h
		mem.writeWord8(1, 0x23);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x33));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_XOR_n() {
		reg.setRegister(A, 0x81);
		mem.writeWord8(0, 0xEE); // XOR 82h
		mem.writeWord8(1, 0x82);

        cpu.step();

        assertThat(reg.getRegister(A), is(0x03));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_CP_n() {
		reg.setRegister(A, 33);
		mem.writeWord8(0, 0xFE); // CP 33
		mem.writeWord8(1, 33);

        cpu.step();

        assertThat(reg.testFlag(Flag.Z), is(true));
        assertThat(cpu.getCycles(), is(7L));
	}

	@Test
	public void test_INC_r() {
		reg.setRegister(L, 14);
		mem.writeWord8(0, 0x2C); // INC L

        cpu.step();

        assertThat(reg.getRegister(L), is(15));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_DEC_r() {
		reg.setRegister(B, 20);
		mem.writeWord8(0, 0x05); // DEC B

        cpu.step();

        assertThat(reg.getRegister(B), is(19));
        assertThat(cpu.getCycles(), is(5L));
	}

	@Test
	public void test_CPL() {
		reg.setRegister(A, 1);
		mem.writeWord8(0, 0x2F); // CPL

        cpu.step();

        assertThat(reg.getRegister(A), is(254));
        assertThat(cpu.getCycles(), is(4L));
	}

	private Object[] NOP_opCodes() {
		return $(0x00, 0x08, 0x10, 0x18, 0x20, 0x28, 0x30, 0x38);
	}

	@Test
	@Parameters(method = "NOP_opCodes")
	public void test_NOP(int opCode) {
		mem.writeWord8(0, opCode); // NOP

        cpu.step(); // check if silently passes thru

        assertThat(cpu.getCycles(), is(4L));
	}

	private Object[] JP_nn_opCodes() {
		return $(0xC3, 0xCB);
	}

	@Test
	@Parameters(method = "JP_nn_opCodes")
	public void test_JP_nn(int opCode) {
		mem.writeWord8(0, opCode);
		mem.writeWord16(1, 5000); // JP 5000

		cpu.step();

		assertThat(reg.getRegister(PC), is(5000));
		assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_JP_cc_nn() {
		reg.setFlag(Z, true);
		mem.writeWord8(0, 0xCA); // JP Z,3000
		mem.writeWord16(1, 3000);

		cpu.step();

		assertThat(reg.getRegister(PC), is(3000));
		assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_JP_cc_nn_conditionNotFulfilled() {
		reg.setFlag(Z, true);
		mem.writeWord8(0, 0xC2); // JP NZ,3000
		mem.writeWord16(1, 3000);

		cpu.step();

		assertThat(reg.getRegister(PC), is(3));
		assertThat(cpu.getCycles(), is(10L));
	}

	private Object[] CALL_nn_opCodes() {
		return $(0xCD, 0xDD, 0xED, 0xFD);
	}

	@Test
	@Parameters(method = "CALL_nn_opCodes")
	public void test_CALL_nn(int opCode) {
		reg.setRegister(SP, 0xFFFF);
		mem.writeWord8(0, opCode); // CALL 5000
		mem.writeWord16(1, 5000);

		cpu.step();

		assertThat(reg.getRegister(PC), is(5000));
		assertThat(reg.getRegister(SP), is(0xFFFD));
		assertThat(mem.readWord16(0xFFFD), is(3));
		assertThat(cpu.getCycles(), is(17L));
	}

	private Object[] RET_opCodes() {
		return $(0xC9, 0xD9);
	}

	@Test
	@Parameters(method = "RET_opCodes")
	public void test_RET(int opCode) {
		reg.setRegister(SP, 0xFFFD);
		mem.writeWord8(0, opCode); // RET
		mem.writeWord16(0xFFFD, 12000);

		cpu.step();

		assertThat(reg.getRegister(PC), is(12000));
		assertThat(reg.getRegister(SP), is(0xFFFF));
		assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_IN_A_n() {
		IOPort port = mock(IOPort.class);
		stub(port.read()).toReturn(new Word(7));
		cpu.attachDevice(100, port);
		mem.writeWord8(0, 0xDB); // IN A,(100)
		mem.writeWord8(1, 100);

		cpu.step();

		verify(port).read();
		assertThat(reg.getRegister(A), is(7));
		assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_OUT_n_A() {
		IOPort port = mock(IOPort.class);
		cpu.attachDevice(50, port);
		reg.setRegister(A, 123);
		mem.writeWord8(0, 0xD3); // OUT (50),A
		mem.writeWord8(1, 50);

		cpu.step();

		verify(port).write(new Word(123));
		assertThat(cpu.getCycles(), is(10L));
	}

	@Test
	public void test_DAA() {
		reg.setRegister(A, 0b00111011); // result of BCD 13 + 28
		mem.writeWord8(0, 0x27); // DAA

 		cpu.step();

		assertThat(reg.getRegister(A), is(0b01000001)); // BCD 41
		assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_EI() {
		mem.writeWord8(0, 0xFB); // EI

		cpu.step();

		assertThat(cpu.isInterruptsEnabled(), is(true));
		assertThat(cpu.getCycles(), is(4L));
	}

	@Test
	public void test_DI() {
		mem.writeWord8(0, 0xFB); // EI
		mem.writeWord8(1, 0xF3); // DI

		cpu.step();
		cpu.step();

		assertThat(cpu.isInterruptsEnabled(), is(false));
		assertThat(cpu.getCycles(), is(8L));
	}

	@Test
	public void testInterruptWhenDisabled() {
		mem.writeWord8(0, 0x00);
		mem.writeWord8(1, 0x00);

		cpu.interrupt(0x80);
		cpu.step(); // NOP
		cpu.step(); // NOP

		assertThat(reg.getRegister(PC), is(0x0002));
	}

	@Test
	public void testInterruptWontInterrupt_DI() {
		mem.writeWord8(0, 0xFB); // EI
		mem.writeWord8(1, 0x00); // NOP
		mem.writeWord8(2, 0xF3); // DI
		mem.writeWord8(3, 0x00); // NOP

		cpu.step(); // EI
		cpu.step(); // NOP
		cpu.interrupt(0xFF);
		cpu.step(); // DI
		cpu.step(); // NOP

		assertThat(reg.getRegister(PC), is(0x0004));
	}

	@Test
	public void testInterruptWontInterrupt_EI() {
		reg.setRegister(SP, 0xFFFF);
		reg.setRegister(A, 0);
		mem.writeWord8(0, 0x00); // NOP
		mem.writeWord8(1, 0xFB); // EI
		mem.writeWord8(2, 0x3C); // INR A
		mem.writeWord8(3, 0x3C); // INR A

		cpu.step(); // NOP
		cpu.interrupt(0xFF);
		cpu.step(); // EI
		cpu.step(); // INR A
		cpu.step(); // interrupt cycle #1

		assertThat(reg.getRegister(PC), is(0x0038));
		assertThat(reg.getRegister(A), is(1));
	}

	@Test
	public void testSimpleInterrupt() {
		reg.setRegister(SP, 0xFFFF);
		reg.setRegister(A, 0x00);
		mem.writeWord8(0, 0xFB); // EI
		mem.writeWord8(1, 0x00); // NOP
		mem.writeWord8(2, 0x00); // NOP
		mem.writeWord8(0x38, 0x3C); // INR A - interrupt handler
		mem.writeWord8(0x39, 0xC9); // RET - end of interrupt handler

		cpu.step(); // EI
		cpu.interrupt(0xFF); // RST 7
		cpu.step(); // NOP
		cpu.step(); // interrupt cycle #1

		assertThat(cpu.isInterruptsEnabled(), is(false));
		assertThat(reg.getRegister(PC), is(0x0038));
		assertThat(reg.getRegister(A), is(0));

		cpu.step(); // interrupt cycle #2

		assertThat(cpu.isInterruptsEnabled(), is(false));
		assertThat(reg.getRegister(PC), is(0x0039));
		assertThat(reg.getRegister(A), is(1));

		cpu.step(); // RET

		assertThat(reg.getRegister(PC), is(2));

		cpu.step(); // NOP

		assertThat(reg.getRegister(PC), is(3));
		assertThat(cpu.isInterruptsEnabled(), is(false));
	}

}
