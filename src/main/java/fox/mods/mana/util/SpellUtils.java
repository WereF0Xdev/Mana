package fox.mods.mana.util;

import fox.mods.mana.entity.SuperFireballEntity;
import fox.mods.mana.network.ManaModVariables;
import fox.mods.mana.spell.Spell;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SpellUtils {

    public static double getMana(Player player) {
        double mana = 0;
        mana = player.getData(ManaModVariables.PLAYER_VARIABLES).mana;
        return mana;
    }

    public static void selectSpell(Player player, Spell spell) {
        {
            ManaModVariables.PlayerVariables _vars = player.getData(ManaModVariables.PLAYER_VARIABLES);
            _vars.selectedSpell = spell.getName();
            _vars.syncPlayerVariables(player);
        }

        player.displayClientMessage(Component.literal(ChatFormatting.GREEN.toString() + "You've now selected the spell " + spell.getDisplayColor() + ChatFormatting.BOLD.toString() + spell.getDisplayName()), true);
    }

    public static void castSpell(Spell spell, Player sourcePlayer) {

        if ((sourcePlayer == null))
            return;

        if (spell == null || spell == Spell.NONE) {
            sourcePlayer.displayClientMessage(Component.literal(ChatFormatting.RED.toString() + "No spell selected!"), true);
            return;
        }

        double mana = 0;
        mana = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES).mana;

        if (mana <= 0 || mana < spell.getMana()) {
            sourcePlayer.displayClientMessage(Component.literal(ChatFormatting.RED.toString() + "Not enough " + ChatFormatting.AQUA.toString() + "Mana " + ChatFormatting.GRAY.toString() + "(" + ChatFormatting.AQUA.toString() + (int) mana + ChatFormatting.GRAY.toString() + "/" + spell.getMana() + ")"), true);
            return;
        }

        if (spell == Spell.ZAP) {

            boolean zapSpellInCooldown = false;
            zapSpellInCooldown = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES).zapSpellInCooldown;


            if (zapSpellInCooldown) {
                double zapSpellCooldown = 0;
                zapSpellCooldown = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES).zapSpellCooldown;
                sourcePlayer.displayClientMessage(Component.literal(spell.getDisplayColor() + spell.getDisplayName() + ChatFormatting.RED.toString() + " is charging!" + ChatFormatting.GRAY.toString() + " (" + (int) zapSpellCooldown + "s)"), true);
                return;
            }

            {
                ManaModVariables.PlayerVariables _vars = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.zapSpellInCooldown = true;
                _vars.syncPlayerVariables(sourcePlayer);
            }

            {
                ManaModVariables.PlayerVariables _vars = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.mana = mana - spell.getMana();
                _vars.syncPlayerVariables(sourcePlayer);
            }


            double lookX = 0;
            double lookZ = 0;
            double playerX = sourcePlayer.getX();
            double playerY = sourcePlayer.getY();
            double playerZ = sourcePlayer.getZ();
            LevelAccessor world = sourcePlayer.level();
            double failChance = Mth.nextDouble(RandomSource.create(), 0.1, 1.0);

            if (failChance <= spell.getFailChance()) {
                if (world instanceof ServerLevel _level) {
                    LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level, EntitySpawnReason.TRIGGERED);
                    entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(playerX, playerY, playerZ)));
                    _level.addFreshEntity(entityToSpawn);
                }
                sourcePlayer.displayClientMessage(Component.literal(spell.getDisplayColor() + spell.getDisplayName() + ChatFormatting.RED.toString() + " failed and got casted on you instead!"), true);
                return;
            }

            lookX = sourcePlayer.level().clip(new ClipContext(sourcePlayer.getEyePosition(1f),
                    sourcePlayer.getEyePosition(1f).add(sourcePlayer.getViewVector(1f).scale(50)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, sourcePlayer)).getBlockPos().getX();

            lookZ = sourcePlayer.level().clip(new ClipContext(sourcePlayer.getEyePosition(1f),
                    sourcePlayer.getEyePosition(1f).add(sourcePlayer.getViewVector(1f).scale(50)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, sourcePlayer)).getBlockPos().getZ();

            if (world instanceof ServerLevel _level) {
                LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level, EntitySpawnReason.TRIGGERED);
                entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(lookX, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) lookX, (int) lookZ), lookZ)));;
                _level.addFreshEntity(entityToSpawn);
            }


        } else if (spell == Spell.FIREBALL) {

            boolean fireballSpellInCooldown = false;
            fireballSpellInCooldown = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES).fireballSpellInCooldown;


            if (fireballSpellInCooldown) {
                double fireballSpellCooldown = 0;
                fireballSpellCooldown = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES).fireballSpellCooldown;
                sourcePlayer.displayClientMessage(Component.literal(spell.getDisplayColor() + spell.getDisplayName() + ChatFormatting.RED.toString() + " is charging!" + ChatFormatting.GRAY.toString() + " (" + (int) fireballSpellCooldown + "s)"), true);
                return;
            }

            {
                ManaModVariables.PlayerVariables _vars = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.fireballSpellInCooldown = true;
                _vars.syncPlayerVariables(sourcePlayer);
            }

            {
                ManaModVariables.PlayerVariables _vars = sourcePlayer.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.mana = mana - spell.getMana();
                _vars.syncPlayerVariables(sourcePlayer);
            }


            SuperFireballEntity.shoot(sourcePlayer.level(), sourcePlayer, RandomSource.create());


        }

        sourcePlayer.swing(InteractionHand.MAIN_HAND);
    }


    public static void selectNextSpell(Player player) {
        String selectedSpell = "none";
        selectedSpell = player.getData(ManaModVariables.PLAYER_VARIABLES).selectedSpell;
        Spell spell = Spell.fromName(selectedSpell);

        Spell next = spell.getNext();

        selectSpell(player, next);

    }

    public static void selectPreviousSpell(Player player) {
        String selectedSpell = "none";
        selectedSpell = player.getData(ManaModVariables.PLAYER_VARIABLES).selectedSpell;
        Spell spell = Spell.fromName(selectedSpell);

        Spell next = spell.getPrevious();

        selectSpell(player, next);

    }
}
