// 
// Decompiled by Procyon v0.5.36
// 

package net.mackenziemolloy.BMSG.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.mackenziemolloy.BMSG.Main;
import net.mackenziemolloy.BMSG.utility.MessageUtility;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MsgCommand extends Command implements TabExecutor {

    public static List<String> comandos;
    
    static { MsgCommand.comandos = new ArrayList<>(); }

    
    public MsgCommand() {
        super("msg", "bmsg.command.msg", "tell", "say", "pm");
    }
    
    public void execute(final CommandSender commandSender, final String[] args) {
        final String prefix = MessageUtility.color(Main.cg.getString("Prefix"));

        if (commandSender instanceof ProxiedPlayer) {
            final ProxiedPlayer sender = (ProxiedPlayer) commandSender;

            if (sender.hasPermission("bmsg.command.msg")) {
                if (args.length >= 2) {
                    final ProxiedPlayer receiver = Main.getInstance().getProxy().getPlayer(args[0]);

                    if (receiver == null) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Player_Offline")));
                    else if (sender.getName().equals(receiver.getName())) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Message_Yourself")));
                    else if (!Main.playerManager.getPlayerMessageState(receiver)) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Messages_Disabled").replace("%player%", receiver.getName())));
                    else if (Main.playerManager.getPlayerIgnoreList(receiver.getUniqueId().toString()).contains(sender.getUniqueId().toString()) && !sender.hasPermission("bmsg.command.ignore.bypass")) sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Message_Sender_Ignored").replace("%player%", receiver.getDisplayName())));

                    else {

                        if(Main.getInstance().messageCooldowns.containsKey(sender.getName())) {
                            final int cooldownDuration = Main.cg.getInt("Message_Cooldown");
                            final long secondsLeft = Main.getInstance().messageCooldowns.get(sender.getName()) / 1000L + cooldownDuration - System.currentTimeMillis() / 1000L;
                            if (secondsLeft > 0L) {
                                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Message_Cooldown_Wait")).replace("%sender%", sender.getName()).replace("%time%", String.valueOf(secondsLeft)));
                                return;
                            }
                            else Main.getInstance().messageCooldowns.remove(sender.getName());
                        }

                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; ++i) {
                            message.append(args[i]).append(" ");
                        }
                        final LuckPerms api = LuckPermsProvider.get();
                        final String senderPrefix = Objects.requireNonNull(api.getUserManager().getUser(sender.getUniqueId())).getCachedData().getMetaData().getPrefix() != null ? Objects.requireNonNull(api.getUserManager().getUser(sender.getUniqueId())).getCachedData().getMetaData().getPrefix() : "";
                        final String receiverPrefix = Objects.requireNonNull(api.getUserManager().getUser(receiver.getUniqueId())).getCachedData().getMetaData().getPrefix() != null ? Objects.requireNonNull(api.getUserManager().getUser(receiver.getUniqueId())).getCachedData().getMetaData().getPrefix() : "";
                        final String senderServer = sender.getServer().getInfo().getName();
                        final String receiverServer = receiver.getServer().getInfo().getName();

                        final String senderFormat = MessageUtility.color(Main.cg.getString("Sender_Format").replace("%sender-name%", sender.getName()).replace("%sender-server%", senderServer).replace("%msg%", message.toString()).replace("%receiver-server%", receiverServer).replace("%receiver-name%", receiver.getName()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));
                        final String receiverFormat = MessageUtility.color(Main.cg.getString("Receiver_Format").replace("%sender-name%", sender.getName()).replace("%sender-server%", senderServer).replace("%msg%", message.toString()).replace("%receiver-server%", receiverServer).replace("%receiver-name%", receiver.getName()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));
                        sender.sendMessage(TextComponent.fromLegacyText(senderFormat));
                        receiver.sendMessage(TextComponent.fromLegacyText(receiverFormat));

                        if(!sender.hasPermission("bmsg.command.msg.bypasscooldown")) Main.getInstance().messageCooldowns.put(sender.getName(), System.currentTimeMillis());


                        for (final ProxiedPlayer staffMember : Main.getInstance().getProxy().getPlayers()) {
                            if (SocialSpy.sp.containsKey(staffMember)) {
                                final String format = MessageUtility.color(Main.cg.getString("SocialSpy_Format").replace("%sender%", sender.getName()).replace("%receiver%", receiver.getName()).replace("%msg%", message.toString()).replace("%sender-prefix%", senderPrefix).replace("%receiver-prefix%", receiverPrefix));
                                switch (SocialSpy.sp.get(staffMember)) {

                                    case 0:
                                        staffMember.sendMessage(format);
                                        break;

                                    case 1:

                                        if (sender.getServer().getInfo().getName().equals(staffMember.getServer().getInfo().getName()) || receiver.getServer().getInfo().getName().equals(staffMember.getServer().getInfo().getName())) {
                                            staffMember.sendMessage(format);
                                        }
                                        break;
                                }

                            }
                        }

                        //if (ReplyCommand.replyhash.containsKey(sender) || ReplyCommand.replyhash.containsKey(receiver)) {
                            //ReplyCommand.replyhash.remove(sender);
                            //ReplyCommand.replyhash.remove(receiver);
                          //  ReplyCommand.replyhash.put(sender, receiver);
                            //ReplyCommand.replyhash.put(receiver, sender);
                       // }
                        ReplyCommand.replyhash.put(sender, receiver);
                        ReplyCommand.replyhash.put(receiver, sender);
                    }
                }
                else {
                    sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("Message_Usage")));
                }
            }
            else {
                sender.sendMessage(prefix + MessageUtility.color(Main.cg.getString("No_Permission")));
            }
        }
        else {
            commandSender.sendMessage(prefix + "command only for senders!");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer)
            if(args.length == 1) return Main.getPlayerManager().getOnlinePlayerNames((ProxiedPlayer) sender, args[0]);

        return Collections.emptyList();
    }
}
