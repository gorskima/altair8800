package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.cpu.IOPort;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Iterator;

import static gorskima.altair8800.io.Mits88Sio._INPUT_DEVICE_READY_;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class Mits88SioTest {

	private SerialDevice serialDevice = mock(SerialDevice.class);
	private Mits88Sio classUnderTest = new Mits88Sio(serialDevice);
	
	@Test
	public void testStatusWhenDataBecomesAvailable() {
		IOPort statusPort = classUnderTest.getStatusPort();
		stub(serialDevice.read()).toReturn(123);

		assertTrue((statusPort.read() & _INPUT_DEVICE_READY_) == _INPUT_DEVICE_READY_);
		
		classUnderTest.notifyInputAvailable();

		assertThat(statusPort.read() & _INPUT_DEVICE_READY_, is(0x00));
	}
	
	@Test
	public void testStatusWhenDataIsNotAvailableAnymore() {
		IOPort statusPort = classUnderTest.getStatusPort();
		stub(serialDevice.read()).toReturn(25);

		classUnderTest.notifyInputAvailable();

		assertThat(statusPort.read() & _INPUT_DEVICE_READY_, is(0x00));
		
		classUnderTest.getDataPort().read();

		assertThat(statusPort.read() & _INPUT_DEVICE_READY_, is(_INPUT_DEVICE_READY_));
	}

	@Test
	public void testThatMultipleReadsReturnSameValue() {
		IOPort dataPort = classUnderTest.getDataPort();
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80).iterator();
		stub(serialDevice.read()).toAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(final InvocationOnMock invocation) throws Throwable {
				return testValues.next();
			}
		});

		classUnderTest.notifyInputAvailable();

		assertThat(dataPort.read(), is(123));
		assertThat(dataPort.read(), is(123)); // And not 80 or 0 or whatever
	}

	@Test
	public void testReadingBufferedData() {
		IOPort dataPort = classUnderTest.getDataPort();
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80, 15).iterator();
		stub(serialDevice.read()).toAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(final InvocationOnMock invocation) throws Throwable {
				return testValues.next();
			}
		});

		classUnderTest.notifyInputAvailable();

		assertThat(dataPort.read(), is(123));

		classUnderTest.notifyInputAvailable(); // Read 80
		classUnderTest.notifyInputAvailable(); // Overrun 80 with 15

		// TODO assert on data overrun

		assertThat(dataPort.read(), is(15));
	}

	@Test
	public void testWriteData() {
		IOPort dataPort = classUnderTest.getDataPort();

		dataPort.write(7);

		verify(serialDevice).write(7);
	}

	@Test
	public void testThatWritingDoesntInfluenceReading() {
		IOPort dataPort = classUnderTest.getDataPort();
		stub(serialDevice.read()).toReturn(80);

		classUnderTest.notifyInputAvailable();
		dataPort.write(7);

		assertThat(dataPort.read(), is(80));
	}

	@Test
	@Ignore("This is probably wrong, verify")
	public void testWritingAndReadingStatus() {
		IOPort statusPort = classUnderTest.getStatusPort();

		statusPort.write(160);

		assertThat(statusPort.read(), is(160));
	}

	// TODO add matcher for bit mask checking

}
