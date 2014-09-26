package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.Word;
import gorskima.altair8800.cpu.IOPort;

import java.util.Queue;

public class Mits88Sio implements InputListener {

	// TODO verify if used synchronization is OK

    final static int _OUTPUT_DEVICE_READY_ = 0x80;
	final static int DATA_OVERFLOW = 0x10;
    final static int _INPUT_DEVICE_READY_ = 0x01;

	private final SerialDevice serialDevice;

	private int status = 0x00 | _INPUT_DEVICE_READY_;
	private int data = 0x00;

	public Mits88Sio(final SerialDevice serialDevice) {
		this.serialDevice = serialDevice;
	}

	public IOPort getDataPort() {
		return new IOPort() {
			
			@Override
			public void write(final Word n) {
				serialDevice.write(n.toInt());
			}
			
			@Override
			public Word read() {
				synchronized (Mits88Sio.this) {
					status &= ~DATA_OVERFLOW;
					status |= _INPUT_DEVICE_READY_;
					return new Word(data);
				}
			}
		};
	}

	public IOPort getStatusPort() {
		return new IOPort() {
			
			@Override
			public void write(final Word n) {
				// Do nothing for now. In the future implement interrupt control
			}
			
			@Override
			public Word read() {
				return new Word(status);
			}
		};
	}

    @Override
	public void notifyInputAvailable() {
		synchronized (this) {
			data = serialDevice.read();
			if ((status & _INPUT_DEVICE_READY_) == 0x00) {
				status |= DATA_OVERFLOW;
			}
			status &= ~_INPUT_DEVICE_READY_;
		}
	}

}
