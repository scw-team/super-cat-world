package de.ggj21.scw.world.actor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.world.actor.effect.StatusEffect;

public interface GameActor {

    /**
     * @return The bottom left coordinate of the actor graphical representation in world coordinates.
     */
    Vector2 getPosition();

    /**
     * @return The width of the actor body in world coordinates.
     */
    float getWidth();

    /**
     * @return The height of the actor body in world coordinates.
     */
    float getHeight();

    /**
     * @return The X offset in world coordinates from the actor's position to its leftmost body part.
     */
    float getXOffset();

    /**
     * @return The Y offset in world coordinates from the actor's position to its lowest body part.
     */
    float getYOffset();

    void update(float delta);

    void render(SpriteBatch batch);

    Rectangle getBoundingBox();

    void kill();

    boolean isDead();

    void interactWith(GameActor otherActor);

    void addStatusEffect(StatusEffect toAdd);
}
