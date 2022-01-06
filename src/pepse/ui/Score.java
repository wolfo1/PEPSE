package pepse.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;

public class Score extends GameObject {
    private static final String PHRASE = " Enemies killed";
    // global counter, holds how many lives the player has
    private final Counter score;
    // holds the number which is presented on the screen right now
    private int currScore;
    private final TextRenderable textRenderable;

    /**
     * Construct a new GameObject of type Paddle.
     * @param scoreCounter A real-time counter of how many lives player has left.
     * @param topLeftCorner a Vector represents the location of the numeric counter on screen.
     * @param dimensions the dimensions of the text
     * @param gameObjectCollection used to add / remove widgets from the screen
     **/
    public Score (Counter scoreCounter, Vector2 topLeftCorner, Vector2 dimensions,
                               GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, null);
        this.score = scoreCounter;
        this.currScore = scoreCounter.value();
        // create a text box, bold letters in white text, add it to the screen.
        this.textRenderable = new TextRenderable(
                this.score.value() + PHRASE,
                Font.SANS_SERIF, false, true);
        this.textRenderable.setColor(Color.WHITE);
        GameObject counterText = new GameObject(topLeftCorner, dimensions, this.textRenderable);
        gameObjectCollection.addGameObject(counterText, Layer.UI);
        counterText.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Should be called once per frame, checks if livesCounter changed, and if so, updates the text.
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     **/
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // if scoreCounter is different than currCOUNTER, update.
        if (this.score.value() != this.currScore) {
            this.textRenderable.setString(this.score.value() + PHRASE);
            this.currScore = this.score.value();
        }
    }
}
