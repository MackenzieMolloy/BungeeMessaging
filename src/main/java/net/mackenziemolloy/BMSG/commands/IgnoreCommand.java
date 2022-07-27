package net.mackenziemolloy.BMSG.commands;

import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;

public class IgnoreCommand extends Command implements TabExecutor {

    public IgnoreCommand() {
        super("ignore", "bmsg.command.ignore", "ignoreuser", "ignoreplayer", "block");

    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        // If the command sender is the console
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage("Ignoring players can only be used by players");
            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        // If the player executing the command lacks the permission "bmsg.command.ignore"
        if(!sender.hasPermission("bmsg.command.ignore")) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_No_Permission")));
        else if(args.length == 0) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Usage")));
        else {

            if(Main.getInstance().ignoreCooldowns.containsKey(sender.getName())) {
                final int cooldownDuration = Main.cg.getInt("Ignore_Cooldown");
                final long secondsLeft = Main.getInstance().ignoreCooldowns.get(sender.getName()) / 1000L + cooldownDuration - System.currentTimeMillis() / 1000L;
                if (secondsLeft > 0L) {
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Cooldown_Wait")).replace("%sender%", sender.getName()).replace("%time%", String.valueOf(secondsLeft)));
                    return;
                }
                else Main.getInstance().ignoreCooldowns.remove(sender.getName());
            }

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if(target == null) {

                String targetUUID = Main.playerManager.getUUIDFromUsername(args[0]);
                if(targetUUID == null) {
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Player_Not_Found").replace("%player%", args[0])));
                    return;
                }
            }

            String targetUUID = target == null ? Main.playerManager.getUUIDFromUsername(args[0]) : target.getUniqueId().toString();
            String playerName = target == null ? args[0] : target.getDisplayName();

            List<String> ignoreList = Main.playerManager.getPlayerIgnoreList(sender.getUniqueId().toString());

            // Target user is to be removed from ignore list
            if(ignoreList.contains(targetUUID)) {
                boolean removed = Main.playerManager.removePlayerFromIgnoreList(sender.getUniqueId().toString(), targetUUID);
                if(removed) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_User_No_Longer_Ignored").replace("%player%", playerName)));
                else sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Error_Unignoring_Player").replace("%player%", playerName)));
            }

            // Target user is to be added to the list
            else {
                boolean added = Main.playerManager.addPlayerToIgnoreList(sender.getUniqueId().toString(), targetUUID);
                if(added) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_User_Ignored").replace("%player%", playerName)));
                else sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Error_Iignoring_Player").replace("%player%", playerName)));

            }

            if(!sender.hasPermission("bmsg.command.ignore.bypasscooldown")) Main.getInstance().ignoreCooldowns.put(sender.getName(), System.currentTimeMillis());

        }



    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer)
            if(args.length == 1) return Main.getPlayerManager().getOnlinePlayerNames((ProxiedPlayer) sender, args[0]);

        return Collections.emptyList();
    }
}
