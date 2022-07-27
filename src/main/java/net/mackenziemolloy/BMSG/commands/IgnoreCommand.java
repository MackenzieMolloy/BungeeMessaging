package net.mackenziemolloy.BMSG.commands;

import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;

public class IgnoreCommand extends Command implements TabExecutor {

    final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

    public IgnoreCommand() {
        super("ignore", "bmsg.command.ignore", "ignoreuser", "ignoreplayer", "block");

    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        // If the command sender is the console
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage("Ignoring players can only be used by players");
            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        // If the player executing the command lacks the permission "bmsg.command.ignore"
        if(!sender.hasPermission("bmsg.command.ignore")) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_No_Permission")));
        else if(args.length == 0) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Ignore_Usage")));

        switch(args[0].toLowerCase()) {
            case "add": commandAdd(sender, args[1]); break;
            case "remove": commandRemove(sender, args[1]); break;
            default: commandToggle(sender, args[1]);



        }



    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer)
            if(args.length <= 2) return Main.getPlayerManager().getOnlinePlayerNames((ProxiedPlayer) sender, args[0]);

        return Collections.emptyList();
    }


    private void commandAdd(ProxiedPlayer sender, String target) {
        if(target == null) sendMessage(sender, "bad usage :)");

        String targetUUID = Main.playerManager.getUUIDFromUsername(target);
        String targetName = Main.playerManager.getNameFromString(target);

        if(targetUUID == null) sendMessage(sender, Main.cg.getString("Ignore_Player_Not_Found"));
        else if(Main.playerManager.getPlayerIgnoreList(sender.getUniqueId().toString()).contains(targetUUID)) sendMessage(sender, Main.cg.getString("Ignore_Already_Ignoring"));
        else {
            boolean added = Main.playerManager.addPlayerToIgnoreList(sender.getUniqueId().toString(), targetUUID);
            if (added) sendMessage(sender, Main.cg.getString("Ignore_User_Ignored").replace("%player%", targetName));
            else sendMessage(sender, Main.cg.getString("Ignore_Error_Ignoring_Player").replace("%player%", targetName));
        }

        if (!sender.hasPermission("bmsg.command.ignore.bypasscooldown")) Main.getInstance().ignoreCooldowns.put(sender.getName(), System.currentTimeMillis());
    }

    private void commandRemove(ProxiedPlayer sender, String target) {
        if(target == null) sendMessage(sender, "bad usage :)");

        String targetUUID = Main.playerManager.getUUIDFromUsername(target);
        String targetName = Main.playerManager.getNameFromString(target);

        if(targetUUID == null) sendMessage(sender, Main.cg.getString("Ignore_Player_Not_Found"));
        else if(!Main.playerManager.getPlayerIgnoreList(sender.getUniqueId().toString()).contains(targetUUID)) sendMessage(sender, Main.cg.getString("Ignore_Not_Ignoring"));
        else {
            boolean added = Main.playerManager.removePlayerFromIgnoreList(sender.getUniqueId().toString(), targetUUID);
            if (added) sendMessage(sender, Main.cg.getString("Ignore_User_No_Longer_Ignored").replace("%player%", targetName));
            else sendMessage(sender, Main.cg.getString("Ignore_Error_Unignoring_Player").replace("%player%", targetName));
        }

        if (!sender.hasPermission("bmsg.command.ignore.bypasscooldown")) Main.getInstance().ignoreCooldowns.put(sender.getName(), System.currentTimeMillis());
    }

    private void commandToggle(ProxiedPlayer sender, String targetName) {
        if(targetName == null) sendMessage(sender, "bad usage :)");
        if (Main.getInstance().ignoreCooldowns.containsKey(sender.getName())) {
            final long secondsLeft = Main.getInstance().ignoreCooldowns.get(sender.getName()) / 1000L + Main.cg.getInt("Ignore_Cooldown") - System.currentTimeMillis() / 1000L;
            if (secondsLeft > 0L) {
                sendMessage(sender, Main.cg.getString("Ignore_Cooldown_Wait").replace("%sender%", sender.getName()).replace("%time%", String.valueOf(secondsLeft)));
                return;
            } else Main.getInstance().ignoreCooldowns.remove(sender.getName());
        }

        String targetUUID = Main.playerManager.getUUIDFromUsername(targetName);
        if(targetUUID == null) sendMessage(sender, Main.cg.getString("Ignore_Player_Not_Found"));

        else if(Main.playerManager.getPlayerIgnoreList(sender.getUniqueId().toString()).contains(targetUUID)) commandRemove(sender, targetName);
        else commandAdd(sender, targetName);
    }

    private void sendMessage(ProxiedPlayer target, String message) {
        if(!message.isEmpty()) target.sendMessage(new TextComponent(prefix + MessageUtility.color(message)));
    }

}
