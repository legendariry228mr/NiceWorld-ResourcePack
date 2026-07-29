package com.bossplugin.boss;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

@FunctionalInterface
public interface BossAbility {
    void execute(LivingEntity boss);
    
    static BossAbility createExplosion(String name, int radius, double damage) {
        return (boss) -> {
            Location loc = boss.getLocation();
            boss.getWorld().createExplosion(loc, radius, false, false);
            
            for (Entity entity : boss.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity living && entity != boss) {
                    living.damage(damage, boss);
                    Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2);
                    entity.setVelocity(knockback);
                }
            }
            
            boss.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        };
    }
    
    static BossAbility createLightningStrike(String name) {
        return (boss) -> {
            Location loc = boss.getLocation();
            List<Player> nearbyPlayers = boss.getNearbyEntities(15, 15, 15).stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .toList();
            
            if (!nearbyPlayers.isEmpty()) {
                Player target = nearbyPlayers.get((int) (Math.random() * nearbyPlayers.size()));
                Location targetLoc = target.getLocation();
                boss.getWorld().strikeLightning(targetLoc);
                target.damage(8.0, boss);
                boss.getWorld().playSound(targetLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
            }
        };
    }
    
    static BossAbility createHeal(String name, double amount) {
        return (boss) -> {
            double newHealth = Math.min(boss.getHealth() + amount, boss.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            boss.setHealth(newHealth);
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            boss.getWorld().spawnParticle(Particle.HEART, boss.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        };
    }
    
    static BossAbility createSpeedBoost(String name, int duration, int amplifier) {
        return (boss) -> {
            boss.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SPEED, 
                duration * 20, 
                amplifier
            ));
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
            boss.getWorld().spawnParticle(Particle.SWEEP_ATTACK, boss.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0);
        };
    }
    
    static BossAbility createFireNova(String name, int radius, int duration) {
        return (boss) -> {
            Location loc = boss.getLocation();
            for (double angle = 0; angle < 360; angle += 30) {
                double rad = Math.toRadians(angle);
                Vector direction = new Vector(Math.cos(rad), 0, Math.sin(rad)).multiply(0.5);
                Location particleLoc = loc.clone().add(direction);
                
                for (int i = 0; i < radius; i++) {
                    particleLoc.add(direction.clone().normalize());
                    boss.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                    
                    for (Entity entity : particleLoc.getNearbyEntities(1, 1, 1)) {
                        if (entity instanceof LivingEntity living && entity != boss) {
                            living.setFireTicks(duration * 20);
                            living.damage(2.0, boss);
                        }
                    }
                }
            }
            boss.getWorld().playSound(loc, Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
        };
    }
    
    static BossAbility createTeleportAndStrike(String name) {
        return (boss) -> {
            List<Player> nearbyPlayers = boss.getNearbyEntities(30, 30, 30).stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .toList();
            
            if (!nearbyPlayers.isEmpty()) {
                Player target = nearbyPlayers.get((int) (Math.random() * nearbyPlayers.size()));
                Location targetLoc = target.getLocation().add(0, 1, 0);
                
                // Teleport boss behind player
                Location teleportLoc = targetLoc.clone().subtract(target.getLocation().getDirection().multiply(3));
                boss.teleport(teleportLoc);
                
                // Strike lightning at player
                boss.getWorld().strikeLightning(targetLoc);
                target.damage(10.0, boss);
                
                boss.getWorld().playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                boss.getWorld().playSound(targetLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
            }
        };
    }
}
