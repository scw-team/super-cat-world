package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelperFactory;
import de.ggj21.scw.world.GameWorld;
import de.ggj21.scw.world.actor.effect.StatusEffect;

import java.util.List;

public class Cat extends AbstractActor {

    public Cat(final Vector2 startPosition,
               final CollisionHelperFactory collisionHelperFactory,
               SoundManager soundManager, final float worldScale) {
        super(startPosition, collisionHelperFactory, soundManager,
                44, 64, 5, 0,
                true, worldScale, GameWorld.VIEWPORT_SCALE);
    }

    @Override
    Animation<TextureRegion> getAnimation() {
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/Cat_Default.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 64, 64);
        TextureRegion[] animationFrames = frameSplit[0];
        return new Animation<TextureRegion>(0.35f, animationFrames);
    }

    public List<StatusEffect> getStatusEffects() {
        return positionAndCondition.getStatusEffects();
    }
}
