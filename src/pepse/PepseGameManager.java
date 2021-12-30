package pepse;

import danogl.GameManager;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;


public class PepseGameManager extends GameManager {

    private static final int SKYLAYER = 50; // need to decide the value later
    private static final int BLOCKS_AMOUNT_OF_COL = 8;
    private static final int BLOCKS_AMOUNT_OF_ROWS = 5;
    private static final int INIT_X_COORDINATE = 50;
    private static final int INIT_Y_COORDINATE = 45;
    private WindowController windowController;
    private Vector2 windowDimensions;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = this.windowController.getWindowDimensions(); // gets window dimensions

        //create sky
        createSky(windowDimensions);
        // create blocks
        Terrain terrain = new Terrain(gameObjects(), 100, windowDimensions, 10);
    } // overrides initializeGame


    // creates the sky
    private void createSky(Vector2 windowDimensions){
    Sky sky = new Sky();
    sky.create(gameObjects(), windowDimensions, SKYLAYER);
    } // end of private method createSky

    // create block
    private void createBlock(Vector2 windowDimensions){

    } // end of private method createBlock


    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args){
    new PepseGameManager().run();
    // yo
        for (int i = 0; i < ; i++) {
            continue;
        }
    } // end of main

} // end of PepseGameManager
