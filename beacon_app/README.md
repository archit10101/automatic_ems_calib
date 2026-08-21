# Bluetooth Low Energy Distance Monitoring App

This Android application facilitates the connection to multiple Bluetooth Low Energy (BLE) devices, enabling distance monitoring between them. The key components of this application include:

## Beacon
Utilizing a XIAO ESP32C3 microcontroller as a beacon, the application establishes a Bluetooth connection with an Android smartwatch. It broadcasts positional information and uses the received signal strength indicator (RSSI) to calculate the distance from the smartwatch. [Beacon Repo](https://github.com/archit10101/BluetoothArduinoESPBeacon)

## Microcontroller
The microcontroller functions as the receiver of data transmitted by the smartwatch. It detects when the smartwatch enters a predefined range from the beacon and broadcasts a signal indicating proximity. [Microcontroller Repo](https://github.com/archit10101/Arduino_BLE)

## Functionality
The application's core function is to assess the proximity between the XIAO ESP32C3 beacon and an Android smartwatch. It then relays this information to another microcontroller, enabling users to leverage the data for various purposes.

## Usage
To utilize this application, follow these steps:

1. Install the application on your Android device.
2. Ensure that your device supports Bluetooth Low Energy.
3. Power on both the XIAO ESP32C3 beacon and the microcontroller.
4. Pair your Android device with the beacon.
5. Monitor distance readings between the beacon and smartwatch using the application.

## Note
This application serves as a demonstration of BLE distance monitoring and is customizable for integration into larger projects. Refer to the documentation or contact the repository owner for further information.

**Disclaimer**: This application is provided as-is without warranty. Use at your own risk.

---

Contributions and feedback are welcomed through issue submission or pull requests!
