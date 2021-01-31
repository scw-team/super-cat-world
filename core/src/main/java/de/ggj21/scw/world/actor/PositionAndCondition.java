package de.ggj21.scw.world.actor;

import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelper;
import de.ggj21.scw.world.GameWorld;
import de.ggj21.scw.world.actor.effect.StatusEffect;

import java.util.*;

class PositionAndCondition {

    private static final float MAX_VERTICAL_SPEED = 3_000 * GameWorld.VIEWPORT_SCALE;
    private static final float GRAVITY = 4_500f * GameWorld.VIEWPORT_SCALE;

    private final Vector2 position;
    private float verticalSpeed = 0;
    private final float horizonalSpeed;
    private final float jumpSpeed;
    private final SoundManager soundManager;
    private boolean jumping = false;

    private final Set<State> currentStates;
    private final List<StatusEffect> statusEffects = new ArrayList<>();

    PositionAndCondition(final Vector2 startPosition, float horizontalSpeed, float jumpSpeed, boolean affectedByGravity, SoundManager soundManager) {
        position = startPosition;
        this.horizonalSpeed = horizontalSpeed;
        this.jumpSpeed = jumpSpeed;
        this.soundManager = soundManager;
        if (affectedByGravity) {
            currentStates = EnumSet.of(State.Falling);
        } else {
            currentStates = EnumSet.noneOf(State.class);
        }
    }

    Vector2 getPosition() {
        return position;
    }

    private float getVerticalSpeed() {
        return verticalSpeed;
    }

    void startMovingLeft() {
        currentStates.add(State.MovingLeft);
    }

    void stopMovingLeft() {
        currentStates.remove(State.MovingLeft);
    }

    void startMovingRight() {
        currentStates.add(State.MovingRight);
    }

    void stopMovingRight() {
        currentStates.remove(State.MovingRight);
    }

    void kill() {
        currentStates.add(State.Dead);
    }

    boolean isDead() {
        return currentStates.contains(State.Dead);
    }

    void jump() {
        if (!jumping && verticalSpeed == 0) {
            float horizontalSpeedModifier = 1f;
            for (StatusEffect effect : statusEffects) {
                horizontalSpeedModifier *= effect.getSpeedModifier();
            }
            setVerticalSpeed(-jumpSpeed * horizontalSpeedModifier);
            jumping = true;
            soundManager.playSound(SoundManager.Sounds.JumpStart);
        }
    }

    public void addStatusEffect(StatusEffect toAdd) {
        statusEffects.add(toAdd);
    }

    List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    SoundManager getSoundManager() {
        return soundManager;
    }

    private void setVerticalSpeed(float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    void update(final float delta, CollisionHelper collisionHelper) {
        float horizontalSpeedModifier = 1f;
        final ListIterator<StatusEffect> effectListIterator = statusEffects.listIterator();
        while (effectListIterator.hasNext()) {
            final StatusEffect effect = effectListIterator.next();
            effect.update(delta);
            if (effect.isWornOff()) {
                effect.dispose();
                effectListIterator.remove();
            } else {
                horizontalSpeedModifier *= effect.getSpeedModifier();
            }
        }
        Vector2 endPosition = new Vector2(position);
        for (final State s : currentStates) {
            endPosition = s.update(this, delta, collisionHelper, horizontalSpeedModifier);
            position.set(endPosition);
        }
    }

    enum State {
        /**
         * On the ground, moving left.
         */
        MovingLeft {
            @Override
            Vector2 update(PositionAndCondition pos, float delta, CollisionHelper collisionHelper, float horizontalSpeedModifier) {
                final Vector2 start = pos.getPosition();
                final Vector2 end = new Vector2(start.x - pos.horizonalSpeed * delta * horizontalSpeedModifier, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * On the ground, moving right.
         */
        MovingRight {
            @Override
            Vector2 update(PositionAndCondition pos, float delta, CollisionHelper collisionHelper, float horizontalSpeedModifier) {
                final Vector2 start = pos.getPosition();
                final Vector2 end = new Vector2(start.x + pos.horizonalSpeed * delta * horizontalSpeedModifier, start.y);
                return collisionHelper.resolve(start, end);
            }
        },
        /**
         * Affected by gravity.
         */
        Falling {
            @Override
            Vector2 update(PositionAndCondition pos, float delta, CollisionHelper collisionHelper, float horizontalSpeedModifier) {
                final Vector2 start = pos.getPosition();
                if (pos.getVerticalSpeed() < MAX_VERTICAL_SPEED) {
                    pos.setVerticalSpeed(Math.min(MAX_VERTICAL_SPEED, pos.getVerticalSpeed() + delta * GRAVITY));
                }
                final Vector2 end = new Vector2(start.x, start.y - delta * pos.getVerticalSpeed());
                final Vector2 resolvedEnd = collisionHelper.resolve(start, end);
                if (!end.equals(resolvedEnd)) {
                    if (pos.jumping && pos.getVerticalSpeed() > 0) {
                        pos.jumping = false;
                        pos.soundManager.playSound(SoundManager.Sounds.JumpEnd);
                    }
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
            Vector2 update(PositionAndCondition start, float delta, CollisionHelper collisionHelper, float horizontalSpeedModifier) {
                return start.getPosition();
            }
        };

        abstract Vector2 update(final PositionAndCondition start, final float delta, CollisionHelper collisionHelper, float horizontalSpeedModifier);
    }
}
