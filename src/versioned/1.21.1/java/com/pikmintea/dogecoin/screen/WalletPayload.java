package com.pikmintea.dogecoin.screen;

import com.pikmintea.dogecoin.Dogecoin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class WalletPayload {
    public record Sync(long balance, int inventoryCount) implements CustomPayload {
        public static final Id<Sync> ID = new Id<>(Identifier.of(Dogecoin.MOD_ID, "wallet_sync"));
        public static final PacketCodec<PacketByteBuf, Sync> CODEC = PacketCodec.of(
            (p, b) -> { b.writeLong(p.balance); b.writeVarInt(p.inventoryCount); },
            b -> new Sync(b.readLong(), b.readVarInt())
        );
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    public record Action(int action, int amount) implements CustomPayload {
        public static final Id<Action> ID = new Id<>(Identifier.of(Dogecoin.MOD_ID, "wallet_action"));
        public static final PacketCodec<PacketByteBuf, Action> CODEC = PacketCodec.of(
            (p, b) -> { b.writeByte(p.action); b.writeVarInt(p.amount); },
            b -> new Action(b.readByte(), b.readVarInt())
        );
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }
}
