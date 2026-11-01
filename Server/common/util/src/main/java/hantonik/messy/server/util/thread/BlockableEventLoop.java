package hantonik.messy.server.util.thread;

import com.google.common.collect.Queues;
import hantonik.messy.util.LogUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class BlockableEventLoop<R extends Runnable> implements Executor {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
    private final String name;

    private int blockingCount;

    protected abstract R wrapRunnable(Runnable runnable);

    protected abstract boolean shouldRun(R runnable);

    public boolean isSameThread() {
        return Thread.currentThread() == this.getRunningThread();
    }

    protected abstract Thread getRunningThread();

    protected boolean scheduleExecutables() {
        return !this.isSameThread();
    }

    public int getPendingTasksCount() {
        return this.pendingRunnables.size();
    }

    public String name() {
        return this.name;
    }

    public <V> CompletableFuture<V> submit(Supplier<V> supplier) {
        return this.scheduleExecutables() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable task) {
        return CompletableFuture.supplyAsync(() -> {
            task.run();

            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable task) {
        if (this.scheduleExecutables())
            return this.submitAsync(task);
        else {
            task.run();

            return CompletableFuture.completedFuture(null);
        }
    }

    public void executeBlocking(Runnable task) {
        if (!this.isSameThread())
            this.submitAsync(task).join();
        else
            task.run();
    }

    public void tell(R task) {
        this.pendingRunnables.add(task);

        LockSupport.unpark(this.getRunningThread());
    }

    @Override
    public void execute(@NonNull Runnable command) {
        if (this.scheduleExecutables())
            this.tell(this.wrapRunnable(command));
        else
            command.run();
    }

    public void executeIfPossible(Runnable task) {
        this.execute(task);
    }

    protected void dropAllTasks() {
        this.pendingRunnables.clear();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    protected void runAllTasks() {
        while (this.pollTask()) {

        }
    }

    public boolean pollTask() {
        var runnable = this.pendingRunnables.peek();

        if (runnable == null)
            return false;
        else if (this.blockingCount == 0 && !this.shouldRun(runnable))
            return false;
        else {
            this.doRunTask(this.pendingRunnables.remove());

            return true;
        }
    }

    protected void doRunTask(R task) {
        try {
            task.run();
        } catch (Exception e) {
            LOGGER.error(LogUtils.FATAL, "Error executing task on {}", this.name(), e);
        }
    }

    public void managedBlock(BooleanSupplier isDone) {
        this.blockingCount++;

        try {
            while (!isDone.getAsBoolean()) {
                if (!this.pollTask())
                    this.waitForTasks();
            }
        } finally {
            this.blockingCount--;
        }
    }

    protected void waitForTasks() {
        Thread.yield();

        LockSupport.parkNanos("waiting for tasks", 100_000L);
    }
}
