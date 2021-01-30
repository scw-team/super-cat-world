package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelper;
import de.ggj21.scw.world.CollisionHelperFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractActor implements GameActor {
    private static final float HORIZONTAL_SPEED = 200;
    private static final int JUMP_SPEED = 600;
    private static final Logger LOG = LogManager.getLogger(AbstractActor.class);

    private final PositionAndMovement positionAndMovement;
    private final Animation<TextureRegion> animation;
    private final float worldScale;
    private final float actorScale;
    private final CollisionHelper collisionHelper;

    float elapsedTime = 0;

    protected AbstractActor(
            final Vector2 startPosition,
            final CollisionHelperFactory collisionHelperFactory,
            SoundManager soundManager,
            final float width,
            final float height,
            final float worldScale,
            final float actorScale) {
        this.worldScale = worldScale;
        this.actorScale = actorScale;
        this.animation = getAnimation();
        positionAndMovement = new PositionAndMovement(startPosition, HORIZONTAL_SPEED, JUMP_SPEED, soundManager);
        collisionHelper = collisionHelperFactory.getHelperForActor(width, height);
    }

    abstract Animation<TextureRegion> getAnimation();

    public Vector2 getPosition() {
        return positionAndMovement.getPosition();
    }


    @Override
    public void update(float delta) {
        elapsedTime += delta;
        positionAndMovement.update(delta, collisionHelper);
    }

    @Override
    public void render(SpriteBatch batch) {
        final TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);
        batch.draw(currentFrame,
                positionAndMovement.getPosition().x * worldScale,
                positionAndMovement.getPosition().y * worldScale,
                currentFrame.getRegionWidth() * worldScale * actorScale,
                currentFrame.getRegionHeight() * worldScale * actorScale);
    }

    public InputProcessor getInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (Input.Keys.RIGHT == keycode) {
                    LOG.debug("Right move order");
                    positionAndMovement.startMovingRight();
                    return true;
                } else if (Input.Keys.LEFT == keycode) {
                    LOG.debug("Left move order");
                    positionAndMovement.startMovingLeft();
                    return true;
                } else if (Input.Keys.UP == keycode) {
                    LOG.debug("Jump order");
                    positionAndMovement.jump();
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (Input.Keys.RIGHT == keycode) {
                    positionAndMovement.stopMovingRight();
                    return true;
                } else if (Input.Keys.LEFT == keycode) {
                    positionAndMovement.stopMovingLeft();
                    return true;
                }

                return super.keyUp(keycode);
            }
        };
    }


}
