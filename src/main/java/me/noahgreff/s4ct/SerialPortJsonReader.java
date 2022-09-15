package me.noahgreff.s4ct;

import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SerialPortJsonReader implements SerialPortDataListener {

    private static final String START_DELIMITER = "<";
    private static final String END_DELIMITER = ">";

    private final int event;
    private final List<Consumer<JsonObject>> listeners;

    private String partialData = "";

    public SerialPortJsonReader(int event) {
        this.event = event;
        this.listeners = new ArrayList<>();
    }

    public void addListener(Consumer<JsonObject> consumer) {
        listeners.add(consumer);
    }

    @Override
    public int getListeningEvents() {
        return event;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        String rawData = new String(event.getReceivedData(), StandardCharsets.UTF_8)
                .replaceAll("\\n", "")
                .replaceAll("\\r", "")
                .trim();

        if (rawData.contains(START_DELIMITER)) {
            partialData = rawData;
        } else if (rawData.contains(END_DELIMITER)) {
            String completeData = (partialData + rawData)
                    .replace(START_DELIMITER, "")
                    .replace(END_DELIMITER, "");

            try {
                JsonObject jsonObject = JsonParser.parseString(completeData).getAsJsonObject();
                listeners.forEach(consumer -> {
                    try {
                        consumer.accept(jsonObject);
                    } catch (Exception exception) {
                        System.err.println("An error occurred in a JSON listener.");
                        exception.printStackTrace(System.err);
                    }
                });
            } catch (Exception exception) {
                System.err.println("Error while parsing serial port data to JSON; likely a formatting error.");
                System.err.println("Is this correct: " + completeData);
                exception.printStackTrace(System.err);
            } finally {
                partialData = "";
            }
        } else {
            partialData += rawData;
        }
    }
}
