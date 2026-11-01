package hantonik.messy.server.util.handler;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Logger logger;

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        this.logger.error("Caught previously unhandled exception: ", e);
    }
}
