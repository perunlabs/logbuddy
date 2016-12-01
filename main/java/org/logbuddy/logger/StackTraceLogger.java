package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.model.Depth.depth;

import org.logbuddy.Logger;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class StackTraceLogger implements Logger {
  private final Logger logger;
  private final ThreadLocal<Integer> numberOfInvocations = new ThreadLocal<Integer>() {
    protected Integer initialValue() {
      return 0;
    }
  };

  private StackTraceLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger stackTrace(Logger logger) {
    return new StackTraceLogger(logger);
  }

  public void log(Object model) {
    if (model instanceof Returned || model instanceof Thrown) {
      numberOfInvocations.set(numberOfInvocations.get() - 1);
    }
    logger.log(depth(numberOfInvocations.get(), model));
    if (model instanceof Invocation) {
      numberOfInvocations.set(numberOfInvocations.get() + 1);
    }
  }

  public String toString() {
    return format("stackTrace(%s)", logger);
  }
}
