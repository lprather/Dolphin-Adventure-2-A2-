package tage;

import java.lang.Math;
//import tage.rml.*;
import net.java.games.input.Event;
import org.joml.*;
import tage.input.*;
import tage.input.action.*;

public class CameraOrbit3D {

  private Engine engine;
  private Camera camera; // the camera being controlled
  private GameObject avatar; // the target avatar the camera looks at
  private float cameraAzimuth; // rotation around target Y axis
  private float cameraElevation; // elevation of camera above target
  private float cameraRadius; // distance between camera and target
  private boolean hasGp;

  /** Creates a new orbitting camera with the associated viewport camera,
   * avatar, game engine, and the boolean gp.
   * Initializes the camera behind and above the avatar.
   * boolean gp tells us whether there is a gamepad associated
   * with the instance of the game.
   */
  public CameraOrbit3D(Camera cam, GameObject av, boolean gp, Engine e) {
    engine = e;
    camera = cam;
    avatar = av;
    hasGp = gp;
    cameraAzimuth = 0.0f; // start BEHIND and ABOVE the target
    cameraElevation = 30.0f; // elevation is in degrees
    cameraRadius = 3.0f; // distance from camera to avatar
    setupInputs();
    updateCameraPosition();
  }

  /** Sets up inputs for associated orbit camera actions.
   * If there is a gamepad associated with the game instance,
   * gamepad action controls will be set up as well.
   */
  private void setupInputs() {
    OrbitAzimuthAction azmAction = new OrbitAzimuthAction(-1);
    OrbitAzimuthAction azmRAction = new OrbitAzimuthAction(0);
    OrbitAzimuthAction azmLAction = new OrbitAzimuthAction(1);
    OrbitElevationAction elevUpActionKb = new OrbitElevationAction(0);
    OrbitElevationAction elevDownActionKb = new OrbitElevationAction(1);
    OrbitElevationAction elevUpActionGp = new OrbitElevationAction(0);
    OrbitElevationAction elevDownActionGp = new OrbitElevationAction(1);
    OrbitRadiusAction radAction = new OrbitRadiusAction(-1);
    OrbitRadiusAction radInActionKb = new OrbitRadiusAction(0);
    OrbitRadiusAction radOutActionKb = new OrbitRadiusAction(1);
    InputManager im = engine.getInputManager();

    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key._1,
      azmLAction,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key._3,
      azmRAction,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.P,
      elevUpActionKb,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.O,
      elevDownActionKb,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.L,
      radInActionKb,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.K,
      radOutActionKb,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );

    if (hasGp) {
      im.associateActionWithAllGamepads(
        net.java.games.input.Component.Identifier.Axis.RX,
        azmAction,
        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
      );
      im.associateActionWithAllGamepads(
        net.java.games.input.Component.Identifier.Axis.Z,
        radAction,
        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
      );
      im.associateActionWithAllGamepads(
        net.java.games.input.Component.Identifier.Button._5,
        elevUpActionGp,
        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
      );
      im.associateActionWithAllGamepads(
        net.java.games.input.Component.Identifier.Button._4,
        elevDownActionGp,
        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
      );
    }
  }

  /** Compute the camera’s azimuth, elevation, and distance, relative to 
   * the target in spherical coordinates, then convert to world Cartesian
   * coordinates and set the camera position from that. */
  public void updateCameraPosition() {
    Vector3f avatarRot = avatar.getWorldForwardVector();
    double avatarAngle = Math.toDegrees(
      (double) avatarRot.angleSigned(
        new Vector3f(0, 0, -1),
        new Vector3f(0, 1, 0)
      )
    );
    float totalAz = cameraAzimuth - (float) avatarAngle;
    double theta = Math.toRadians(totalAz);
    double phi = Math.toRadians(cameraElevation);
    float x = cameraRadius * (float) (Math.cos(phi) * Math.sin(theta));
    float y = cameraRadius * (float) (Math.sin(phi));
    float z = cameraRadius * (float) (Math.cos(phi) * Math.cos(theta));
    camera.setLocation(new Vector3f(x, y, z).add(avatar.getWorldLocation()));
    camera.lookAt(avatar);
  }

  // --------------- private class for AzimuthAction ----------------
  private class OrbitAzimuthAction extends AbstractInputAction {

    private int inputType = -1;

    public OrbitAzimuthAction(int rOrL) {
      inputType = rOrL;
    }

    public void performAction(float time, Event event) {
      float rotAmount;
      if (inputType == 0) {
        rotAmount = 0.1f * time;
      } else if (inputType == 1) {
        rotAmount = -0.1f * time;
      } else if (event.getValue() < -0.2) {
        rotAmount = -0.1f * time;
      } else if (event.getValue() > 0.2) {
        rotAmount = 0.1f * time;
      } else { // deadzone
        rotAmount = 0.0f;
      }
      cameraAzimuth += rotAmount;
      cameraAzimuth = cameraAzimuth % 360;
      updateCameraPosition();
    }
  }

  // --------------- private class for ElevationAction ----------------
  private class OrbitElevationAction extends AbstractInputAction {

    private int inputType = -1;

    public OrbitElevationAction(int upOrDown) {
      inputType = upOrDown;
    }

    public void performAction(float time, Event event) {
      float pitchAmount;
      if (inputType == 0) {
        pitchAmount = 0.2f * time;
      } else if (inputType == 1) {
        pitchAmount = -0.2f * time;
      } else { // deadzone
        pitchAmount = 0.0f * time;
      }

      if (
        cameraElevation + pitchAmount < 5.0f ||
        cameraElevation + pitchAmount > 85.0f
      ) {
        pitchAmount = 0.0f * time;
      }

      cameraElevation += pitchAmount;
      updateCameraPosition();
    }
  }

  // --------------- private class for RadiusAction ----------------
  private class OrbitRadiusAction extends AbstractInputAction {

    private int inputType = -1;

    public OrbitRadiusAction(int inOrOut) {
      inputType = inOrOut;
    }

    public void performAction(float time, Event event) {
      float zoomAmount;
      if (inputType == 0) {
        zoomAmount = -0.01f * time;
      } else if (inputType == 1) {
        zoomAmount = 0.01f * time;
      } else if (event.getValue() > 0.2) {
        zoomAmount = 0.01f * time;
      } else if (event.getValue() < -0.2) {
        zoomAmount = -0.01f * time;
      } else { // deadzone
        zoomAmount = 0.0f * time;
      }

      if (
        cameraRadius + zoomAmount < 2.0f || cameraRadius + zoomAmount > 20.0f
      ) {
        zoomAmount = 0.0f;
      }

      cameraRadius += zoomAmount;
      updateCameraPosition();
    }
  }
}
