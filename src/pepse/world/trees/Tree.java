package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Tree {

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final Random rand;
    private final int rootLayer;
    private final int leavesLayer;
    private final int seed;

    private static final int MINIMAL_DISTANCE_BETWEEN_TREES = 300 ;
    private static final float FADEOUT_TIME = 10;
    private final Color LEAF_COLOUR = new Color(50,200,30);
    private final Color TRUNK_COLOUR =new Color(100,50,20);
    private static final int MIN_HEIGHT = 5;
    private static final int MAX_HEIGHT = 10;
    private static final float ODDS = 0.6f;

    /**
     * Responsible for the creation and management of trees.
     * @param gameObjects The current game object in use
     * @param terrain The terrain of the game
     * @param seed The amount of seeds in the game
     * @param rootLayer The value of the root layer
     * @param leavesLayer The value of the leaves layer
     */
    public Tree(GameObjectCollection gameObjects, Terrain terrain,
                int seed, int rootLayer, int leavesLayer) {
        this.gameObjects = gameObjects;
        this.terrain = terrain;
        this.rand = new Random(seed);
        this.rootLayer = rootLayer;
        this.leavesLayer = leavesLayer;
        this.seed = seed;
    } // end of constructor tree

    /**
     * This method creates trees in a given range of x-values.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX){
        int distance = minX % MINIMAL_DISTANCE_BETWEEN_TREES;
        minX = minX - distance;
        for (int x = minX; x <= maxX; x += MINIMAL_DISTANCE_BETWEEN_TREES){ // loop from mimiaml value for x until maximum value for x
            Random rando = new Random(Objects.hash(x, seed)); // adds tree to hash table
            if (rando.nextFloat() < ODDS)
                create(x, rando.nextInt(MAX_HEIGHT - MIN_HEIGHT) + MIN_HEIGHT);
        } // end of for loop
    } // end of createInRange method

    // creats a tree object
    private void create(int loaction, int rootHeight) {
        int groundHeight = heightAt(loaction); // the ground height at a certain location
        createTrunk(groundHeight, loaction, rootHeight); // creates the trunk
        int sizeTopTree = Block.SIZE*(rootHeight*2/3);
        int col = loaction-sizeTopTree/2;
        int row = rootHeight*Block.SIZE-sizeTopTree/2;
        for (int i = col; i <= (col + sizeTopTree); i+=Block.SIZE) {
            for (int j = row; j <= row + sizeTopTree; j+=Block.SIZE) {
                Vector2 locationOrigLeaf = new Vector2(i, groundHeight - j);
                Leaf leafBlock = createLeaf(locationOrigLeaf); // creates leaf with the original location of the leaf
                leafAnimation(leafBlock); //uses animation for leaf
                createLeafFallTask(leafBlock, locationOrigLeaf); // makes the leaf to fall
                gameObjects.addGameObject(leafBlock, leavesLayer); // Add leaves to the game
            } // end of inner for loop
        } // end of outer fol loop
    } // end of method create

    // creates a tree trunk
    private void createTrunk(int groundHeight, int treeLocation, int rootHeight) {
        Vector2 blockSize = new Vector2(Block.SIZE, Block.SIZE);
        for (int i = 0; i < rootHeight; i++) {
            GameObject rootBlock = new GameObject(
                    new Vector2(treeLocation, groundHeight - (i*Block.SIZE)), blockSize,
                    new RectangleRenderable(pepse.util.ColorSupplier.approximateColor(TRUNK_COLOUR, 10) ));
            rootBlock.setTag("root");
            gameObjects.addGameObject(rootBlock, rootLayer);
        } // end of foor loop
    } // end of createTrunk method

    // Creates a leaf
    private Leaf createLeaf(Vector2 originalLeafLocation) {
        return new Leaf(originalLeafLocation, new Vector2(Block.SIZE, Block.SIZE),
                new RectangleRenderable(pepse.util.ColorSupplier.approximateColor(LEAF_COLOUR, 20)));
    } // end of private method create leaf

    // Animation of the leaf
    private void leafAnimation(Leaf leaf) {
        new ScheduledTask(leaf, rand.nextInt(19) + 1, true,
                () -> changeAngleTransition(leaf));
        new ScheduledTask(leaf, rand.nextInt(19) + 1, true,
                () -> changeDimensionsTransition(leaf));
    } // end of private method

    // Changes the dimensions transition
    private void changeDimensionsTransition(Leaf leaf) {
        new Transition<>(
                leaf,
                (size) -> leaf.setDimensions(new Vector2(Block.SIZE + size, Block.SIZE + size)),
                -1f, 4f, Transition.CUBIC_INTERPOLATOR_FLOAT, rand.nextInt(7) + 3,
                Transition.TransitionType.TRANSITION_LOOP, null);
    } // end of private method

    // Changes the angle transition
    private void changeAngleTransition(Leaf leaf) {
        new Transition<>(leaf,
                (angle) -> leaf.renderer().setRenderableAngle(angle), //moves the leaves in the air
                0f, 5f, Transition.CUBIC_INTERPOLATOR_FLOAT, rand.nextInt(7) + 3,
                Transition.TransitionType.TRANSITION_LOOP, null
        );
    } //end of private method

    // In charge of the falling of the leaf
    private void createLeafFallTask(Leaf leafBlock, Vector2 originalLeafLocation) {
        leafBlock.renderer().setOpaqueness(1);
        leafBlock.setTopLeftCorner(originalLeafLocation);
        new ScheduledTask(
                leafBlock, rand.nextInt(60) + 7, false,
                () -> {
                    leafBlock.initLeafVerticalFallTransition(leafBlock, rand.nextInt(5) + 2);   //  transition of vertical movement
                    leafBlock.renderer().fadeOut(FADEOUT_TIME, () -> { // add fadeout time
                        initLeafAfterlifeWaitTask(leafBlock, originalLeafLocation, rand.nextInt(5));}); // end of fadeout
                }); // end of lambda
    } // end of private method

    // Initializes the leaf after task is done
    private void initLeafAfterlifeWaitTask(Leaf leafBlock, Vector2 originalLeafLocation, int afterlifeTime) {
        new ScheduledTask(leafBlock, afterlifeTime, false,
                () -> createLeafFallTask(leafBlock, originalLeafLocation));
    } // end of private method

    // The height of terrain at a certain point
    private int heightAt(int location){
        int height = (int) terrain.groundHeightAt(location);
        return height -(height % Block.SIZE) - Block.SIZE; // returns the rounded height of tree
    } // end of private method heightAt
} // end of class tree
