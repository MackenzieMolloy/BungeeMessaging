// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import java.util.Iterator;
import java.util.Objects;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

public class onChat implements Listener
{
    @EventHandler
    public void Chat(final ChatEvent e) {

        final ProxiedPlayer player = (ProxiedPlayer)e.getSender();

        if (!e.isCommand() && Main.staffchat.contains(player)) {

            for (final ProxiedPlayer all : Main.instance.getProxy().getPlayers()) {

                if (all.hasPermission("bmsg.command.staffchat")) {
                    e.setCancelled(true);

                    final String msg = MessageUtility.color(e.getMessage());
                    final String server = player.getServer().getInfo().getName();
                    final LuckPerms api = LuckPermsProvider.get();
                    final String senderPrefix = MessageUtility.color(
                            Objects.requireNonNull(Objects.requireNonNull(api.getUserManager().getUser(player.getUniqueId())).getCachedData().getMetaData().getPrefix()));

                    final String format = MessageUtility.color(Main.cg.getString("StaffChat_Format")
                            .replace("%player%", player.getName())
                            .replace("%server%", server)
                            .replace("%msg%", msg)
                            .replace("%sender-prefix%", senderPrefix));

                    all.sendMessage(format);

                }

            }

        }

    }

}
