// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG;

import net.mackenziemolloy.BMSG.commands.*;
import net.mackenziemolloy.BMSG.listeners.QuitEvent;
import net.mackenziemolloy.BMSG.listeners.onChat;
import net.mackenziemolloy.BMSG.listeners.onPlayerJoin;
import net.mackenziemolloy.BMSG.managers.PlayerManager;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.CopyOption;
import java.io.File;
import java.util.ArrayList;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.entity.Player;

public class Main extends Plugin {

    public HashMap<String, Long> messageCooldowns = new HashMap<>();
    public HashMap<String, Long> ignoreCooldowns = new HashMap<>();
    public HashMap<String, Long> toggleMessageCooldowns = new HashMap<>();

    public static Main instance;
    public static Configuration cg;
    public static List<ProxiedPlayer> staffchat;
    
    static {
        Main.staffchat = new ArrayList<>();
    }

    public static PlayerManager playerManager;

    public void onEnable() {
        (Main.instance = this).createFiles();
        Main.playerManager = new PlayerManager(this);
        registerConfig();

        getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
        getProxy().getPluginManager().registerCommand(this, new MsgCommand());
        getProxy().getPluginManager().registerCommand(this, new SCCommand());
        getProxy().getPluginManager().registerCommand(this, new SocialSpy());
        getProxy().getPluginManager().registerCommand(this, new HelpOp());
        getProxy().getPluginManager().registerCommand(this, new ToggleMsg());
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand());
        getProxy().getPluginManager().registerCommand(this, new AlertCommand());
        getProxy().getPluginManager().registerCommand(this, new IgnoreCommand());

        getProxy().getPluginManager().registerListener(this, new QuitEvent());
        getProxy().getPluginManager().registerListener(this, new onChat());
        getProxy().getPluginManager().registerListener(this, new onPlayerJoin());

    }
    
    private void createFiles() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();

        File playerDatas = new File(this.getDataFolder(), "playerdata");
        if(!playerDatas.exists()) playerDatas.mkdir();

        final File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                final InputStream in = this.getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());
            }
            catch (IOException e) { e.printStackTrace(); }

        }

        final File playersCache = new File(this.getDataFolder(), "players.yml");
        if (!playersCache.exists()) {
            try {
                final InputStream in = this.getResourceAsStream("players.yml");
                Files.copy(in, playersCache.toPath());
            }
            catch (IOException e) { e.printStackTrace(); }

        }

    }
    
    public static void registerConfig() {
        try {
            Main.cg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.instance.getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Main getInstance() {
        return Main.instance;
    }

    public static PlayerManager getPlayerManager() {
        return Main.playerManager;
    }
}
