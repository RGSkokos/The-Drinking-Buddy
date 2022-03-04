#include "BluetoothSerial.h"
#define MQ3pin 34

float MQ3Value; // Variable to Store the MQ3 Sensor Value
BluetoothSerial SerialBT; // Initializing the Bluetooth Connection (Unconditional)
int userCommand; // Variable to Detect if Sample Shall be Taken
boolean takeSample = false; // Take Sample
float sum = 0; // Add All Samples
int counter = 0; // Count Amount of Samples

void setup() {
  SerialBT.begin("ESP32-MQ3-BT"); //Bluetooth device name
  Serial.begin(9600);
}

void loop() {
  if(SerialBT.available()) { // Run if There is Incomming Bluetooth Message
    userCommand = SerialBT.read(); // SerialBT.read() Produces ASCII Code Values
    if(userCommand == 48) { // (48)ASCII = (0)DECIMAL -> Android App Buttons Send 1s and 0s, Nothing Else
      takeSample = false; // Turn Off Sampler
      Serial.println("Average is: ");
      Serial.print(sum/counter); 
      SerialBT.println(sum/counter); // Calculate and Send Average
      counter = 0;
      sum = 0;
    }

    if(userCommand == 49) { // (49)ASCII = (1)DECIMAL
      takeSample = true; // Turn On Sampler
    }
  }
  
  if(takeSample) { // SAMPLER
    MQ3Value = analogRead(MQ3pin);
    Serial.println(MQ3Value);
    sum = sum + MQ3Value;
    counter++;
  }
  
  delay(20);
}
