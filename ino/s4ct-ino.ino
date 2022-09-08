int value = 0;

void setup()
{
    Serial.begin(9600);
}

void loop()
{
    Serial.println(analogRead(0));

    // int sensorValue = analogRead(0);

    // if (sensorValue > value + 1 || sensorValue < value - 1)
    // {
    // value = sensorValue;
    // Serial.println(value);
    // }
}
