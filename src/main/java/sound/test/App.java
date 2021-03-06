package sound.test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class App {

  static Clip slowClip;
  static Clip fastClip;
  static Clip shotClip;
  static Clip idleClip;
  static Clip rotationClip;
  static Clip promotionClip;
  private Clip currentlyActive = idleClip;
  public static void play(Clip clip) throws Exception {
    clip.start();
    clip.loop(Clip.LOOP_CONTINUOUSLY);
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
    fastClip = AudioSystem.getClip();
    fastClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("fast.wav")));
    

    slowClip = AudioSystem.getClip();
    slowClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("slow.wav")));
    
    idleClip = AudioSystem.getClip();
    idleClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("stopped.wav")));
    
    
    shotClip = AudioSystem.getClip();
    shotClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("Cannon2.wav")));
    
    rotationClip = AudioSystem.getClip();
    rotationClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("rotation.wav")));

    promotionClip = AudioSystem.getClip();
    promotionClip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream("promotion.wav")));
    
    
    //FloatControl gainControl = (FloatControl) slowClip.getControl(FloatControl.Type.MASTER_GAIN);
    //FloatControl gainControl2 = (FloatControl) fastClip.getControl(FloatControl.Type.MASTER_GAIN);
    FloatControl gainControl3 = (FloatControl) idleClip.getControl(FloatControl.Type.MASTER_GAIN);

    double gain = 0.7;    
    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
    //gainControl.setValue(dB);
    //gainControl2.setValue(dB);
    gainControl3.setValue(dB);
    


    new App().run();
  }

  public void run() throws Exception {

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table1 = inst.getTable("datatable");
    NetworkTableEntry slowEntry = table1.getEntry("slow");
    NetworkTableEntry fastEntry = table1.getEntry("fast");
    NetworkTableEntry idleEntry = table1.getEntry("idle");
    NetworkTableEntry shotEntry = table1.getEntry("shot");
    NetworkTableEntry rotationEntry = table1.getEntry("rotation");
    NetworkTableEntry entryMain = table1.getEntry("audio");
    NetworkTableEntry promotionEntry = table1.getEntry("promotion");

    
    inst.startClientTeam(7028);
    // inst.startDSClient();
    // inst.startClient("localhost");
    

    entryMain.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean promotionState = promotionEntry.getBoolean(false);
        boolean rotationState = rotationEntry.getBoolean(false);
        if (state == true && !promotionState){
          play(currentlyActive);
          if (rotationState == true){
            play(rotationClip);
          }
        } else if (state == false){
          stop(slowClip);
          stop(fastClip);
          stop(idleClip);
          stop(rotationClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);



    slowEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true){
          currentlyActive = slowClip;
          if (activated){
            play(slowClip);
            stop(fastClip);
            stop(idleClip);
          }
        } else if (state==false){
          stop(slowClip);
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);


    fastEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true){
          currentlyActive = fastClip;
          if (activated){
            play(fastClip);
            stop(slowClip);
            stop(idleClip);
          }
        } else if (state==false){
          stop(fastClip);
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    idleEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true){
          currentlyActive = idleClip;
          if (activated){
            play(idleClip);
            stop(slowClip);
            stop(fastClip);
          }
        } else if (state==false){
          stop(idleClip);
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);


    shotEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true && activated == true){
          playOnce(shotClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    rotationEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        boolean promotionState = promotionEntry.getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true){
          if (activated && !promotionState){
            play(rotationClip);
          }
        } else if (state==false){
          stop(rotationClip);
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    promotionEntry.addListener(event -> {
      try {
        System.out.println("Event");
        boolean state = event.getEntry().getBoolean(false);
        if (state == true){
          stop(slowClip);
          stop(fastClip);
          stop(idleClip);
          stop(rotationClip);
          playOnce(promotionClip);
        }
        else if (state==false){
        } 
      } catch (Exception e) {
        e.printStackTrace();
      }
      }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    promotionClip.addLineListener(event -> {
      boolean rotationState = rotationEntry.getBoolean(false);

      if (event.getType() == LineEvent.Type.STOP){
        promotionEntry.setBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (activated){
          try {
            play(currentlyActive);
            if (rotationState == true){
              play(rotationClip);
           }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });
    

  
      while(true){
        Thread.sleep(1000);
      }    
    }
}
