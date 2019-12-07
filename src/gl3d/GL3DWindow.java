package gl3d;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import gl3d.Xform;


public class GL3DWindow extends Application {
	private World3D gameWorld;
	private Box colorChangingBox = new Box(20, 20, 20);
	private Box[][][] cellBox;
	private PhongMaterial DEAD_CELL_MATERIAL = new PhongMaterial();
	private PhongMaterial LIVE_CELL_MATERIAL = new PhongMaterial();
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("start()");

        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        buildAxes();
        buildBackgroundGame();
//        buildTestObject();
        
        //set up for changeBoxColor()
//		colorChangingBox.setTranslateZ(-50);
//		world.getChildren().add(colorChangingBox);

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleKeyboard(scene, world);
        handleMouse(scene, world);

        primaryStage.setTitle("Molecule Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(camera);
        
        //longrunning operation runs on different thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("thread run");
//                        addTestObjToScene();
//                        changeBoxColor();
                        updateAndDisplay();
                    }

                };

                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                }

                    // UI update is run on the Application thread
                    Platform.runLater(updater);
                }
            }

        });
        
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();

        System.out.println("exit start()");
	}
	
	private void buildBackgroundGame() {
		System.out.println("buildBackgroundGame()");
		gameWorld = new World3D();
		gameWorld.randomInitialize(500);
		//initializing material color for live and dead cell representation
		DEAD_CELL_MATERIAL.setDiffuseColor(new Color(0.0, 0.7, 0.0, 0.0));
		DEAD_CELL_MATERIAL.setSpecularColor(new Color(0.0, 0.5, 0.0, 0.0));
		LIVE_CELL_MATERIAL.setDiffuseColor(new Color(0.0, 0.6, 0.7, 0.7));
		LIVE_CELL_MATERIAL.setSpecularColor(new Color(0.0, 0.4, 0.4, 0.7));
		//TODO: make if available for the general case, even if the world isn't a cube
		int worldCubeSize = gameWorld.getWorldWidth();
		int temp = worldCubeSize/2;
		int boxSize = 10;
		int interval = 5;
		int xOffSet = -(temp*(boxSize + interval));
		int yOffSet = -(temp*(boxSize + interval));
		int zOffSet = -(temp*(boxSize + interval));
		
		cellBox = new Box[gameWorld.getWorldWidth()][gameWorld.getWorldLength()][gameWorld.getWorldHeight()];
		for(int x = 0; x < cellBox.length; x++) {
			for(int y = 0; y < cellBox[0].length; y++) {
				for(int z = 0; z < cellBox[0][0].length; z++) {
					Box b = new Box(boxSize, boxSize, boxSize);
					cellBox[x][y][z] = b;
					b.setTranslateX(xOffSet + x*(boxSize+interval));
					b.setTranslateY(yOffSet + y*(boxSize+interval));
					b.setTranslateZ(zOffSet + z*(boxSize+interval));
				}
			}
		}
		for(int x = 0; x < cellBox.length; x++) {
			for(int y = 0; y < cellBox[0].length; y++) {
				for(int z = 0; z < cellBox[0][0].length; z++) {
					world.getChildren().add(cellBox[x][y][z]);
				}
			}
		}
	}
	
	private void updateAndDisplay() {
		//display
		for(int x = 0; x < gameWorld.getWorldWidth(); x++) {
			for(int y = 0; y < gameWorld.getWorldLength(); y++) {
				for(int z = 0; z < gameWorld.getWorldHeight(); z++) {
					if(gameWorld.isAlive(x, y, z)) {
//						world.getChildren().add(cellBox[x][y][z]);
						cellBox[x][y][z].setMaterial(LIVE_CELL_MATERIAL);
					}else {
						cellBox[x][y][z].setMaterial(DEAD_CELL_MATERIAL);
//						world.getChildren().remove(cellBox[x][y][z]);
					}
				}
			}
		}
		//update
		gameWorld.update();
		
	}

	Double startColor = 0.1;
	private void changeBoxColor() {
		PhongMaterial m = new PhongMaterial();
		m.setDiffuseColor(new Color(startColor, 0.5, 0.5, 0.5));
		m.setSpecularColor(new Color(startColor+0.2, 0.5, 0.5, 0.5));
		colorChangingBox.setMaterial(m);
		startColor += 0.1;
	}

	private void buildTestObject() {
		System.out.println("buildTestObject()");
		
		Xform testXform = new Xform();
		
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);
		
		Cylinder cylinder = new Cylinder(5, 100);
		cylinder.setMaterial(redMaterial);
		cylinder.setTranslateX(0.0);
		cylinder.setRotationAxis(Rotate.X_AXIS);
		cylinder.setRotate(90.0);
		
		Rectangle r = new Rectangle();
		r.setFill(Color.BURLYWOOD);
		r.setX(25);
		r.setY(25);
		r.setWidth(50);
		r.setHeight(25);
		r.setArcWidth(5);
		r.setArcHeight(5);
		
		Box b = new Box();
		b.setWidth(20);
		b.setHeight(20);
		b.setDepth(20);
		b.setTranslateY(20.0);
		b.setMaterial(redMaterial);
		
		testXform.getChildren().add(cylinder);
		
		testXform.getChildren().add(r);
		
		testXform.getChildren().add(b);
		
		testXform.getChildren().addAll(makeCubeCluster(5, 5, 10));
				
		rootObject.getChildren().add(testXform);
		
		world.getChildren().addAll(rootObject);
		
	}

	private int numTestObj = 0;
	private void addTestObjToScene() {
		PhongMaterial cyanMaterial = new PhongMaterial();
		cyanMaterial.setDiffuseColor(new Color(0.5, 0.5, 0.5, 0.5));
		cyanMaterial.setSpecularColor(new Color(0.7, 0.7, 0.7, 0.5));
		Box b = new Box();
		b.setWidth(5);
		b.setHeight(5);
		b.setDepth(5);
		b.setTranslateZ(-20);
		b.setTranslateX(-20);
		b.setTranslateY(numTestObj * 10);
		b.setMaterial(cyanMaterial);
//		Xform obj = new Xform();
		
//		obj.getChildren().add(b);
//		rootObject.getChildren().add(obj);
//		rootObject.getChildren().add(b);
		world.getChildren().add(b);
//		world.getChildren().addAll(rootObject);
		
		numTestObj++;
		
	}

	private List<Node> makeCubeCluster(int size, int space, int num) {
		return makeCubeBySize(size, space, num, num, num);
	}
	
	private List<Node> makeCubeBySize(int size, int space, int width, int length, int height){
		int defaultDisplacement = 20;
		List<Node> output = new ArrayList<Node>();
		PhongMaterial cyanMaterial = new PhongMaterial();
		cyanMaterial.setDiffuseColor(new Color(0.5, 0.5, 0.5, 0.5));
		cyanMaterial.setSpecularColor(new Color(0.7, 0.7, 0.7, 0.5));
		for(int i = 0; i < width; i++) {	
			for(int j = 0; j < length; j++) {
				for(int k = 0; k < height; k++) {					
					Box b = new Box();
					b.setWidth(size);
					b.setHeight(size);
					b.setDepth(size);
					b.setTranslateZ(i*(size + space));
					b.setTranslateX(j*(size + space) + defaultDisplacement);
					b.setTranslateY(k*-(size + space));
//					b.setOpacity(0.005);
					b.setMaterial(cyanMaterial);
					output.add(b);				
				}
			}
		}
		return output;
	}

	public static void main(String[] args) {
		launch();
	}
	
	/**
	 * the following is imported from moleculeSampleApp.java
	 */
	
    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform rootObject = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    //   private void buildScene() {
    //       root.getChildren().add(world);
    //   }
    private void buildCamera() {
        System.out.println("buildCamera()");
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        System.out.println("buildAxes()");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY); 
                
                double modifier = 1.0;
                
                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                } 
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }     
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);  
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);  
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  
                }
            }
        });
    }
    
    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                    case V:
                        rootObject.setVisible(!rootObject.isVisible());
                        break;
                }
            }
        });
    }


}
