#include <ArduinoJson.h>

const String START_DELIMITER = "<";
const String END_DELIMITER = ">";

int value = 0; // for potentiometer debouncing

void setup()
{
    Serial.begin(9600);
}

void loop()
{
    DynamicJsonDocument doc(1024);
    JsonObject root = doc.to<JsonObject>();

    // world time
    int sensorValue = analogRead(0);
    if (sensorValue > value + 1 || sensorValue < value - 1)
    {
        value = sensorValue;
        root["time"] = map(value, 0, 1023, 0, 24000);
    }

    sendJson(root);
    delay(50);
}

void sendJson(JsonObject &json)
{
    String data = START_DELIMITER;
    serializeJson(json, data);
    data += END_DELIMITER;

    // only print if `data` has json
    if (data.length() > 4)
    {
        Serial.println(data);
    }
}
