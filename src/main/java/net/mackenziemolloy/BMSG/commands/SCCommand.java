// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import java.util.Iterator;
import java.util.Objects;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SCCommand extends Command
{
    public SCCommand() {
        super("sc", "bmsg.command.staffchat", "staffchat");
    }
    
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));
            final ProxiedPlayer player = (ProxiedPlayer)sender;
            if (player.hasPermission("bmsg.command.staffchat")) {
                if (Main.cg.getBoolean("StaffChat_Enabled")) {
                    if (args.length >= 1) {
                        Main.cg.getBoolean("StaffChat_Enabled");
                        String mensaje = "";
                        for (String arg : args) {
                            mensaje = mensaje + arg + " ";
                        }
                        final LuckPerms api = LuckPermsProvider.get();
                        final String senderPrefix = Objects.requireNonNull(api.getUserManager().getUser(player.getUniqueId())).getCachedData().getMetaData().getPrefix();
                        final String server = player.getServer().getInfo().getName();
                        final String format = MessageUtility.color(Main.cg.getString("StaffChat_Format").replace("&", "§").replace("%player%", player.getName()).replace("%server%", server).replace("%sender-prefix%", senderPrefix).replace("%msg%", mensaje));
                        for (final ProxiedPlayer all : Main.instance.getProxy().getPlayers()) {
                            if (all.hasPermission("bmsg.command.staffchat")) {
                                all.sendMessage(format);
                            }
                        }
                    }
                    else {
                        if (Main.staffchat.contains(player)) {
                            Main.staffchat.remove(player);
                            player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Staff_Chat_Off")));
                            return;
                        }
                        Main.staffchat.add(player);
                        player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Staff_Chat_On")));
                    }
                }
                else {
                    player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("StaffChat_Disabled")));
                }
            }
            else {
                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));
            }
        }
    }
}
