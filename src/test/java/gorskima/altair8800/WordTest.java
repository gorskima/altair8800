package gorskima.altair8800;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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

}
