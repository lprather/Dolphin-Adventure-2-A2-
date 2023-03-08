package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

/**
* A PitchAction is an action that, when referenced, changes the pitch of
* the associate game's avatar or camera up or down.
* @author Lauren Prather
*/

public class PitchAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean up;

  /** Creates an action associated with the given game. */
  public PitchAction(MyGame g) {
    game = g;
  }

  /** Creates an action associated with the given game and using
   * the boolean input as indication of up or down desired pitch.
   * boolean input should be true for pitching up and false for pitching down.
   * This input is handled inside the pitch() calls for the game object and camera classes.
   */
  public PitchAction(MyGame g, boolean input) {
    game = g;
    up = input;
  }

  /** Performs movement action with given time and event.
   * This version calls pitch() only for the game's avatar, but
   * commented out code from a1 called pitch() on the camera if the camera
   * was not currently attached to/on the avatar.
   */
  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // if the camera is on the avatar, pitch avatar and reposition camera
    //if (game.isCamOnAv()) {
      av = game.getAvatar();
      av.pitch(up, keyValue, time);
      //game.positionCameraOnAv(game.getCamera());
    // else, pitch camera
    /*} else {
      cam = game.getCamera();
      cam.pitch(up, time, keyValue);
    }*/
  }
}
