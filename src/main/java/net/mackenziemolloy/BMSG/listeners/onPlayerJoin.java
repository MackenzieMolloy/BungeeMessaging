package net.mackenziemolloy.BMSG.listeners;

import net.mackenziemolloy.BMSG.Main;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class onPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) { Main.playerManager.storePlayerName(event.getPlayer()); }

}
