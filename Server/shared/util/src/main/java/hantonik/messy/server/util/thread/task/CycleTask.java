package hantonik.messy.server.util.thread.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CycleTask implements Runnable {
    @Getter
    private final long time;
    private final Runnable runnable;

    @Override
    public void run() {
        this.runnable.run();
    }
}
