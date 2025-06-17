package fox.mods.mana.client.screens;

import fox.mods.mana.network.ManaModVariables;
import fox.mods.mana.spell.Spell;
import fox.mods.mana.util.SpellUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber({Dist.CLIENT})
public class SpellOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getGuiGraphics().guiWidth();
		int h = event.getGuiGraphics().guiHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		ItemStack mainHand = ItemStack.EMPTY;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
			mainHand = entity.getMainHandItem();
		}
		if (mainHand.getItem() == Items.STICK) {
			Spell spell = Spell.fromName(entity.getData(ManaModVariables.PLAYER_VARIABLES).selectedSpell);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font,

					ChatFormatting.WHITE.toString() + "Spell: " + spell.getDisplayColor() + ChatFormatting.BOLD.toString() +  spell.getDisplayName(), 350, h - 24, -1, false);
		}
	}
}