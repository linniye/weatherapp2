#include <LiquidCrystal_I2C.h>
#include <DHT.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <MQ135.h>
#include <math.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);  // Устанавливаем дисплей
#define DHTPIN 5
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
MQ135 gasSensor(A3);
int buzzer = 4;
unsigned long previousMillis = 0;
const long interval = 5000;
bool isHumidityDisplayed = false;
SoftwareSerial BTSerial(10, 11);

float prevTemp, prevHum, prevGasPPM;

void setup() {
  lcd.begin(16, 2);
  lcd.init();
  lcd.backlight();
  dht.begin();
  Serial.begin(9600);
  BTSerial.begin(9600);
  MQ135 gasSensor = MQ135(A0);
}
void loop() {
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  int gasValue = gasSensor.getPPM();
  unsigned long currentMillis = millis();

  if (isnan(temp)) {
    temp = prevTemp;
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Temp: ");
    lcd.print(temp);
    lcd.print("C");

    BTSerial.print("Temperature: ");
    BTSerial.print(temp);
    BTSerial.println(" C");
  } else {
    if (temp != prevTemp) {
      prevTemp = temp;
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Temp: ");
      lcd.print(temp);
      lcd.print("C");

      BTSerial.print("Temperature: ");
      BTSerial.print(temp);
      BTSerial.println(" C");
    }
  }
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    if (isHumidityDisplayed) {
      if (isnan(hum)) {
        hum = prevHum;
        lcd.setCursor(0, 2);
        lcd.print("Humidity: ");
        lcd.print(hum - 10);
        lcd.print("%");

        BTSerial.print("Humidity: ");
        BTSerial.print(hum);
        BTSerial.println(" %");
        isHumidityDisplayed = false;
      } else {
        if (hum != prevHum) {
          prevHum = hum;
          lcd.setCursor(0, 1);
          lcd.print("Humidity: ");
          lcd.print(hum - 10);
          lcd.print("%");

          BTSerial.print("Humidity: ");
          BTSerial.print(hum);
          BTSerial.println(" %");
          isHumidityDisplayed = false;
        }
      }}else {
        if (gasValue > 100 && gasValue != prevGasPPM) {
          prevGasPPM = gasValue;
          lcd.setCursor(0, 1);
          lcd.print("CO2: ");
          lcd.print(prevGasPPM + 300);
          lcd.print("  PPM");

          BTSerial.print("Gas Level: ");
          BTSerial.print(prevGasPPM);
          BTSerial.println(" PPM");
          isHumidityDisplayed = true;
        }
        if (prevGasPPM + 300 > 100) {
          tone(buzzer, 100);
          delay(500);
          noTone(buzzer);
          isHumidityDisplayed = true;
        }
        Serial.print("Temperature: ");
        Serial.print(temp);
        Serial.println(" C");

        Serial.print("Humidity: ");
        Serial.print(hum);
        Serial.println(" %");

        Serial.print("Gas Level: ");
        Serial.print(prevGasPPM);
        Serial.println(" PPM");

        delay(4000);  // Задержка между чтениями
      }
    }
  }
