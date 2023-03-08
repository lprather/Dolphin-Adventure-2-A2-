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
 * A Vp2PanAction is an action that, when referenced, pans the
 * camera for the secondary viewport of the associated game
 * right, left, up, or down.
 * @author Lauren Prather
 */

public class Vp2PanAction extends AbstractInputAction {

  private MyGame game;
  private Camera cam2;
  private Vector3f oldPos, newPos, fwdDir;
  private int rlfb = -1;

  /** Creates an action associated with the given game and using
   * the int input as indication of panning right, left, up, or down as desired.
   * integer input should be o for panning right, 1 for left, 2 for forward, and 3 for backward.
   */
  public Vp2PanAction(MyGame g, int input) {
    game = g;
    rlfb = input;
  }

  /** Performs panning action with given time and event.
   * Inputted integer value from constructor used
   * to determine panning direction and required uvn vector.
   */
  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    cam2 = game.getCamera2();
    oldPos = cam2.getLocation();
    if (rlfb == 0 || rlfb == 1) {
      fwdDir = cam2.getU();
    } else {
      fwdDir = cam2.getV();
    }
    if (rlfb == 0 || rlfb == 2) {
      fwdDir.mul(0.005f * time);
    } else {
      fwdDir.mul(-0.005f * time);
    }

    newPos = oldPos.add(fwdDir.x(), fwdDir.y(), fwdDir.z());
    cam2.setLocation(newPos);
  }
}
