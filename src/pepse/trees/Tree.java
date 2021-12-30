package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.world.Terrain;

import java.util.Random;

public class Tree {
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final Terrain terrain;
    private final Random rand;
    private final int rootLayer;
    private final int leavesLayer;
    private final int seed;

    public Tree(GameObjectCollection gameObjects, Vector2 windowDimensions, Terrain terrain,
                int seed, int rootLayer, int leavesLayer) {
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.terrain = terrain;
        this.rand = new Random(seed);
        this.rootLayer = rootLayer;
        this.leavesLayer = leavesLayer;
        this.seed = seed;
    } // end of constructor tree
    

} // end of class tree
