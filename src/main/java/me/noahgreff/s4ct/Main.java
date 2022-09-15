package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.JsonArray;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"StatementWithEmptyBody", "unused"})
public class Main extends JavaPlugin {

    private static final String COM_PORT = "COM3";
    private static final int BAUD_RATE = 9600;

    private static final AtomicReference<Integer> VALUE = new AtomicReference<>(0);
    private static final AtomicReference<Vector> ANGLE = new AtomicReference<>(new Vector(0, 0, 0));

    @Override
    public void onEnable() {
        SerialPort port = SerialPort.getCommPort(COM_PORT);
        while (!port.openPort()) { } // continuously attempt to open port

        SerialPortJsonReader reader = new SerialPortJsonReader(SerialPort.LISTENING_EVENT_DATA_RECEIVED);
        reader.addListener(jsonObject -> Optional.ofNullable(jsonObject.get("time")).ifPresent(value -> VALUE.set(value.getAsInt())));
        reader.addListener(jsonObject -> Optional.ofNullable(jsonObject.get("angle")).ifPresent(value -> {
            JsonArray angle = value.getAsJsonArray();
            double pitch = angle.get(0).getAsDouble();
            double yaw = angle.get(1).getAsDouble();
            ANGLE.get().setX(0).setY(pitch).setZ(yaw);
        }));
        port.addDataListener(reader);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            World world = Bukkit.getWorld("world");

            if (world != null) {
                world.setTime(VALUE.get().longValue());

                for (Player player : world.getPlayers()) {
                    Location location = player.getLocation().clone();
                    location.setPitch((float) ANGLE.get().getY());
                    location.setYaw((float) ANGLE.get().getZ());
                    player.teleport(location); // set looking vector for every player by teleporting them
                }
            }
        }, 0, 1);
    }
}
