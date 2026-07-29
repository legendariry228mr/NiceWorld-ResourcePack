package com.bossplugin.commands;

import com.bossplugin.BossPlugin;
import com.bossplugin.boss.CustomBoss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BossCommand implements CommandExecutor, TabCompleter {
    
    private final BossPlugin plugin;
    
    public BossCommand(BossPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bossplugin.admin")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "spawn":
            case "summon":
                handleSpawn(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "kill":
                handleKill(sender, args);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная команда! Используйте /boss help");
                break;
        }
        
        return true;
    }
    
    private void handleSpawn(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /boss spawn <названиеБосса> [мир] [x] [y] [z]");
            sender.sendMessage(ChatColor.RED + "Или: /boss spawn <названиеБосса> (спавнит рядом с вами)");
            return;
        }
        
        String bossName = args[1];
        
        // Проверка существования босса
        if (plugin.getBossManager().getBossTemplate(bossName) == null) {
            sender.sendMessage(ChatColor.RED + "Босс с названием '" + bossName + "' не найден!");
            sender.sendMessage(ChatColor.YELLOW + "Доступные боссы: " + String.join(", ", plugin.getBossManager().getBossNames()));
            return;
        }
        
        org.bukkit.Location location;
        
        if (args.length >= 5) {
            // Спавн по координатам
            try {
                String worldName = args.length >= 3 ? args[2] : "world";
                org.bukkit.World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "Мир '" + worldName + "' не найден!");
                    return;
                }
                
                double x = Double.parseDouble(args[args.length - 3]);
                double y = Double.parseDouble(args[args.length - 2]);
                double z = Double.parseDouble(args[args.length - 1]);
                location = new org.bukkit.Location(world, x, y, z);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Неверный формат координат!");
                return;
            }
        } else if (sender instanceof Player player) {
            // Спавн рядом с игроком
            location = player.getLocation();
        } else {
            sender.sendMessage(ChatColor.RED + "Вы должны указать координаты для спавна!");
            return;
        }
        
        CustomBoss spawnedBoss = plugin.getBossManager().spawnBoss(bossName, location);
        
        if (spawnedBoss != null) {
            sender.sendMessage(ChatColor.GREEN + "Босс " + spawnedBoss.getName() + ChatColor.GREEN + " успешно призван!");
            
            // Сообщение всем игрокам на сервере
            Bukkit.broadcastMessage(ChatColor.RED + "⚠️ " + ChatColor.BOLD + spawnedBoss.getName() + 
                ChatColor.RESET + ChatColor.RED + " был призван на сервере!");
        } else {
            sender.sendMessage(ChatColor.RED + "Не удалось призвать босса!");
        }
    }
    
    private void handleList(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Доступные Боссы ===");
        
        for (String bossName : plugin.getBossManager().getBossNames()) {
            CustomBoss boss = plugin.getBossManager().getBossTemplate(bossName);
            if (boss != null) {
                sender.sendMessage(ChatColor.YELLOW + "- " + bossName + 
                    ChatColor.WHITE + " (Здоровье: " + getHealthForBoss(bossName) + 
                    ", Урон: " + getDamageForBoss(bossName) + ")");
            }
        }
        
        List<CustomBoss> activeBosses = plugin.getBossManager().getActiveBosses();
        if (!activeBosses.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "\n=== Активные Боссы ===");
            for (CustomBoss boss : activeBosses) {
                if (boss.isActive() && boss.getEntity() != null && !boss.getEntity().isDead()) {
                    sender.sendMessage(ChatColor.RED + "- " + boss.getName() + 
                        ChatColor.WHITE + " HP: " + (int)boss.getEntity().getHealth() + "/" + 
                        (int)getHealthForBoss(boss.getName().replace("§", "").toLowerCase().replaceAll("[^a-z]", "")));
                }
            }
        }
    }
    
    private void handleKill(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /boss kill <названиеБосса>");
            sender.sendMessage(ChatColor.RED + "Или: /boss kill all (убить всех боссов)");
            return;
        }
        
        String target = args[1].toLowerCase();
        
        if (target.equals("all")) {
            int killed = 0;
            for (CustomBoss boss : plugin.getBossManager().getActiveBosses()) {
                if (boss.isActive()) {
                    boss.remove();
                    killed++;
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Убито " + killed + " боссов!");
            return;
        }
        
        boolean found = false;
        for (CustomBoss boss : plugin.getBossManager().getActiveBosses()) {
            if (boss.getName().toLowerCase().contains(target)) {
                boss.remove();
                sender.sendMessage(ChatColor.GREEN + "Босс " + boss.getName() + ChatColor.GREEN + " был убит!");
                found = true;
                break;
            }
        }
        
        if (!found) {
            sender.sendMessage(ChatColor.RED + "Активный босс с названием '" + target + "' не найден!");
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Помощь по Боссам ===");
        sender.sendMessage(ChatColor.YELLOW + "/boss spawn <название> [мир] [x] [y] [z] " + ChatColor.WHITE + "- Призвать босса");
        sender.sendMessage(ChatColor.YELLOW + "/boss list " + ChatColor.WHITE + "- Показать список боссов");
        sender.sendMessage(ChatColor.YELLOW + "/boss kill <название|all> " + ChatColor.WHITE + "- Убить босса");
        sender.sendMessage(ChatColor.YELLOW + "/boss help " + ChatColor.WHITE + "- Показать эту справку");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Команда для быстрого призыва:");
        sender.sendMessage(ChatColor.YELLOW + "/summonboss <название> " + ChatColor.WHITE + "- Призвать босса рядом с собой");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("spawn", "list", "kill", "help").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            return plugin.getBossManager().getBossNames().stream()
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("kill")) {
            List<String> completions = new ArrayList<>(plugin.getBossManager().getBossNames());
            completions.add("all");
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    private double getHealthForBoss(String name) {
        return switch (name.toLowerCase().replaceAll("[^a-z]", "")) {
            case "firelord", "огненныйлорд" -> 200.0;
            case "stormtitan", "грозовойтитан" -> 250.0;
            case "darkknight", "темныйрыцарь" -> 300.0;
            case "icequeen", "ледянаякоролева" -> 180.0;
            case "ancientwarden", "древнийстраж" -> 500.0;
            default -> 100.0;
        };
    }
    
    private double getDamageForBoss(String name) {
        return switch (name.toLowerCase().replaceAll("[^a-z]", "")) {
            case "firelord", "огненныйлорд" -> 12.0;
            case "stormtitan", "грозовойтитан" -> 15.0;
            case "darkknight", "темныйрыцарь" -> 18.0;
            case "icequeen", "ледянаякоролева" -> 10.0;
            case "ancientwarden", "древнийстраж" -> 25.0;
            default -> 5.0;
        };
    }
}
