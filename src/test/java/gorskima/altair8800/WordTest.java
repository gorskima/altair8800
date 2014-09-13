package gorskima.altair8800;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class WordTest {

	@Test(expected =  IllegalArgumentException.class)
	public void testCreatingWord_withDataOverflow() {
		new Word(0x1FF);
	}

	@Test
	public void testRoundTrip() {
		assertThat(new Word(0x8F).toInt(), is(0x8F));
	}

	@Test
	public void testConvertingToDoubleWord() {
		assertThat(new Word(0x7C).toDoubleWord().toInt(), is(0x7C));
	}

	@Test
	public void testMergingWithUpperByte() {
		assertThat(new Word(0xF0).withUpperByte(new Word(0x3A)).toInt(), is(0x3AF0));
	}

	@Test
	@Parameters(method = "bitmaskTestingParams")
	public void testBitmaskTesting(int value, int mask, boolean result) {
		assertThat(new Word(value).testBitmask(new Word(mask)), is(result));
	}

	private Object[] bitmaskTestingParams() {
		return $(
				$(0x00, 0x00, true),
				$(0xFF, 0x10, true),
				$(0x01, 0x03, false)
		);
	}

	@Test
	@Parameters(method = "bitSettingParams")
	public void testBitSettings(int value, int mask, int result) {
		assertThat(new Word(value).setBits(new Word(mask)).toInt(), is(result));
	}

	private Object[] bitSettingParams() {
		return $(
				$(0x00, 0x01, 0x01),
				$(0xFF, 0x10, 0xFF),
				$(0x18, 0x03, 0x1B)
		);
	}

	@Test
	@Parameters(method = "bitUnsettingParams")
	public void testBitUnsettings(int value, int mask, int result) {
		assertThat(new Word(value).unsetBits(new Word(mask)).toInt(), is(result));
	}

	private Object[] bitUnsettingParams() {
		return $(
				$(0xA3, 0x00, 0xA3),
				$(0x00, 0x11, 0x00),
				$(0xF3, 0x39, 0xC2)
		);
	}

	@Test
	@Parameters(method = "invertParams")
	public void testInvert(int value, int result) {
		assertThat(new Word(value).invert().toInt(), is(result));
	}

	private Object[] invertParams() {
		return $(
				$(0x00, 0xFF),
				$(0xFF, 0x00),
				$(0xA3, 0x5C)
		);
	}

    @Test
    @Parameters(method = "andParams")
    public void testAnd(int value, int other, int result) {
        assertThat(new Word(value).and(new Word(other)).toInt(), is(result));
    }

    private Object[] andParams() {
        return $(
                $(0xFE, 0x03, 0x02),
                $(0xFF, 0x3A, 0x3A),
                $(0x00, 0x56, 0x00)
        );
    }

	@Test
	@Parameters(method = "orParams")
	public void testOr(int value, int other, int result) {
		assertThat(new Word(value).or(new Word(other)).toInt(), is(result));
	}

	private Object[] orParams() {
		return $(
				$(0xF0, 0x0F, 0xFF),
				$(0x00, 0x3A, 0x3A),
				$(0xF2, 0x16, 0xF6)
		);
	}

	@Test
	@Parameters(method = "xorParams")
	public void testXor(int value, int other, int result) {
		assertThat(new Word(value).xor(new Word(other)).toInt(), is(result));
	}

	private Object[] xorParams() {
		return $(
				$(0x00, 0x00, 0x00),
				$(0xFF, 0x00, 0xFF),
				$(0x03, 0xF1, 0xF2)
		);
	}

}
