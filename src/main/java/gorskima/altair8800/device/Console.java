package gorskima.altair8800.device;

import gorskima.altair8800.io.SerialDevice;

import java.io.IOException;

public class Console extends ListenableDevice implements SerialDevice {

	private int inputBuffer;

    @Override
	public int read() {
		return inputBuffer;
	};
	
	@Override
	public void write(int n) {
		System.out.print((char)(n & 0x7f));
	}

    public void startPolling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        inputBuffer = System.in.read();
                        notifyListener();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
