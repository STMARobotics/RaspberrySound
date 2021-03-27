package sound.test;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class App {

  private Clip slowClip;
  private Clip fastClip;
  private Clip shotClip;
  private Clip idleClip;
  private Clip rotationClip;
  private Clip promotionClip;
  private NetworkTable table1;
  private NetworkTableEntry slowEntry;
  private NetworkTableEntry fastEntry;
  private NetworkTableEntry idleEntry;
  private NetworkTableEntry shotEntry;
  private NetworkTableEntry rotationEntry;
  private NetworkTableEntry entryMain;
  private NetworkTableEntry promotionEntry;

  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

  private void play(Clip clip) {
    if (clip != null) {
      clip.start();
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  private void setVolume(Clip clip, Double volume) {
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    double gain = volume;   
    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
    gainControl.setValue(dB);
  }

  private void playOnce(Clip clip) throws Exception {
    if (clip != null) {
      clip.setMicrosecondPosition(0);
      clip.start();
    }
  }

  private void stop(Clip clip) throws Exception {
    if (clip != null) {
      clip.stop();
      clip.setFramePosition(0);
    }
  }

  private NetworkTableEntry getOrCreateEntry(String key) {
    NetworkTableEntry entry = table1.getEntry(key);
    entry.setBoolean(entry.getBoolean(false));
    return entry;
  }

  private void resumeActive() {
    if (entryMain.getBoolean(false)) {
      shotEntry.setBoolean(false);
      if (rotationEntry.getBoolean(false)) {
        play(rotationClip);
      }
      if (idleEntry.getBoolean(false)) {
        play(idleClip);
      }
      if (slowEntry.getBoolean(false)) {
        play(slowClip);
      }
      if (fastEntry.getBoolean(false)) {
        play(fastClip);
      }
    }
  }

  public void run(String[] args) throws Exception {
    if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
      System.out.println("Starting pulseaudio");
      Runtime.getRuntime().exec("pulseaudio --start");
    }
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    if (args == null || args.length ==0 || args[0] == null || args[0].length() == 0) {
      inst.startClientTeam(7028);
    } else {
      inst.startClient(args[0]);
    }
    
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

    setVolume(idleClip,0.1);
    setVolume(slowClip,0.2);
    setVolume(fastClip,0.3);

    while(!inst.isConnected()) {
      Thread.sleep(1000);
    }

    table1 = inst.getTable("audio");
    slowEntry = getOrCreateEntry("slow");
    fastEntry = getOrCreateEntry("fast");
    idleEntry = getOrCreateEntry("idle");
    shotEntry = getOrCreateEntry("shot");
    rotationEntry = getOrCreateEntry("rotation");
    entryMain = getOrCreateEntry("audio");
    promotionEntry = getOrCreateEntry("promotion");

    inst.addConnectionListener((event) -> {
      if (event.connected) {
        System.out.println("Connected");
        resumeActive();
      } else {
        System.out.println("Disconnected");
        try {
          entryMain.setBoolean(false);
          stop(slowClip);
          stop(fastClip);
          stop(idleClip);
          stop(rotationClip);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }, true);

    entryMain.addListener(event -> {
      try {
        boolean state = event.getEntry().getBoolean(false);
        boolean promotionState = promotionEntry.getBoolean(false);
        if (state && !promotionState) {
          System.out.println("Sound on");
          resumeActive();
        } else if (!state) {
          System.out.println("Sound off");
          stop(slowClip);
          stop(fastClip);
          stop(idleClip);
          stop(rotationClip);
        } else {
          System.out.println("Sound on but promo playing");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    slowEntry.addListener(event -> {
      try {
        System.out.println("Slow");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true) {
          if (activated) {
            play(slowClip);
            stop(fastClip);
            stop(idleClip);
          }
        } else if (state == false) {
          stop(slowClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    fastEntry.addListener(event -> {
      try {
        System.out.println("Fast");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true) {
          if (activated) {
            play(fastClip);
            stop(slowClip);
            stop(idleClip);
          }
        } else if (state == false) {
          stop(fastClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    idleEntry.addListener(event -> {
      try {
        System.out.println("Idle");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true) {
          if (activated) {
            play(idleClip);
            stop(slowClip);
            stop(fastClip);
          }
        } else if (state == false) {
          stop(idleClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    shotEntry.addListener(event -> {
      try {
        System.out.println("Shot");
        boolean state = event.getEntry().getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true && activated == true) {
          playOnce(shotClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    shotClip.addLineListener(event -> {
      if (event.getType() == LineEvent.Type.STOP) {
        shotEntry.setBoolean(false);
      }
    });

    rotationEntry.addListener(event -> {
      try {
        System.out.println("Rotate");
        boolean state = event.getEntry().getBoolean(false);
        boolean promotionState = promotionEntry.getBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (state == true) {
          if (activated && !promotionState) {
            play(rotationClip);
          }
        } else if (state == false) {
          stop(rotationClip);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    promotionEntry.setBoolean(false);
    promotionEntry.addListener(event -> {
      try {
        System.out.println("Promo");
        boolean state = event.getEntry().getBoolean(false);
        if (state == true) {
          stop(slowClip);
          stop(fastClip);
          stop(idleClip);
          stop(rotationClip);
          playOnce(promotionClip);
        } else if (state == false) {
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    promotionClip.addLineListener(event -> {
      if (event.getType() == LineEvent.Type.STOP) {
        promotionEntry.setBoolean(false);
        boolean activated = entryMain.getBoolean(false);
        if (activated) {
          try {
            resumeActive();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });

    resumeActive();

    while (true) {
      Thread.sleep(1000);
    }
  }
}
