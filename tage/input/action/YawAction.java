package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

/**
* A YawAction is an action that, when referenced, turns the avatar
* or camera of the associated game instance right or left.
* @author Lauren Prather
*/

public class YawAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam, c;
  private Vector3f rightVector, upVector, fwdVector;
  private int right = -1;

  /** Creates an action associated with the given game. */
  public YawAction(MyGame g) {
    game = g;
  }

  /** Creates an action associated with the given game and using
   * the int input as indication of right or left desired motion.
   * int input should be 0 for right yaw and 1 for left. This input is handled
   * inside the yaw() calls for the game object and camera classes.
   */
  public YawAction(MyGame g, int input) {
    game = g;
    right = input;
  }

  /** Performs movement action with given time and event.
   * This version calls yaw() only for the game's avatar, but
   * commented out code from a1 called yaw() on the camera if the camera
   * was not currently attached to/on the avatar.
   */
  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // if the camera is on the avatar, turn the avatar and reposition camera
    //if (game.isCamOnAv()) {
      av = game.getAvatar();
      av.yaw(right, time, keyValue);
      //game.positionCameraOnAv(game.getCamera());
    // else turn camera
    /*} else {
      cam = game.getCamera();
      cam.yaw(right, time, keyValue);
    }*/
  }
}
