package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"StatementWithEmptyBody", "unused"})
public class Main extends JavaPlugin {

    private static final String COM_PORT = "COM3";
    private static final int BAUD_RATE = 9600;

    private static final AtomicReference<Integer> VALUE = new AtomicReference<>(0);

    @Override
    public void onEnable() {
        SerialPort port = SerialPort.getCommPort(COM_PORT);
        while (!port.openPort()) { } // continuously attempt to open port

        SerialPortJsonReader reader = new SerialPortJsonReader(SerialPort.LISTENING_EVENT_DATA_RECEIVED);
        reader.addListener(jsonObject -> Optional.of(jsonObject.get("value")).ifPresent(value -> VALUE.set(value.getAsInt())));
        port.addDataListener(reader);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Optional.ofNullable(Bukkit.getWorld("world")).ifPresent(world -> world.setTime((long) VALUE.get()));
//            System.out.println(time);
        }, 0, 1);
    }
}
