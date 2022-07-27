// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.mackenziemolloy.BMSG.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command
{
    public ReloadCommand() {
        super("bmsgreload", "bmsg.command.reload", "");
    }
    
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer player = (ProxiedPlayer)sender;
            if (player.hasPermission("bmsg.command.reload")) {
                Main.registerConfig();
                player.sendMessage("§aConfig has been reloaded!");
            }
            else {
                player.sendMessage("§cYou dont have permissions to use this command!");
            }
        }
        else {
            Main.registerConfig();
            sender.sendMessage("§aConfig has been reloaded!");
        }
    }
}
