package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.Word;
import gorskima.altair8800.cpu.IOPort;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;

import static gorskima.altair8800.io.Mits88Sio.DATA_OVERFLOW;
import static gorskima.altair8800.io.Mits88Sio._INPUT_DEVICE_READY_;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class Mits88SioTest {

	private SerialDevice serialDevice = mock(SerialDevice.class);
	private Mits88Sio classUnderTest = new Mits88Sio(serialDevice);
	
	@Test
	public void testStatusWhenDataBecomesAvailable() {
		IOPort statusPort = classUnderTest.getStatusPort();
		stub(serialDevice.read()).toReturn(new Word(123));

		assertTrue(statusPort.read().testBitmask(_INPUT_DEVICE_READY_));
		
		classUnderTest.notifyInputAvailable();

		assertFalse(statusPort.read().testBitmask(_INPUT_DEVICE_READY_));
	}
	
	@Test
	public void testStatusWhenDataIsNotAvailableAnymore() {
		IOPort statusPort = classUnderTest.getStatusPort();
		stub(serialDevice.read()).toReturn(new Word(25));

		classUnderTest.notifyInputAvailable();

		assertFalse(statusPort.read().testBitmask(_INPUT_DEVICE_READY_));
		
		classUnderTest.getDataPort().read();

		assertTrue(statusPort.read().testBitmask(_INPUT_DEVICE_READY_));
	}

	@Test
	public void testThatMultipleReadsReturnSameValue() {
		IOPort dataPort = classUnderTest.getDataPort();
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80).iterator();
		stub(serialDevice.read()).toAnswer(new Answer<Word>() {
			@Override
			public Word answer(final InvocationOnMock invocation) throws Throwable {
				return new Word(testValues.next());
			}
		});

		classUnderTest.notifyInputAvailable();

		assertThat(dataPort.read().toInt(), is(123));
		assertThat(dataPort.read().toInt(), is(123)); // And not 80 or 0 or whatever
	}

	@Test
	public void testInputBufferOverrun() {
		IOPort statusPort = classUnderTest.getStatusPort();
		IOPort dataPort = classUnderTest.getDataPort();
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80, 15).iterator();
		stub(serialDevice.read()).toAnswer(new Answer<Word>() {
			@Override
			public Word answer(final InvocationOnMock invocation) throws Throwable {
				return new Word(testValues.next());
			}
		});

		assertFalse(statusPort.read().testBitmask(DATA_OVERFLOW));

		classUnderTest.notifyInputAvailable();

		assertFalse(statusPort.read().testBitmask(DATA_OVERFLOW));

		classUnderTest.notifyInputAvailable();

		assertTrue(statusPort.read().testBitmask(DATA_OVERFLOW));

		dataPort.read();

		assertFalse(statusPort.read().testBitmask(DATA_OVERFLOW));
	}

	@Test
	public void testReadingBufferedData() {
		IOPort dataPort = classUnderTest.getDataPort();
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80, 15).iterator();
		stub(serialDevice.read()).toAnswer(new Answer<Word>() {
			@Override
			public Word answer(final InvocationOnMock invocation) throws Throwable {
				return new Word(testValues.next());
			}
		});

		classUnderTest.notifyInputAvailable();

		assertThat(dataPort.read().toInt(), is(123));

		classUnderTest.notifyInputAvailable(); // Read 80
		classUnderTest.notifyInputAvailable(); // Overrun 80 with 15

		assertThat(dataPort.read().toInt(), is(15));
	}

	@Test
	public void testWriteData() {
		IOPort dataPort = classUnderTest.getDataPort();

		dataPort.write(new Word(7));

		verify(serialDevice).write(new Word(7));
	}

	@Test
	public void testThatWritingDoesntInfluenceReading() {
		IOPort dataPort = classUnderTest.getDataPort();
		stub(serialDevice.read()).toReturn(new Word(80));

		classUnderTest.notifyInputAvailable();
		dataPort.write(new Word(7));

		assertThat(dataPort.read().toInt(), is(80));
	}

	@Test
	public void testThatWritingToStatusDoesntModifyIt() {
		IOPort statusPort = classUnderTest.getStatusPort();
		assertThat(statusPort.read().toInt(), is(0x01));

		statusPort.write(new Word(0xFE));

		assertThat(statusPort.read().toInt(), is(0x01));
	}

	// TODO add matcher for bit mask checking

}
