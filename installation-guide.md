# Thank you for downloading yoCore!
Here is the official installation guide.

### Step 1: Setup Config.yml
Fortunately, there's only really 2 values you absolutely **must** have set. Scroll down to line `1216`, it should look like this:

```yaml
Servers:
  WorldSeparation: true
  ChatSeparation: true
  Hub:
    Server: "server"
    World: "world"
    HubEveryJoin: true
    Command:
      Enabled: true
      MustBePlayer: "&cYou must be a player to use that command."
      CommandNotEnabled: "&cHub command is not enabled."
      Format: "&bYou have sent yourself to the hub."
```

By default, the server value for hub should be just "server". If you open up your `servers.yml` file, you'll find that there is only 1 server,
with the ID of "SERVER" and the name of "server". That is why the server value in the config.yml is just set to "server", so whenever you are adding servers anywhere,
make sure you use the **server name** (findable in `servers.yml`). The second key is called "world", and this is also very important. By default, in the `servers.yml`
file, all of your worlds should be added to the server called "server". So, should you want a hub, put the value in the config.yml to whatever world you want it in.

### Step 2: Setup Servers.yml
In the servers.yml, you'll notice it looks something like this:

```yaml
Servers:
  SERVER:
    ID: SERVER
    Name: server
    Worlds:
    - world
    - world_nether
    - world_the_end
    Spawn:
      World: world
      X: 0.5
      Y: 70.0
      Z: 0.5
      Yaw: 0.0
      Pitch: 0.0
```

This is the **default server**. To add more servers, you can just copy and paste from the second line of that section all the way to the bottom, change the worlds and
spawn, and change the name, and you have another server. Make sure any world your players will be travelling to is located in the `Worlds` section of a server of your
choice.

### Step 3: Customize as you will
Now you have the must-haves down, so you can start editing things like ranks, permissions, messages, tags, etc. Enjoy using yoCore!

**Here's a reminder that you should almost NEVER have to enter the files** `punishments.yml`, `grants.yml`, `stats,yml`, **or** `economy.yml`. **If you mess around
with these files, you could very well end up breaking them, which could affect multiple parts of the plugin. ONLY EDIT THESE FILES IF YOU KNOW WHAT YOU ARE DOING.**
