package fox.mods.mana.event;

import fox.mods.mana.network.ManaModVariables;
import fox.mods.mana.spell.Spell;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlayerTickUpdate {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        execute(event, event.getEntity());
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null)
            return;
        double mana = 0;
        double manaRestorationSpeed = 0;
        mana = entity.getData(ManaModVariables.PLAYER_VARIABLES).mana;
        manaRestorationSpeed = entity.getData(ManaModVariables.PLAYER_VARIABLES).manaRestorationSpeed;
        if (mana < 100) {
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.mana = mana + manaRestorationSpeed;
                _vars.syncPlayerVariables(entity);
            }
        }

        boolean zapSpellInCooldown = false;
        double zapSpellCooldown = 0;
        double zapSpellCooldownRechargeSpeed = 0;
        zapSpellInCooldown = entity.getData(ManaModVariables.PLAYER_VARIABLES).zapSpellInCooldown;
        zapSpellCooldown = entity.getData(ManaModVariables.PLAYER_VARIABLES).zapSpellCooldown;
        zapSpellCooldownRechargeSpeed = entity.getData(ManaModVariables.PLAYER_VARIABLES).zapSpellCooldownRechargeSpeed;

        if (zapSpellInCooldown) {
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.zapSpellCooldown = zapSpellCooldown - zapSpellCooldownRechargeSpeed;
                _vars.syncPlayerVariables(entity);
            }
        }

        if (zapSpellCooldown <= 0) {
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.zapSpellInCooldown = false;
                _vars.syncPlayerVariables(entity);
            }
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.zapSpellCooldown = Spell.ZAP.getCooldown();
                _vars.syncPlayerVariables(entity);
            }
        }


        boolean fireballSpellInCooldown = false;
        double fireballSpellCooldown = 0;
        double fireballSpellCooldownRechargeSpeed = 0;
        fireballSpellInCooldown = entity.getData(ManaModVariables.PLAYER_VARIABLES).fireballSpellInCooldown;
        fireballSpellCooldown = entity.getData(ManaModVariables.PLAYER_VARIABLES).fireballSpellCooldown;
        fireballSpellCooldownRechargeSpeed = entity.getData(ManaModVariables.PLAYER_VARIABLES).fireballSpellCooldownRechargeSpeed;

        if (fireballSpellInCooldown) {
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.fireballSpellCooldown = fireballSpellCooldown - fireballSpellCooldownRechargeSpeed;
                _vars.syncPlayerVariables(entity);
            }
        }

        if (fireballSpellCooldown <= 0) {
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.fireballSpellInCooldown = false;
                _vars.syncPlayerVariables(entity);
            }
            {
                ManaModVariables.PlayerVariables _vars = entity.getData(ManaModVariables.PLAYER_VARIABLES);
                _vars.fireballSpellCooldown = Spell.FIREBALL.getCooldown();
                _vars.syncPlayerVariables(entity);
            }
        }

    }
}
