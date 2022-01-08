package pepse.hud;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class HPBar extends GameObject{
    private static final String HEART_PATH = "src/assets/heart.png";
    private static final Vector2 HEART_DIMENSIONS = new Vector2(10, 10);
    private static final int HEARTS_HEIGHT_FROM_CHARACTER = 20;
    private static final int HEARTS_SPACE = 3;
    private static final int HEARTS_LAYER = Layer.FOREGROUND - 1;

    private final GameObject owner;
    private final GameObject[] hearts;
    private final int maxHP;
    private final GameObjectCollection gameObjects;
    private int currHP = 0;

    public HPBar(GameObject owner, int amount, ImageReader imageReader, GameObjectCollection gameObjects) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.owner = owner;
        this.maxHP = amount;
        this.gameObjects = gameObjects;
        this.hearts = new GameObject[amount];
        initHP(imageReader);
    }

    private void initHP (ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_PATH, true);
        // calculate the top left corner X,Y of the left most heart (so hearts will be even above model).
        float topLeftHeartX = owner.getCenter().x() - ((maxHP * HEART_DIMENSIONS.x() + (maxHP - 1) * HEARTS_SPACE)) / 2;
        float topLeftHeartY = owner.getTopLeftCorner().y() - HEARTS_HEIGHT_FROM_CHARACTER;
        // create hearts
        for (int i = 0; i < maxHP; i++) {
            Vector2 topLeft = new Vector2(topLeftHeartX + i * (HEARTS_SPACE + HEART_DIMENSIONS.x()),
                    topLeftHeartY);
            hearts[i] = new GameObject(topLeft, HEART_DIMENSIONS, heartImage);
            gameObjects.addGameObject(hearts[i], HEARTS_LAYER);
        }
        currHP = maxHP;
    }

    public int getCurrHP () { return currHP; }

    public void removeHearts(int amount) {
        // delete amount of hearts.
        for (int i = 0; i < amount && i < currHP; i++)
            gameObjects.removeGameObject(hearts[currHP - i - 1], HEARTS_LAYER);
        currHP = Math.max(0, currHP - amount);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // update hearts location to be above enemy
        float topLeftHeartX = owner.getCenter().x() - ((maxHP * HEART_DIMENSIONS.x() + (maxHP - 1) * HEARTS_SPACE)) / 2;
        float topLeftHeartY = owner.getTopLeftCorner().y() - HEARTS_HEIGHT_FROM_CHARACTER;
        for (int i = 0; i < currHP; i++) {
            Vector2 topLeft = new Vector2(topLeftHeartX + i * (HEARTS_SPACE + HEART_DIMENSIONS.x()),
                    topLeftHeartY);
            hearts[i].setTopLeftCorner(topLeft);
        }
    }
}
