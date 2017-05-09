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

To setup the Fibaro Gateway bridge you need to configure the following:
* Ip address: The ip address of your Fibaro Home Center 2.
* Username: Admin username of your Fibaro Home Center 2.
* Password: Admin password of your Fibaro Home Center 2.
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
Since the thing types are very generic (actor or sensor) the binding instead supports a variety of channels to configure for each thing. By default only the `dead` channel is enabled (all devices has this property indicating if the device is accessable from the Fibaro Home Center 2 gateway).
Other channels needs to be enabled based on what type of device it is. So for a temperature sensor you will need to enable the `temperature` channel. Some devices may also support several channels. An example is the Fibaro switch which can measure power and energy consumption. In this example you can (besides the `dead`channel) enable `switch`, `power`and `energy` channels.

### Supported channels
|Name             |Id               |Description                                                                             |Item type     |Actor | Sensor |
|-----------------|-----------------|----------------------------------------------------------------------------------------|--------------|------|--------|
|Alarm            |alarm            |Controls an alarm such as a siren                                                       |Switch        | X    |        | 
|Battery level    |battery          |Reads current battery level (in %) of a device                                          |Number        | X    | X      |
|Dead             |dead             |Reads device dead status (i.e. device is configured but not reachable from the gateway) |Switch        | X    | X      |
|Dimmer           |dimmer           |Controls a dimmer                                                                       |Dimmer        | X    |        |
|Door             |door             |Reads door sensor value                                                                 |Contact       |      | X      |
|Electric current |electric-current |Reads the electric current (in A) of a device                                           |Number        |      | X      |
|Energy           |energy           |Reads the total energy consumption (in kWh) of this device                              |Number        | X    | X      |
|Heat             |heat             |Reads heat sensor value (from a smoke sensor for example)                               |Switch        |      | X      |
|Illuminance      |illuminance      |Reads the illuminance (in lux) of this device                                           |Number        |      | X      |
|Motion           |motion           |Reads motion sensor value                                                               |Switch        |      | X      |
|Power            |power            |Reads the current power usage (in W) of this device                                     |Number        | X    | X      |
|Power outlet     |power-outlet     |Controls a power outlet                                                                 |Switch        | X    |        |
|Smoke            |smoke            |Reads smoke sensor value                                                                |Switch        |      | X      |
|Switch           |switch           |Controls a binary switch                                                                |Switch        | X    |        |
|Temperature      |temperature      |Reads the current temperature (in °C) of this device                                    |Number        |      | X      |
|Thermostat       |thermostat       |Control the temperature in a thermostat device                                          |Number        | X    |        |
|Voltage          |voltage          |Reads the current voltage (in V) of this device                                         |Number        |      | X      |
|Window           |window           |Reads window sensor value                                                               |Contact       |      | X      |

### Not yet supported channels
|Name             |Id               |Description                                                                             |Item type     |Actor | Sensor |
|-----------------|-----------------|----------------------------------------------------------------------------------------|--------------|------|--------|
|Blinds           |blinds           |Controls a motorized roller blind                                                       |RollerShutter | X    |        | 
|Color light      |color-light      |Controls a RGBW color light                                                             |Color         | X    |        |

The above channels are easy to add. Information about possible actions and some testing would be needed however.


## Configuration examples
.things file configuration
```
Bridge  fibaro:gateway:hc2      [ ipAddress="192.168.1.4", username="admin", password="admin", port=9000 ] {
    
    // Temperature sensors
    Thing   sensor  15  [ id=15 ]
    Thing   sensor  20  [ id=20 ]
    
    // Motion sensors
    Thing   sensor  14  [ id=14 ]
    Thing   sensor  19  [ id=19 ]

    // Illuminance sensors
    Thing   sensor  16  [ id=16 ]
    Thing   sensor  21  [ id=21 ]

    // Dimmers
    Thing   actor   141 [ id=141 ]
    Thing   actor   145 [ id=145 ]

    // Door sensors
    Thing   sensor  102 [ id=102 ]
    Thing   sensor  104 [ id=104 ]
}
```

.items file configuration
```
Number  Temperature_Entrance        "Entrance [%.1f °C]"      <temperature>      { channel="fibaro:sensor:hc2:15:temperature" }
Number  Temperature_Hallway         "Hallway [%.1f °C]"       <temperature>      { channel="fibaro:sensor:hc2:20:temperature" }

Switch  Motion_Entrance             "Entrance [%s]"           <camera>           { channel="fibaro:sensor:hc2:14:motion" }
Switch  Motion_Hallway              "Hallway [%s]"            <camera>           { channel="fibaro:sensor:hc2:19:motion" }

Number  Illuminance_Entrance        "Entrance [%.0f lux]"     <sun>              { channel="fibaro:sensor:hc2:16:illuminance" }
Number  Illuminance_Hallway         "Entrance [%.0f lux]"     <sun>              { channel="fibaro:sensor:hc2:21:illuminance" }

Dimmer  Dimmer_Entrance             "Entrance [%s]"           <dimmablelight>    { channel="fibaro:actor:hc2:141:dimmer" }
Number  DimmerE_Entrance            "Entrance [%.2f kWh]"     <energy>           { channel="fibaro:actor:hc2:141:energy" }
Number  DimmerP_Entrance            "Entrance [%.2f W]"       <energy>           { channel="fibaro:actor:hc2:141:power" }

Dimmer  Dimmer_Hallway              "Hallway [%s]"            <dimmablelight>    { channel="fibaro:actor:hc2:145:dimmer" }
Number  DimmerE_Hallway             "Hallway [%.2f kWh]"      <energy>           { channel="fibaro:actor:hc2:145:energy" }
Number  DimmerP_Hallway             "Hallway [%.2f W]"        <energy>           { channel="fibaro:actor:hc2:145:power" }

Contact Door_Entrance               "Entrance [%s]"           <frontdoor>        { channel="fibaro:sensor:hc2:102:door" }
Contact Door_Garage                 "Garage [%s]"             <garagedoor>       { channel="fibaro:sensor:hc2:104:door" }
```

