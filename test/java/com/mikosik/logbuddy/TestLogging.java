package com.mikosik.logbuddy;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestLogging {
  private Logger logger;
  private Formatter formatter;
  private Object argumentA, argumentB, argumentC, field;
  private Throwable throwable;
  private Wrappable instance;
  private String string;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
    given(formatter = object -> "format(" + object + ")");
  }

  @Test
  public void returns_from_method() {
    when(new Logging(logger, formatter)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenReturned(field);
  }

  @Test
  public void returns_from_typed_method() {
    when(new Logging(logger, formatter)
        .wrap(new Wrappable())
        .methodReturningString(string));
    thenReturned(string);
  }

  @Test
  public void throws_from_method() {
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable(throwable))
        .methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void logs_method_name() throws IOException {
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable())
        .method());
    thenCalled(logger).log(any(String.class, containsString("method")));
  }

  @Test
  public void logs_arguments() throws IOException {
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, containsString(formatter.format(argumentA))));
    thenCalled(logger).log(any(String.class, containsString(formatter.format(argumentB))));
    thenCalled(logger).log(any(String.class, containsString(formatter.format(argumentC))));
  }

  @Test
  public void logs_instance() throws IOException {
    given(instance = new Wrappable());
    when(() -> new Logging(logger, formatter)
        .wrap(instance)
        .method());
    thenCalled(logger).log(any(String.class, containsString(formatter.format(instance))));
  }

  @Test
  public void logs_returned_result() throws IOException {
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenCalled(logger).log(any(String.class, containsString("returned " + formatter.format(field))));
  }

  @Test
  public void logs_thrown_exception() throws IOException {
    given(field = new RuntimeException());
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable(field))
        .methodThrowingField());
    thenCalled(logger).log(any(String.class, containsString("thrown " + formatter.format(field))));
  }

  @Test
  public void formats_invocation() throws IOException {
    when(() -> new Logging(logger, formatter)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, stringContainsInOrder(asList(".", "(", ",", ",", ")"))));
  }

  public static class Wrappable {
    private Object field;

    public Wrappable() {}

    public Wrappable(Object field) {
      this.field = field;
    }

    public void method() {}

    public void methodWithArguments(Object a, Object b, Object c) {}

    public Object methodReturningField() {
      return field;
    }

    public String methodReturningString(String string) {
      return string;
    }

    public Object methodThrowingField() throws Throwable {
      throw (Throwable) field;
    }
  }
}
