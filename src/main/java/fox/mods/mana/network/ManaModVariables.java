package fox.mods.mana.network;

import fox.mods.mana.spell.Spell;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;

import fox.mods.mana.ManaMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ManaModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ManaMod.MODID);
	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new PlayerVariables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		ManaMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
			PlayerVariables clone = new PlayerVariables();
			clone.mana = original.mana;
			clone.manaRestorationSpeed = original.manaRestorationSpeed;
			clone.selectedSpell = original.selectedSpell;

			clone.zapSpellCooldown = original.zapSpellCooldown;
			clone.zapSpellInCooldown = original.zapSpellInCooldown;
			clone.zapSpellCooldownRechargeSpeed = original.zapSpellCooldownRechargeSpeed;

			clone.fireballSpellCooldown = original.fireballSpellCooldown;
			clone.fireballSpellInCooldown = original.fireballSpellInCooldown;
			clone.fireballSpellCooldownRechargeSpeed = original.fireballSpellCooldownRechargeSpeed;
			if (!event.isWasDeath()) {
			}
			event.getEntity().setData(PLAYER_VARIABLES, clone);
		}
	}

	public static class PlayerVariables implements INBTSerializable<CompoundTag> {
		public double mana = 100.0;
		public double manaRestorationSpeed = 0.05;
		public String selectedSpell = Spell.NONE.getName();

		public double zapSpellCooldown = Spell.ZAP.getCooldown();
		public boolean zapSpellInCooldown = false;
		public double zapSpellCooldownRechargeSpeed = 0.05;

		public double fireballSpellCooldown = Spell.FIREBALL.getCooldown();
		public boolean fireballSpellInCooldown = false;
		public double fireballSpellCooldownRechargeSpeed = 0.05;

		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.putDouble("mana", mana);
			nbt.putDouble("manaRestorationSpeed", manaRestorationSpeed);
			nbt.putString("selectedSpell", selectedSpell);

			nbt.putDouble("zapSpellCooldown", zapSpellCooldown);
			nbt.putBoolean("zapSpellInCooldown", zapSpellInCooldown);
			nbt.putDouble("zapSpellCooldownRechargeSpeed", zapSpellCooldownRechargeSpeed);

			nbt.putDouble("fireballSpellCooldown", fireballSpellCooldown);
			nbt.putBoolean("fireballSpellInCooldown", fireballSpellInCooldown);
			nbt.putDouble("fireballSpellCooldownRechargeSpeed", fireballSpellCooldownRechargeSpeed);
			return nbt;
		}

		@Override
		public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
			mana = nbt.getDouble("mana");
			manaRestorationSpeed = nbt.getDouble("manaRestorationSpeed");
			selectedSpell = nbt.getString("selectedSpell");

			zapSpellCooldown = nbt.getDouble("zapSpellCooldown");
			zapSpellInCooldown = nbt.getBoolean("zapSpellInCooldown");
			zapSpellCooldownRechargeSpeed = nbt.getDouble("zapSpellCooldownRechargeSpeed");

			fireballSpellCooldown = nbt.getDouble("fireballSpellCooldown");
			fireballSpellInCooldown = nbt.getBoolean("fireballSpellInCooldown");
			fireballSpellCooldownRechargeSpeed = nbt.getDouble("fireballSpellCooldownRechargeSpeed");
		}

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ManaMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
				.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
					PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
					message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
					return message;
				});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> context.player().getData(PLAYER_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}