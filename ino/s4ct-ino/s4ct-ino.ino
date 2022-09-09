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
    int sensorValue = analogRead(0);

    if (sensorValue > value + 1 || sensorValue < value - 1)
    {
        value = sensorValue;

        JsonObject root = doc.to<JsonObject>();
        root["value"] = value;
        sendJson(root);
    }

    delay(50);
}

void sendJson(JsonObject &json)
{
    String data = START_DELIMITER;
    serializeJson(json, data);
    data += END_DELIMITER;
    Serial.println(data);
}
