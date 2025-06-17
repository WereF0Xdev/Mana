package fox.mods.mana.network;

import fox.mods.mana.ManaMod;
import fox.mods.mana.util.SpellUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record PreviousSpellMessage(int eventType, int pressedms) implements CustomPacketPayload {
	public static final Type<PreviousSpellMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ManaMod.MODID, "key_previous_spell"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PreviousSpellMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PreviousSpellMessage message) -> {
		buffer.writeInt(message.eventType);
		buffer.writeInt(message.pressedms);
	}, (RegistryFriendlyByteBuf buffer) -> new PreviousSpellMessage(buffer.readInt(), buffer.readInt()));

	@Override
	public Type<PreviousSpellMessage> type() {
		return TYPE;
	}

	public static void handleData(final PreviousSpellMessage message, final IPayloadContext context) {
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

			SpellUtils.selectPreviousSpell(entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ManaMod.addNetworkMessage(PreviousSpellMessage.TYPE, PreviousSpellMessage.STREAM_CODEC, PreviousSpellMessage::handleData);
	}
}