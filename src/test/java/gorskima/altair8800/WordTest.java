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

}
