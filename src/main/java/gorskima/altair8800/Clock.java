package gorskima.altair8800;

import gorskima.altair8800.cpu.I8080;

public class Clock {

	private static final int MS_PER_SEC = 1000;
    private static final int SLEEP_FREQ = 100;

    private final int cyclesBetweenSleeps;
    private final Thread thread;

    public Clock(final I8080 cpu, int frequency) {
        cyclesBetweenSleeps = frequency / SLEEP_FREQ;

        Runnable runner = new Runnable() {
            @Override
            public void run() {
                long previousCycles = 0;
                long previousTimestamp = System.currentTimeMillis();

                for (;;) {
                    cpu.step();

                    long elapsedCycles = cpu.getCycles() - previousCycles;
                    if (elapsedCycles >= cyclesBetweenSleeps) {
                        long elapsedTime = System.currentTimeMillis() - previousTimestamp;

                        sleep(Math.max(0, (MS_PER_SEC / SLEEP_FREQ) - elapsedTime));

						previousCycles += cyclesBetweenSleeps;
                        previousTimestamp = System.currentTimeMillis();
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
		// TODO find out if this is the best way to stop a running thread
        thread.interrupt();
    }
}
