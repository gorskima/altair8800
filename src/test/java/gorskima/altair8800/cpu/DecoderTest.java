package gorskima.altair8800.cpu;

import static gorskima.altair8800.cpu.Decoder.RegisterType.dd;
import static gorskima.altair8800.cpu.Decoder.RegisterType.qq;
import static gorskima.altair8800.cpu.Register.A;
import static gorskima.altair8800.cpu.Register.AF;
import static gorskima.altair8800.cpu.Register.B;
import static gorskima.altair8800.cpu.Register.BC;
import static gorskima.altair8800.cpu.Register.C;
import static gorskima.altair8800.cpu.Register.D;
import static gorskima.altair8800.cpu.Register.DE;
import static gorskima.altair8800.cpu.Register.E;
import static gorskima.altair8800.cpu.Register.H;
import static gorskima.altair8800.cpu.Register.HL;
import static gorskima.altair8800.cpu.Register.L;
import static gorskima.altair8800.cpu.Register.SP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gorskima.altair8800.Word;
import org.junit.Test;

public class DecoderTest {

	private Decoder decoder = new Decoder();

	@Test
	public void testDecodingUpperR() {
		assertThat(decoder.decodeUpperR(new Word(0x00)), is(B));
		assertThat(decoder.decodeUpperR(new Word(0x08)), is(C));
		assertThat(decoder.decodeUpperR(new Word(0x10)), is(D));
		assertThat(decoder.decodeUpperR(new Word(0x18)), is(E));
		assertThat(decoder.decodeUpperR(new Word(0x20)), is(H));
		assertThat(decoder.decodeUpperR(new Word(0x28)), is(L));
		assertThat(decoder.decodeUpperR(new Word(0x38)), is(A));
	}
	
	@Test
	public void testDecodingLowerR() {
		assertThat(decoder.decodeLowerR(new Word(0x00)), is(B));
		assertThat(decoder.decodeLowerR(new Word(0x01)), is(C));
		assertThat(decoder.decodeLowerR(new Word(0x02)), is(D));
		assertThat(decoder.decodeLowerR(new Word(0x03)), is(E));
		assertThat(decoder.decodeLowerR(new Word(0x04)), is(H));
		assertThat(decoder.decodeLowerR(new Word(0x05)), is(L));
		assertThat(decoder.decodeLowerR(new Word(0x07)), is(A));
	}
	
	@Test
	public void testDecodingDD() {
		assertThat(decoder.decodeRegister(dd, new Word(0x00)), is(BC));
		assertThat(decoder.decodeRegister(dd, new Word(0x10)), is(DE));
		assertThat(decoder.decodeRegister(dd, new Word(0x20)), is(HL));
		assertThat(decoder.decodeRegister(dd, new Word(0x30)), is(SP));
	}

	@Test
	public void testDecodingSS() {
		assertThat(decoder.decodeRegister(dd, new Word(0x00)), is(BC));
		assertThat(decoder.decodeRegister(dd, new Word(0x10)), is(DE));
		assertThat(decoder.decodeRegister(dd, new Word(0x20)), is(HL));
		assertThat(decoder.decodeRegister(dd, new Word(0x30)), is(SP));
	}

	@Test
	public void testDecodingQQ() {
		assertThat(decoder.decodeRegister(qq, new Word(0x00)), is(BC));
		assertThat(decoder.decodeRegister(qq, new Word(0x10)), is(DE));
		assertThat(decoder.decodeRegister(qq, new Word(0x20)), is(HL));
		assertThat(decoder.decodeRegister(qq, new Word(0x30)), is(AF));
	}

	@Test
	public void testDecodingCC() {
		assertCondition(decoder.decodeCondition(new Word(0x00)), Flag.Z, false);
		assertCondition(decoder.decodeCondition(new Word(0x08)), Flag.Z, true);
		assertCondition(decoder.decodeCondition(new Word(0x10)), Flag.C, false);
		assertCondition(decoder.decodeCondition(new Word(0x18)), Flag.C, true);
		assertCondition(decoder.decodeCondition(new Word(0x20)), Flag.P, false);
		assertCondition(decoder.decodeCondition(new Word(0x28)), Flag.P, true);
		assertCondition(decoder.decodeCondition(new Word(0x30)), Flag.S, false);
		assertCondition(decoder.decodeCondition(new Word(0x38)), Flag.S, true);
	}
	
	@Test
	public void testDecodingPage() {
		assertThat(decoder.decodePage(new Word(0xC7)), is(0x00));
		assertThat(decoder.decodePage(new Word(0xCF)), is(0x08));
		assertThat(decoder.decodePage(new Word(0xD7)), is(0x10));
		assertThat(decoder.decodePage(new Word(0xDF)), is(0x18));
		assertThat(decoder.decodePage(new Word(0xE7)), is(0x20));
		assertThat(decoder.decodePage(new Word(0xEF)), is(0x28));
		assertThat(decoder.decodePage(new Word(0xF7)), is(0x30));
		assertThat(decoder.decodePage(new Word(0xFF)), is(0x38));
	}

	// TODO why not writing hamcrest matcher?
	private void assertCondition(final Condition decodeCondition, final Flag flag, final boolean expectedValue) {
		assertThat(decodeCondition.getFlag(), is(flag));
		assertThat(decodeCondition.getExpectedValue(), is(expectedValue));
	}

}
