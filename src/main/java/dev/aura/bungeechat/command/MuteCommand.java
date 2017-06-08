package dev.aura.bungeechat.command;

import dev.aura.bungeechat.account.BungeecordAccountManager;
import dev.aura.bungeechat.api.account.BungeeChatAccount;
import dev.aura.bungeechat.api.enums.Permission;
import dev.aura.bungeechat.message.Message;
import dev.aura.bungeechat.module.MutingModule;
import dev.aura.bungeechat.permission.PermissionManager;
import net.md_5.bungee.api.CommandSender;

import java.util.Optional;

public class MuteCommand extends BaseCommand {
    public MuteCommand(MutingModule mutingModule) {
        super("mute", mutingModule.getModuleSection().getStringList("aliases.mute"));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String[] args) {
        if (PermissionManager.hasPermission(sender, Permission.COMMAND_MUTE)) {
            if (args.length < 1) {
                sender.sendMessage(Message.INCORRECT_USAGE.get(sender, "/mute <player>"));
            } else {
                Optional<BungeeChatAccount> targetAccount = BungeecordAccountManager.getAccount(args[0]);

                if (!targetAccount.isPresent()) {
                    sender.sendMessage(Message.PLAYER_NOT_FOUND.get());
                    return;
                }

                CommandSender target = BungeecordAccountManager.getCommandSender(targetAccount.get()).get();

                if (targetAccount.get().isMuted()) {
                    sender.sendMessage(Message.MUTE_IS_MUTED.get());
                    return;
                }

                targetAccount.get().mutePermanetly();
                sender.sendMessage(Message.MUTE.get(target));
            }
        }
    }
}