// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SocialSpy extends Command implements TabExecutor {
    public static HashMap<ProxiedPlayer, ProxiedPlayer> replyhash;
    public static HashMap<ProxiedPlayer, Integer> sp;
    
    static {
        SocialSpy.replyhash = new HashMap<ProxiedPlayer, ProxiedPlayer>();
        SocialSpy.sp = new HashMap<ProxiedPlayer, Integer>();
        // 0 => All servers
        // 1 => Local Server
    }
    
    public SocialSpy() {
        super("socialspy", "bmsg.command.socialspy", "sp");
    }
    
    public void execute(final CommandSender sender, final String[] args) {

        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        if (sender instanceof ProxiedPlayer) {

            final ProxiedPlayer player = (ProxiedPlayer)sender;

            // Player has permission
            if (player.hasPermission("bmsg.command.socialspy")) {

                // Player is in list
                if (SocialSpy.sp.containsKey(player)) {

                    // Player state switch
                    switch(SocialSpy.sp.get(player)) {

                        case 0:

                            if(args.length == 0 || !args[0].equalsIgnoreCase("-l")) {
                                SocialSpy.sp.remove(player);
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("SocialSpy_Off")));
                                break;
                            }

                            else {
                                SocialSpy.sp.replace(player, 1);
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("SocialSpy_Change")
                                        .replace("%state%", Main.cg.getString("SocialSpy_Local"))));
                                break;
                            }

                        case 1:

                            if(args.length == 0 || !args[0].equalsIgnoreCase("-l")) {
                                SocialSpy.sp.replace(player, 0);
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("SocialSpy_Change")
                                        .replace("%state%", Main.cg.getString("SocialSpy_Global"))));
                                break;
                            }
                            else {
                                SocialSpy.sp.remove(player);
                                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("SocialSpy_Off")));
                                break;
                            }

                    }

                }

                else if(args.length == 0 || !args[0].equalsIgnoreCase("-l")) {
                    SocialSpy.sp.put(player, 0);
                    player.sendMessage(MessageUtility.color(prefix + Main.cg.getString("SocialSpy_On")
                            .replace("%state%", Main.cg.getString("SocialSpy_Global"))));
                }

                else if(args[0].equalsIgnoreCase("-l")) {
                    SocialSpy.sp.put(player, 1);
                    player.sendMessage(MessageUtility.color(prefix+ Main.cg.getString("SocialSpy_On")
                            .replace("%state%", Main.cg.getString("SocialSpy_Local"))));

                }

            }

            else {
                player.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));
            }

        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return (Iterable<String>) Collections.singletonList("-l");
        else return Collections.emptyList();
    }
}
