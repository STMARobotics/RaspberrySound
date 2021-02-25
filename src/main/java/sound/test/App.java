package sound.test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import javax.sound.sampled.FloatControl;

public class App {

  static Clip clip;
  static Clip clip2;
  static Clip clip3;
  public static void play(Clip clip) throws Exception {
    clip.start();
    clip.loop(100);
  }

  public static void playOnce(Clip clip) throws Exception {
    clip.setMicrosecondPosition(0);
    clip.start();
  }

  public static void stop(Clip clip) throws Exception {
    if (clip != null) {
      clip.stop();
      clip.setFramePosition(0);

    }
  }


  public static void main(String[] args) throws Exception {
    clip = AudioSystem.getClip();
    clip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("moving2.wav")));
    
    
    clip2 = AudioSystem.getClip();
    clip2.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("idle2.wav")));
    
    
    clip3 = AudioSystem.getClip();
    clip3.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("Cannon2.wav")));
    
    
    
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    FloatControl gainControl2 = (FloatControl) clip2.getControl(FloatControl.Type.MASTER_GAIN);


    double gain = 0.8;    
    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
    double gain2 = 0.6;    
    float dB2 = (float) (Math.log(gain2) / Math.log(10.0) * 20.0);
    gainControl.setValue(dB);
    gainControl2.setValue(dB2);



    new App().run();
  }

  public void run() throws Exception {

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table1 = inst.getTable("datatable");
    //NetworkTableEntry entry1 = table1.getEntry("Background");
    //NetworkTableEntry entry2 = table1.getEntry("Cannon");
    NetworkTableEntry entry1 = table1.getEntry("clip");
    NetworkTableEntry entry2 = table1.getEntry("clip2");
    NetworkTableEntry entry3 = table1.getEntry("clip3");
    NetworkTableEntry entryMain = table1.getEntry("audio");

    
    inst.startClientTeam(7028);
    // inst.startDSClient();
    // inst.startClient("localhost");

    entry1.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true && activated == true){
          play(clip);
          stop(clip2);
        } else if (state == false){
          stop(clip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);


    entry2.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true && activated == true){
          play(clip2);
          stop(clip);
        } else if (state == false){
          stop(clip2);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);


    entry3.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true && activated == true){
          playOnce(clip3);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

  
      while(true){
        Thread.sleep(1000);
      }    
    }
}
