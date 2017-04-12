# Fibaro Binding

The Fibaro binding integrates with the Fibaro Home Center 2 gateway.

There are other Z-Wave bindings available. Target users for this binding are those who already own a Fibaro Home Center 2 and wish to keep this gateway to setup and manage your z-wave network but want to control and access them though openHab.

See http://www.fibaro.com/ for details on their product.
    
## Binding Configuration
 
The binding is configured by setting up a gateway bridge which needs to be configured with connectivity parameters (ip, username & password) to the Fibaro Home Center 2 gateway. The binding also sets up a http server which listens to incoming updates from the devices on the Fibaro side. This is to be able to get instant push information to Openhab instead of polling the devices. For this communication a port number also needs to be configured.

When the gateway bridge is configured and online your z-wave devices can be added as Things. The binding currently support two different thing types:
* Actor - Z-wave actor, i.e. any device that can receive an update such as turning on a switch, dimming a light, setting the temperature on a thermostat.
* Sensor - Z-wave sensor, i.e. all "read-only" devices such as termometers, light sensors, energy meters etc.
 
## Gateway Configuration

To setup the Fibaro Gateway bridge you need the configure the following:
* Ip address: The ip address of your Fibaro Home Center 2.
* Username: Admin username of your Fibaro Home Center 2.
* Password: Admin username of your Fibaro Home Center 2.
* Port: Port number to use for the Fibaro communication to push device update to openHab.

Before you start to add other things (actores and/or sensors) make sure the gateway gets initialised and `ONLINE`. Otherwise your other devices will not be able to communicate from/to the Fibaro Home center 2.

## Configure Things

To setup your z-wave devices you add them as things. Actors are all z-wave devices that support sending them an action (such as turning on/off a switch or dimming a light). Sensors are all read-only devices that only sends data (such as temperature readings, motion detection information etc).

To setup the Fibaro Actor/Sensor thing you need to configure the following:
* Id : The z-wave id of you device (available in the Fibaro Home Center 2)

Remember to associate your thing with the already configured gateway bridge.

Each device with a unique z-wave id needs to be configured as a thing in the binding. For example the Fibaro motion sensor is not one thing but actually four, all with different z-wave id:s
* The motion sensor
* The temperature sensor
* The light intensity sensor
* The tamper/seismograph sensor

## Discovery
Not implemented yet
 
## Channels
Since the thing types are very generic (actor or sensor) the binding instead supports a variety of channels to configure for each thing. By default only the `dead` channel is enabled (all devices has this property indicating id the device is accessable from the Fibaro Home Center 2 gateway).
Other channels needs to be enabled based on what type of device it is. So for a temperature sensor you will need to enable the `temperature` channel. Some devices may also support several channels. An example is the Fibaro switch which can measure power and energy consumption. In this example you can (besides the `dead`channel) enable `switch`, `power`and `energy` channels.

### Supported channels
| Name     | id    | Description                                                                                           | Actor | Sensor |
| ---------|-------|-------------------------------------------------------------------------------------------------------|-------|--------|
| Dead     | dead  | Channel which holds a z-wave device dead status (i.e. configured but not connected to the controller) |   X   |   X    |
 

## Configuration examples
Todo