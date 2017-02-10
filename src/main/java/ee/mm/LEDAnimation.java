package ee.mm;

import java.util.Map.Entry;
import java.util.TreeMap;

public class LEDAnimation {

  private final TreeMap<Long, Color> animationStates;
  
  private final boolean loop;

  public LEDAnimation() {
    animationStates = new TreeMap<>();
    loop = false;
  }

  LEDAnimation(TreeMap<Long, Color> animation) {
    animationStates = animation;
    loop = false;
  }
  
  LEDAnimation(TreeMap<Long, Color> animation, boolean loop) {
    animationStates = animation;
    this.loop = loop;
  }

  public static class State {

    private double red, green, blue;
    private long offsetMsec;

    public State(double red, double green, double blue, long offsetMsec) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.offsetMsec = offsetMsec;
    }
  }

  public static class Color {

    private final double red, green, blue;

    public Color(double red, double green, double blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
    }

    public double red() {
      return red;
    }

    public double green() {
      return green;
    }

    public double blue() {
      return blue;
    }

    @Override
    public String toString() {
      return "("+red+","+green+","+blue+")";
    }
  }

  public Color render(long offset) {
    if(animationStates.isEmpty()) {
      throw new IllegalStateException("Animation is empty");
    }
    
    if(loop) {
      offset = offset % getDuration();
    }

    Entry<Long, Color> low = animationStates.floorEntry(offset);
    Entry<Long, Color> high = animationStates.ceilingEntry(offset);

    if (low == null) {
      throw new IllegalArgumentException("Offset before first entry in animation table");
    } else if (high == null || low.getKey().equals(high.getKey())) {
      return low.getValue();
    } else {
      double dt = (double) (offset - low.getKey()) / (high.getKey() - low.getKey());
      return new Color(
              (1.0 - dt) * low.getValue().red() + dt * high.getValue().red(),
              (1.0 - dt) * low.getValue().green() + dt * high.getValue().green(),
              (1.0 - dt) * low.getValue().blue() + dt * high.getValue().blue());
    }
  }
  
  public long getDuration() {
    return animationStates.lastKey();
  }
}
