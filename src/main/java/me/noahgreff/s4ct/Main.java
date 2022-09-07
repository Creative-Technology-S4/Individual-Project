package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Scanner;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {

    @Override
    public void onLoad() {}

    @Override
    public void onEnable() {
//        SerialPort[] ports = SerialPort.getCommPorts();
//
//        System.out.println("Select a port:");
//        for (int i = 0; i < ports.length; i++) {
//            System.out.println(i +  ": " + ports[i].getSystemPortName());
//        }
//
//        Scanner scanner = new Scanner(System.in);
//        SerialPort serialPort = ports[scanner.nextInt()];
//
//        if (serialPort.openPort()) {
//            System.out.println("Port opened successfully.");
//        } else {
//            System.out.println("Unable to open the port.");
//            return;
//        }
//
//        //serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
//        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
//        Scanner data = new Scanner(ports[0].getInputStream());
//
//        while (data.hasNextLine()) {
//            System.out.println(data.nextLine());
//        }
        SerialPort port = SerialPort.getCommPort("COM3");

        if (!port.openPort()) {
            throw new RuntimeException("Could not open serial port 'COM3'.");
        }

        //serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        Scanner data = new Scanner(port.getInputStream());

        while (data.hasNextLine()) {
            System.out.println(data.nextLine());
        }
    }

    @Override
    public void onDisable() {}
}
