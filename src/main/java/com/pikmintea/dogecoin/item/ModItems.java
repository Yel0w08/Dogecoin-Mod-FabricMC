package com.pikmintea.dogecoin.item;

import com.pikmintea.dogecoin.Dogecoin;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item DOGECOIN = registerItem("dogecoin", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Dogecoin.MOD_ID, name), item);

    }

    public static void registerModItems() {
        Dogecoin.LOGGER.info("Registering Items for "+ Dogecoin.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(entries -> {


            entries.add(DOGECOIN);


});


    }

}
