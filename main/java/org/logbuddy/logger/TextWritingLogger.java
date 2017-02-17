package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import java.io.IOException;
import java.io.Writer;

import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;

public class TextWritingLogger implements Logger {
  private final Renderer<String> renderer;
  private final Writer writer;

  private TextWritingLogger(Renderer<String> renderer, Writer writer) {
    this.renderer = renderer;
    this.writer = writer;
  }

  public static Logger writing(Renderer<String> renderer, Writer writer) {
    check(renderer != null);
    check(writer != null);
    return new TextWritingLogger(renderer, writer);
  }

  public void log(Message message) {
    try {
      writer.write(renderer.render(message));
      writer.flush();
    } catch (IOException e) {
      throw new LogBuddyException(e);
    }
  }

  public String toString() {
    return format("writing(%s, %s)", renderer, writer);
  }
}
