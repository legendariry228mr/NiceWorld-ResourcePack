package com.bossplugin.listeners;

import com.bossplugin.BossPlugin;
import com.bossplugin.boss.CustomBoss;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class BossListener implements Listener {
    
    private final BossPlugin plugin;
    private final Random random;
    
    public BossListener(BossPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBossDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        // Проверяем, является ли сущность боссом
        if (entity.getCustomName() != null && entity.getCustomName().contains("§l")) {
            String bossName = ChatColor.stripColor(entity.getCustomName());
            
            // Находим соответствующего босса в менеджере
            for (CustomBoss boss : plugin.getBossManager().getActiveBosses()) {
                if (boss.getEntity() != null && boss.getEntity().equals(entity)) {
                    boss.remove();
                    plugin.getBossManager().getActiveBosses().remove(boss);
                    
                    // Награда за убийство босса
                    giveRewards(entity, bossName);
                    
                    // Сообщение о смерти босса
                    org.bukkit.Bukkit.broadcastMessage(
                        ChatColor.GREEN + "⚔️ " + ChatColor.BOLD + bossName + 
                        ChatColor.RESET + ChatColor.GREEN + " был повержен!"
                    );
                    
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamageByBoss(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        
        // Проверяем, является ли атакующий боссом
        if (damager.getCustomName() != null && damager.getCustomName().contains("§l")) {
            // Эффект удара босса
            if (random.nextDouble() < 0.15) { // 15% шанс на особый эффект
                applyBossEffect(player, damager);
            }
        }
    }
    
    @EventHandler
    public void onBossDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity boss)) return;
        
        // Проверяем, является ли цель боссом
        if (boss.getCustomName() != null && boss.getCustomName().contains("§l")) {
            // Шанс на критический удар по боссу
            if (random.nextDouble() < 0.05) { // 5% шанс на крит
                double extraDamage = event.getDamage() * 0.5;
                event.setDamage(event.getDamage() + extraDamage);
                player.sendMessage(ChatColor.YELLOW + "Критический удар по боссу! +" + (int)extraDamage + " урона");
            }
        }
    }
    
    private void giveRewards(LivingEntity entity, String bossName) {
        Location location = entity.getLocation();
        
        // Разные награды для разных боссов
        ItemStack[] drops = switch (bossName.toLowerCase().replaceAll("[^a-z]", "")) {
            case "firelord", "огненныйлорд" -> new ItemStack[]{
                createItemStack(Material.BLAZE_ROD, 3, "Пылающее Сердце"),
                createItemStack(Material.BLAZE_POWDER, 8, "Пепел Лорда"),
                createItemStack(Material.GOLD_INGOT, 5, null)
            };
            case "stormtitan", "грозовойтитан" -> new ItemStack[]{
                createItemStack(Material.TRIDENT, 1, "Осколок Грома"),
                createItemStack(Material.PRISMARINE_CRYSTALS, 10, "Энергия Шторма"),
                createItemStack(Material.DIAMOND, 3, null)
            };
            case "darkknight", "темныйрыцарь" -> new ItemStack[]{
                createItemStack(Material.NETHERITE_SCRAP, 2, "Темная Сталь"),
                createItemStack(Material.OBSIDIAN, 8, "Слеза Тьмы"),
                createItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1, null)
            };
            case "icequeen", "ледянаякоролева" -> new ItemStack[]{
                createItemStack(Material.BLUE_ICE, 16, "Ледяное Сердце"),
                createItemStack(Material.SNOWBALL, 32, "Холодный Ветер"),
                createItemStack(Material.DIAMOND, 2, null)
            };
            case "ancientwarden", "древнийстраж" -> new ItemStack[]{
                createItemStack(Material.ECHO_SHARD, 5, "Древний Осколок"),
                createItemStack(Material.SCULK_CATALYST, 3, "Эссенция Тьмы"),
                createItemStack(Material.NETHERITE_INGOT, 1, null)
            };
            default -> new ItemStack[]{
                createItemStack(Material.GOLD_INGOT, 5, null),
                createItemStack(Material.EXPERIENCE_BOTTLE, 10, null)
            };
        };
        
        // Выпадаем предметы
        for (ItemStack drop : drops) {
            if (drop != null) {
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
        
        // Опыт
        int expAmount = switch (bossName.toLowerCase().replaceAll("[^a-z]", "")) {
            case "firelord", "огненныйлорд" -> 500;
            case "stormtitan", "грозовойтитан" -> 600;
            case "darkknight", "темныйрыцарь" -> 700;
            case "icequeen", "ледянаякоролева" -> 450;
            case "ancientwarden", "древнийстраж" -> 1000;
            default -> 300;
        };
        
        entity.setExpToDrop(expAmount);
    }
    
    private void applyBossEffect(Player player, LivingEntity boss) {
        String bossName = ChatColor.stripColor(boss.getCustomName());
        
        switch (bossName.toLowerCase().replaceAll("[^a-z]", "")) {
            case "firelord", "огненныйлорд" -> {
                player.setFireTicks(100); // 5 секунд горения
                player.sendMessage(ChatColor.RED + "🔥 Вас поджег Огненный Лорд!");
            }
            case "stormtitan", "грозовойтитан" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
                player.sendMessage(ChatColor.BLUE + "⚡ Грозовой Титан ослабил вас!");
            }
            case "darkknight", "темныйрыцарь" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                player.sendMessage(ChatColor.DARK_PURPLE + "🌑 Темный Рыцарь ослепил вас!");
            }
            case "icequeen", "ледянаякоролева" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 3));
                player.setFreezeTicks(140);
                player.sendMessage(ChatColor.AQUA + "❄️ Ледяная Королева заморозила вас!");
            }
            case "ancientwarden", "древнийстраж" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0));
                player.damage(3.0, boss);
                player.sendMessage(ChatColor.DARK_GREEN + "👁️ Древний Страж атаковал вашу душу!");
            }
        }
    }
    
    private ItemStack createItemStack(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, amount);
        if (name != null) {
            // Здесь можно добавить мета-данные для кастомного названия
            // Для простоты оставляем базовый вариант
        }
        return item;
    }
}
