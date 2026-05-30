package com.pikmintea.dogecoin;

import com.pikmintea.dogecoin.screen.WalletPayload;
import com.pikmintea.dogecoin.screen.WalletScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DogecoinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WalletPayload.Sync.ID, (payload, context) -> {
            context.client().execute(() -> {
                var client = context.client();
                if (client.currentScreen instanceof WalletScreen ws) {
                    ws.updateData(payload.balance(), payload.inventoryCount());
                } else {
                    client.setScreen(new WalletScreen(payload.balance(), payload.inventoryCount()));
                }
            });
        });
    }
}
