package net.mackenziemolloy.BMSG.managers;

import net.mackenziemolloy.BMSG.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerManager {

    Main main;


    public PlayerManager(Main main) { this.main = main; }

    // Add cooldowns later?


    public boolean getPlayerMessageState(ProxiedPlayer player) { return getPlayerFileContent(player.getUniqueId().toString()).getBoolean("privatemessages");  }
    public boolean setPlayerMessageState(ProxiedPlayer player, boolean state) {
        Configuration playerFile = getPlayerFileContent(player.getUniqueId().toString());
        playerFile.set("privatemessages", state);
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerFile, getPlayerFile(player.getUniqueId().toString()));
            return true;
        }
        catch(IOException e) { e.printStackTrace(); return false; }
    }

    // Find a way to switch to and from a backlist/whitelist system

    public List<String> getPlayerIgnoreList(String playerUUID) { return getPlayerFileContent(playerUUID).getStringList("ignoredplayers"); }

    public boolean addPlayerToIgnoreList(String requesterUUID, String targetUUID) {
        Configuration playerFile = getPlayerFileContent(requesterUUID);
        List<String> ignored = playerFile.getStringList("ignoredplayers");
        if(ignored.contains(targetUUID)) return false;
        ignored.add(targetUUID);
        playerFile.set("ignoredplayers", ignored);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerFile, getPlayerFile(requesterUUID));
            return true;
        }
        catch(IOException e) { e.printStackTrace(); return false; }
    }
    public boolean removePlayerFromIgnoreList(String requesterUUID, String targetUUID) {
        Configuration playerFile = getPlayerFileContent(requesterUUID);
        List<String> ignored = playerFile.getStringList("ignoredplayers");
        if(!ignored.contains(targetUUID)) return false;
        ignored.remove(targetUUID);
        playerFile.set("ignoredplayers", ignored);

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(playerFile, getPlayerFile(requesterUUID));
            return true;
        }
        catch(IOException e) { e.printStackTrace(); return false; }

    }


    public Configuration getPlayerFileContent(String playerUUID) {
        Configuration playerFileContent;
        File playerFile = getPlayerFile(playerUUID);

        try {
            playerFileContent = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.instance.getDataFolder() + "/playerdata", playerFile.getName()));
            return playerFileContent;
        }
        catch (IOException e) { e.printStackTrace(); return null;}

    }

    public File getPlayerFile(String playerUUID) {

        String playerFileName = playerUUID+".yml";
        final File file = new File(main.getDataFolder() + "/playerdata", playerFileName);
        if (!file.exists()) {
            try {
                final InputStream in = main.getResourceAsStream("playerdata/player.yml");
                Files.copy(in, file.toPath());
                return file;
            }
            catch (IOException e) { e.printStackTrace(); return null;}
        }
        return file;
    }

    public File getPlayerCacheFile() {

        String playersFileName = "players.yml";
        final File file = new File(main.getDataFolder(), playersFileName);
        if (!file.exists()) {
            try {
                final InputStream in = main.getResourceAsStream(playersFileName);
                Files.copy(in, file.toPath());
                return file;
            }
            catch (IOException e) { e.printStackTrace(); return null;}
        }
        return file;
    }


    public boolean storePlayerName(ProxiedPlayer player) {
        File playersCache = getPlayerCacheFile();

        try {
            Configuration storedData = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.instance.getDataFolder(), playersCache.getName()));
            storedData.set(player.getUniqueId().toString(), player.getName());
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(storedData, playersCache);
            return true;
        }
        catch(IOException e) { e.printStackTrace(); return false; }

    }

    public String getUUIDFromUsername(String playerName) {
        File playersCache = getPlayerCacheFile();

        try {
            Configuration storedData = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.instance.getDataFolder(), playersCache.getName()));
            for (String key : storedData.getKeys()) {
                if (storedData.getString(key).equalsIgnoreCase(playerName)) return key;
            }
        }
        catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    public Iterable<String> getOnlinePlayerNames(ProxiedPlayer sender, String filter) {

        List<String> playerNames = new ArrayList<String>();
        ProxyServer.getInstance().getPlayers().forEach(player -> { if(player.getName().toUpperCase().startsWith(filter.toUpperCase())) playerNames.add(player.getName()); });

        return (Iterable<String>) playerNames;

    }

}
