package pepse.world.NPC;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

public abstract class Enemy extends GameObject{

    private static final String HEART_PATH = "src/assets/heart.png";
    private static final Vector2 HEART_DIMENSIONS = new Vector2(10, 10);
    private static final int HEARTS_HEIGHT_FROM_CHARACTER = 15;
    private static final int HEARTS_SPACE = 3;
    private static final int HEARTS_LAYER = Layer.FOREGROUND - 1;

    private final GameObject[] hearts;
    private final Avatar avatar;
    private final int maxHP;
    // can be used by child classes.
    protected final GameObjectCollection gameObjects;
    protected int currHP = 0;

    /**
     * creates an Enemy in the game.
     * @param topLeftCorner top left corner of the enemy
     * @param dimensions dimensions of the enemy
     * @param renderable renderable of the enemy.
     * @param avatar the player character, for AI uses
     * @param hp how much hp the enemy has
     * @param gameObjects game Object collection
     */
    public Enemy(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Avatar avatar, int hp,
                 GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable);
        this.avatar = avatar;
        this.gameObjects = gameObjects;
        this.maxHP = hp;
        this.hearts = new GameObject[maxHP];

    }

    /**
     * initializes HP shown as Hearts above the enemy on screen to the user.
     * Call this method when addind the enemy to the game
     * @param imageReader to read hearts images.
     */
    public void initHP (ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_PATH, true);
        // calculate the top left corner X,Y of the left most heart (so hearts will be even above model).
        float topLeftHeartX = getCenter().x() - (maxHP + (getCenter().x() - 1) * (maxHP + HEARTS_SPACE)) / 2;
        float topLeftHeartY = getTopLeftCorner().y() + HEARTS_HEIGHT_FROM_CHARACTER;

        for (int i = 0; i < maxHP; i++) {
            Vector2 topLeft = new Vector2(topLeftHeartX + i * (HEARTS_SPACE + HEART_DIMENSIONS.x()),
                    topLeftHeartY);
            hearts[i] = new GameObject(topLeft, HEART_DIMENSIONS, heartImage);
            gameObjects.addGameObject(hearts[i], HEARTS_LAYER);
        }
        currHP = maxHP;
    }

    /**
     * returns the location of the player. can be used by AI purposes
     * @return Vector of player's location on map.
     */
    protected Vector2 getAvatarLocation() { return avatar.getCenter(); }

    /**
     * method to be called when something damages the enemy. input is number of damage.
     * method reduces enemey's hp accordingly, returns true if damage kills the enemy, false otherwise.
     * @param damage number of damage give to enemy
     * @return true if enemy dies, false otherwise
     */
    public boolean damageEnemy(int damage) {
        if (damage >= currHP)
            return true;
        else {
            for (int i = 0; i < damage; i++) {
                gameObjects.removeGameObject(hearts[currHP - i], HEARTS_LAYER);
            }
            currHP = currHP - damage;
            return false;
        }
    }

    /**
     * implement enemy AI
     * @param deltaTime game time
     */
    @Override
    public abstract void update(float deltaTime);
}
