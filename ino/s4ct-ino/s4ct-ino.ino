#include <MPU9250.h>

MPU9250 mpu;

const String START_DELIMITER = "<";
const String END_DELIMITER = ">";

int value = 0; // for potentiometer debouncing

void setup()
{
    Serial.begin(9600);

    Wire.begin();
    delay(2000);

    mpu.setup(0x68); // change to your own address

    delay(5000);

    // calibrate anytime you want to
    mpu.calibrateAccelGyro();
    mpu.calibrateMag();
}

void loop()
{
    // world time
    int sensorValue = analogRead(0);
    if (sensorValue > value + 1 || sensorValue < value - 1)
    {
        value = sensorValue;
        // send(toJson("time", String(map(value, 0, 1023, 0, 24000))));
    }

    // camera angle
    if (mpu.update())
    {
        String x = String(mpu.getRoll());
        String y = String(mpu.getPitch());
        String z = String(mpu.getYaw());
        send(toJson("camera", "[" + x + "," + y + "," + z + "]"));
    }

    delay(50); // the game runs at 20hz
}

void send(String raw)
{
    Serial.println(START_DELIMITER + raw + END_DELIMITER);
}

String toJson(String key, String value)
{
    return "{\"" + key + "\":\"" + value + "\"}";
}
