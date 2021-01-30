package de.ggj21.scw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    public enum Sounds {
        JumpStart("sound/JUMP_START_16.wav"),
        JumpEnd("sound/JUMP_END_16.wav"),
        Miao("sound/MIAO_16.wav"),
        Yum("sound/SCHMATZ_16.wav"),
        Death("sound/DEATH_16.wav"),
        ;

        private String file;

        Sounds(String file) {
            this.file = file;
        }

        public String getSoundFileName() {
            return this.file;
        }
    }

    private final Map<Sounds, Sound> sounds = new HashMap<Sounds, Sound>();
    private final Music music;

    public SoundManager() {
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/ATMO_16.wav"));
        music.setLooping(true);
        music.setVolume(.15f);
        music.play();

        for (Sounds sound : Sounds.values()) {
            sounds.put(sound, Gdx.audio.newSound(Gdx.files.internal(sound.getSoundFileName())));
        }
    }

    public void setMusicEnabled(final boolean enableMusic) {
        if (enableMusic) {
            music.play();
        } else {
            music.pause();
        }
    }

    public void setVolume(final float volumeToSet) {
        music.setVolume(volumeToSet);
    }

    public void dispose() {
        music.stop();
        music.dispose();
    }

    public void playSound(Sounds sound) {
        playSound(sound, 1f);
    }

    private void playSound(Sounds sound, float volume) {
        sounds.get(sound).play(volume);
    }
}
