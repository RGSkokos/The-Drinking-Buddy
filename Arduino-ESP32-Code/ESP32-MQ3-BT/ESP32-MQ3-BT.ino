#include "BluetoothSerial.h"
#define MQ3pin 34
#define LEDpin 32

float MQ3Value; // Variable to Store the MQ3 Sensor Value
BluetoothSerial SerialBT; // Initializing the Bluetooth Connection (Unconditional)
int userCommand; // Variable to Detect if Sample Shall be Taken
boolean takeSample = false; // Take Sample
float sum = 0; // Add All Samples
int counter = 0; // Count Amount of Samples
int LEDCounter = 0; // Using Counter to Control Time On/Off of LED

void setup() {
  SerialBT.begin("ESP32-MQ3-BT"); //Bluetooth device name
  Serial.begin(9600);
  pinMode(LEDpin, OUTPUT); // Setting pin 32 as Output
}

void loop() {
  if(SerialBT.available()) { // Run if There is Incoming Bluetooth Message
    userCommand = SerialBT.read(); // SerialBT.read() Produces ASCII Code Values
    if(userCommand == 48) { // (48)ASCII = (0)DECIMAL -> Android App Buttons Send 1s and 0s, Nothing Else
      digitalWrite(LEDpin, LOW); // Turning off LED if user wants to stop sampling
      takeSample = false; // Turn Off Sampler
      Serial.println("Average is: ");
      Serial.print(sum/counter);
      SerialBT.println(sum/counter); // Calculate and Send Average
      counter = 0;
      sum = 0;
    }

    if(userCommand == 49) { // (49)ASCII = (1)DECIMAL
      digitalWrite(LEDpin, HIGH); // Turning of LED as fast as sample starts to be taken
      takeSample = true; // Turn On Sampler
    }
  }

  if(takeSample) { // SAMPLER
    MQ3Value = analogRead(MQ3pin);
    Serial.println(MQ3Value);
    sum = sum + MQ3Value;
    counter++;
    LEDCounter++;
    if(LEDCounter == 10) {
      digitalWrite(LEDpin, HIGH);
    }

    if(LEDCounter == 20) {
      LEDCounter = 0;
      digitalWrite(LEDpin, LOW);
    }
  }

  delay(20);
}