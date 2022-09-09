package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import me.noahgreff.s4ct.util.Math;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"StatementWithEmptyBody"})
public class Main extends JavaPlugin {

    private static final String COM_PORT = "COM3";
    private static final int BAUD_RATE = 9600;

    private static final AtomicReference<Integer> VALUE = new AtomicReference<>(0);

    @Override
    public void onEnable() {
        SerialPort port = SerialPort.getCommPort(COM_PORT);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0 /* 1000 */, 0);
        port.setComPortParameters(BAUD_RATE, 8, SerialPort.ONE_POINT_FIVE_STOP_BITS, SerialPort.NO_PARITY);

        while (!port.openPort()) { } // continuously attempt to open port

        port.addDataListener(new SerialPortDataListener() {
            private static final String START_DELIMITER = "[";
            private static final String END_DELIMITER = "]";

            private String partialData = "";

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                String raw = new String(event.getReceivedData(), StandardCharsets.UTF_8)
                        .replaceAll("\\n", "")
                        .replaceAll("\\r", "")
                        .trim();

                if (raw.contains(START_DELIMITER)) {
                    partialData = raw;
                } else if (raw.contains(END_DELIMITER)) {
                    String full = (partialData + raw)
                            .replace(START_DELIMITER, "")
                            .replace(END_DELIMITER, "");

                    if (full.isEmpty() || full.isBlank()) return; // sometimes string data is empty

                    try {
                        VALUE.set(Integer.parseInt(full));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            long time = (long) Math.clampedMap(VALUE.get(), 0, 1023, 0, 24000);
            Optional.ofNullable(Bukkit.getWorld("world")).ifPresent(world -> world.setTime(time));
//            System.out.println(time);
        }, 0, 20);
    }
}
