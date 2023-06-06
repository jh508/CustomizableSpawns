# Customizable Spawns Plugin

The Customizable Spawns plugin is a powerful tool for customizing and managing spawn locations in your Minecraft server. With this plugin, you can define and modify multiple spawn points across different worlds, providing flexibility and control over the player spawning experience.

## Features
- Works on 1.19.4!
- Define and manage multiple spawn locations for different worlds.
- Easily set new spawn points in-game using a specific block (of your choice!) placement event.
- Configurable options for each spawn location, such as coordinates (x, y, z) and additional properties.
- Real-time updates of spawn locations without requiring a server reload.
- Compatible with popular server platforms like Spigot, Paper and Bukkit.

## Why Customizable Spawns?

- **Enhanced Player Experience**: Customizable Spawns allows you to create unique spawn points tailored to your server's gameplay and community. You can create dedicated spawn areas for different groups or events, improving the player experience and navigation.

- **Flexibility and Adaptability**: The plugin offers a user-friendly configuration system that allows you to easily define and modify spawn locations in real-time. You can add, remove, or update spawn points without the need for server reloads, making it convenient for administrators and server owners.

- **Intuitive Setup and Usage**: Setting up and using Customizable Spawns is straightforward. Simply install the plugin, configure your desired spawn locations in the YAML file, and let the plugin handle the rest. It seamlessly integrates with your existing server setup and requires minimal effort to get started. Don't want to mess with the configuration? Simply use the required bottom and top block to set the spawns in-game!

## Installation

1. Download the latest release of the Customizable Spawns plugin from the [releases page](https://github.com/your-username/customizable-spawns/releases).

2. Copy the plugin JAR file into the `plugins` directory of your Minecraft server.

3. Start or restart your server.

4. Configure the spawn locations in the `config.yml` file located in the `plugins/CustomizableSpawns` directory. Define the desired spawn points for each world using the provided structure or feel free to change the block types for setting the spawns.

5. Save the `config.yml` file and restart your server to apply the changes.

6. Enjoy the customizability of spawn points in your Minecraft server!

## Usage

- To set a new spawn point, simply place the designated block (configured in the `config.yml`) at the desired location. The plugin will automatically detect the event and update the spawn location accordingly.

- Players will now spawn at these spawn points randomly (if configured correctly) OR players will spawn at the very first spawn point in the configuration file.

## Configuration

The `config.yml` file allows you to define and manage the spawn locations for your server. The structure is as follows:

```yaml
spawnLocations:
  world-name:
    spawn1:
      x: 0
      y: 64
      z: 0
    spawn2:
      x: -100
      y: 70
      z: 200
      
      
