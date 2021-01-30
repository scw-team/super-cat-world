package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.world.CollisionHelper;
import de.ggj21.scw.world.CollisionHelperFactory;

import java.util.EnumSet;
import java.util.Set;

public class Cat implements GameActor {

    private static final float HORIZONTAL_SPEED = 5;

    private final Vector2 position;
    private final Animation<TextureRegion> animation;
    private final float scale;
    private final CollisionHelper collisionHelper;

    private final Set<State> currentStates = EnumSet.of(State.InTheAir);

    float elapsedTime = 0;

    public Cat(final Vector2 startPosition, final float scale, final CollisionHelperFactory collisionHelperFactory) {
        final float width = 16;
        final float height = 16;
        this.scale = scale;
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/cat.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 16, 16);
        TextureRegion[] animationFrames = frameSplit[0];
        animation = new Animation<TextureRegion>(0.35f, animationFrames);
        position = startPosition;
        collisionHelper = collisionHelperFactory.getHelperForActor(width, height);
    }


    @Override
    public void update(float delta) {
        elapsedTime += delta;
        Vector2 endPosition = new Vector2(position);
        for (final State s : currentStates) {
            endPosition = s.update(endPosition, delta, collisionHelper);
        }
        position.set(endPosition);
    }

    @Override
    public void render(SpriteBatch batch) {
        final TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);
        batch.draw(currentFrame, position.x * scale, position.y * scale, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }

    public enum State {
        /**
         * On the ground, but not moving.
         */
        AtRest {
            @Override
            Vector2 update(Vector2 start, float delta, CollisionHelper collisionHelper) {
                return start;
            }
        },
        /**
         * On the ground, moving left.
         */
        MovingLeft {
            @Override
            Vector2 update(Vector2 start, float delta, CollisionHelper collisionHelper) {
                final Vector2 end = new Vector2(start.x - HORIZONTAL_SPEED * delta, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * On the ground, moving right.
         */
        MovingRight {
            @Override
            Vector2 update(Vector2 start, float delta, CollisionHelper collisionHelper) {
                final Vector2 end = new Vector2(start.x + HORIZONTAL_SPEED * delta, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * In the air, jumping / falling.
         */
        InTheAir {
            private static final float MAX_VERTICAL_SPEED = 20;
            private static final float GRAVITY = 3f;
            private float verticalSpeed = 0;

            @Override
            float getVerticalSpeed() {
                return super.getVerticalSpeed();
            }

            @Override
            Vector2 update(Vector2 start, float delta, CollisionHelper collisionHelper) {
                if (verticalSpeed < MAX_VERTICAL_SPEED) {
                    verticalSpeed = Math.min(MAX_VERTICAL_SPEED, verticalSpeed + delta * GRAVITY);
                }
                final Vector2 end = new Vector2(start.x, start.y - delta * verticalSpeed);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * Unable to do anything.
         */
        Dead {
            @Override
            Vector2 update(Vector2 start, float delta, CollisionHelper collisionHelper) {
                return start;
            }
        };

        abstract Vector2 update(final Vector2 start, final float delta, CollisionHelper collisionHelper);

        float getVerticalSpeed() {
            return 0;
        }
    }
}
