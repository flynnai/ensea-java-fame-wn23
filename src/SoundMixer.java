import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class SoundMixer {
    private static SoundMixer instance = null;
    Dictionary<String, MediaPlayer> allowedSounds;
    private SoundMixer() {

        // sound filenames must be put here before using
        List<String> allowedSoundFileNames = new ArrayList<>(Arrays.asList(
                "footstep1.wav",
                "footstep2.wav",
                "ding.wav",
                "land_on_ground.wav",
                "jump.wav",
                "grab_edge.wav",
                "climb_up.wav",
                "win_flag.wav"
        ));

        allowedSounds = new Hashtable<>();
        for (String fileName : allowedSoundFileNames) {
            Media sound = new Media(new File("sound/" + fileName).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            allowedSounds.put(fileName, mediaPlayer);
        }

    }

    public static void initialize() {
        instance = new SoundMixer();
    }

    public static void playSound(String fileName) {
        MediaPlayer player = instance.allowedSounds.get(fileName);
        player.seek(new Duration(0));
        player.play();
    }
}