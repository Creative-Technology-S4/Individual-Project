package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Scanner;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {

    public static final String COM_PORT = "COM3";

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
        SerialPort port = SerialPort.getCommPort(COM_PORT);

        if (!port.openPort()) {
            throw new RuntimeException("Could not open serial port '" + COM_PORT + "'.");
        }

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        Scanner data = new Scanner(port.getInputStream());

        while (data.hasNextLine()) {
            System.out.println(data.nextLine());
        }
    }

    @Override
    public void onDisable() {}
}
