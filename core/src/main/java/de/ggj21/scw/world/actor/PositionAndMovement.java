package de.ggj21.scw.world.actor;

import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.world.CollisionHelper;

import java.util.EnumSet;
import java.util.Set;

class PositionAndMovement {

    private static final float MAX_VERTICAL_SPEED = 500;
    private static final float GRAVITY = 1500f;

    private final Vector2 position;
    private float verticalSpeed = 0;
    private final float horizonalSpeed;

    private final Set<State> currentStates = EnumSet.noneOf(State.class);

    PositionAndMovement(final Vector2 startPosition, float horizonalSpeed) {
        position = startPosition;
        this.horizonalSpeed = horizonalSpeed;
    }

    Vector2 getPosition() {
        return position;
    }

    private float getVerticalSpeed() {
        return verticalSpeed;
    }

    void setVerticalSpeed(float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    void addState(final State toAdd) {
        this.currentStates.add(toAdd);
    }

    void removeState(final State toRemove) {
        this.currentStates.remove(toRemove);
    }

    void update(final float delta, CollisionHelper collisionHelper) {
        Vector2 endPosition = new Vector2(position);
        for (final State s : currentStates) {
            endPosition = s.update(this, delta, collisionHelper);
            position.set(endPosition);
        }
    }

    enum State {
        /**
         * On the ground, moving left.
         */
        MovingLeft {
            @Override
            Vector2 update(PositionAndMovement pos, float delta, CollisionHelper collisionHelper) {
                final Vector2 start = pos.getPosition();
                final Vector2 end = new Vector2(start.x - pos.horizonalSpeed * delta, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * On the ground, moving right.
         */
        MovingRight {
            @Override
            Vector2 update(PositionAndMovement pos, float delta, CollisionHelper collisionHelper) {
                final Vector2 start = pos.getPosition();
                final Vector2 end = new Vector2(start.x + pos.horizonalSpeed * delta, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * Affected by gravity.
         */
        Falling {
            @Override
            Vector2 update(PositionAndMovement pos, float delta, CollisionHelper collisionHelper) {
                final Vector2 start = pos.getPosition();
                if (pos.getVerticalSpeed() < MAX_VERTICAL_SPEED) {
                    pos.setVerticalSpeed(Math.min(MAX_VERTICAL_SPEED, pos.getVerticalSpeed() + delta * GRAVITY));
                }
                final Vector2 end = new Vector2(start.x, start.y - delta * pos.getVerticalSpeed());
                final Vector2 resolvedEnd = collisionHelper.resolve(start, end);
                if (!end.equals(resolvedEnd)) {
                    pos.setVerticalSpeed(0);
                }
                return resolvedEnd;
            }
        },
        /**
         * Unable to do anything.
         */
        Dead {
            @Override
            Vector2 update(PositionAndMovement start, float delta, CollisionHelper collisionHelper) {
                return start.getPosition();
            }
        };

        abstract Vector2 update(final PositionAndMovement start, final float delta, CollisionHelper collisionHelper);
    }
}
