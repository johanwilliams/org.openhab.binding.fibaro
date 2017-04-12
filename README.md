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

## Fibaro Home Center 2 configuration
We need to add a lua scene in the Fibaro Home Center 2 in order to send updates to the biding. This enables us to push instant device updates to the binging and avoids constant pulling of the Fibaro api. 
In the scene you need add your openHab ip address as well as the port number you configured for the gateway. You also need to list all your device id:s you wish to include as well as the properties to "listen" to. Most devices use `value`to hold the device value but there are also other proprties such as `energy`, `power`, `batteryLevel` etc. Please read the documentation for the Fibaro api for more information. 

```lua
--[[
%% properties
31 value
31 dead
149 dead
149 value
149 power
149 energy
%% events
%% globals
--]]

-- Give debug a fancy color
function log(message, color)
  fibaro:debug(string.format('<span style="color:%s;">%s</span>', color, message)) 
end

-- HTTP requests
local function request(requestUrl, deviceData)
  local http = net.HTTPClient() 
  log(requestUrl .. " : " .. jsonString, "blue")
  
  http:request(requestUrl, {
      options = {
        method = "PUT",
        headers = {},
        data = deviceData
      },      
      success = function (response)        
        log("OK: " .. requestUrl .. " - " .. deviceData, "green")
      end,
      error = function (err)        
        log("FAIL: " .. requestUrl .. " - " .. deviceData ". Error: " .. err, "red")
      end
    })
end

-- MAIN

-- Server settings
local openhabIp = "192.168.1.198"
local openhabPort = 9000
local openhabUrl = "http://" .. openhabIp .. ":" .. openhabPort

-- Info needed in the json request
local trigger = fibaro:getSourceTrigger()
local deviceID = trigger['deviceID']
local deviceName = fibaro:getName(deviceID)
local propertyName = trigger['propertyName']
local propertyValue = fibaro:getValue(deviceID, propertyName)

-- Assemble the json string
jsonTable = {}
jsonTable.id = deviceID
jsonTable.name = deviceName
jsonTable.property = propertyName
jsonTable.value = propertyValue
jsonString = json.encode(jsonTable)

-- Send it!
request(openhabUrl, jsonString)
```
In future releases of this binding this step will not be needed as lua scenes can be created thought the Fibaro api. This will enable the binding itself to create the needed lua scene for all configured things.

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
|Name          |Id           |Description                                                                             |Item type     |Actor | Sensor |
|--------------|-------------|----------------------------------------------------------------------------------------|--------------|------|--------|
|Alarm         |alarm        |Controls an alarm device such as a siren                                                |Switch        | X    |        | 
|Blinds        |blinds       |Controls a motorized roller blind                                                       |RollerShutter | X    |        | 
|Color Light   |colorLight   |Controls a RGBW color light                                                             |Color         | X    |        |
|Dimmer        |dimmer       |Controls a dimmer                                                                       |Dimmer        | X    |        |
|Switch        |switch       |Controls a binary switch                                                                |Switch        | X    |        |
|Power Outlet  |powerOutlet  |Controls a power outlet                                                                 |Switch        | X    |        |
|Battery Level |battery      |Reads device battery level                                                              |Number        | X    | X      |
|Dead          |dead         |Reads device dead status (i.e. device is configured but not reachable from the gateway) |Switch        | X    | X      |
|Energy        |energy       |Reads total energy consumption of device in kWh                                         |Number        | X    | X      |
|Power         |power        |Reads current power consumption of device in W                                          |Number        | X    | X      |
|Temperature   |temperature  |Controls and/or reads the temperature                                                   |Number        | X    | X      |
|Last Breached |lastBreached |Reads the timestamp for when the device was last breached                               |DateTime      |      | X      |
|Motion        |motion       |Reads motion sensor value                                                               |Switch        |      | X      |
|Illuminance   |illuminance  |Reads illuminance sensor value                                                          |Number        |      | X      |
|Smoke         |smoke        |Reads smoke sensor value                                                                |Switch        |      | X      |
|Heat          |heat         |Reads heat sensor value                                                                 |Switch        |      | X      |
|Door          |door         |Reads door sensor value                                                                 |Switch        |      | X      |
|Voltage       |voltage      |Reads current voltage of device in V                                                    |Number        |      | X      |
|Ampere        |ampere       |Reads current ampere of device in A                                                     |Number        |      | X      |
|Window        |window       |Reads window sensor value                                                               |Switch        |      | X      |



## Configuration examples
Todo