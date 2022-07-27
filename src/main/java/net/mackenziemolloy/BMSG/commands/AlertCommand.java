// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import io.netty.handler.codec.AsciiHeadersEncoder;
import net.mackenziemolloy.BMSG.Main;

import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import java.util.HashMap;
import net.md_5.bungee.api.plugin.Command;

public class AlertCommand extends Command {
    public HashMap<String, Long> cooldowns = new HashMap<>();
    
    public AlertCommand() { super("broadcast", "bmsg.command.broadcast", "bc", "alert", "bcast"); }

    public void execute(final CommandSender sender, final String[] args) {
        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer player = (ProxiedPlayer)sender;

            if (sender.hasPermission("bmsg.command.broadcast")) {
                if (args.length >= 1) {
                    if (Main.cg.getBoolean("BroadCast_Enabled")) {

                        final int c = Main.cg.getInt("BroadCast_Cooldown");

                        if (this.cooldowns.containsKey(player.getName())) {
                            final long secondsLeft = this.cooldowns.get(player.getName()) / 1000L + c - System.currentTimeMillis() / 1000L;
                            if (secondsLeft > 0L) {
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("BroadCast_Cooldown_Wait")).replace("%player%", player.getName()).replace("%time%", String.valueOf(secondsLeft)));
                                return;
                            }
                        }

                        StringBuilder message = new StringBuilder();
                        for (String arg : args) message.append(arg).append(" ");

                        final String finalMessage = MessageUtility.color(Main.cg.getString("BroadCast_Format").replace("%player%", player.getName()).replace("%server%", player.getServer().getInfo().getName()).replace("%msg%", message.toString().replace("\\n", System.getProperty("line.separator"))));
                        Main.instance.getProxy().broadcast(finalMessage);

                        if(!player.hasPermission("bmsg.command.broadcast.bypasscooldown")) this.cooldowns.put(player.getName(), System.currentTimeMillis());
                    }

                    else player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("BroadCast_Disabled")));
                } else player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("BroadCast_Usage")));
            } else player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));

        }

        else if (args.length >= 1) {
            if (Main.cg.getBoolean("BroadCast_Enabled")) {
                StringBuilder message = new StringBuilder();
                for (String arg : args) message.append(arg).append(" ");

                final String finalMessage = MessageUtility.color(Main.cg.getString("BroadCast_Format").replace("%player%", "Console").replace("%msg%", message.toString().replace("\\n", System.getProperty("line.separator"))));
                Main.instance.getProxy().broadcast(finalMessage);
            }
            else sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("BroadCast_Disabled")));

        } else sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("BroadCast_Usage")));

    }
}
