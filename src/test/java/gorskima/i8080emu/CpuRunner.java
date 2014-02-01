package gorskima.i8080emu;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;

public class CpuRunner {
	
	private static final int DEFAULT_TIMEOUT = 100; // ms
	
	private final I8080 cpu;

	private Thread t;

	public CpuRunner(Thread t, I8080 cpu) {
		this.t = t;
		this.cpu = cpu;
	}

	// TODO check if this threading stuff actually works as expected :D
	public I8080 getCpu() {
		while (t.isAlive())
			;
		
		if (!cpu.isHalt()) {
			throw new RuntimeException("Timeout of " + DEFAULT_TIMEOUT + " ms exceeded");
		}
		
		return cpu;
	}

	public static CpuRunner runWithRunner(String programPath) {
		final I8080 cpu = initCpu(programPath);
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				final long then = System.currentTimeMillis();

				while (!cpu.isHalt() && ((System.currentTimeMillis() - then <= DEFAULT_TIMEOUT))) {
					cpu.step();
				}
			}
		};
		Thread t = new Thread(runnable, "CpuRunnerThread");
		t.start();
		return new CpuRunner(t, cpu);
	}

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
