package pepse.world.NPC;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.hud.HPBar;
import pepse.world.Avatar;

/**
 * Enemy is an AI played character that wants to kill the avatar.
 * An Enemy can be damaged and die (see methods).
 */
public abstract class Enemy extends GameObject{
    private final Avatar avatar;
    // can be used by child classes.
    protected final GameObjectCollection gameObjects;
    protected final HPBar hpBar;

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
                 GameObjectCollection gameObjects, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.avatar = avatar;
        this.gameObjects = gameObjects;
        this.hpBar = new HPBar(this, hp, imageReader, gameObjects);
        gameObjects.addGameObject(hpBar, Layer.UI);
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
     */
    public void damageEnemy(int damage) {
        hpBar.removeHearts(damage);
    }

    /**
     * kills the enemy. keeps track of score (number of enemies killed), and how many enemies are alive.
     */
    public void die() {
        gameObjects.removeGameObject(hpBar, Layer.UI);
        PepseGameManager.score.increment();
        PepseGameManager.numOfEnemiesAlive.decrement();
    }
}
