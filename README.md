# s4ct-individual

### Overview

This repository aims at utilising an arduino to control various elements within a video game using analog controls. As a proof of concept, a potentiometer is connected to the arduino and used to control the time of day the video game [Minecraft](https://minecraft.net). This game has a very large programming community with various ways the 

### How it works

There are numerous ways an arduino can be connected to a game (ei Bluetooth, WiFi, etc). In this case, communication is via a serial port. Specifically, the arduino outputs minified JSON to the port which is then parsed in the game directly,

