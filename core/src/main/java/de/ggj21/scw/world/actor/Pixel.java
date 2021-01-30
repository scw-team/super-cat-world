package de.ggj21.scw.world.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.ggj21.scw.SoundManager;
import de.ggj21.scw.world.CollisionHelperFactory;

public class Pixel extends AbstractActor {

    public Pixel(final Vector2 startPosition,
                 final CollisionHelperFactory collisionHelperFactory,
                 SoundManager soundManager, final float worldScale) {
        super(startPosition, collisionHelperFactory, soundManager, 64, 45, worldScale, 1 / 2f);
    }

    @Override
    Animation<TextureRegion> getAnimation() {
        final Texture catSpriteSheet = new Texture(Gdx.files.internal("sprite/Pixel.png"));
        TextureRegion[][] frameSplit = TextureRegion.split(catSpriteSheet, 64, 45);
        TextureRegion[] animationFrames = frameSplit[0];
        return new Animation<TextureRegion>(0.35f, animationFrames);
    }
}
