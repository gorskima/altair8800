package gorskima.altair8800.io;

import com.google.common.collect.Lists;
import gorskima.altair8800.Word;
import gorskima.altair8800.cpu.IOPort;

import java.util.Queue;

public class Mits88Sio implements InputListener {

	// TODO verify if used synchronization is OK

    final static Word _OUTPUT_DEVICE_READY_ = new Word(0x80);
	final static Word DATA_OVERFLOW = new Word(0x10);
    final static Word _INPUT_DEVICE_READY_ = new Word(0x01);

	private final SerialDevice serialDevice;

	private Word status = new Word(0).or(_INPUT_DEVICE_READY_);
	private Word data = new Word(0);

	public Mits88Sio(final SerialDevice serialDevice) {
		this.serialDevice = serialDevice;
	}

	public IOPort getDataPort() {
		return new IOPort() {
			
			@Override
			public void write(final Word n) {
				serialDevice.write(n);
			}
			
			@Override
			public Word read() {
				synchronized (Mits88Sio.this) {
					status = status.unsetBits(DATA_OVERFLOW).setBits(_INPUT_DEVICE_READY_);
					return data;
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
				return status;
			}
		};
	}

    @Override
	public void notifyInputAvailable() {
		synchronized (this) {
			data = serialDevice.read();
			if (!status.testBitmask(_INPUT_DEVICE_READY_)) {
				status = status.setBits(DATA_OVERFLOW);
			}
			status = status.unsetBits(_INPUT_DEVICE_READY_);
		}
	}

}
