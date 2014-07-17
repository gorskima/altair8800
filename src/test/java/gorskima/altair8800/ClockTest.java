package gorskima.altair8800;

import gorskima.altair8800.cpu.I8080;
import gorskima.altair8800.cpu.Memory;
import gorskima.altair8800.cpu.Registers;
import org.junit.Test;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class ClockTest {

    @Test
    public void test() throws InterruptedException {
        I8080 cpu = new I8080(new Registers(), new Memory());
        Clock clock = new Clock(cpu, 2_000_000);

        clock.start();
        Thread.sleep(250);
        clock.stop();

        assertThat(Long.valueOf(cpu.getCycles()).doubleValue(), closeTo(500_000, 5000));
    }

}
