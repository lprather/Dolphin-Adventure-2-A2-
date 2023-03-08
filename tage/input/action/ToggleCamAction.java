package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

/**
* A ToggleCamAction is an action that, when referenced, toggles the
* camera of the associated game between being on and off the avatar.
* This action is not used in a2.
* @author Lauren Prather
*/

public class ToggleCamAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean up;

  /** Creates an action associated with the given game. */
  public ToggleCamAction(MyGame g) {
    game = g;
  }

  /** Performs action with given time and event.
   * Calls toggleCam() method in the associated game class and
   * passes it the camera for the first/main viewport.
   */
  @Override
  public void performAction(float time, Event e) {
    game.toggleCam(game.getCamera());
  }
}
