package me.yourname.ranksystem.commands;

import me.yourname.ranksystem.RankSystem;
import me.yourname.ranksystem.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    private final RankSystem plugin;

    public RankCommand(RankSystem plugin) { this.plugin = plugin; }

    private void msg(CommandSender sender, String s) { sender.sendMessage(ColorUtils.color(s)); }

    private boolean admin(CommandSender sender) {
        if (!sender.hasPermission("ranksystem.admin")) {
            msg(sender, "&cYou do not have permission to use this command.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!admin(sender)) return true;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            help(sender); return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            plugin.reloadConfig();
            plugin.getRankManager().reload();
            plugin.getPlayerRankManager().reload();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                plugin.getPermissionManager().apply(p);
                plugin.getTabManager().update(p);
            }
            msg(sender, "&aRankSystem configuration reloaded.");
            return true;
        }

        if (sub.equals("list")) {
            msg(sender, "&6Ranks:");
            for (String r : plugin.getRankManager().getRanks()) {
                msg(sender, "&7- &f" + r + " &8(priority " + plugin.getRankManager().getPriority(r) + ")");
            }
            return true;
        }

        if (sub.equals("create")) {
            if (args.length < 2) { msg(sender, "&cUsage: /rank create <rank>"); return true; }
            String rank = args[1];
            if (plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank already exists."); return true; }
            plugin.getRankManager().createRank(rank);
            msg(sender, "&aRank &f" + rank + " &ahas been created.");
            return true;
        }

        if (sub.equals("delete")) {
            if (args.length < 2) { msg(sender, "&cUsage: /rank delete <rank>"); return true; }
            String rank = args[1];
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
            if (rank.equalsIgnoreCase(plugin.getConfig().getString("settings.protected-rank", "Owner"))
                    && !sender.hasPermission("ranksystem.bypass")) {
                msg(sender, "&cThat rank is protected.");
                return true;
            }
            plugin.getRankManager().deleteRank(rank);
            msg(sender, "&aRank &f" + rank + " &ahas been deleted.");
            return true;
        }

        if (sub.equals("set")) {
            if (args.length < 3) { msg(sender, "&cUsage: /rank set <player> <rank>"); return true; }
            return setRank(sender, args[1], args[2]);
        }

        if (sub.equals("remove")) {
            if (args.length < 2) { msg(sender, "&cUsage: /rank remove <player>"); return true; }
            org.bukkit.OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
            plugin.getPlayerRankManager().removeRank(target.getUniqueId());
            if (target.isOnline()) {
                plugin.getPermissionManager().apply(target.getPlayer());
                plugin.getTabManager().update(target.getPlayer());
            }
            msg(sender, "&aRemoved custom rank from &f" + target.getName() + "&a.");
            return true;
        }

        if (sub.equals("info")) {
            if (args.length < 2) { msg(sender, "&cUsage: /rank info <rank>"); return true; }
            String rank = args[1];
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
            msg(sender, "&8&m------------------------------");
            msg(sender, "&6Rank: &f" + rank);
            msg(sender, "&6Priority: &f" + plugin.getRankManager().getPriority(rank));
            msg(sender, "&6Prefix: &r" + ColorUtils.color(plugin.getRankManager().getPrefix(rank)));
            msg(sender, "&6Suffix: &r" + ColorUtils.color(plugin.getRankManager().getSuffix(rank)));
            msg(sender, "&6Inherits: &f" + String.join("&7, &f", plugin.getRankManager().getParents(rank)));
            msg(sender, "&6Permissions:");
            for (String p : plugin.getRankManager().getAllPermissions(rank)) msg(sender, "&7- &f" + p);
            msg(sender, "&8&m------------------------------");
            return true;
        }

        if (sub.equals("prefix") || sub.equals("suffix")) {
            if (args.length < 3) {
                msg(sender, "&cUsage: /rank " + sub + " <rank> <text>");
                return true;
            }
            String rank = args[1];
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
            String text = join(args, 2);
            if (sub.equals("prefix")) plugin.getRankManager().setPrefix(rank, text);
            else plugin.getRankManager().setSuffix(rank, text);
            refreshRank(rank);
            msg(sender, "&a" + capitalize(sub) + " updated for &f" + rank + "&a.");
            return true;
        }

        if (sub.equals("priority")) {
            if (args.length < 3) { msg(sender, "&cUsage: /rank priority <rank> <number>"); return true; }
            String rank = args[1];
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
            try {
                int priority = Integer.parseInt(args[2]);
                plugin.getRankManager().setPriority(rank, priority);
                refreshAll();
                msg(sender, "&aPriority for &f" + rank + " &ais now &f" + priority + "&a.");
            } catch (NumberFormatException e) {
                msg(sender, "&cPriority must be a number.");
            }
            return true;
        }

        if (sub.equals("inherit")) {
            if (args.length < 3) { msg(sender, "&cUsage: /rank inherit <rank> <parent>"); return true; }
            String rank = args[1], parent = args[2];
            if (!plugin.getRankManager().exists(rank) || !plugin.getRankManager().exists(parent)) {
                msg(sender, "&cBoth ranks must exist.");
                return true;
            }
            if (rank.equalsIgnoreCase(parent)) { msg(sender, "&cA rank cannot inherit itself."); return true; }
            plugin.getRankManager().addParent(rank, parent);
            refreshRank(rank);
            msg(sender, "&aRank &f" + rank + " &anow inherits &f" + parent + "&a.");
            return true;
        }

        if (sub.equals("uninherit")) {
            if (args.length < 3) { msg(sender, "&cUsage: /rank uninherit <rank> <parent>"); return true; }
            String rank = args[1], parent = args[2];
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
            plugin.getRankManager().removeParent(rank, parent);
            refreshRank(rank);
            msg(sender, "&aInheritance removed.");
            return true;
        }

        if (sub.equals("permission") || sub.equals("permissions")) {
            if (args.length < 3) { msg(sender, "&cUsage: /rank permission <rank> <add|remove|list> [permission]"); return true; }
            String rank = args[1], action = args[2].toLowerCase();
            if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }

            if (action.equals("list")) {
                msg(sender, "&6Permissions for &f" + rank + "&6:");
                for (String p : plugin.getRankManager().getAllPermissions(rank)) msg(sender, "&7- &f" + p);
                return true;
            }

            if (args.length < 4 || (!action.equals("add") && !action.equals("remove"))) {
                msg(sender, "&cUsage: /rank permission <rank> <add|remove|list> [permission]");
                return true;
            }

            String permission = args[3];
            if (action.equals("add")) {
                plugin.getRankManager().addPermission(rank, permission);
                msg(sender, "&aAdded &f" + permission + " &ato &f" + rank + "&a.");
            } else {
                plugin.getRankManager().removePermission(rank, permission);
                msg(sender, "&aRemoved &f" + permission + " &afrom &f" + rank + "&a.");
            }
            refreshRank(rank);
            return true;
        }

        msg(sender, "&cUnknown subcommand. Use /rank help");
        return true;
    }

    private boolean setRank(CommandSender sender, String playerName, String rank) {
        if (!plugin.getRankManager().exists(rank)) { msg(sender, "&cThat rank does not exist."); return true; }
        org.bukkit.OfflinePlayer target = plugin.getServer().getOfflinePlayer(playerName);
        String current = target.isOnline() ? plugin.getPlayerRankManager().getRank(target.getUniqueId()) : "";
        String protectedRank = plugin.getConfig().getString("settings.protected-rank", "Owner");
        if (current.equalsIgnoreCase(protectedRank) && !sender.hasPermission("ranksystem.bypass") && !rank.equalsIgnoreCase(protectedRank)) {
            msg(sender, "&cYou cannot remove the protected Owner rank.");
            return true;
        }
        plugin.getPlayerRankManager().setRank(target.getUniqueId(), rank);
        if (target.isOnline()) {
            plugin.getPermissionManager().apply(target.getPlayer());
            plugin.getTabManager().update(target.getPlayer());
        }
        msg(sender, "&aSet &f" + target.getName() + "&a's rank to &f" + rank + "&a.");
        return true;
    }

    private void refreshRank(String rank) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPlayerRankManager().getRank(p.getUniqueId()).equalsIgnoreCase(rank)) {
                plugin.getPermissionManager().apply(p);
                plugin.getTabManager().update(p);
            }
        }
    }

    private void refreshAll() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getPermissionManager().apply(p);
            plugin.getTabManager().update(p);
        }
    }

    private String join(String[] a, int start) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < a.length; i++) {
            if (i > start) s.append(" ");
            s.append(a[i]);
        }
        return s.toString();
    }

    private String capitalize(String s) { return s.substring(0, 1).toUpperCase() + s.substring(1); }

    private void help(CommandSender sender) {
        msg(sender, "&8&m----------------------------------------");
        msg(sender, "&6&lRankSystem &7v1.0.1 &f- Help");
        msg(sender, "&e/rank create <rank> &7- Create a rank");
        msg(sender, "&e/rank delete <rank> &7- Delete a rank");
        msg(sender, "&e/rank set <player> <rank> &7- Set rank");
        msg(sender, "&e/rank remove <player> &7- Reset rank");
        msg(sender, "&e/rank list &7- List ranks");
        msg(sender, "&e/rank info <rank> &7- Rank information");
        msg(sender, "&e/rank prefix <rank> <prefix>");
        msg(sender, "&e/rank suffix <rank> <suffix>");
        msg(sender, "&e/rank priority <rank> <number>");
        msg(sender, "&e/rank inherit <rank> <parent>");
        msg(sender, "&e/rank uninherit <rank> <parent>");
        msg(sender, "&e/rank permission <rank> add <permission>");
        msg(sender, "&e/rank permission <rank> remove <permission>");
        msg(sender, "&e/rank permission <rank> list");
        msg(sender, "&e/rank reload &7- Reload configuration");
        msg(sender, "&8&m----------------------------------------");
    }
}
