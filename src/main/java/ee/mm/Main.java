package ee.mm;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    LEDAnimation hsvLoop = new AnimationBuilder(1, 0, 0)
        .transitionTo(1, 1, 0, 1000)
        .transitionTo(0, 1, 0, 1000)
        .transitionTo(0, 1, 1, 1000)
        .transitionTo(0, 0, 1, 1000)
        .transitionTo(1, 0, 1, 1000)
        .transitionTo(1, 0, 0, 1000)
        .build(true);

    LEDNotifier.runAnimation(hsvLoop);

    while(true) {
      Thread.sleep(1000);
    }
  }
}
