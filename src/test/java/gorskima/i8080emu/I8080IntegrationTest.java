package gorskima.i8080emu;

import static gorskima.i8080emu.CpuRunner.run;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import gorskima.i8080emu.Register;
import gorskima.i8080emu.I8080;

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

}
