package ee.mm;

import ee.mm.LEDAnimation.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class AnimationBuilder {  
  private long currentTime = 0L;
  
  private final TreeMap<Long, Color> animation = new TreeMap<>();
  
  public AnimationBuilder() {
    
  }
  
  public AnimationBuilder(double r, double g, double b) {
    animation.put(currentTime, new Color(r, g, b));
  }

  public AnimationBuilder transitionTo(double r, double g, double b, long durationMsec) {
    if(animation.isEmpty()) {
      animation.put(currentTime, new Color(0,0,0));
    }
    
    animation.put(currentTime+durationMsec, new Color(r,g,b));
    currentTime = currentTime+durationMsec;
  
    return this;
  }
  
  private AnimationBuilder transiationTo(Color c, long durationMsec) {
    if(animation.isEmpty()) {
      animation.put(currentTime, new Color(0,0,0));
    }
    
    animation.put(currentTime+durationMsec, c);
    currentTime = currentTime+durationMsec;
  
    return this;
  }
  
  public AnimationBuilder setColor(double r, double g, double b, long durationMsec) {
    animation.put(currentTime+1, new Color(r,g,b));
    animation.put(currentTime+durationMsec, new Color(r, g, b));
    
    currentTime = currentTime+durationMsec;
  
    return this;
  }
  
  private AnimationBuilder setColor(Color c, long durationMsec) {
    animation.put(currentTime+1, c);
    animation.put(currentTime+durationMsec, c);
    
    currentTime = currentTime+durationMsec;
  
    return this;
  }
  
  public AnimationBuilder repeatAll(long nTimes) {
    List<Entry<Long,Color>> temp = new ArrayList<>(animation.entrySet());
    
    //Fix overlap, TODO: Better ideas?
    if(temp.get(0).getKey().equals(0L)) {
      temp.set(0, new TreeMap.SimpleEntry<>(temp.get(0).getKey()+1L,temp.get(0).getValue()));
    }
    
    for(int i=0; i<nTimes; i++) {
      temp.forEach(e -> animation.put(currentTime + e.getKey(), e.getValue()));
      currentTime = animation.lastEntry().getKey();
    }

    return this;
  }
  
  public AnimationBuilder append(AnimationBuilder nextAnimation) {
    List<Entry<Long,Color>> temp = new ArrayList<>(nextAnimation.animation.entrySet());
    
    //Fix overlap, TODO: Better ideas?
    if(temp.get(0).getKey().equals(0L)) {
      temp.set(0, new TreeMap.SimpleEntry<>(temp.get(0).getKey() + 1L, temp.get(0).getValue()));
    }
    
    temp.forEach(e -> animation.put(currentTime + e.getKey(), e.getValue()));
    currentTime = animation.lastEntry().getKey();
  
    return this;
  }
  
  public LEDAnimation build() {
    return build(false);
  }
  
  public LEDAnimation build(boolean loop) {
    return new LEDAnimation(animation, loop);
  }
  
  /************************************
   * Commonly used animation templates
   ************************************/
  
  public static AnimationBuilder blinker(double r1, double g1, double b1, double r2, double g2, double b2, long period, long count) {
    return new AnimationBuilder(r1,g1,b1).transitionTo(r2,g2,b2,period/2).transitionTo(r1, g1, b1, period/2).repeatAll(count-1);
  }
}
