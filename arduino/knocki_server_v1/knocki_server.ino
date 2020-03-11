#include <SoftwareSerial.h>
SoftwareSerial esp(12, 10); // RX, TX
int relay1 = 2;
int relay2 = 3;
String ssid = "AVR";
String password = "//avrtech;";

void setup() {
  Serial.begin(9600);
  esp.begin(9600);
  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);
  esp.println("AT");
  Serial.println(esp.readString());
//  esp.println("AT+CWMODE=3");
//  Serial.println(esp.readString());
//  esp.println("AT+CWJAP=\"" + ssid + "\"" + ",\"" + password + "\"" );
//  delay(100);
//  while(esp.available()){Serial.print('.');}
//  Serial.println(esp.readString());
  esp.println("AT+CIPMUX=1");
  Serial.println(esp.readString());
  delay(500);
  esp.println("AT+CIPSERVER=1,7777");
  Serial.println(esp.readString());
  delay(500);
}

void loop() {
  if(esp.available()){
//    char data = esp.read();
//    Serial.println(data);
//    switch (data){
//      case 'A' :
//        digitalWrite(relay1, HIGH);
//        break;
//      case 'a' :
//        digitalWrite(relay1, LOW);
//        break;
//      case 'B' :
//        digitalWrite(relay2, HIGH);
//        break;
//      case 'b' :
//        digitalWrite(relay2, LOW);
//        break;
//      default  :
//        break;

//+IPD,0,1:A
//      String data = esp.readString();
//      Serial.println(data);
    String first = esp.readStringUntil(',');
    esp.read(); //next character is comma, so skip it using this
    String second = esp.readStringUntil(',');
    esp.read(); //next character is comma, so skip it using this
    String third = esp.readStringUntil(':');
    esp.read(); //next character is comma, so skip it using this
    String fourth = esp.readStringUntil('\0');
    Serial.print(second);
    Serial.print(':');
    Serial.println(fourth);
    if(fourth == "A") digitalWrite(relay1, HIGH); else
    if(fourth == "a") digitalWrite(relay1, LOW);  else
    if(fourth == "B") digitalWrite(relay2, HIGH); else
    if(fourth == "b") digitalWrite(relay2, LOW);  else
    if(fourth == "p") sendToEsp(second, fourth);  else;
  }
}
void sendToEsp(String second, String fourth){
  Serial.print("playing music");  
  esp.println("AT+CIPSEND=0,2");
  //Serial.println(esp.readString());
  esp.println("p");
  //Serial.println(esp.readString());
}
