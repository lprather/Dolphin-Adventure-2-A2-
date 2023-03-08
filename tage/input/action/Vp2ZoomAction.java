package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

//import tage.rml.*;

/**
 * A Vp2ZoomAction is an action that, when referenced, zooms in or out
 * the camera for the secondary viewport of the associated game instance.
 * @author Lauren Prather
 */

public class Vp2ZoomAction extends AbstractInputAction {

  private MyGame game;
  private Camera cam2;
  private Vector3f oldPos, newPos, fwdDir;
  private int in = -1;

  /** Creates an action associated with the given game and using
   * the int input as indication of zoom in or out as desired.
   * integer input should be 0 for zooming in and 1 for zooming out.
   */
  public Vp2ZoomAction(MyGame g, int input) {
    game = g;
    in = input;
  }

  /** Performs zooming action with given time and event.
   * Inputted integer value from constructor as well as keyValue used
   * to determine zooming in or out.
   */
  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    cam2 = game.getCamera2();
    oldPos = cam2.getLocation();
    fwdDir = cam2.getN();
    if (in == 0) {
      fwdDir.mul(0.005f * time);
    } else if (in == 1) {
      fwdDir.mul(-0.005f * time);
    } else if (keyValue > .2) {
      fwdDir.mul(0.005f * time);
    } else {
      fwdDir.mul(0.005f * time);
    }
    newPos = oldPos.add(fwdDir.x(), fwdDir.y(), fwdDir.z());
    cam2.setLocation(newPos);
  }
}
