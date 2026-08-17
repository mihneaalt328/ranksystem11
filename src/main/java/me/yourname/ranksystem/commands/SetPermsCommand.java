package me.yourname.ranksystem.commands;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetPermsCommand implements CommandExecutor {
    private final RankSystem plugin;
    public SetPermsCommand(RankSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ranksystem.admin")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /setperms <rank> <add|remove|list> [permission]");
            return true;
        }
        String rank = args[0], action = args[1].toLowerCase();
        if (!plugin.getRankManager().exists(rank)) {
            sender.sendMessage("§cThat rank does not exist.");
            return true;
        }
        if (action.equals("list")) {
            sender.sendMessage("§6Permissions for §f" + rank + "§6:");
            for (String p : plugin.getRankManager().getAllPermissions(rank)) sender.sendMessage("§7- §f" + p);
            return true;
        }
        if (args.length < 3 || (!action.equals("add") && !action.equals("remove"))) {
            sender.sendMessage("§cUsage: /setperms <rank> <add|remove|list> [permission]");
            return true;
        }
        if (action.equals("add")) {
            plugin.getRankManager().addPermission(rank, args[2]);
            sender.sendMessage("§aPermission added.");
        } else {
            plugin.getRankManager().removePermission(rank, args[2]);
            sender.sendMessage("§aPermission removed.");
        }
        for (org.bukkit.entity.Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPlayerRankManager().getRank(p.getUniqueId()).equalsIgnoreCase(rank))
                plugin.getPermissionManager().apply(p);
        }
        return true;
    }
}
