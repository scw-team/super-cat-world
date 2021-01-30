package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.GameWorld;
import de.ggj21.scw.world.CollisionHelper;
import de.ggj21.scw.world.CollisionHelperFactory;
import de.ggj21.scw.world.actor.effect.StatusEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractActor implements GameActor {
    private static final float HORIZONTAL_SPEED = 400 * GameWorld.VIEWPORT_SCALE;
    private static final float JUMP_SPEED = 1_500 * GameWorld.VIEWPORT_SCALE;
    private static final Logger LOG = LogManager.getLogger(AbstractActor.class);

    final PositionAndCondition positionAndCondition;
    private final Animation<TextureRegion> animation;
    private final float width;
    private final float height;
    private final float xOffset;
    private final float yOffset;
    private final float worldScale;
    private final float actorVisualScale;
    private final CollisionHelper collisionHelper;

    float elapsedTime = 0;

    protected AbstractActor(
            final Vector2 startPosition,
            final CollisionHelperFactory collisionHelperFactory,
            SoundManager soundManager,
            final float width,
            final float height,
            final float xOffset,
            final float yOffset,
            final boolean affectedByGravity,
            final float worldScale,
            final float actorVisualScale) {
        this.width = width * actorVisualScale;
        this.height = height * actorVisualScale;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.worldScale = worldScale;
        this.actorVisualScale = actorVisualScale;
        this.animation = getAnimation();
        positionAndCondition = new PositionAndCondition(startPosition, HORIZONTAL_SPEED, JUMP_SPEED, affectedByGravity, soundManager);
        collisionHelper = collisionHelperFactory.getHelperForActor(this);
    }

    abstract Animation<TextureRegion> getAnimation();

    @Override
    public Vector2 getPosition() {
        return positionAndCondition.getPosition();
    }


    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getXOffset() {
        return xOffset;
    }

    @Override
    public float getYOffset() {
        return yOffset;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(
                positionAndCondition.getPosition().x + xOffset,
                positionAndCondition.getPosition().y + yOffset,
                width,
                height);
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        positionAndCondition.update(delta, collisionHelper);
        if (positionAndCondition.getPosition().y + height < 0) {
            positionAndCondition.kill();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        final TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);
        batch.draw(currentFrame,
                positionAndCondition.getPosition().x * worldScale,
                positionAndCondition.getPosition().y * worldScale,
                currentFrame.getRegionWidth() * worldScale * actorVisualScale,
                currentFrame.getRegionHeight() * worldScale * actorVisualScale);
    }

    public InputProcessor getInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (Input.Keys.RIGHT == keycode || Input.Keys.D == keycode) {
                    LOG.debug("Right move order");
                    positionAndCondition.startMovingRight();
                    return true;
                } else if (Input.Keys.LEFT == keycode || Input.Keys.A == keycode) {
                    LOG.debug("Left move order");
                    positionAndCondition.startMovingLeft();
                    return true;
                } else if (Input.Keys.UP == keycode || Input.Keys.W == keycode) {
                    LOG.debug("Jump order");
                    positionAndCondition.jump();
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (Input.Keys.RIGHT == keycode || Input.Keys.D == keycode) {
                    positionAndCondition.stopMovingRight();
                    return true;
                } else if (Input.Keys.LEFT == keycode || Input.Keys.A == keycode) {
                    positionAndCondition.stopMovingLeft();
                    return true;
                }

                return super.keyUp(keycode);
            }
        };
    }

    @Override
    public void kill() {
        positionAndCondition.kill();
    }


    @Override
    public boolean isDead() {
        return positionAndCondition.isDead();
    }

    @Override
    public void interactWith(GameActor otherActor) {
    }

    @Override
    public void addStatusEffect(StatusEffect toAdd) {
        positionAndCondition.addStatusEffect(toAdd);
    }
}
