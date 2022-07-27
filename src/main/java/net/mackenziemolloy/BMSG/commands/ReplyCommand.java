// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.HexColorUtility;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.HashMap;
import java.util.Objects;

import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command
{
    public static HashMap<ProxiedPlayer, ProxiedPlayer> replyhash;
    
    static {
        ReplyCommand.replyhash = new HashMap<>();
    }
    
    public ReplyCommand() {
        super("r", "bmsg.command.msg", "reply");
    }
    
    public void execute(final CommandSender commandSender, final String[] args) {
        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        if (commandSender instanceof ProxiedPlayer) {
            final ProxiedPlayer sender = (ProxiedPlayer) commandSender;

            if (sender.hasPermission("bmsg.command.msg")) {
                if (ReplyCommand.replyhash.containsKey(sender)) {
                    final ProxiedPlayer receiver = ReplyCommand.replyhash.get(sender);
                    if (ReplyCommand.replyhash.containsValue(receiver)) {
                        if (receiver != null) {
                            if (args.length >= 1) {
                                if (ToggleMsg.tmsg.contains(receiver)) {
                                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Messages_Disabled").replace("%player%", receiver.getName())));
                                    ReplyCommand.replyhash.remove(sender);
                                    ReplyCommand.replyhash.remove(receiver);
                                    return;
                                }

                                if(Main.getInstance().messageCooldowns.containsKey(sender.getName())) {
                                    final int cooldownDuration = Main.cg.getInt("Message_Cooldown");
                                    final long secondsLeft = Main.getInstance().messageCooldowns.get(sender.getName()) / 1000L + cooldownDuration - System.currentTimeMillis() / 1000L;
                                    if (secondsLeft > 0L) {
                                        sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Message_Cooldown_Wait")).replace("%sender%", sender.getName()).replace("%time%", String.valueOf(secondsLeft)));
                                        return;
                                    }
                                    else Main.getInstance().messageCooldowns.remove(sender.getName());
                                }

                                StringBuilder mensaje = new StringBuilder();
                                for (String arg : args) {
                                    mensaje.append(arg).append(" ");
                                }
                                final LuckPerms api = LuckPermsProvider.get();
                                final String senderPrefix = Objects.requireNonNull(api.getUserManager().getUser(sender.getUniqueId())).getCachedData().getMetaData().getPrefix() != null ? Objects.requireNonNull(api.getUserManager().getUser(sender.getUniqueId())).getCachedData().getMetaData().getPrefix() : "";
                                final String receiverPrefix = Objects.requireNonNull(api.getUserManager().getUser(receiver.getUniqueId())).getCachedData().getMetaData().getPrefix() != null ? Objects.requireNonNull(api.getUserManager().getUser(receiver.getUniqueId())).getCachedData().getMetaData().getPrefix() : "";
                                final String senderServer = sender.getServer().getInfo().getName();
                                final String receiverServer = receiver.getServer().getInfo().getName();

                                final String senderFormat = MessageUtility.color(Main.cg.getString("Sender_Format").replace("%msg%", mensaje.toString()).replace("%sender-name%", sender.getName()).replace("%sender-server%", senderServer).replace("%receiver-server%", receiverServer).replace("%receiver-name%", receiver.getName()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));
                                final String receiverFormat = MessageUtility.color(Main.cg.getString("Receiver_Format").replace("%msg%", mensaje.toString()).replace("%sender-name%", sender.getName()).replace("%sender-server%", senderServer).replace("%receiver-server%", receiverServer).replace("%receiver-name%", receiver.getName()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));
                                sender.sendMessage(senderFormat);
                                receiver.sendMessage(receiverFormat);

                                if(!sender.hasPermission("bmsg.command.msg.bypasscooldown")) Main.getInstance().messageCooldowns.put(sender.getName(), System.currentTimeMillis());

                                for (final ProxiedPlayer staffMember : Main.getInstance().getProxy().getPlayers()) {
                                    if (SocialSpy.sp.containsKey(staffMember)) {
                                        final String format = MessageUtility.color(Main.cg.getString("SocialSpy_Format").replace("%sender%", sender.getName()).replace("%receiver%", receiver.getName()).replace("%msg%", mensaje.toString()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));

                                        switch(SocialSpy.sp.get(staffMember)) {

                                            case 0:
                                                staffMember.sendMessage(format);

                                            case 1:
                                                if(sender.getServer() == staffMember.getServer() || receiver.getServer() == staffMember.getServer()) {
                                                    staffMember.sendMessage(format);
                                                }

                                        }

                                    }
                                }

                                if (ReplyCommand.replyhash.containsKey(sender) || ReplyCommand.replyhash.containsKey(receiver)) {
                                    ReplyCommand.replyhash.remove(sender);
                                    ReplyCommand.replyhash.remove(receiver);
                                    ReplyCommand.replyhash.put(sender, receiver);
                                    ReplyCommand.replyhash.put(receiver, sender);
                                }
                                ReplyCommand.replyhash.put(sender, receiver);
                                ReplyCommand.replyhash.put(receiver, sender);
                            }
                            else {
                                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Reply_Usage")));
                            }
                        }
                        else {
                            sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Player_Offline")));
                            ReplyCommand.replyhash.remove(sender);
                            ReplyCommand.replyhash.remove(receiver);
                        }
                    }
                    else {
                        sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Player_Offline")));
                    }
                }
                else {
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Player_Reply")));
                }
            }
            else {
                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Reply_Permission")));
            }
        }
    }
}
