# Raspberry PI Sound Player

This is a Network Table client that can run on a Raspberry Pi to play audio.

## Building
The project uses Gradle. Clone it to your Pi and be build it with the following command:
``` bash
./gradlew build
```

## Running
The project can easily be run using Gradle. It will run on a Raspberry Pi, but it will also run on Windows (and
probably Linux and Mac.)
The program takes the following arguments:
- The address of the Network Table server (the robot)

``` bash
./gradlew run --args="10.70.28.2"
```

## Pi Configuration
### Output device
First, make sure you have selected the right audio output device, the default is HDMI audio. To configure audio, right
click the speaker icon on the desktop and select the output device. You can also configure it from the terminal (SSH)
using `sudo raspi-config` and selecting System Options > Audio.

### Scheduling at startup
Schedule this project to run on startup with systemd.

Create a file with the below contents in the folder `/etc/systemd/system`.

```
sudo nano /etc/systemd/system/raspberrysound.service
```

Add contents like this to the file and save:
```
[Unit]
Description=Tank Bot Sound
Wants=sound.target
After=sound.target
[Service]
Restart=on-failure
RestartSec=5s
User=pi
Type=simple
RemainAfterExit=no
WorkingDirectory=/home/pi/RaspberrySound
ExecStart=/home/pi/RaspberrySound/gradlew run
[Install]
WantedBy=multi-user.target
```

Start the service and then check that it starts up with these comands:
```
sudo systemctl start raspberrysound
journalctl -f -u raspberrysound
```

If the service was successful enable so it will start automatically when the computer boots up:
```
sudo systemctl enable raspberrysound.service
```

### Java configuration
You may need to edit the `sound.properties` file in the Java conf folder. These settings work on a Raspberry Pi B
running Raspberry Pi OS (Raspbian):

```
javax.sound.sampled.Clip=com.sun.media.sound.DirectAudioDeviceProvider
javax.sound.sampled.Port=com.sun.media.sound.PortMixerProvider
javax.sound.sampled.SourceDataLine=com.sun.media.sound.DirectAudioDeviceProvider
javax.sound.sampled.TargetDataLine=com.sun.media.sound.DirectAudioDeviceProvider
```
This solution was found [here](https://nealvs.wordpress.com/2017/08/11/java-sound-on-a-raspberry-pi-with-openjdk/).

## Using Outline Viewer
In a VS Code with WPILib extensions, press CTRL + SHIFT + P, select _WPILib: Start Tool_, then select _OutlineViewer_.
This will launch the Outline Viewer where you can see and edit Network Tables.

Make sure Server Mode and the Default Port setting are on, then press OK.

Finally, tart the Pi Audio Player application. The application should connect to the Network Table. Now, in Outline
Viewer you can change the Network Table values to trigger events in the player.

## Triggering sound from the robot
The robot sets a network table value to trigger playing the audio file.
``` java
    private final NetworkTableInstance inst = NetworkTableInstance.getDefault();
    private final NetworkTable table = inst.getTable("audio");
    private final NetworkTableEntry playEntry = table.getEntry("shot");

    public void playGun() {
        playEntry.setBoolean(true);
    }
```

## Raspberry Pi Amp HAT
Documentation for the Amp HAT can be found here: https://www.inno-maker.com/product/hifi-amp-hat/

The overlay needs to be added to the `config.txt` file, and then a reboot is needed. This can be done with the following commands:
```
sudo echo "dtoverlay=hifiberry-amp" >> /boot/config.txt
sudo reboot 0
```
Once the reboot is complete use `raspi-config` to select the output device (see above).
