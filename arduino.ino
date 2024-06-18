#include <LiquidCrystal_I2C.h>
#include <DHT.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <MQ135.h>
#include <math.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);  // Устанавливаем дисплей
#define DHTPIN 5
#define DHTTYPE DHT11
#define CACHE_SIZE 5
DHT dht(DHTPIN, DHTTYPE);
MQ135 gasSensor(A3);
int buzzer = 4;
unsigned long previousMillis = 0;
const long interval = 5000;
bool isHumidityDisplayed = false;
SoftwareSerial BTSerial(10, 11);

float prevTemp, prevHum, prevGasPPM;

// Контракт обмена сообщениями размера CACHE_SIZE
// "{temperature}#{humidity}#{ppm}", значения приходящего кеша разделяются ","
String outputCache[CACHE_SIZE];

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
        displayInformation(0, 0, concat("Temp: ", temp, "C"), true);
    } else {
        if (temp != prevTemp) {
            prevTemp = temp;
            displayInformation(0, 0, concat("Temp: ", temp, "C"), true);
        }
    }
    if (currentMillis - previousMillis >= interval) {
        previousMillis = currentMillis;
        if (isHumidityDisplayed) {
            if (isnan(hum)) {
                hum = prevHum;
                displayInformation(0, 2, concat("Humidity: ", hum - 10, "%"), false);
                isHumidityDisplayed = false;
            } else {
                if (hum != prevHum) {
                    prevHum = hum;
                    displayInformation(0, 1, concat("Humidity: ", hum - 10, "%"), false);
                    isHumidityDisplayed = false;
                }
            }}else {
            if (gasValue > 100 && gasValue != prevGasPPM) {
                prevGasPPM = gasValue;
                displayInformation(0, 1, concat("CO2: ", prevGasPPM + 300, "ppm"), false);
                isHumidityDisplayed = true;
            }
            if (prevGasPPM + 300 > 100) {
                tone(buzzer, 100);
                delay(500);
                noTone(buzzer);
                isHumidityDisplayed = true;
            }

            Serial.println(concat("Temperature: ", temp, " C"));
            Serial.println(concat("Humidity: ", hum, " %"));
            Serial.println(concat("Gas Level: ", gasValue, " PPM"));

            String currentResult = getCurrentOutput(temp, hum, gasValue);
            updateCache(currentResult);
            Serial.println(currentResult);
            BTSerial.println(getOverallOutput());
            Serial.println(getOverallOutput());

            delay(interval);  // Задержка между чтениями
        }
    }
}

// Отображение какого-либо контента на LCD
void displayInformation(int offsetX, int offsetY, String content, bool clear) {
    if (clear) {
        lcd.clear();
    }
    lcd.setCursor(offsetX, offsetY);
    lcd.print(content);
}

// Получает текущий контрактный вывод информации вида {temperature}#{humidity}#{ppm}
String getCurrentOutput(float temperature, float humidity, int gasValue) {
    return concat(temperature, "#", humidity, "#", gasValue);
}

// Получает общий вывод по кешу, разделяя его ","
String getOverallOutput() {
    String cacheDump = "";
    for (int i = 0; i < CACHE_SIZE; i++) {
        cacheDump = concat(cacheDump, outputCache[i], ",");
    }
    return cacheDump;
}

// Обновляет кеш, постепенно избавляясь от самых старых значений
// Добавляет новое значение в самое начало
void updateCache(String newOutput) {
    for (int i = CACHE_SIZE - 2; i >= 0; i--) {
        outputCache[i + 1] = outputCache[i];
    }
    outputCache[0] = newOutput;
}

// Костыль, чтобы конкатенировать примитивы помимо строк
// (по умолчанию необходимо приводить float/int/byte... в String(float), а только потом можно конкатенироваться)
String concat() {
    return "";
}
template<typename T, typename... Args>
String concat(T first, Args... args) {
    return String(first) + concat(args...);
}