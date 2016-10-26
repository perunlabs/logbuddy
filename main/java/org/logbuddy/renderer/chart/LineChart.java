package org.logbuddy.renderer.chart;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;

import java.awt.Color;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.logbuddy.renderer.Html;

public class LineChart {
  private final Configuration configuration;

  private LineChart(Configuration configuration) {
    this.configuration = configuration;
  }

  public static LineChart lineChart() {
    return new LineChart(new Configuration());
  }

  public LineChart width(int width) {
    return new LineChart(configuration.width(width));
  }

  public LineChart height(int height) {
    return new LineChart(configuration.height(height));
  }

  public LineChart minimum(double minimum) {
    return new LineChart(configuration.minimum(minimum));
  }

  public LineChart maximum(double maximum) {
    return new LineChart(configuration.maximum(maximum));
  }

  public LineChart color(Color color) {
    return new LineChart(configuration.color(color));
  }

  public LineChart axisColor(Color color) {
    return new LineChart(configuration.axisColor(color));
  }

  public LineChart axisWidth(double width) {
    return new LineChart(configuration.axisWidth(width));
  }

  public LineChart lineWidth(double width) {
    return new LineChart(configuration.lineWidth(width));
  }

  public LineChart dotSize(double size) {
    return new LineChart(configuration.dotSize(size));
  }

  public Html plot(NumberTable table) {
    DoubleSummaryStatistics statistics = table.statistics();
    double minimum = configuration.minimum().orElse(statistics.getMin());
    double maximum = configuration.maximum().orElse(statistics.getMax());

    int height = (int) (1.0 * configuration.height() / table.numberOfColumns());
    return html(table.columns().stream()
        .map(list -> plotDoubles(list, minimum, maximum, height))
        .map(html -> html.body)
        .collect(joining()));
  }

  private Html plotDoubles(List<Number> values, double minimum, double maximum, int height) {
    List<Double> dots = values.stream()
        .map(number -> (1 - phase(minimum, number.doubleValue(), maximum)) * height)
        .collect(toList());

    Canvas canvas = canvas(configuration.width(), height);
    drawAxis(canvas, minimum, maximum);
    drawChart(canvas, dots);
    return html(canvas.toHtml());
  }

  private void drawChart(Canvas canvas, List<Double> dots) {
    double scaleX = 1.0 * canvas.width / dots.size();
    canvas.beginPath();
    for (int i = 0; i < dots.size() - 1; i++) {
      canvas.moveTo(i * scaleX, dots.get(i));
      canvas.lineTo((i + 1) * scaleX, dots.get(i + 1));
    }
    canvas.lineWidth(configuration.lineWidth());
    canvas.strokeStyle(configuration.color());
    canvas.stroke();
    canvas.fillStyle(configuration.color());
    double dotSize = configuration.dotSize();
    for (int i = 0; i < dots.size(); i++) {
      canvas.fillRect(i * scaleX - 0.5 * dotSize, dots.get(i) - 0.5 * dotSize, dotSize, dotSize);
    }
  }

  private void drawAxis(Canvas canvas, double minimum, double maximum) {
    double phase = phase(minimum, 0, maximum);
    int axisY = (int) ((1 - phase) * canvas.height);
    canvas.beginPath();
    canvas.moveTo(0, axisY);
    canvas.lineTo(canvas.width, axisY);
    canvas.lineWidth(configuration.axisWidth());
    canvas.strokeStyle(configuration.axisColor());
    canvas.stroke();
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }
}