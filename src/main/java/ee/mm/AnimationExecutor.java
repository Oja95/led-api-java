package ee.mm;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class AnimationExecutor {

  private volatile boolean running;

  private volatile boolean restart;

  private final Thread thread;

  private volatile LEDAnimation currentAnim;

  private volatile RGBRenderer renderer;

  class ReplacingJobExecutor implements Runnable {

    private final long frameTime;

    public ReplacingJobExecutor(long frameTime) {
      this.frameTime = frameTime;
    }

    @Override
    public void run() {
      LoggerFactory.getLogger("Anim-Exec").info("Started AnimationExecutor-" + Thread.currentThread().getName());
      long timeStart = Instant.now().toEpochMilli();

      long currentFrameTime, prevFrameTime;
      prevFrameTime = Instant.now().toEpochMilli();

      while (running) {
        if (renderer != null && currentAnim != null) {
          if (restart) {
            timeStart = Instant.now().toEpochMilli();

            restart = false;
          }

          currentFrameTime = Instant.now().toEpochMilli();
          long currentTime = currentFrameTime - timeStart;

          try {
            renderer.setRGB(currentAnim.render(currentTime));
          } catch (IOException e) {
            LoggerFactory.getLogger("Anim-Exec").error("Renderer device not available, removing animation", e);
            currentAnim = null;
          }

          long tDiff = Instant.now().toEpochMilli() - currentFrameTime;
          long wait = tDiff < frameTime ? frameTime - tDiff : frameTime;
          prevFrameTime = currentFrameTime;

          try {
            TimeUnit.MILLISECONDS.sleep(wait);
          } catch (InterruptedException ex) {
          }
        } else {
          try {
            TimeUnit.MILLISECONDS.sleep(100);
          } catch (InterruptedException ex) {
          }
        }
      }
    }
  }

  public void setRenderer(RGBRenderer r) {
    this.renderer = r;
  }

  public AnimationExecutor() {
    running = true;
    restart = true;
    thread = new Thread(new ReplacingJobExecutor(20));
    thread.setDaemon(true);
    thread.start();
  }

  public void shutdown() {
    running = false;
  }

  public void run(LEDAnimation anim) {
    if (anim == null) {
      LoggerFactory.getLogger("Anim-Exec").warn("Running null animation");
    }
    currentAnim = anim;
    restart = true;
  }

}
