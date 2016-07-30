#include <NAxisMotion.h>
#include <Wire.h>

NAxisMotion mySensor;       // Object that for the sensor
bool intDetected = false;   // Flag to indicate if an interrupt was detected
int threshold = 250;        // "Fall" threshold of ~1.95g (using default range 4g for NDOF mode)
int duration = 1;           // "Fall" duration of 8ms (using default bandwidth 62.5Hz for NDOF mode)

void setup() {
  // Setup pins
  pinMode(3, OUTPUT);

  // Initialise peripherals
  Serial.begin(9600); // Serial for Bluetooth module
  I2C.begin(); // I2C for the sensor

  // Initialise sensor
  mySensor.initSensor();
  mySensor.setOperationMode(OPERATION_MODE_NDOF);
  mySensor.setUpdateMode(MANUAL); // Setting to MANUAL requires fewer reads to the sensor

  // Setup the initial interrupt
  attachInterrupt(INT_PIN, motionISR, RISING);
  mySensor.accelInterrupts(ENABLE, ENABLE, ENABLE); // All 3 axes
  mySensor.enableAnyMotion(threshold, duration);
}

void loop() {
  if (intDetected) {
    intDetected = false;
    mySensor.resetInterrupt();
    Serial.println("fall");
    tone(3, 1000, 2000); // Buzzer on pin 3, 1000Hz sound, 2s duration
  }
}

// Sensor interrupt will trigger this function
void motionISR() {
  intDetected = true;
}

