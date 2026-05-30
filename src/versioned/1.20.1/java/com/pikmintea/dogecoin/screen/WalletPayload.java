package com.pikmintea.dogecoin.screen;

import net.minecraft.util.Identifier;

public class WalletPayload {
    public static final Identifier ACTION_ID = new Identifier("dogecoin", "wallet_action");
    public static final Identifier SYNC_ID = new Identifier("dogecoin", "wallet_sync");

    private WalletPayload() {}
}
