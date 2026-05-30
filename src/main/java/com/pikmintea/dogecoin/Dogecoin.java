package com.pikmintea.dogecoin;

import com.pikmintea.dogecoin.command.DogecoinCommand;
import com.pikmintea.dogecoin.config.DogecoinConfig;
import com.pikmintea.dogecoin.item.ModItems;
import com.pikmintea.dogecoin.screen.WalletPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dogecoin implements ModInitializer {
	public static final String MOD_ID = "dogecoin";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Wouf!");
		ModItems.registerModItems();
		DogecoinConfig.load();
		DogecoinCommand.load();
		DogecoinCommand.register();
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> DogecoinCommand.save());
		registerWolfInteraction();
		registerNetworking();
	}

	private void registerWolfInteraction() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (hand != Hand.MAIN_HAND || !(entity instanceof WolfEntity wolf))
				return ActionResult.PASS;
			ItemStack stack = player.getStackInHand(hand);
			if (stack.getItem() != Items.GOLD_INGOT)
				return ActionResult.PASS;
			if (!world.isClient) {
				if (!player.isCreative()) stack.decrement(1);
				ItemStack dogecoin = new ItemStack(ModItems.DOGECOIN);
				if (!player.getInventory().insertStack(dogecoin))
					player.dropItem(dogecoin, false);
				world.playSound(null, wolf.getBlockPos(),
					SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.NEUTRAL, 1.0f, 1.0f);
			}
			return ActionResult.SUCCESS;
		});
	}

	private void registerNetworking() {
		PayloadTypeRegistry.playC2S().register(WalletPayload.Action.ID, WalletPayload.Action.CODEC);
		PayloadTypeRegistry.playS2C().register(WalletPayload.Sync.ID, WalletPayload.Sync.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(WalletPayload.Action.ID, (payload, context) -> {
			var player = context.player();
			context.server().execute(() -> {
				switch (payload.action()) {
					case 0 -> DogecoinCommand.depositAmount(player, payload.amount());
					case 1 -> DogecoinCommand.withdrawAmount(player, payload.amount());
					case 2 -> DogecoinCommand.depositAll(player);
					case 3 -> DogecoinCommand.withdrawAll(player);
				}
				DogecoinCommand.sendSync(player);
			});
		});
	}
}
