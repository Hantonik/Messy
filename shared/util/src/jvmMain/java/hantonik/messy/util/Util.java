package hantonik.messy.util;

import com.google.common.util.concurrent.MoreExecutors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);

    public static final TimeSource.NanoTimeSource TIME_SOURCE = System::nanoTime;

    public static long getMillis() {
        return getNanos() / 1_000_000L;
    }

    public static long getNanos() {
        return TIME_SOURCE.getAsLong();
    }

    public static <T> T make(T obj, Consumer<? super T> action) {
        action.accept(obj);

        return obj;
    }

    public static ExecutorService backgroundExecutor() {
        return BACKGROUND_EXECUTOR;
    }

    private static ExecutorService makeExecutor(String serviceName) {
        var i = Math.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());

        ExecutorService service;

        if (i <= 0)
            service = MoreExecutors.newDirectExecutorService();
        else {
            service = new ForkJoinPool(i, pool -> {
                var thread = new ForkJoinWorkerThread(pool) {
                    @Override
                    protected void onTermination(Throwable exception) {
                        if (exception != null)
                            Util.LOGGER.warn("{} died", this.getName(), exception);
                        else
                            Util.LOGGER.debug("{} shutdown", this.getName());

                        super.onTermination(exception);
                    }
                };

                thread.setName("Worker-" + serviceName + "-" + WORKER_COUNT.getAndIncrement());

                return thread;
            }, Util::onThreadException, true);
        }

        return service;
    }

    public static void shutdownExecutors() {
        shutdownExecutor(BACKGROUND_EXECUTOR);
    }

    private static void shutdownExecutor(ExecutorService service) {
        service.shutdown();

        boolean flag;

        try {
            flag = service.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            flag = false;
        }

        if (!flag)
            service.shutdownNow();
    }

    private static int getMaxThreads() {
        var propertyName = "max.bg.threads";

        var property = System.getProperty(propertyName);

        if (property != null) {
            try {
                var i = Integer.parseInt(property);

                if (i >= 1 && i <= 255)
                    return i;

                LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", propertyName, property, 255);
            } catch (NumberFormatException e) {
                LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", propertyName, property, 255);
            }
        }

        return 255;
    }

    private static void onThreadException(Thread thread, Throwable throwable) {
        if (throwable instanceof CompletionException)
            throwable = throwable.getCause();

        LOGGER.error("Caught exception in thread {}", thread, throwable);
    }

    public static void safeReplaceFile(File current, File latest, File oldBackup) {
        safeReplaceFile(current.toPath(), latest.toPath(), oldBackup.toPath());
    }

    public static void safeReplaceFile(Path current, Path latest, Path oldBackup) {
        safeReplaceOrMoveFile(current, latest, oldBackup, false);
    }

    public static void safeReplaceOrMoveFile(File current, File latest, File oldBackup, boolean move) {
        safeReplaceOrMoveFile(current.toPath(), latest.toPath(), oldBackup.toPath(), move);
    }

    public static void safeReplaceOrMoveFile(Path current, Path latest, Path oldBackup, boolean move) {
        if (!Files.exists(current) || runWithRetries(10, "create backup " + oldBackup, createDeleter(oldBackup), createRenamer(current, oldBackup), createFileCreatedCheck(oldBackup)))
            if (runWithRetries(10, "remove old " + current, createDeleter(current), createFileDeletedCheck(current)))
                if (!runWithRetries(10, "replace " + current + " with " + latest, createRenamer(latest, current), createFileCreatedCheck(current)) && !move)
                    runWithRetries(10, "restore " + current + " from " + oldBackup, createRenamer(oldBackup, current), createFileCreatedCheck(current));
    }

    private static boolean runWithRetries(int maxTries, String actionName, BooleanSupplier... suppliers) {
        for (var i = 0; i < maxTries; i++) {
            if (executeInSequence(suppliers))
                return true;

            LOGGER.error("Failed to {}, retrying {}/{}", actionName, i, maxTries);
        }

        LOGGER.error("Failed to {}, aborting, progress might be lost", actionName);

        return false;
    }

    private static BooleanSupplier createRenamer(Path filePath, Path newName) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                try {
                    Files.move(filePath, newName);

                    return true;
                } catch (IOException e) {
                    LOGGER.error("Failed to rename", e);

                    return false;
                }
            }

            public String toString() {
                return "rename " + filePath + " to " + newName;
            }
        };
    }

    private static BooleanSupplier createDeleter(Path filePath) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists(filePath);

                    return true;
                } catch (IOException e) {
                    Util.LOGGER.warn("Failed to delete", e);

                    return false;
                }
            }

            public String toString() {
                return "delete old " + filePath;
            }
        };
    }

    private static BooleanSupplier createFileDeletedCheck(Path filePath) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                return !Files.exists(filePath);
            }

            public String toString() {
                return "verify that " + filePath + " is deleted";
            }
        };
    }

    private static BooleanSupplier createFileCreatedCheck(Path filePath) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                return Files.isRegularFile(filePath);
            }

            public String toString() {
                return "verify that " + filePath + " is present";
            }
        };
    }

    private static boolean executeInSequence(BooleanSupplier... suppliers) {
        for (var supplier : suppliers) {
            if (!supplier.getAsBoolean()) {
                LOGGER.warn("Failed to execute {}", supplier);

                return false;
            }
        }

        return true;
    }
}
