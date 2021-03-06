package dev.aura.bungeechat.command;

import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.message.Messages;
import dev.aura.bungeechat.message.MessagesService;
import dev.aura.bungeechat.module.MessengerModule;
import dev.aura.bungeechat.permission.Permission;
import dev.aura.bungeechat.permission.PermissionManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;

public class ReplyCommand extends BaseCommand {
  private static HashMap<CommandSender, CommandSender> replies;

  public ReplyCommand(MessengerModule messengerModule) {
    super("reply", messengerModule.getModuleSection().getStringList("aliases.reply"));

    if (replies == null) {
      replies = new HashMap<>();
    } else {
      replies.clear();
    }
  }

  protected static void setReply(CommandSender sender, CommandSender target) {
    replies.put(sender, target);
    replies.put(target, sender);
  }

  private static CommandSender getReplier(CommandSender player) {
    return replies.getOrDefault(player, null);
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (PermissionManager.hasPermission(sender, Permission.COMMAND_MESSAGE)) {
      if (args.length < 1) {
        MessagesService.sendMessage(
            sender, Messages.INCORRECT_USAGE.get(sender, "/reply <message>"));
      } else {
        Optional<BungeeChatAccount> targetAccount =
            BungeecordAccountManager.getAccount(getReplier(sender));

        if (!targetAccount.isPresent()
            || (targetAccount.get().isVanished()
                && !PermissionManager.hasPermission(sender, Permission.COMMAND_VANISH_VIEW))) {
          MessagesService.sendMessage(sender, Messages.NO_REPLY.get());
          return;
        }

        CommandSender target = BungeecordAccountManager.getCommandSender(targetAccount.get()).get();

        if (!targetAccount.get().hasMessangerEnabled()
            && !PermissionManager.hasPermission(sender, Permission.BYPASS_TOGGLE_MESSAGE)) {
          MessagesService.sendMessage(sender, Messages.HAS_MESSAGER_DISABLED.get(target));
          return;
        }

        String finalMessage = Arrays.stream(args).collect(Collectors.joining(" "));

        MessagesService.sendPrivateMessage(sender, target, finalMessage);
        ReplyCommand.setReply(sender, target);
      }
    }
  }
}
