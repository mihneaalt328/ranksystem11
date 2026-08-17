package me.yourname.ranksystem.commands;

import me.yourname.ranksystem.RankSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RankTabCompleter implements TabCompleter {
    private final RankSystem plugin;
    public RankTabCompleter(RankSystem plugin) { this.plugin = plugin; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<String>();
        if (args.length == 1) {
            out.addAll(Arrays.asList("help","create","delete","set","remove","list","info","prefix","suffix","priority","inherit","uninherit","permission","reload"));
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("delete") || sub.equals("info") || sub.equals("prefix") || sub.equals("suffix")
                    || sub.equals("priority") || sub.equals("inherit") || sub.equals("uninherit")
                    || sub.equals("permission") || sub.equals("permissions")) {
                out.addAll(plugin.getRankManager().getRanks());
            } else if (sub.equals("set") || sub.equals("remove")) {
                for (org.bukkit.entity.Player p : plugin.getServer().getOnlinePlayers()) out.add(p.getName());
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("set")) out.addAll(plugin.getRankManager().getRanks());
            if (sub.equals("permission") || sub.equals("permissions")) out.addAll(Arrays.asList("add","remove","list"));
            if (sub.equals("inherit") || sub.equals("uninherit")) out.addAll(plugin.getRankManager().getRanks());
        }
        return filter(out, args[args.length - 1]);
    }

    private List<String> filter(List<String> list, String start) {
        List<String> result = new ArrayList<String>();
        for (String s : list) if (s.toLowerCase().startsWith(start.toLowerCase())) result.add(s);
        return result;
    }
}
