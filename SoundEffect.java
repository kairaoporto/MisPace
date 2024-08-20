import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

class SoundEffect {

    Clip c;
    URL soundArray[] = new URL[10];

    public SoundEffect(){

        soundArray[1] = getClass().getResource("/sounds/Explosion.wav");
        soundArray[2] = getClass().getResource("/sounds/Pickup.wav");
    }

    public void File (int i){
        try{
            AudioInputStream audio = AudioSystem.getAudioInputStream(soundArray[i]);
            c = AudioSystem.getClip();
            c.open(audio);

        } catch(Exception e){}
    }

    public void play(){
        c.start();
    }

}
