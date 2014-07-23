package gorskima.altair8800;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class DoubleWordTest {

	@Test(expected =  IllegalArgumentException.class)
	public void testCreatingDoubleWord_withDataOverflow() {
		new DoubleWord(0x1FFFF);
	}

	@Test
	public void testRoundTrip() {
		assertThat(new DoubleWord(0xF80A).toInt(), is(0xF80A));
	}

	@Test
	public void testConvertingToWord() {
		assertThat(new DoubleWord(0x7C20).toWord().toInt(), is(0x20));
	}

	@Test
	public void testGettingUpperByte() {
		assertThat(new DoubleWord(0x73A0).getUpperByte().toInt(), is(0x73));
	}

	@Test
	public void testGettingLowerByte() {
		assertThat(new DoubleWord(0x73A0).getLowerByte().toInt(), is(0xA0));
	}

	@Test
	@Parameters(method = "testIncrementingParams")
	public void testIncrementing(int value, int expected) {
		assertThat(new DoubleWord(value).increment().toInt(), is(expected));
	}

	private Object[] testIncrementingParams() {
		return $(
				$(0x0001, 0x0002),
				$(0xFFFF, 0x0000)
		);
	}

}
