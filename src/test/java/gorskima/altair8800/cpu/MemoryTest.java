package gorskima.altair8800.cpu;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MemoryTest {

	private Memory memory = new Memory();

	@Test
	public void test8() {
		memory.writeWord8(0, 123);
		assertEquals(123, memory.readWord8(0));

		memory.writeWord8(1234, 0x66);
		assertEquals(0x66, memory.readWord8(1234));

		memory.writeWord8(0, 0x34);
		assertEquals(0x34, memory.readWord8(0));
	}

	@Test
	public void test16() {
		memory.writeWord16(0, 32000);
		assertEquals(32000, memory.readWord16(0));

		memory.writeWord16(100, 65535);
		assertEquals(65535, memory.readWord16(100));

		memory.writeWord8(200, 255);
		memory.writeWord8(201, 255);
		assertEquals(65535, memory.readWord16(200));
	}

	@Test
	public void testUninitializedMemory() {
		// Not necessarily true in real life, but at least it's deterministic :)
		assertThat(memory.readWord8(0), is(0));
		assertThat(memory.readWord16(0), is(0));
	}

}
