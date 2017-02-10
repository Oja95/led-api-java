package ee.mm;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.mm.LEDAnimation.Color;

public class LEDNotifier {

  private static final long MAX_RANGE = 31;

  private static final AnimationExecutor executor = new AnimationExecutor();

  private static final RGBRenderer debugRenderer = (Color c) -> System.out.println("[DEBUG] Setting RGB: " + visualize(c));

  private static Logger log;

  private static Properties properties = new Properties();

  static {
    executor.setRenderer(LEDNotifier::setRGBInternal);

    try {
      System.out.println(LEDNotifier.class.getResourceAsStream("/paths.properties"));
      properties.load(LEDNotifier.class.getResourceAsStream("/paths.properties"));
    }
    catch (IOException e) {
      log.error("Unlucky", e);
    }
  }

  private static String visualize(Color c) {
    return visualize(c.red()) + " " + visualize(c.green()) + " " + visualize(c.blue());
  }

  private static String visualize(double d) {
    StringBuilder sb = new StringBuilder(10);
    for (int i = 1; i <= 10; i++) {
      if (i <= Math.round(d * 10.0)) {
        sb.append('|');
      } else {
        sb.append('.');
      }
    }
    return sb.toString();
  }

  private static void setRGBInternal(Color c) throws IOException {
    setRGBInternal(c.red(), c.green(), c.blue());
  }

  private static void setRGBInternal(double r, double g, double b) throws IOException {
    try (OutputStream red = Files.newOutputStream(Paths.get(properties.getProperty("red.brightness")));
         OutputStream green = Files.newOutputStream(Paths.get(properties.getProperty("green.brightness")));
         OutputStream blue = Files.newOutputStream(Paths.get(properties.getProperty("blue.brightness")))) {
      //Clamp correct range 0.0...1.0
      r = Math.max(0.0, Math.min(1.0, r));
      g = Math.max(0.0, Math.min(1.0, g));
      b = Math.max(0.0, Math.min(1.0, b));

      //Gamma correction
//      r = Math.pow(r, 2.2);
//      g = Math.pow(g, 2.2);
//      b = Math.pow(b, 2.2);

      byte[] rBytes = ("" + Math.round(r * MAX_RANGE)).getBytes(StandardCharsets.US_ASCII);
      byte[] gBytes = ("" + Math.round(g * MAX_RANGE)).getBytes(StandardCharsets.US_ASCII);
      byte[] bBytes = ("" + Math.round(b * MAX_RANGE)).getBytes(StandardCharsets.US_ASCII);

      red.write(rBytes);
      green.write(gBytes);
      blue.write(bBytes);

//      log.debug("render " + ("" + Math.round(r * MAX_RANGE)) + " " + ("" + Math.round(g * MAX_RANGE)) + " " + ("" + Math.round(b * MAX_RANGE)));
    }
  }

  public static void runAnimation(LEDAnimation anim) {
    log = LoggerFactory.getLogger("Renderer");
    executor.run(anim);
  }

  //TODO:Color looping, transitions
  //TODO:Calibrate the color values
  //TODO:Concurrency? Priority? Overriding transition?
  //TODO:Hold current value(or get from file?) to transition nicely
}
