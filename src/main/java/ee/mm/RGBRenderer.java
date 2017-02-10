package ee.mm;

import java.io.IOException;

public interface RGBRenderer {
  void setRGB(LEDAnimation.Color color) throws IOException;
}
