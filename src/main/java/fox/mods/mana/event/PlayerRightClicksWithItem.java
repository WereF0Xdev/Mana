package fox.mods.mana.event;

import fox.mods.mana.network.ManaModVariables;
import fox.mods.mana.spell.Spell;
import fox.mods.mana.util.SpellUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlayerRightClicksWithItem {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != event.getEntity().getUsedItemHand())
            return;
        execute(event, event.getEntity());
    }

    public static void execute(Player entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Player entity) {
        if (entity == null)
            return;
        ItemStack itemStack = ItemStack.EMPTY;
        itemStack = entity.getMainHandItem().copy();
        if (itemStack.getItem() == Items.STICK) {
            String spellName = "none";
            spellName = entity.getData(ManaModVariables.PLAYER_VARIABLES).selectedSpell;
            Spell spell = Spell.fromName(spellName);
            SpellUtils.castSpell(spell, entity);
        }
    }
}
