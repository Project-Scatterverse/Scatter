package dev.lirox.scatter.commands;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.states.Meowthpiece;
import dev.lirox.scatter.states.Scatterred;
import dev.lirox.scatter.states.Trapped;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StateCommand implements CommandExecutor, TabCompleter {

    public static final List<String> options = List.of("regular", "tagged", "trapped", "ghost", "meowthpiece");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) return false;

        List<Player> targets = new ArrayList<>();

        switch (args[0].toLowerCase()) {
            case "@p" -> {
                if (sender instanceof Player p) targets.add(p);
            }
            case "@a" -> targets.addAll(sender.getServer().getOnlinePlayers());
            case "@r" -> {
                List<Player> online = new ArrayList<>(sender.getServer().getOnlinePlayers());
                if (!online.isEmpty()) targets.add(online.get((int) (Math.random() * online.size())));
            }
            case "@s" -> {
                if (sender instanceof Player p) targets.add(p);
            }
            default -> {
                Player p = sender.getServer().getPlayerExact(args[0]);
                if (p != null) targets.add(p);
            }
        }

        for (Player target : targets) {
            switch (args[1].toLowerCase()) {
                case "regular" -> {
                    PlayerUtils.release(target, true);
                    PlayerUtils.revive(target);
                    PlayerUtils.demeowthpiecefy(target);
                }
                case "trapped" -> PlayerUtils.trap(target);
                case "tagged" -> PlayerUtils.tag(target);
                case "ghost" -> PlayerUtils.scatter(target, false);
                case "meowthpiece" -> PlayerUtils.meowthpiecefy(target);
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> selectors = Arrays.asList("@p", "@a", "@r", "@s", sender.getName());
            List<String> result = new ArrayList<>();
            String input = args[0].toLowerCase();
            for (String sel : selectors) {
                if (sel.startsWith(input)) result.add(sel);
            }
            return result;
        } else if (args.length == 2) {
            List<String> result = new ArrayList<>();
            String input = args[1].toLowerCase();
            for (String opt : options) {
                if (opt.toLowerCase().startsWith(input)) result.add(opt);
            }
            return result;
        }
        return new ArrayList<>();
    }
}
