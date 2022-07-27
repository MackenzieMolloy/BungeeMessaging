// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.CommandSender;
import java.util.ArrayList;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.List;
import net.md_5.bungee.api.plugin.Command;

public class ToggleMsg extends Command
{
    public static List<ProxiedPlayer> tmsg;
    
    static { ToggleMsg.tmsg = new ArrayList<>(); }
    
    public ToggleMsg() {
        super("togglemsg", "bmsg.command.togglemsg", "tmsg", "pmtoggle", "msgtoggle");
    }
    
    public void execute(final CommandSender commandSender, final String[] args) {
        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        if (commandSender instanceof ProxiedPlayer) {
            final ProxiedPlayer sender = (ProxiedPlayer)commandSender;

            if (sender.hasPermission("bmsg.command.togglemsg")) {

                if(Main.getInstance().toggleMessageCooldowns.containsKey(sender.getName())) {
                    final int cooldownDuration = Main.cg.getInt("ToggleMessage_Cooldown");
                    final long secondsLeft = Main.getInstance().toggleMessageCooldowns.get(sender.getName()) / 1000L + cooldownDuration - System.currentTimeMillis() / 1000L;
                    if (secondsLeft > 0L) {
                        sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("ToggleMessage_Cooldown_Wait")).replace("%sender%", sender.getName()).replace("%time%", String.valueOf(secondsLeft)));
                        return;
                    }
                    else Main.getInstance().toggleMessageCooldowns.remove(sender.getName());
                }

                if (!Main.getPlayerManager().getPlayerMessageState(sender)) {
                    ToggleMsg.tmsg.remove(sender);
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("ReceiveMessages_Enabled")));
                    Main.getPlayerManager().setPlayerMessageState(sender, true);

                }
                else {
                    ToggleMsg.tmsg.add(sender);
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("ReceiveMessages_Disabled")));
                    Main.getPlayerManager().setPlayerMessageState(sender, false);
                }

                if(!sender.hasPermission("bmsg.command.togglemsg.bypasscooldown")) Main.getInstance().toggleMessageCooldowns.put(sender.getName(), System.currentTimeMillis());
            }

            else sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));
        } else commandSender.sendMessage(prefix + "command only for players!");

    }
}
