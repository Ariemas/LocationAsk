# Location Ask Minecraft Plugin
LocationAsk is a Minecraft plugin that allows players to request the location of other players and share their own location upon request. The plugin also includes a request cooldown and timeout for added control.

## Features
* Request the location of another player
* Accept a location request and share your current location
* Configurable request cooldown to prevent spamming requests
* Configureable request timeout to automatically cancel pending requests

## Installation
1. Download the latest version of the LocationAsk plugin.
2. Place the downloaded JAR file in the plugins folder of your Minecraft server.
3. Restart your server to load the plugin and generate the configuration files.

## Configuration
The config.yml file contains two settings:
* request-timeout: The number of seconds before a location request times out (default: 60 seconds).
* request-cooldown: The number of seconds a player must wait before sending another location request (default: 10 seconds).

## Commands
* /requestlocation <player>: Request the location of another player.
* /locationaccept <player>: Accept a location request from another player and share your location with them.

## Permissions
*Note: Location requests will not be sent to players who do not have permission to accept them.*
* locationask.request: Request the location of another player.
* locationask.accept: Accept a location request from another player and share your location with them.

## License
This plugin is available under the MIT License. See the LICENSE file for more information.