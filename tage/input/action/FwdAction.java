package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import tage.*;
//import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import tage.rml.*;

/**
* A FwdAction is an action that, when referenced, moves the avatar
* or camera of the associated game instance forward or backwards.
* @author Lauren Prather
*/

public class FwdAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean xClose, yClose, zClose, avAndCamClose;
  private int pos = -1;

  /** Creates an action associated with the given game. */
  public FwdAction(MyGame g) {
    game = g;
  }

  /** Creates an action associated with the given game and using
   * the int input as indication of forward or backward desired motion.
   * int input should be 0 for forward movement and 1 for backward movement.
   * This input is handled inside the fwd() calls for the game object and camera classes.
   */
  public FwdAction(MyGame g, int input) {
    game = g;
    pos = input;
  }

  /** Performs movement action with given time and event.
   * This version calls fwd() only for the game's avatar, but
   * commented out code from a1 called fwd() on the camera if the camera
   * was not currently attached to/on the avatar.
   */
  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // if the camera is on the avatar, move the avatar and resposition camera
    //if (game.isCamOnAv()) {

    // check if avatar within bounds before allowing it to move avWithinBounds()
    // I want to check it similar to how I checked avAndCamClose in the below example
    av = game.getAvatar();
    av.fwd(pos, time, keyValue, game.avWithinBounds());
    //game.positionCameraOnAv(game.getCamera());
    // else move the camera
    /*} else {
      av = game.getAvatar();
      cam = game.getCamera();

      // check whether x,y,z of the camera are close enough to the avatar
      // being too far from the avatar means the camera should be unable to move
      xClose =
        (Math.abs(av.getWorldLocation().x() - cam.getLocation().x())) < 5;
      yClose =
        (Math.abs(av.getWorldLocation().y() - cam.getLocation().y())) < 5;
      zClose =
        (Math.abs(av.getWorldLocation().z() - cam.getLocation().z())) < 5;
      avAndCamClose = xClose && yClose && zClose;

      cam.fwd(pos, avAndCamClose, time, keyValue);
    }*/
  }
}
