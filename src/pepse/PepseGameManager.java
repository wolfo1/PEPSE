package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.hud.OnScreenCounter;
import pepse.util.ReadScores;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.NPC.NPCFactory;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Moon;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.phenomenon.Rain;
import pepse.world.trees.Tree;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.*;
import java.util.Random;

public class PepseGameManager extends GameManager {
    // URLs
    private static final String BASE_URL = "https://gmscoreboard-2021-backend-nodejs-9hpr6.ondigitalocean.app/api/";
    private static final String GET_SCORES_URL = "get-scores/?tagid=0c30c8f471e895e46a47e98e21c7087b&num=5";
    private static final String SET_SCORE_URL_1 = "set-score/?tagid=0c30c8f471e895e46a47e98e21c7087b&player=";
    private static final String SET_SCORE_URL_2 = "&score=";
    // assets
    private static final String SOUNDTRACK_PATH = "src/assets/soundtrack.wav";
    private static final String GAME_OVER_MSG = "Game Over! Do you want to play again?";
    private static final String ENTER_NAME_MSG = "Enter your name, in english letters only: ";
    private static final String SCORE_MSG = "PEPSE by Omri Wolf & Gabi Album\n         ====HIGHSCORES====\n";
    private static final String ERROR_MSG = "connection to server timed out.";
    // constants
    private static final int SEED = 123456;
    private static final int MAX_ENEMIES = 2;
    private static final int NIGHT_CYCLE = 30;
    private static final int CHANCE_FOR_RAIN = 2000; // in once per update frames
    private static final int MIN_RAIN_DURATION = 10; // in seconds
    private static final int MAX_RAIN_DUARTION = 60;
    private static final float MIN_GAP = 50;
    private static final int EXTEND_WORLD_BY = 10 * Block.SIZE;
    private static final Color SUN_HALO_COLOR = new Color(255, 0, 0, 20);
    private static final Color MOON_HALO_COLOR = new Color(255, 255, 255, 80);
    private static final Vector2 SCORE_HUD_DIM = new Vector2(20, 20);
    private static final int SCORE_HUD_Y_OFFSET = 100;
    private static final float SCORE_HUD_X_OFFSET = 50;
    private static final String SCORE_HUD_MSG = " Enemies Killed";
    private static final String ENERGY_HUD_MSG = " Energy";
    private static final int ENERGY_HUD_Y_OFFSET = 50;
    //layers
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int MOON_LAYER = Layer.BACKGROUND + 3;
    private static final int MOON_HALO_LAYER = Layer.BACKGROUND + 4;
    private static final int RAIN_LAYER = Layer.STATIC_OBJECTS - 11;
    private static final int LOWER_GROUND_LAYER = Layer.STATIC_OBJECTS - 10;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int PROJECTILES_LAYER = Layer.DEFAULT - 10;
    private static final int TRUNK_LAYER = Layer.DEFAULT - 9;
    private static final int TOP_TRUNK_LAYER = Layer.DEFAULT - 8;
    private static final int LEAVES_LAYER = Layer.DEFAULT - 7;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    //tags
    private static final String TRUNK_TAG = "trunk";
    private static final String LEAF_TAG = "leaf";
    private static final String GROUND_TAG = "ground";
    private static final String LOWER_GROUND_TAG = "lower ground";
    private static final String ENEMY_TAG = "enemy";
    // game objects
    private Tree tree;
    private Avatar avatar;
    private Camera camera;
    private Terrain terrain;
    private Sound soundtrack;
    // infinite world
    private Random random;
    private int leftPointer;
    private int rightPointer;
    private NPCFactory npcFactory;
    // fields
    private ImageReader imageReader;
    private SoundReader soundReader;
    private WindowController windowController;
    private Vector2 windowDimensions;
    private Counter energy;
    // static fields
    public static Counter score;
    public static Counter numOfEnemiesAlive;

    /**
     * initializes the world. Creates and adds all basic objects.
     * @param imageReader ImageReader
     * @param soundReader SoundReader
     * @param inputListener InputListener
     * @param windowController WindowController
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        // initialize static counters.
        score = new Counter();
        numOfEnemiesAlive = new Counter();
        // initialize seed
        this.random = new Random(SEED);
        this.windowController = windowController;
        this.windowDimensions = windowController.getWindowDimensions(); // gets window dimensions
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        // play soundtrack for the duration of the game.
        soundtrack = soundReader.readSound(SOUNDTRACK_PATH);
        soundtrack.playLooped();
        // create terrain
        this.terrain = new Terrain(this.gameObjects(), GROUND_LAYER, windowDimensions, SEED);
        // create trees
        this.tree = new Tree(this.gameObjects(), terrain, SEED, TRUNK_LAYER, LEAVES_LAYER, TRUNK_TAG, LEAF_TAG, GROUND_TAG);
        // create avatar
        this.avatar = Avatar.create(gameObjects(), AVATAR_LAYER, windowDimensions.mult(0.5f), inputListener, imageReader);
        this.avatar.setSounds(soundReader);
        this.avatar.setProjectileLayer(PROJECTILES_LAYER);
        this.avatar.setTerrain(terrain);
        // create HUD elements
        createHUD();
        // create celestial objects (moon, night, sun, halos, rain)
        createCelestials();
        // create camera
        this.camera = new Camera(this.avatar, Vector2.ZERO, windowDimensions, windowDimensions);
        setCamera(camera);
        // create NPCFactory
        this.npcFactory = new NPCFactory(SEED, avatar, gameObjects(), imageReader, soundReader,
                AVATAR_LAYER, windowController, terrain, ENEMY_TAG);
        // create world
        initialWorld();
        // all collision rules. making a new Object on PROJECTILES to be able to include PROJECTILE_LAYER
        gameObjects().addGameObject(new GameObject(Vector2.ZERO, Vector2.ZERO, null), PROJECTILES_LAYER);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TOP_TRUNK_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(GROUND_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(PROJECTILES_LAYER, TRUNK_LAYER, true);
        gameObjects().layers().shouldLayersCollide(PROJECTILES_LAYER, LEAVES_LAYER, true);
        gameObjects().layers().shouldLayersCollide(PROJECTILES_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(PROJECTILES_LAYER, AVATAR_LAYER, true);
    }// overrides initializeGame

    private void createHUD() {
        // create Score HUD & energy HUD
        OnScreenCounter scoreUI = new OnScreenCounter(PepseGameManager.score,
                new Vector2(SCORE_HUD_X_OFFSET, this.windowDimensions.y() - SCORE_HUD_Y_OFFSET),
                SCORE_HUD_DIM, gameObjects(), SCORE_HUD_MSG);
        gameObjects().addGameObject(scoreUI, Layer.UI);
        energy = new Counter((int) avatar.getEnergy());
        OnScreenCounter energyUI = new OnScreenCounter(energy,
                new Vector2(SCORE_HUD_X_OFFSET, this.windowDimensions.y() - ENERGY_HUD_Y_OFFSET),
                SCORE_HUD_DIM, gameObjects(), ENERGY_HUD_MSG);
        gameObjects().addGameObject(energyUI, Layer.UI);
    }

    private void createCelestials() {
        //create sky
        Sky.create( gameObjects(), windowDimensions , SKY_LAYER);
        // create night
        Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, NIGHT_CYCLE);
        // create sun
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, NIGHT_CYCLE);
        // create halo
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
        // create moon
        GameObject moon = Moon.create(gameObjects(), MOON_LAYER, windowDimensions, NIGHT_CYCLE, imageReader);
        // create moon halo
        SunHalo.create(gameObjects(), MOON_HALO_LAYER, moon, MOON_HALO_COLOR);
        // create Rain singleton object
        Rain.create(gameObjects(), RAIN_LAYER, windowDimensions, imageReader, soundReader);
    }

    /**
     * Updates the frame
     * @param deltaTime Current time.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // check for end game condition
        if (avatar.isDead())
            endGame();
        // update energy UI
        energy.reset();
        energy.increaseBy((int) avatar.getEnergy());
        //the most right x coordinate
        float rightXCoordinate = camera.screenToWorldCoords(windowDimensions).x();
        // the most left x coordinate
        float leftXCoordinate = camera.screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
        // checks if to extend to right
        if (rightXCoordinate >= this.rightPointer)
            extendRight(this.rightPointer, rightXCoordinate + EXTEND_WORLD_BY);
        // checks if to extend to left
        if (leftXCoordinate <= this.leftPointer)
            extendLeft(leftXCoordinate + MIN_GAP, this.leftPointer - EXTEND_WORLD_BY);
        // check for rain, and start raining for a random amount of time
        if (random.nextInt(CHANCE_FOR_RAIN) == 0) {
            int duration = random.nextInt( MAX_RAIN_DUARTION - MIN_RAIN_DURATION) + MIN_RAIN_DURATION;
            Rain.startRain(duration);
        }
    } //end of update

    private void initialWorld() {
        float rightXCoordinate = camera.screenToWorldCoords(windowDimensions).x();
        float leftXCoordinate = camera.screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
        this.leftPointer = (int) (Math.floor(leftXCoordinate / Block.SIZE) * Block.SIZE) - EXTEND_WORLD_BY;
        this.rightPointer = (int) (Math.floor(rightXCoordinate / Block.SIZE) * Block.SIZE) + EXTEND_WORLD_BY;
        this.terrain.createInRange(leftPointer, rightPointer);
        this.tree.createInRange(leftPointer, rightPointer);
    } // end of initial world

    private void buildWorld(int start, int end){
        this.terrain.createInRange(start, end);
        this.tree.createInRange(start, end);
        // create random enemy
        if (numOfEnemiesAlive.value() < MAX_ENEMIES) {
            npcFactory.createEnemy(Math.min(start, end) + random.nextInt(Math.abs(start-end)));
            numOfEnemiesAlive.increment();
        }
    } // end of build world

    private void extendRight(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // extend right
        buildWorld(normalizeStart, normalizeEnd);
        // remove irrelevant objects from right
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() < leftPointer) {
                removeObjects(obj);
            }
        } //end of for loop
        this.rightPointer = normalizeEnd; //update right pointer
        this.leftPointer += (normalizeEnd - normalizeStart); //update left pointer
    } // end of extendRight method

    // extends the world to the left
    private void extendLeft(float start, float end){
        int normalizeStart = (int) (Math.floor(start / Block.SIZE) * Block.SIZE); // normalize start position
        int normalizeEnd = (int) (Math.floor(end / Block.SIZE) * Block.SIZE); // normalize end position
        // extend left
        buildWorld(normalizeStart, normalizeEnd);
        // remove irrelevant objects from right
        for (GameObject obj : gameObjects()){
            if (obj.getCenter().x() > this.rightPointer) {
                removeObjects(obj);
            }
        }// end of for loop
        this.leftPointer = normalizeEnd; //update left pointer
        this.rightPointer -= (normalizeStart - normalizeEnd); //update right pointer
    } // end of extend left method

    // removes the objects
    private void removeObjects(GameObject obj){
        // remove ground
        if (obj.getTag() .equals(GROUND_TAG))
            gameObjects().removeGameObject(obj, GROUND_LAYER);
        // remove tree trunk
        else if (obj.getTag().equals(TRUNK_TAG))
             gameObjects().removeGameObject(obj, TRUNK_LAYER);
        // remove leaves
        else if (obj.getTag().equals(LEAF_TAG))
             gameObjects().removeGameObject(obj, LEAVES_LAYER);
        // remove bottom bricks
        else if (obj.getTag().equals(LOWER_GROUND_TAG))
            gameObjects().removeGameObject(obj, LOWER_GROUND_LAYER);
        else if (obj.getTag().equals(ENEMY_TAG)) {
            if (gameObjects().removeGameObject(obj, AVATAR_LAYER))
                numOfEnemiesAlive.decrement();
        // delete UI elements
        else
            gameObjects().removeGameObject(obj, Layer.UI);
        }
    } // end of method remove objects

    public void endGame() {
        // ask the user for his name
        String playerName = JOptionPane.showInputDialog(null, ENTER_NAME_MSG);
        // if user agreed to give name (and did not press cancel), enter name to highscores server.
        if (playerName != null) {
            String setUrl = BASE_URL + SET_SCORE_URL_1 + playerName + SET_SCORE_URL_2 + score.value();
            // send a connection to SET_SCORE URL command, which adds the name and score to the highscores.
            try {
                URL myURL = new URL(setUrl);
                URLConnection myURLConnection = myURL.openConnection();
                myURLConnection.getContentLength();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // try to get the high scores from the server
        try(InputStream is = new java.net.URL(BASE_URL + GET_SCORES_URL).openStream()) {
            // contents is in JSON format. Parse it.
            String contents = new String(is.readAllBytes());
            String scores = ReadScores.readScores(contents);
            // show user the highscores.
            this.windowController.showMessageBox(SCORE_MSG + scores);
        } catch (Exception e) {
            e.printStackTrace();
            windowController.showMessageBox(ERROR_MSG);
        }
        if (windowController.openYesNoDialog(GAME_OVER_MSG)) {
            // stop all looping sounds, as the reset game doesn't do it.
            Rain.stopRain();
            soundtrack.stopAllOccurences();
            windowController.resetGame();
        }
        else
            windowController.closeWindow();
    }

    /**
     * Runs the entire simulation.
     * @param args This argument should not be used.
     */
    public static void main(String[] args) { new PepseGameManager().run(); } // end of main

} // end of PepseGameManager
