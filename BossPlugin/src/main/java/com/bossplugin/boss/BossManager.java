package com.bossplugin.boss;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BossManager {
    
    private final Map<String, CustomBoss> bossTemplates;
    private final List<CustomBoss> activeBosses;
    
    public BossManager(Object plugin) {
        bossTemplates = new HashMap<>();
        activeBosses = new ArrayList<>();
        registerDefaultBosses();
    }
    
    private void registerDefaultBosses() {
        // Boss 1: Огненный Лорд (Fire Lord)
        List<BossAbility> fireLordAbilities = Arrays.asList(
            BossAbility.createExplosion("Огненный взрыв", 4, 6.0),
            BossAbility.createFireNova("Огненная волна", 8, 5),
            BossAbility.createHeal("Воспламенение", 20.0),
            BossAbility.createSpeedBoost("Ярость пламени", 10, 1)
        );
        
        ItemStack[] fireLordEquipment = new ItemStack[6];
        fireLordEquipment[0] = new ItemStack(Material.FLAMING_SWORD);
        fireLordEquipment[2] = new ItemStack(Material.GOLDEN_HELMET);
        fireLordEquipment[3] = new ItemStack(Material.GOLDEN_CHESTPLATE);
        fireLordEquipment[4] = new ItemStack(Material.GOLDEN_LEGGINGS);
        fireLordEquipment[5] = new ItemStack(Material.GOLDEN_BOOTS);
        
        CustomBoss fireLord = new CustomBoss(
            "§c§lОгненный Лорд",
            EntityType.BLAZE,
            200.0,
            12.0,
            0.35,
            8,
            fireLordAbilities,
            fireLordEquipment,
            Arrays.asList(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0))
        );
        bossTemplates.put("firelord", fireLord);
        
        // Boss 2: Грозовой Титан (Storm Titan)
        List<BossAbility> stormTitanAbilities = Arrays.asList(
            BossAbility.createLightningStrike("Удар молнии"),
            BossAbility.createTeleportAndStrike("Телепортация с ударом"),
            BossAbility.createExplosion("Грозовой взрыв", 5, 8.0),
            BossAbility.createSpeedBoost("Скорость шторма", 8, 2)
        );
        
        ItemStack[] stormTitanEquipment = new ItemStack[6];
        stormTitanEquipment[0] = new ItemStack(Material.TRIDENT);
        stormTitanEquipment[2] = new ItemStack(Material.DIAMOND_HELMET);
        stormTitanEquipment[3] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        stormTitanEquipment[4] = new ItemStack(Material.DIAMOND_LEGGINGS);
        stormTitanEquipment[5] = new ItemStack(Material.DIAMOND_BOOTS);
        
        CustomBoss stormTitan = new CustomBoss(
            "§b§lГрозовой Титан",
            EntityType.HUSK,
            250.0,
            15.0,
            0.4,
            10,
            stormTitanAbilities,
            stormTitanEquipment,
            Arrays.asList(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0))
        );
        bossTemplates.put("stormtitan", stormTitan);
        
        // Boss 3: Темный Рыцарь (Dark Knight)
        List<BossAbility> darkKnightAbilities = Arrays.asList(
            BossAbility.createExplosion("Темная волна", 3, 5.0),
            BossAbility.createHeal("Темное восстановление", 25.0),
            BossAbility.createSpeedBoost("Темная ярость", 12, 1),
            BossAbility.createFireNova("Темное пламя", 6, 4)
        );
        
        ItemStack[] darkKnightEquipment = new ItemStack[6];
        darkKnightEquipment[0] = new ItemStack(Material.NETHERITE_SWORD);
        darkKnightEquipment[1] = new ItemStack(Material.SHIELD);
        darkKnightEquipment[2] = new ItemStack(Material.NETHERITE_HELMET);
        darkKnightEquipment[3] = new ItemStack(Material.NETHERITE_CHESTPLATE);
        darkKnightEquipment[4] = new ItemStack(Material.NETHERITE_LEGGINGS);
        darkKnightEquipment[5] = new ItemStack(Material.NETHERITE_BOOTS);
        
        CustomBoss darkKnight = new CustomBoss(
            "§5§lТемный Рыцарь",
            EntityType.ARMOR_STAND,
            300.0,
            18.0,
            0.3,
            12,
            darkKnightAbilities,
            darkKnightEquipment,
            Arrays.asList(
                new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1)
            )
        );
        bossTemplates.put("darkknight", darkKnight);
        
        // Boss 4: Ледяная Королева (Ice Queen)
        List<BossAbility> iceQueenAbilities = Arrays.asList(
            BossAbility.createExplosion("Ледяной взрыв", 4, 5.0),
            BossAbility.createSpeedBoost("Ледяная скорость", 10, 2),
            BossAbility.createHeal("Ледяное восстановление", 15.0),
            BossAbility.createFireNova("Ледяная волна", 7, 6)
        );
        
        ItemStack[] iceQueenEquipment = new ItemStack[6];
        iceQueenEquipment[0] = new ItemStack(Material.DIAMOND_SWORD);
        iceQueenEquipment[2] = new ItemStack(Material.CHAINMAIL_HELMET);
        iceQueenEquipment[3] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        iceQueenEquipment[4] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        iceQueenEquipment[5] = new ItemStack(Material.CHAINMAIL_BOOTS);
        
        CustomBoss iceQueen = new CustomBoss(
            "§b§lЛедяная Королева",
            EntityType.STRAY,
            180.0,
            10.0,
            0.45,
            6,
            iceQueenAbilities,
            iceQueenEquipment,
            Arrays.asList(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0))
        );
        bossTemplates.put("icequeen", iceQueen);
        
        // Boss 5: Древний Страж (Ancient Warden)
        List<BossAbility> ancientWardenAbilities = Arrays.asList(
            BossAbility.createExplosion("Сонический взрыв", 6, 10.0),
            BossAbility.createTeleportAndStrike("Теневая телепортация"),
            BossAbility.createHeal("Древнее восстановление", 30.0),
            BossAbility.createSpeedBoost("Ярость хранителя", 15, 2)
        );
        
        ItemStack[] ancientWardenEquipment = new ItemStack[6];
        ancientWardenEquipment[2] = new ItemStack(Material.NETHERITE_HELMET);
        ancientWardenEquipment[3] = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ancientWardenEquipment[4] = new ItemStack(Material.NETHERITE_LEGGINGS);
        ancientWardenEquipment[5] = new ItemStack(Material.NETHERITE_BOOTS);
        
        CustomBoss ancientWarden = new CustomBoss(
            "§2§lДревний Страж",
            EntityType.WARDEN,
            500.0,
            25.0,
            0.25,
            15,
            ancientWardenAbilities,
            ancientWardenEquipment,
            Arrays.asList(
                new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2),
                new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2)
            )
        );
        bossTemplates.put("ancientwarden", ancientWarden);
    }
    
    public CustomBoss spawnBoss(String bossName, org.bukkit.Location location) {
        CustomBoss template = bossTemplates.get(bossName.toLowerCase());
        if (template == null) {
            return null;
        }
        
        CustomBoss spawnedBoss = new CustomBoss(
            template.getName(),
            getEntityType(bossName),
            getHealth(bossName),
            getDamage(bossName),
            getSpeed(bossName),
            getArmor(bossName),
            template.abilities,
            template.equipment,
            template.effects
        );
        
        LivingEntity entity = spawnedBoss.spawn(location);
        if (entity != null) {
            activeBosses.add(spawnedBoss);
        }
        
        return spawnedBoss;
    }
    
    public CustomBoss getBossTemplate(String name) {
        return bossTemplates.get(name.toLowerCase());
    }
    
    public Set<String> getBossNames() {
        return bossTemplates.keySet();
    }
    
    public List<CustomBoss> getActiveBosses() {
        return new ArrayList<>(activeBosses);
    }
    
    public int getLoadedBossesCount() {
        return bossTemplates.size();
    }
    
    public void cleanup() {
        for (CustomBoss boss : activeBosses) {
            boss.remove();
        }
        activeBosses.clear();
    }
    
    // Helper methods to get boss properties
    private EntityType getEntityType(String name) {
        return switch (name.toLowerCase()) {
            case "firelord" -> EntityType.BLAZE;
            case "stormtitan" -> EntityType.HUSK;
            case "darkknight" -> EntityType.ZOMBIE;
            case "icequeen" -> EntityType.STRAY;
            case "ancientwarden" -> EntityType.WARDEN;
            default -> EntityType.ZOMBIE;
        };
    }
    
    private double getHealth(String name) {
        return switch (name.toLowerCase()) {
            case "firelord" -> 200.0;
            case "stormtitan" -> 250.0;
            case "darkknight" -> 300.0;
            case "icequeen" -> 180.0;
            case "ancientwarden" -> 500.0;
            default -> 100.0;
        };
    }
    
    private double getDamage(String name) {
        return switch (name.toLowerCase()) {
            case "firelord" -> 12.0;
            case "stormtitan" -> 15.0;
            case "darkknight" -> 18.0;
            case "icequeen" -> 10.0;
            case "ancientwarden" -> 25.0;
            default -> 5.0;
        };
    }
    
    private double getSpeed(String name) {
        return switch (name.toLowerCase()) {
            case "firelord" -> 0.35;
            case "stormtitan" -> 0.4;
            case "darkknight" -> 0.3;
            case "icequeen" -> 0.45;
            case "ancientwarden" -> 0.25;
            default -> 0.2;
        };
    }
    
    private int getArmor(String name) {
        return switch (name.toLowerCase()) {
            case "firelord" -> 8;
            case "stormtitan" -> 10;
            case "darkknight" -> 12;
            case "icequeen" -> 6;
            case "ancientwarden" -> 15;
            default -> 0;
        };
    }
}
