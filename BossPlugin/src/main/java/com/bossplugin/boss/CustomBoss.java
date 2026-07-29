package com.bossplugin.boss;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CustomBoss {
    
    private final String name;
    private final EntityType entityType;
    private final double health;
    private final double damage;
    private final double speed;
    private final int armor;
    private final List<BossAbility> abilities;
    private final ItemStack[] equipment;
    private final List<PotionEffect> effects;
    
    private LivingEntity entity;
    private BukkitTask abilityTask;
    private boolean isActive;
    
    public CustomBoss(String name, EntityType entityType, double health, double damage, 
                      double speed, int armor, List<BossAbility> abilities, 
                      ItemStack[] equipment, List<PotionEffect> effects) {
        this.name = name;
        this.entityType = entityType;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.armor = armor;
        this.abilities = abilities != null ? abilities : new ArrayList<>();
        this.equipment = equipment != null ? equipment : new ItemStack[5];
        this.effects = effects != null ? effects : new ArrayList<>();
        this.isActive = false;
    }
    
    public LivingEntity spawn(Location location) {
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
        
        entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + name);
        entity.setCustomNameVisible(true);
        entity.setGlowing(true);
        
        // Set health
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(health);
        }
        entity.setHealth(health);
        
        // Set damage
        AttributeInstance attackDamage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(damage);
        }
        
        // Set speed
        AttributeInstance movementSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(speed);
        }
        
        // Set armor
        AttributeInstance armorAttribute = entity.getAttribute(Attribute.GENERIC_ARMOR);
        if (armorAttribute != null) {
            armorAttribute.setBaseValue(armor);
        }
        
        // Set equipment
        if (equipment.length > 0 && equipment[0] != null) {
            entity.getEquipment().setItemInMainHand(equipment[0]);
        }
        if (equipment.length > 1 && equipment[1] != null) {
            entity.getEquipment().setItemInOffHand(equipment[1]);
        }
        if (equipment.length > 2 && equipment[2] != null) {
            entity.getEquipment().setHelmet(equipment[2]);
        }
        if (equipment.length > 3 && equipment[3] != null) {
            entity.getEquipment().setChestplate(equipment[3]);
        }
        if (equipment.length > 4 && equipment[4] != null) {
            entity.getEquipment().setLeggings(equipment[4]);
        }
        if (equipment.length > 5 && equipment[5] != null) {
            entity.getEquipment().setBoots(equipment[5]);
        }
        
        // Add potion effects
        for (PotionEffect effect : effects) {
            entity.addPotionEffect(effect);
        }
        
        isActive = true;
        startAbilityCycle();
        
        return entity;
    }
    
    private void startAbilityCycle() {
        if (abilityTask != null) {
            abilityTask.cancel();
        }
        
        abilityTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (entity == null || entity.isDead() || !isActive) {
                    cancel();
                    return;
                }
                
                useRandomAbility();
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("BossPlugin"), 100L, 80L);
    }
    
    private void useRandomAbility() {
        if (abilities.isEmpty()) return;
        
        Random rand = new Random();
        BossAbility ability = abilities.get(rand.nextInt(abilities.size()));
        ability.execute(entity);
    }
    
    public void remove() {
        if (abilityTask != null) {
            abilityTask.cancel();
        }
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
        isActive = false;
    }
    
    public String getName() {
        return name;
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public double getHealthPercentage() {
        if (entity == null || entity.isDead()) return 0;
        return (entity.getHealth() / health) * 100;
    }
    
    public List<BossAbility> getAbilities() {
        return new ArrayList<>(abilities);
    }
    
    public ItemStack[] getEquipment() {
        return Arrays.copyOf(equipment, equipment.length);
    }
    
    public List<PotionEffect> getEffects() {
        return new ArrayList<>(effects);
    }
}
