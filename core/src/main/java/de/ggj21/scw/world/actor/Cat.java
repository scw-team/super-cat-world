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

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> walkAnimation;

    public Cat(final Vector2 startPosition,
               final CollisionHelperFactory collisionHelperFactory,
               SoundManager soundManager, final float worldScale) {
        super(startPosition, collisionHelperFactory, soundManager,
                44, 64, 5, 0,
                true, worldScale, GameWorld.VIEWPORT_SCALE);
        idleAnimation = loadAnimation("sprite/Cat_Default.png", 0.3f);
        jumpAnimation = loadAnimation("sprite/Cat_Jump.png", 0.3f);
        walkAnimation = loadAnimation("sprite/Cat_Walk.png", 0.15f);
    }

    private Animation<TextureRegion> loadAnimation(String spriteFile, float frameDuration) {
        final Animation<TextureRegion> idleAnimation;
        final Texture catSpriteSheet = new Texture(Gdx.files.internal(spriteFile));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 64, 64);
        TextureRegion[] animationFrames = frameSplit[0];
        idleAnimation = new Animation<>(frameDuration, animationFrames);
        return idleAnimation;
    }

    @Override
    Animation<TextureRegion> getActiveAnimation() {
        if (positionAndCondition.isInTheAir()) {
            return jumpAnimation;
        } else if (positionAndCondition.isMoving()) {
            return walkAnimation;
        } else {
            return idleAnimation;
        }
    }

    public List<StatusEffect> getStatusEffects() {
        return positionAndCondition.getStatusEffects();
    }
}
