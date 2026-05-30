package com.pikmintea.dogecoin.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.pikmintea.dogecoin.Dogecoin;
import com.pikmintea.dogecoin.item.ModItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DogecoinCommand {
    private static final Path WALLET_PATH = FabricLoader.getInstance().getConfigDir().resolve("dogecoin_wallets.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Long>>() {}.getType();
    private static Map<UUID, Long> wallets = new HashMap<>();

    @FunctionalInterface
    public interface SyncSender {
        void send(ServerPlayerEntity player, long balance, int inventoryCount);
    }
    private static SyncSender syncSender;

    public static void setSyncSender(SyncSender sender) { syncSender = sender; }

    public static void load() {
        if (!Files.exists(WALLET_PATH)) return;
        try (var reader = Files.newBufferedReader(WALLET_PATH)) {
            Map<String, Long> raw = GSON.fromJson(reader, TYPE);
            if (raw == null) return;
            wallets = new HashMap<>();
            for (var entry : raw.entrySet())
                wallets.put(UUID.fromString(entry.getKey()), entry.getValue());
        } catch (IOException | IllegalArgumentException e) {
            Dogecoin.LOGGER.error("Failed to load wallets", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(WALLET_PATH.getParent());
            Map<String, Long> raw = new HashMap<>();
            wallets.forEach((uuid, bal) -> raw.put(uuid.toString(), bal));
            Files.writeString(WALLET_PATH, GSON.toJson(raw));
        } catch (IOException e) {
            Dogecoin.LOGGER.error("Failed to save wallets", e);
        }
    }

    public static long getBalance(ServerPlayerEntity player) {
        return wallets.getOrDefault(player.getUuid(), 0L);
    }

    public static int countInInventory(ServerPlayerEntity player) {
        int count = 0;
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.getItem() == ModItems.DOGECOIN) count += stack.getCount();
        }
        return count;
    }

    public static void depositAmount(ServerPlayerEntity player, int amount) {
        int removed = removeFromInventory(player, amount);
        if (removed == 0) return;
        wallets.merge(player.getUuid(), (long) removed, Long::sum);
    }

    public static void depositAll(ServerPlayerEntity player) {
        int removed = removeFromInventory(player, Integer.MAX_VALUE);
        if (removed == 0) return;
        wallets.merge(player.getUuid(), (long) removed, Long::sum);
    }

    public static void withdrawAmount(ServerPlayerEntity player, int amount) {
        long balance = getBalance(player);
        if (balance < amount || amount == 0) return;
        ItemStack stack = new ItemStack(ModItems.DOGECOIN, amount);
        if (!player.getInventory().insertStack(stack))
            player.dropItem(stack, false);
        wallets.merge(player.getUuid(), (long) -amount, Long::sum);
        if (wallets.get(player.getUuid()) <= 0) wallets.remove(player.getUuid());
    }

    public static void withdrawAll(ServerPlayerEntity player) {
        long balance = getBalance(player);
        if (balance == 0) return;
        int amount = (int) Math.min(balance, Integer.MAX_VALUE);
        ItemStack stack = new ItemStack(ModItems.DOGECOIN, amount);
        if (!player.getInventory().insertStack(stack))
            player.dropItem(stack, false);
        wallets.remove(player.getUuid());
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(literal("wallet")
                .requires(source -> source.isExecutedByPlayer())
                .executes(ctx -> showBalance(ctx))
                .then(literal("open")
                    .executes(ctx -> openGui(ctx)))
                .then(literal("deposit")
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            ServerPlayerEntity p = ctx.getSource().getPlayer();
                            depositAmount(p, IntegerArgumentType.getInteger(ctx, "amount"));
                            sendSync(p);
                            ctx.getSource().sendFeedback(() ->
                                Text.literal("Deposited. Balance: " + getBalance(p)), false);
                            return 1;
                        })))
                .then(literal("withdraw")
                    .then(argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            ServerPlayerEntity p = ctx.getSource().getPlayer();
                            withdrawAmount(p, IntegerArgumentType.getInteger(ctx, "amount"));
                            sendSync(p);
                            ctx.getSource().sendFeedback(() ->
                                Text.literal("Withdrew. Balance: " + getBalance(p)), false);
                            return 1;
                        })))
            )
        );
    }

    public static void sendSync(ServerPlayerEntity player) {
        if (syncSender != null)
            syncSender.send(player, getBalance(player), countInInventory(player));
    }

    private static int showBalance(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        ctx.getSource().sendFeedback(() -> Text.literal(
            "Wallet: " + getBalance(player) + " | Inventory: " + countInInventory(player)), false);
        return 1;
    }

    private static int openGui(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        sendSync(player);
        return 1;
    }

    private static int removeFromInventory(ServerPlayerEntity player, int amount) {
        var inv = player.getInventory();
        int removed = 0;
        for (int i = 0; i < inv.size() && removed < amount; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.getItem() != ModItems.DOGECOIN) continue;
            int toRemove = Math.min(amount - removed, stack.getCount());
            stack.decrement(toRemove);
            removed += toRemove;
        }
        return removed;
    }
}
