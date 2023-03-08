package tage.nodeControllers;

import a2.MyGame;
import org.joml.*;
import tage.*;

/**
 * A RotationController is a node controller that, when enabled, causes any object
 * it is attached to to switch between two texture images on a timer.
 * @author Lauren Prather
 */
public class PratherController extends NodeController {

  private float cycleTime = 0.0f;
  private float totalTime = 0.0f;
  private Engine engine;
  private boolean curr = false, prev = true;
  private MyGame game;

  /** Creates a rotation controller with speed as specified and linked with given game engine and game instance. */
  public PratherController(Engine e, float ctime, MyGame g) {
    super();
    cycleTime = ctime;
    engine = e;
    game = g;
  }

  /** This is called automatically by the RenderSystem (via SceneGraph) once per frame
   *   during display().  It is for engine use and should not be called by the application.
   */
  public void apply(GameObject go) {
    float elapsedTime = super.getElapsedTime();
    totalTime += elapsedTime / 1000.0f;
    if (totalTime > cycleTime) {
      prev = curr;
      if (curr) {
        curr = false;
      } else {
        curr = true;
      }
      totalTime = 0.0f;
    }
    TextureImage text = game.getTextImage(curr);
    go.setTextureImage(text);
  }
}
