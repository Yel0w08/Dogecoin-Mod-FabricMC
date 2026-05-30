package com.pikmintea.dogecoin;

import com.pikmintea.dogecoin.screen.WalletScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class DogecoinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WalletPayload.SYNC_ID, (client, handler, buf, sender) -> {
            long balance = buf.readLong();
            int count = buf.readVarInt();
            client.execute(() -> {
                if (client.currentScreen instanceof WalletScreen ws) {
                    ws.updateData(balance, count);
                } else {
                    client.setScreen(new WalletScreen(balance, count));
                }
            });
        });
    }
}
