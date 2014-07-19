package gorskima.altair8800;

import gorskima.altair8800.cpu.I8080;

public class Clock {

    private static final int RESOLUTION = 100;

    private final int cyclesBetweenSleeps;
    private final Thread thread;

    public Clock(final I8080 cpu, int frequency) {
        cyclesBetweenSleeps = frequency / RESOLUTION;

        Runnable runner = new Runnable() {
            @Override
            public void run() {
                long prevCycles = 0;
                long prevTimestamp = System.currentTimeMillis();

                for (;;) {
                    cpu.step();
                    long cycles = cpu.getCycles();
                    if (cycles - prevCycles > cyclesBetweenSleeps) {
                        long diff = System.currentTimeMillis() - prevTimestamp;
                        sleep(Math.max(0, 1000 / RESOLUTION - diff));
                        prevCycles += cyclesBetweenSleeps;
                        prevTimestamp = System.currentTimeMillis();
                    }
                }
            }

            private void sleep(long ms) {
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
					// TODO think it over if it ever causes some weird shit...
                    Thread.currentThread().interrupt();
                }
            }
        };
        thread = new Thread(runner);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}
