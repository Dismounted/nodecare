
const int pillPin = 7; //pin to close to create circuit
const int doorPin = 6;
const int trigPin = 2;
const int echoPin = 4;
const int ledPin = 13;
const int tempHigh = 500;

unsigned long oTime;
unsigned long timeOut = 3000; //door open for 30 seconds



int closeState = 0;
int pillClose = 0;
int oCount = 0;
int pillTaken = 0;
int doorState = 0;
int doorAlarmTrig = 0;
int tempTime = 0;
int tempAlarmTrig = 0;

void setup() {
  Serial.begin(9600);
  pinMode(pillPin, INPUT_PULLUP); //input pin for status
  pinMode(doorPin, INPUT); //input pin for status

}

void loop() {

  int tempValue = analogRead(A0);
  tempAlarmTrig = 0;

  //Serial.println(tempValue);

  if (tempValue < tempHigh) {

    tempTime = millis(); //when temperature became too high

    while (tempValue < tempHigh) {

      if ((millis() - tempTime) > timeOut && tempAlarmTrig == 0) {
        Serial.println("temp"); //temperature alarm
        tempAlarmTrig = 1;
      }
      tempValue = analogRead(A0);
      delay(100);

    }

  }

 
  closeState = digitalRead(doorPin);

  if (closeState == LOW) { //if door opened
    oTime = millis(); //clock opening time
    doorAlarmTrig = 0;
    if (doorState == 1) {
      Serial.println("door0"); //indicate door is currently opened
      doorState = 0;
    }


    while (closeState == LOW) { //while door is open

      if ((millis() - oTime) > timeOut && doorAlarmTrig == 0) { //if open for too long
        Serial.println("doora"); //send door open too long alarm
        doorAlarmTrig = 1;
      }

      delay(100);

      closeState = digitalRead(doorPin);
    }

  }

  if (closeState == HIGH && doorState == 0) {
    Serial.println("door1");
    doorState = 1;

  }

 pillClose = digitalRead(pillPin);

  if (pillClose == LOW) {

    oCount++; //indicate pill opened at some point

    while (pillClose == LOW) { //while door is open

      delay(100);

      pillClose = digitalRead(pillPin);
    }

  }

  if (pillClose == HIGH && oCount == 1) { //if pill box closed and was opened before
    Serial.println("pill");
    pillTaken = 1; //indicate pill taken
    oCount = 0;
  }
  //Serial.println("not open");

  oTime = 0;


}

