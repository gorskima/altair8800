package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.cpu.IOPort;

import java.util.Queue;

public class Mits88Sio implements InputListener {

    final static int _OUTPUT_DEVICE_READY_ = 0x80;
    final static int _INPUT_DEVICE_READY_ = 0x01;

	private final SerialDevice serialDevice;
	
	// TODO all synchronization stuff
	private int status = 0x00 | _INPUT_DEVICE_READY_;
	private int data = 0x00;

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
                status |= _INPUT_DEVICE_READY_;
                return data;
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

    @Override
	public void notifyInputAvailable() {
		data = serialDevice.read();
		status &= ~_INPUT_DEVICE_READY_;
	}

}
