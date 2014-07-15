package gorskima.altair8800.io;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.util.Iterator;

import gorskima.altair8800.cpu.IOPort;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;

public class Mits88SioTest {

	private SerialDevice serialDevice = mock(SerialDevice.class);
	private Mits88Sio classUnderTest = new Mits88Sio(serialDevice);
	
	@Test
	public void testWriteData() {
		IOPort dataPort = classUnderTest.getDataPort();
		dataPort.write(7);
		verify(serialDevice).write(7);
	}
	
	@Test
	public void testReadingBufferedData() {
		final Iterator<Integer> testValues = Lists.newArrayList(123, 80, 15).iterator();
		
		stub(serialDevice.read()).toAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(final InvocationOnMock invocation) throws Throwable {
				return testValues.next();
			}
		});

		IOPort dataPort = classUnderTest.getDataPort();

		classUnderTest.notifyInputAvailable();

		assertThat(dataPort.read(), is(123));

		classUnderTest.notifyInputAvailable(); // Read 80
		classUnderTest.notifyInputAvailable(); // Overrun 80 with 15

        // TODO assert on data overrun

		assertThat(dataPort.read(), is(15));
	}
	
	@Test
	public void testReadingWhenNoDataAvailable() {
		IOPort dataPort = classUnderTest.getDataPort();
		assertThat(dataPort.read(), is(0));
	}
	
	@Test
	public void testWritingAndReadingStatus() {
		IOPort statusPort = classUnderTest.getStatusPort();
		statusPort.write(160);
		
		int n = statusPort.read();
		assertThat(n, is(160));
	}
	
	@Test
	public void testStatusWhenDataBecomesAvailable() {
		stub(serialDevice.read()).toReturn(123);
		
		IOPort statusPort = classUnderTest.getStatusPort();
		int status = statusPort.read();
		assertTrue((status & 0x01) == 0x01);
		
		classUnderTest.notifyInputAvailable();
		
		int newStatus = statusPort.read();
		assertThat(newStatus & 0x01, is(0x00));
	}
	
	@Test
	public void testStatusWhenDataIsNotAvailableAnymore() {
		stub(serialDevice.read()).toReturn(25);
		classUnderTest.notifyInputAvailable();
		
		IOPort statusPort = classUnderTest.getStatusPort();
		int status = statusPort.read();
		assertThat(status & 0x01, is(0x00));
		
		classUnderTest.getDataPort().read();
		
		int newStatus = statusPort.read();
		assertThat(newStatus & 0x01, is(0x01));
	}

}
