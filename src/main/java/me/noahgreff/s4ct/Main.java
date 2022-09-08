package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import me.noahgreff.s4ct.util.Math;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"unused", "StatementWithEmptyBody"})
public class Main extends JavaPlugin {

    private static final String COM_PORT = "COM3";
    private static final int BAUD_RATE = 9600;

    private static final AtomicReference<Integer> VALUE = new AtomicReference<>(0);

    private static final Thread SYNCHRONIZER = new Thread(() -> {
        SerialPort port = SerialPort.getCommPort(COM_PORT);
        port.setComPortParameters(BAUD_RATE, 8, 1, 0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

        while (!port.openPort()) { } // continuously attempt to open port
        Scanner stream = new Scanner(port.getInputStream());

        while (stream.hasNextLine()) {
            try {
                VALUE.set(stream.nextInt());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    });

    @Override
    public void onEnable() {
        SYNCHRONIZER.start();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                long time = (long) Math.clampedMap(VALUE.get(), 0, 1023, 0, 24000);
                Bukkit.getWorld("world").setTime(time);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, 0, 1);
    }

    @Override
    public void onDisable() {
        SYNCHRONIZER.interrupt();
    }
}
