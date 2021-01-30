package de.ggj21.scw.world.actor.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public abstract class StatusEffect {

    private final float durationInSeconds;
    private float active = 0f;

    private StatusEffect(final float durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public float getSpeedModifier() {
        return 1.0f;
    }

    public boolean isWornOff() {
        return active >= durationInSeconds;
    }

    public void update(float delta) {
        active += delta;
    }

    abstract public Texture getIcon();

    abstract public boolean isPositive();

    public float getRemainingDuration() {
        return durationInSeconds - active;
    }

    public void dispose() {
        getIcon().dispose();
    }


    public static StatusEffect tonno() {
        return new StatusEffect(5f) {
            final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/tonno.png"));

            @Override
            public float getSpeedModifier() {
                return 1.3f;
            }

            @Override
            public Texture getIcon() {
                return catSpriteSheet;
            }

            @Override
            public boolean isPositive() {
                return true;
            }
        };
    }

}
