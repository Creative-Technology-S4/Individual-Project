#include <MPU9250_WE.h>

MPU9250_WE mpu = MPU9250_WE(0x68);

const String START_DELIMITER = "<";
const String END_DELIMITER = ">";

int value = 0; // for potentiometer debouncing

void setup()
{
    Serial.begin(9600);
    Wire.begin();

    mpu.autoOffsets();
    mpu.initMagnetometer();
}

void loop()
{
    // world time
    int sensorValue = analogRead(0);
    if (sensorValue > value + 1 || sensorValue < value - 1)
    {
        value = sensorValue;
        send(toJson("time", String(map(value, 0, 1023, 0, 24000))));
    }

    // camera angle
    xyzFloat angle = mpu.getAngles();
    send(toJson("angle", "[" + String(angle.y) + "," + String(angle.z) + "]"));

    delay(50); // the game runs at 20hz
}

void send(String raw)
{
    Serial.println(START_DELIMITER + raw + END_DELIMITER);
}

String toJson(String key, String value)
{
    return "{\"" + key + "\":" + value + "}";
}
