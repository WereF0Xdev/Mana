package fox.mods.mana.network;

import fox.mods.mana.util.SpellUtils;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import fox.mods.mana.ManaMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record NextSpellMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<NextSpellMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ManaMod.MODID, "key_next_spell"));
	public static final StreamCodec<RegistryFriendlyByteBuf, NextSpellMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, NextSpellMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new NextSpellMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<NextSpellMessage> type() {
		return TYPE;
	}

	public static void handleData(final NextSpellMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player(), message.eventType, message.pressedms);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {

			SpellUtils.selectNextSpell(entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ManaMod.addNetworkMessage(NextSpellMessage.TYPE, NextSpellMessage.STREAM_CODEC, NextSpellMessage::handleData);
	}
}