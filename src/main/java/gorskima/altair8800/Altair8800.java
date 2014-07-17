package gorskima.altair8800;

import com.google.common.io.Resources;
import gorskima.altair8800.io.Mits88Sio;
import gorskima.altair8800.cpu.I8080;
import gorskima.altair8800.cpu.Memory;
import gorskima.altair8800.cpu.Registers;
import gorskima.altair8800.device.Console;

import java.io.IOException;
import java.net.URL;

public class Altair8800 {

	public static void main(final String[] args) {
        Console console = new Console();
        console.startPolling();

        Mits88Sio serialController = new Mits88Sio(console);
		console.setListener(serialController);

		final I8080 i8080 = initCpu("altair-basic-4k-3.2.bin");
		i8080.attachDevice(0, serialController.getStatusPort());
		i8080.attachDevice(1, serialController.getDataPort());

        new Clock(i8080, 10000).start();
    }
	
	private static I8080 initCpu(final String programPath) {
		URL resource = Resources.getResource(programPath);
		Memory memory = new Memory();
		try {
			writeMemory(memory, Resources.toByteArray(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new I8080(new Registers(), memory);
	}

	private static void writeMemory(final Memory memory, final byte[] code) {
		for (int addr = 0; addr < code.length; addr++) {
			memory.writeWord8(addr, 0xFF & code[addr]);
		}
	}

}
