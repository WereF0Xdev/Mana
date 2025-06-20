package fox.mods.mana.spell;

import fox.mods.mana.util.RegistryUtils;
import net.minecraft.ChatFormatting;

public enum Spell {

    NONE("None", "none", RegistryUtils.formModRegistryName("none"), 0, ChatFormatting.GRAY.toString(), 0.0, 0.0, 0),
    ZAP("Zap", "zap", RegistryUtils.formModRegistryName("zap"), 0, ChatFormatting.DARK_AQUA.toString(), 1.5, 0.2, 5),
    FIREBALL("Fireball", "fireball", RegistryUtils.formModRegistryName("fireball"), 0, ChatFormatting.GOLD.toString(), 2, 0.0, 7),
    TELEPORT("Teleport", "teleport", RegistryUtils.formModRegistryName("teleport"), 0, ChatFormatting.LIGHT_PURPLE.toString(), 2.5, 0.0, 9);

    private final String displayName;
    private final String name;
    private final String registryName;
    private final Double damage;
    private final String displayColor;
    private final Double cooldown;
    private final Double failChance;
    private final Double mana;

    Spell(String displayName, String name, String registryName, double damage, String displayColor, double cooldown, double failChance, double mana) {
        this.displayName = displayName;
        this.name = name;
        this.registryName = registryName;
        this.damage = damage;
        this.displayColor = displayColor;
        this.cooldown = cooldown;
        this.failChance = failChance;
        this.mana = mana;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getRegistryName() {
        return registryName;
    }

    public Double getDamage() {
        return damage;
    }

    public String getDisplayColor() {
        return displayColor;
    }

    public Double getCooldown() {
        return cooldown;
    }

    public Double getFailChance() {
        return failChance;
    }

    public Double getMana() {
        return mana;
    }

    public static Spell fromName(String name) {
        for (Spell spell : values()) {
            if (spell.name.equalsIgnoreCase(name)) {
                return spell;
            }
        }
        return Spell.NONE;
    }

    public Spell getNext() {
        Spell[] spells = Spell.values();
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal < spells.length) {
            return spells[nextOrdinal];
        }
        return Spell.NONE;
    }

    public Spell getPrevious() {
        Spell[] spells = Spell.values();
        int prevOrdinal = this.ordinal() - 1;
        if (prevOrdinal >= 0) {
            return spells[prevOrdinal];
        }
        return Spell.FIREBALL;
    }
}
