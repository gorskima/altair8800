package gorskima.i8080emu;

import gorskima.i8080emu.Memory;
import gorskima.i8080emu.Registers;
import gorskima.i8080emu.I8080;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;

public class CpuRunner {

	public static I8080 run(final String programPath) {
		I8080 cpu = initCpu(programPath);
		while (!cpu.isHalt()) {
			cpu.step();
		}
		return cpu;
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
