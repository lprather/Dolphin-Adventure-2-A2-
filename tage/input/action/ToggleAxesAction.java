package tage.input.action;

import a2.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

/**
* A ToggleAxesAction is an action that, when referenced, toggles the
* visibility of the axis lines for the associated game.
* @author Lauren Prather
*/

public class ToggleAxesAction extends AbstractInputAction {

  private MyGame game;

  /** Creates an action associated with the given game. */
  public ToggleAxesAction(MyGame g) {
    game = g;
  }

  /** Performs action with given time and event.
   * Calls toggleAxes() method in the associated game class, which will
   * in turn call displayAxes().
   */
  @Override
  public void performAction(float time, Event e) {
    game.toggleAxes();
  }
}
