package a2;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import org.joml.*;
import tage.*;
import tage.input.*;
import tage.input.action.*;
import tage.nodeControllers.*;
import tage.rml.Angle;
import tage.shapes.*;

public class MyGame extends VariableFrameRateGame {

  private static Engine engine;

  private double lastFrameTime, currFrameTime, elapsTime, timeDiff;

  private InputManager im;
  private GameObject dol, cub, cub2, sphere, x, y, z, cust, plane, miniCub, miniCub2, miniSphere;
  private ObjShape dolS, cubS, cub2S, sphereS, linxS, linyS, linzS, custS, planeS, miniCubS, miniCub2S, miniSphereS;
  private TextureImage doltx, brick, wood, forest, abst, pebble, water, flower;
  private Light light1;

  private int numPrizes;
  private int goX, goY, goZ, dolX, dolY, dolZ;
  private boolean showAxes = true, avHasObj = false;

  private Random rand = new Random();
  private ArrayList<GameObject> gameObjs = new ArrayList<GameObject>();

  private Vector3f loc, dolLoc;
  private Camera cam, cam2;

  private NodeController rcCub, rcCub2, rcSphere, cCust;
  private boolean cToggled = false;
  private boolean holdingCub, holdingCub2, holdingSphere;

  private CameraOrbit3D orbitController;

  // bounds for object movement
  private final int X_MAX = 100, X_MIN = -100, Z_MAX = 100, Z_MIN = -100;

  public MyGame() {
    super();
  }

  public static void main(String[] args) {
    MyGame game = new MyGame();
    engine = new Engine(game);
    game.initializeSystem();
    game.game_loop();
  }

  @Override
  public void createViewports() {
    (engine.getRenderSystem()).addViewport("LEFT", 0, 0, 1f, 1f);
    (engine.getRenderSystem()).addViewport("RIGHT", .75f, 0, .25f, .25f);

    Viewport leftVp = (engine.getRenderSystem()).getViewport("LEFT");
    Viewport rightVp = (engine.getRenderSystem()).getViewport("RIGHT");
    Camera leftCamera = leftVp.getCamera();
    Camera rightCamera = rightVp.getCamera();

    rightVp.setHasBorder(true);
    rightVp.setBorderWidth(3);
    rightVp.setBorderColor(0.0f, 1.0f, 0.0f);

    leftCamera.setLocation(new Vector3f(-2, 0, 2));
    leftCamera.setU(new Vector3f(1, 0, 0));
    leftCamera.setV(new Vector3f(0, 1, 0));
    leftCamera.setN(new Vector3f(0, 0, -1));

    rightCamera.setLocation(new Vector3f(0, 5, 0));
    rightCamera.setU(new Vector3f(1, 0, 0));
    rightCamera.setV(new Vector3f(0, 0, -1));
    rightCamera.setN(new Vector3f(0, -1, 0));
  }

  @Override
  public void loadShapes() {
    dolS = new ImportedModel("dolphinHighPoly.obj");
    cubS = new Cube();
    cub2S = new Cube();
    sphereS = new Sphere();
    miniCubS = new Cube();
    miniCub2S = new Cube();
    miniSphereS = new Sphere();
    custS = new PratherShape(); // custom object from a1
    planeS = new Plane();
    linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(100f, 0f, 0f));
    linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 100f, 0f));
    linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 100f));
  }

  @Override
  public void loadTextures() {
    doltx = new TextureImage("Dolphin_HighPolyUV.png");
    brick = new TextureImage("brick1.jpg");
    wood = new TextureImage("wood1.jpg");
    abst = new TextureImage("abstract1.jpg");
    forest = new TextureImage("forest1.jpg");
    pebble = new TextureImage("pebbles1.jpg");
    water = new TextureImage("water1.jpg");
    flower = new TextureImage("flowers1.jpg");
  }

  @Override
  public void buildObjects() {
    Matrix4f initialTranslation, initialScale, initialRotation;

    // build dolphin
    dol = new GameObject(GameObject.root(), dolS, doltx);
    initialTranslation = (new Matrix4f()).translation(-1f, 2f, 1f);
    initialScale = (new Matrix4f()).scaling(3.0f);
    dol.setLocalTranslation(initialTranslation);
    dol.setLocalScale(initialScale);
    initialRotation =
      (new Matrix4f()).rotationY((float) java.lang.Math.toRadians(135.0f));
    dol.setLocalRotation(initialRotation);

    // build cube randomly in space (within bounds of 0,50 for each x,z and set at 3 for y)
    cub = new GameObject(GameObject.root(), cubS, abst);
    initialTranslation =
      (new Matrix4f()).translation(rand.nextInt(50), 3, rand.nextInt(50));
    initialScale = (new Matrix4f()).scaling(1.25f);
    cub.setLocalTranslation(initialTranslation);
    cub.setLocalScale(initialScale);
    gameObjs.add(cub);

    // build second cube randomly in space (within bounds of -50,0 for each x,z and set at 3 for y)
    cub2 = new GameObject(GameObject.root(), cub2S, brick);
    initialTranslation =
      (new Matrix4f()).translation(
          rand.nextInt(50) - 50,
          3,
          rand.nextInt(50) - 50
        );
    cub2.setLocalTranslation(initialTranslation);
    initialScale = (new Matrix4f()).scaling(0.75f);
    cub2.setLocalScale(initialScale);
    gameObjs.add(cub2);

    // build sphere randomly in space (within bounds of -50,50 for each x,z and set at 3 for y)
    sphere = new GameObject(GameObject.root(), sphereS, wood);
    initialTranslation =
      (new Matrix4f()).translation(
          rand.nextInt(100) - 50,
          3,
          rand.nextInt(100) - 50
        );
    initialScale = (new Matrix4f()).scaling(0.5f);
    sphere.setLocalTranslation(initialTranslation);
    sphere.setLocalScale(initialScale);
    gameObjs.add(sphere);

    // add X,Y,Z axes
    x = new GameObject(GameObject.root(), linxS);
    y = new GameObject(GameObject.root(), linyS);
    z = new GameObject(GameObject.root(), linzS);
    (x.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
    (y.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
    (z.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

    // build custom shape and place it at (10,3,10) with scale 1.5
    cust = new GameObject(GameObject.root(), custS, forest);
    initialTranslation = (new Matrix4f()).translation(10, 3, 10);
    cust.setLocalTranslation(initialTranslation);
    initialScale = (new Matrix4f()).scaling(1.5f);
    cust.setLocalScale(initialScale);
    cust.getRenderStates().hasLighting(true);

    // build mini cube hung off the custom object in the scenegraph
    // placed near the custom object and set to a small size
    miniCub = new GameObject(cust, miniCubS, abst);
    miniCub.setLocalScale(new Matrix4f().scaling(0.1f));
    miniCub.setLocalLocation(
      cust.getLocalLocation().sub(new Vector3f(8.5f, 5.0f, 8.5f))
    );

    // build second mini cube hung off the custom object in the scenegraph\
    // placed near the custom object and set to a small size
    miniCub2 = new GameObject(cust, miniCub2S, brick);
    miniCub2.setLocalScale(new Matrix4f().scaling(0.1f));
    miniCub2.setLocalLocation(
      cust.getLocalLocation().sub(new Vector3f(8.5f, 4.5f, 8.5f))
    );

    // build mini sphere hung off the custom object in the scenegraph
    // placed near the custom object and set to a small size
    miniSphere = new GameObject(cust, miniSphereS, wood);
    miniSphere.setLocalScale(new Matrix4f().scaling(0.1f));
    miniSphere.setLocalLocation(
      cust.getLocalLocation().sub(new Vector3f(8.5f, 4.0f, 8.5f))
    );

    // do not render mini prizes yet!
    miniCub.getRenderStates().disableRendering();
    miniCub2.getRenderStates().disableRendering();
    miniSphere.getRenderStates().disableRendering();

    // build plane
    plane = new GameObject(GameObject.root(), planeS, water);
    initialTranslation = (new Matrix4f()).translation(0, 0, 0);
    plane.setLocalTranslation(initialTranslation);
    initialScale = (new Matrix4f()).scaling(100f);
    plane.setLocalScale(initialScale);
  }

  @Override
  public void initializeLights() {
    Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
    light1 = new Light();
    light1.setLocation(new Vector3f(5.0f, 10.0f, 5.0f));
    (engine.getSceneGraph()).addLight(light1);
  }

  @Override
  public void initializeGame() {
    lastFrameTime = System.currentTimeMillis();
    currFrameTime = System.currentTimeMillis();
    elapsTime = 0.0;
    (engine.getRenderSystem()).setWindowDimensions(1900, 1000);

    cam = (engine.getRenderSystem()).getViewport("LEFT").getCamera();
    cam2 = (engine.getRenderSystem()).getViewport("RIGHT").getCamera();

    im = engine.getInputManager();
    String gpName = im.getFirstGamepadName();
    if (gpName == null) { // if we do not have a gamepad
      // pass false to the orbit controller, which will cause it to only initialize
      // camera controls for the keyboard
      orbitController = new CameraOrbit3D(cam, dol, false, engine);
    } else { // otherwise, we do have a gamepad
      // and we pass true to the orbit controller, which will make it initialize
      // both the keyboard and the gamepad controls
      orbitController = new CameraOrbit3D(cam, dol, true, engine);
    }

    rcCub = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
    rcCub2 = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
    rcSphere = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
    cCust = new PratherController(engine, 1.0f, this);

    rcCub.addTarget(cub);
    rcCub2.addTarget(cub2);
    rcSphere.addTarget(sphere);
    cCust.addTarget(cust);

    (engine.getSceneGraph()).addNodeController(rcCub);
    (engine.getSceneGraph()).addNodeController(rcCub2);
    (engine.getSceneGraph()).addNodeController(rcSphere);
    (engine.getSceneGraph()).addNodeController(cCust);

    FwdAction fwdActionG = new FwdAction(this); // forward action for gamepads
    FwdAction fwdActionK = new FwdAction(this, 0); // forward action for keyboards
    FwdAction backActionK = new FwdAction(this, 1); // backward action for keyboards

    YawAction yawActionG = new YawAction(this); // turn action for gamepads
    YawAction yawRActionK = new YawAction(this, 0); // turn right action for keyboards
    YawAction yawLActionK = new YawAction(this, 1); // turn left action for keyboards

    // for this iteration of the project, I have chosen to remove the ability to pitch
    // since the game takes place on a flat (ground) plane and we do not want the avatar to move
    // in arbitrary 3D directions, they can no longer move up and down in this way
    // forward/backward movement as well as yaw remains the same
    /*PitchAction pitchActionG = new PitchAction(this); // pitch action for gamepads
    PitchAction pitchUpActionK = new PitchAction(this, true); // pitch up action for keyboards
    PitchAction pitchDownActionK = new PitchAction(this, false); // pitch down action for keyboards*/

    Vp2ZoomAction vp2ZoomInActionG = new Vp2ZoomAction(this, 0); // zoom in secondary camera action for gamepads
    Vp2ZoomAction vp2ZoomOutActionG = new Vp2ZoomAction(this, 1); // zoom out secondary camera action for gamepads
    Vp2ZoomAction vp2ZoomInActionK = new Vp2ZoomAction(this, 0); // zoom in secondary camera action for keyboards
    Vp2ZoomAction vp2ZoomOutActionK = new Vp2ZoomAction(this, 1); // zoom out secondary camera action for keyboards

    Vp2PanAction vp2PanRActionG = new Vp2PanAction(this, 0); // pan secondary camera right action for gamepads
    Vp2PanAction vp2PanLActionG = new Vp2PanAction(this, 1); // pan secondary camera left action for gamepads
    Vp2PanAction vp2PanFActionG = new Vp2PanAction(this, 2); // pan secondary camera forward action for gamepads
    Vp2PanAction vp2PanBActionG = new Vp2PanAction(this, 3); // pan secondary camera backward action for gamepads
    Vp2PanAction vp2PanRActionK = new Vp2PanAction(this, 0); // pan secondary camera right action for keyboard
    Vp2PanAction vp2PanLActionK = new Vp2PanAction(this, 1); // pan secondary camera left action for keyboard
    Vp2PanAction vp2PanFActionK = new Vp2PanAction(this, 2); // pan secondary camera forward action for keyboard
    Vp2PanAction vp2PanBActionK = new Vp2PanAction(this, 3); // pan secondary camera backward action for keyboard

    ToggleAxesAction toggleAxesAction = new ToggleAxesAction(this); // action for toggling axes. used for both gamepads and keyboard

    // Gamepad setup
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.Y,
      fwdActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.X,
      yawActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    /*im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Axis.RY,
      pitchActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );*/
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._9,
      vp2ZoomInActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._8,
      vp2ZoomOutActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._1,
      vp2PanRActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._2,
      vp2PanLActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._3,
      vp2PanFActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._0,
      vp2PanBActionG,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllGamepads(
      net.java.games.input.Component.Identifier.Button._7,
      toggleAxesAction,
      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY
    );

    // Keyboard setup
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.W,
      fwdActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.A,
      yawLActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.S,
      backActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.D,
      yawRActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    /*im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.UP,
      pitchUpActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.DOWN,
      pitchDownActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );*/
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key._0,
      vp2ZoomInActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key._8,
      vp2ZoomOutActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.H,
      vp2PanRActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.F,
      vp2PanLActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.T,
      vp2PanFActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.G,
      vp2PanBActionK,
      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN
    );
    im.associateActionWithAllKeyboards(
      net.java.games.input.Component.Identifier.Key.V,
      toggleAxesAction,
      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY
    );
  }

  @Override
  public void update() {
    // update time
    lastFrameTime = currFrameTime;
    currFrameTime = System.currentTimeMillis();
    timeDiff = (currFrameTime - lastFrameTime);
    elapsTime += timeDiff / 1000.0;

    // update inputs
    im.update((float) timeDiff);

    // update orbit camera position
    orbitController.updateCameraPosition();

    // if cube and avatar collide, and we are not already holding an object
    if (!avHasObj && avCollision(cub)) {
      numPrizes++; // add to score
      rcCub.toggle(); // start or stop cube rotation
      holdingCub = true; // we are holding this object
    }
    // if sphere and avatar collide, and we are not already holding an object
    if (!avHasObj && avCollision(sphere)) {
      numPrizes++; // add to score
      rcSphere.toggle(); // start or stop sphere rotation
      holdingSphere = true; // we are holding this object
    }
    // if 2nd cube and avatar collide, and we are not already holding an object
    if (!avHasObj && avCollision(cub2)) {
      numPrizes++; // add to score
      rcCub2.toggle(); // start or stop 2nd cube rotation
      holdingCub2 = true; // we are holding this object
    }

    // if we are holding any of the objects and we have not toggled the custom controller
    // that is linked to the custom object, do so
    if ((holdingCub || holdingCub2 || holdingSphere) && !cToggled) {
      cCust.toggle();
      cToggled = true;
    }

    // if avatar collides with the custom object, any items being held are offloaded
    if (avCollision(cust)) {
      avHasObj = false;

      // if we are holding any of the objects, toggle the custom controller off
      // and reset the texture of the custom object to its original state (forest)
      if (holdingCub || holdingCub2 || holdingSphere) {
        cToggled = false;
        cCust.toggle();
        resetTextureOfCust();
      }
      if (holdingCub) { // if we are holding the first cube
        miniCub.getRenderStates().enableRendering(); // show mini cube near custom object
        holdingCub = false; // we are no longer holding this object
      } else if (holdingCub2) { // if we are holding the second cube
        miniCub2.getRenderStates().enableRendering(); // show mini cube near custom object
        holdingCub2 = false; // we are no longer holding this object
      } else if (holdingSphere) { // if we are holding the sphere
        miniSphere.getRenderStates().enableRendering(); // show mini cube near custom object
        holdingSphere = false; // we are no longer holding this object
      }
    }

    // get width of viewports to find locations for the HUDs
    float leftVpWidth =
      (engine.getRenderSystem()).getViewport("LEFT").getActualWidth();
    float rightVpWidth =
      (engine.getRenderSystem()).getViewport("RIGHT").getActualWidth();
    int hud2Loc = (int) leftVpWidth - (int) rightVpWidth + 8;

    // world location of avatar in integer form to display in second viewport
    Vector3f dolLoc = dol.getWorldLocation();
    int dolX = (int) dolLoc.x();
    int dolY = (int) dolLoc.y();
    int dolZ = (int) dolLoc.z();

    // build and set HUD
    // HUD1 displays score and whether or not we are holding a price
    // and resides in the first (larger/main) viewport
    // HUD2 displays the world location of the avatar
    // and resides in the second (smaller) viewport
    String numPrizesStr = Integer.toString(numPrizes);
    String dispStr1 =
      "Score = " + numPrizesStr + ",  holding prize?: " + avHasObj;
    String dispStr2 =
      "World pos of avatar: (" + dolX + ", " + dolY + ", " + dolZ + ")";
    Vector3f hud1Color = new Vector3f(1, 0, 0);
    Vector3f hud2Color = new Vector3f(0, 1, 0);
    (engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
    (engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, hud2Loc, 10);
    (engine.getHUDmanager()).setHUD2font(GLUT.BITMAP_HELVETICA_18);
  }

  // return whether or not the avatar is within the set bounds for the game
  // for this particular case, we are not checking the y bounds because
  // the avatar is unable to pitch and move up/down in any sort of vertical fashion
  // movement is restricted to the flat plane for this version
  public boolean avWithinBounds() {
    Vector3f avLoc = dol.getWorldLocation();
    int dolX = (int) dolLoc.x();
    int dolZ = (int) dolLoc.z();
    boolean xOk = false, zOk = false;
    if (dolX < X_MAX && dolX > X_MIN) {
      xOk = true;
    }
    if (dolZ < Z_MAX && dolZ > Z_MIN) {
      zOk = true;
    }
    return xOk && zOk;
  }

  // determine if there is a collision between the avatar and game object
  private boolean avCollision(GameObject go) {
    boolean collision = false;
    loc = go.getWorldLocation();
    dolLoc = dol.getWorldLocation();

    goX = (int) loc.x();
    dolX = (int) dolLoc.x();

    goY = (int) loc.y();
    dolY = (int) dolLoc.y();

    goZ = (int) loc.z();
    dolZ = (int) dolLoc.z();

    // x,y,z bounds need to be within the range of + or - 2
    if (dolX + 2 >= goX && dolX - 2 <= goX) {
      if (dolY + 2 >= goY && dolY - 2 <= goY) {
        if (dolZ + 2 >= goZ && dolZ - 2 <= goZ) {
          collision = true;
          avHasObj = true;
        }
      }
    }
    return collision;
  }

  // switch between the axes being shown or not
  // used in combination with displayAxes()
  public void toggleAxes() {
    if (showAxes) {
      showAxes = false;
    } else {
      showAxes = true;
    }
    displayAxes();
  }

  // used in combination with toggleAxes()
  // enables or disables rendering of the x,y,z axes based on whether we want them shown or not
  public void displayAxes() {
    if (showAxes) {
      x.getRenderStates().enableRendering();
      y.getRenderStates().enableRendering();
      z.getRenderStates().enableRendering();
    } else {
      x.getRenderStates().disableRendering();
      y.getRenderStates().disableRendering();
      z.getRenderStates().disableRendering();
    }
  }

  // returns avatar
  public GameObject getAvatar() {
    return dol;
  }

  // returns camera for main viewport
  public Camera getCamera() {
    return cam;
  }

  // returns camera for secondary viewport
  public Camera getCamera2() {
    return cam2;
  }

  // returns texture image. used in conjunction with the custom controller PratherController
  public TextureImage getTextImage(boolean input) {
    if (input) {
      return pebble;
    } else {
      return flower;
    }
  }

  // when we are not actively using the custom controller (switching back and forth between two textures),
  // we want to reset the texture image of our custom object to its original form
  private void resetTextureOfCust() {
    cust.setTextureImage(forest);
  }

  // kept from previous version so that tage will compile
  // no longer used!
  public void toggleCam(Camera c){

  }

  @Override
  public void keyPressed(KeyEvent e) {
    super.keyPressed(e);
  }
}
