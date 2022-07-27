// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import java.util.HashMap;
import java.util.Objects;

import net.md_5.bungee.api.plugin.Command;

public class HelpOp extends Command
{
    public HashMap<String, Long> cooldowns;
    
    public HelpOp() {
        super("helpop", "bmsg.command.helpop");
        this.cooldowns = new HashMap<>();
    }
    
    public void execute(final CommandSender sender, final String[] args) {
        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));
        if (sender instanceof ProxiedPlayer) {
            if (sender.hasPermission("bmsg.command.helpop")) {
                final ProxiedPlayer player = (ProxiedPlayer)sender;
                if (args.length >= 1) {
                    if (Main.cg.getBoolean("HelpOp_Enabled")) {
                        final int c = Main.cg.getInt("HelpOp_Cooldown");
                        if (this.cooldowns.containsKey(player.getName())) {
                            final long secondsLeft = this.cooldowns.get(player.getName()) / 1000L + c - System.currentTimeMillis() / 1000L;
                            if (secondsLeft > 0L) {
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Cooldown_Wait")).replace("%player%", player.getName()).replace("%time%", new StringBuilder(String.valueOf(secondsLeft)).toString()));
                                return;
                            }
                        }
                        StringBuilder mensaje = new StringBuilder();
                        for (String arg : args) {
                            mensaje.append(arg).append(" ");
                        }
                        final LuckPerms api = LuckPermsProvider.get();
                        final String senderPrefix = Objects.requireNonNull(api.getUserManager().getUser(player.getUniqueId())).getCachedData().getMetaData().getPrefix();
                        assert senderPrefix != null;
                        final String format = MessageUtility.color(Main.cg.getString("HelpOp_Format").replace("%player%", player.getName()).replace("%server%", player.getServer().getInfo().getName()).replace("%msg%", mensaje.toString()).replace("%sender-prefix%", senderPrefix));
                        player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("HelpOp_Sended")));
                        for (final ProxiedPlayer staffs : Main.getInstance().getProxy().getPlayers()) {
                            if (staffs.hasPermission("bmsg.command.helpop.receive")) {
                                staffs.sendMessage(format);
                            }
                        }
                        this.cooldowns.put(player.getName(), System.currentTimeMillis());
                    }
                    else {
                        player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("HelpOp_Disabled")));
                    }
                }
                else {
                    player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("HelpOp_Usage")));
                }
            }
            else {
                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));
            }
        }
        else {
            sender.sendMessage(prefix + "command only for players!");
        }
    }
}
