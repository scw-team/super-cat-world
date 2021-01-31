package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelperFactory;
import de.ggj21.scw.world.GameWorld;

public class Pixel extends AbstractActor {

    private final Animation<TextureRegion> idleAnimation;

    public Pixel(final Vector2 startPosition,
                 final CollisionHelperFactory collisionHelperFactory,
                 SoundManager soundManager, final float worldScale) {
        super(startPosition, collisionHelperFactory, soundManager,
                64, 45, 0, 0,
                true, worldScale, GameWorld.VIEWPORT_SCALE);
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/Pixel.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 64, 45);
        TextureRegion[] animationFrames = frameSplit[0];
        idleAnimation = new Animation<>(0.35f, animationFrames);
    }

    @Override
    Animation<TextureRegion> getActiveAnimation() {
        return idleAnimation;
    }
}
