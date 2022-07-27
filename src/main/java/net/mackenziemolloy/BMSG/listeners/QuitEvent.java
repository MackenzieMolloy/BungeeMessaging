// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.listeners;

import net.mackenziemolloy.BMSG.commands.ReplyCommand;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;

public class QuitEvent implements Listener
{
    @EventHandler
    public void salir(final PlayerDisconnectEvent e) {
        final ProxiedPlayer player = e.getPlayer();
        if (ReplyCommand.replyhash.containsKey(player) || ReplyCommand.replyhash.containsValue(player.getName())) {
            final ProxiedPlayer player2 = ReplyCommand.replyhash.get(player);
            ReplyCommand.replyhash.remove(player);
            ReplyCommand.replyhash.remove(player2);
        }
    }
}
