package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.cpu.IOPort;

import java.util.Queue;

public class Mits88Sio implements InputListener {

	private final SerialDevice serialDevice;
	
	// TODO all synchronization stuff
	private int status = 0x71;
	private final Queue<Integer> data = Lists.newLinkedList();

	public Mits88Sio(final SerialDevice serialDevice) {
		this.serialDevice = serialDevice;
	}

	public IOPort getDataPort() {
		return new IOPort() {
			
			@Override
			public void write(final int n) {
				serialDevice.write(n);
			}
			
			@Override
			public int read() {
				if (!data.isEmpty()) {
					status |= 0x01;
					return data.poll().intValue();
				} else {
					return 0;
				}
			}
		};
	}

	public IOPort getStatusPort() {
		return new IOPort() {
			
			@Override
			public void write(final int n) {
				status = n;
			}
			
			@Override
			public int read() {
				return status;
			}
		};
	}

	public void notifyInputAvailable() {
		data.add(new Integer(serialDevice.read()));
		status &= 0xFE;
	}

}
