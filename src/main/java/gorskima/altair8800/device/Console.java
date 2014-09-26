package gorskima.altair8800.device;

import gorskima.altair8800.Word;
import gorskima.altair8800.io.SerialDevice;

import java.io.IOException;

public class Console extends ListenableDevice implements SerialDevice {

	private Word inputBuffer;

    @Override
	public Word read() {
		return inputBuffer;
	};
	
	@Override
	public void write(Word n) {
		// TODO masks out MSB, hack for Altair BASIC 4K; clean this up, or at least make it configuratble
		System.out.print((char)(n.toInt() & 0x7f));
	}

    public void startPolling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        inputBuffer = new Word(System.in.read());
                        notifyListener();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
