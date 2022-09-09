const String START_DELIMITER = "[";
const String END_DELIMITER = "]";

int value = 0;

void setup()
{
    Serial.begin(9600);
}

void loop()
{
    int sensorValue = analogRead(0);

    if (sensorValue > value + 1 || sensorValue < value - 1)
    {
        value = sensorValue;
        Serial.print(START_DELIMITER + String(value) + END_DELIMITER);
    }

    delay(100);
}
