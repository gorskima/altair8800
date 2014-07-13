package gorskima.altair8800.cpu;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class Adder16Test {
	
	@Test
	@Parameters
	public void testAddWithCarry(final int op1, final int op2, final int carry, final int expectedResult,
			final int expectedCarry) {

		Adder adder = Adder.newAdder16();
		int result = adder.add(op1, op2, carry);
		assertThat(result, is(expectedResult));
		assertThat(adder.isCarry(), is(expectedCarry == 1));
	}
	
	private Object[] parametersForTestAddWithCarry() {
		return $(
			$(0, 0, 0, 0, 0),
			$(1, 0, 0, 1, 0),
			$(1, 0, 1, 2, 0),
			$(1, 1, 1, 3, 0),
	
			$(65535, 0, 0, 65535, 0),
			$(65535, 1, 0, 0, 1),
			$(65535, 0, 1, 0, 1),
			$(65535, 1, 1, 1, 1),
	
			$(65535, 65535, 1, 65535, 1));
	}

	@Test
	@Parameters
	public void testSubWithBorrow(final int op1, final int op2, final int carry, final int expectedResult,
			final int expectedBorrow) {
		
		Adder adder = Adder.newAdder16();
		int result = adder.sub(op1, op2, carry);
		assertThat(result, is(expectedResult));
		assertThat(adder.isBorrow(), is(expectedBorrow == 1));
	}
	
	private Object[] parametersForTestSubWithBorrow() {
		return $(
			$(0, 0, 0, 0, 0),
			$(0, 1, 0, 65535, 1),
			$(0, 1, 1, 65534, 1));
	}

	@Test
	@Parameters
	public void testAddWithHalfCarry(final int op1, final int op2, final int carry, final int expectedResult,
			final int expectedCarry) {
		
		Adder adder = Adder.newAdder16();
		int result = adder.add(op1, op2, carry);
		assertThat(result, is(expectedResult));
		assertThat(adder.isHalfCarry(), is(expectedCarry == 1));
	}
	
	private Object[] parametersForTestAddWithHalfCarry() {
		return $(
			$(4095, 0, 0, 4095, 0),
			$(4095, 1, 0, 4096, 1),
			$(4095, 0, 1, 4096, 1),
			$(4095, 1, 1, 4097, 1));
	}
	
	@Test
	@Parameters
	public void testSubWithHalfCarry(final int op1, final int op2, final int carry, final int expectedResult,
			final int expectedCarry) {
		
		Adder adder = Adder.newAdder16();
		int result = adder.sub(op1, op2, carry);
		assertThat(result, is(expectedResult));
		assertThat(adder.isHalfBorrow(), is(expectedCarry == 1));
	}
	
	private Object[] parametersForTestSubWithHalfCarry() {
		return $(
			$(0, 0, 0, 0, 0),
			$(4096, 0, 0, 4096, 0),
			$(4096, 1, 0, 4095, 1));
	}

}
