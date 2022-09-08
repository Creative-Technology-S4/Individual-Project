package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import me.noahgreff.s4ct.util.Math;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Scanner;

@SuppressWarnings({"unused", "StatementWithEmptyBody"})
public class Main extends JavaPlugin {

    private static final String COM_PORT = "COM3";
    private static final int BAUD_RATE = 9600;

    private static Thread THREAD;

    @Override
    public void onEnable() {
        THREAD = new Thread(() -> {
            SerialPort port = SerialPort.getCommPort(COM_PORT);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
            port.setBaudRate(BAUD_RATE);

            while (!port.openPort()) { } // continuously attempt to open port

            Scanner stream = new Scanner(port.getInputStream());

            while (stream.hasNextLine()) {
                try {
                    int value = Integer.parseInt(stream.nextLine());
                    long time = (long) Math.clampedMap(value, 0, 1023, 0, 24000);
                    Bukkit.getWorld("world").setTime(time);
                    System.out.println(time);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            throw new RuntimeException("Stream has abruptly stopped.");
        });

        THREAD.start();
    }

    @Override
    public void onDisable() {
        THREAD.interrupt();
    }
}
