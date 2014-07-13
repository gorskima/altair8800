package gorskima.altair8800.cpu;

import static gorskima.altair8800.cpu.CpuRunner.run;
import static gorskima.altair8800.cpu.CpuRunner.runWithRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class I8080IntegrationTest {

	@Test
	public void testHalting() {
		I8080 cpu = run("halt/code.bin");
		assertThat(cpu.isHalt(), is(true));
	}

	@Test
	public void testTwoNOPsAndThenHALT() {
		I8080 cpu = run("nop_halt/code.bin");
		assertThat(cpu.isHalt(), is(true));
	}

	@Test
	public void testLoadingNumberIntoRegister() {
		I8080 cpu = run("ld_r_n/code.bin");
		assertThat(cpu.getRegisters().getRegister(Register.A), is(5));
	}

	@Test
	public void testLoadingFromMemoryAndAdding() {
		I8080 cpu = run("ld_add/code.bin");
		assertThat(cpu.getMemory().readWord8(5), is(64));
	}

	@Test
	public void testComputingFactorialOfFive() {
		I8080 cpu = run("factorial/code.bin");
		assertThat(cpu.getRegisters().getRegister(Register.A), is(120));
	}

	@Test
	public void testMul8withStackFrame() {
		I8080 cpu = run("mul8/code.bin");
		assertThat(cpu.getRegisters().getRegister(Register.A), is(63));
	}
	
	// TODO find some nicer way to test interrupts with multiple threads
	@Test
	public void testInterrupts() throws InterruptedException {
		CpuRunner runner = runWithRunner("interrupts/int.bin");
		
		Thread.sleep(10);
		runner.interrupt(0xCF); // RST 1
		Thread.sleep(10);
		runner.interrupt(0xCF); // RST 1
		Thread.sleep(10);
		runner.interrupt(0xCF); // RST 1 
		
		I8080 cpu = runner.getCpu();
		assertThat(cpu.getRegisters().getRegister(Register.A), is(3));
	}

}
