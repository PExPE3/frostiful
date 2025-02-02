package com.github.thedeathlycow.frostiful.item;

import com.github.thedeathlycow.frostiful.block.FBlocks;
import com.github.thedeathlycow.frostiful.entity.FEntityTypes;
import com.github.thedeathlycow.frostiful.entity.effect.FPotions;
import com.github.thedeathlycow.frostiful.init.Frostiful;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FItems {

    public static final Item FUR_HELMET = new ArmorItem(FrostResistantArmorMaterials.FUR_ARMOR, EquipmentSlot.HEAD,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_CHESTPLATE = new ArmorItem(FrostResistantArmorMaterials.FUR_ARMOR, EquipmentSlot.CHEST,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_LEGGINGS = new ArmorItem(FrostResistantArmorMaterials.FUR_ARMOR, EquipmentSlot.LEGS,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_BOOTS = new ArmorItem(FrostResistantArmorMaterials.FUR_ARMOR, EquipmentSlot.FEET,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item FUR_PADDED_CHAINMAIL_HELMET = new ArmorItem(FrostResistantArmorMaterials.FUR_LINED_CHAIN,
            EquipmentSlot.HEAD, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_PADDED_CHAINMAIL_CHESTPLATE = new ArmorItem(
            FrostResistantArmorMaterials.FUR_LINED_CHAIN, EquipmentSlot.CHEST,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_PADDED_CHAINMAIL_LEGGINGS = new ArmorItem(FrostResistantArmorMaterials.FUR_LINED_CHAIN,
            EquipmentSlot.LEGS, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FUR_PADDED_CHAINMAIL_BOOTS = new ArmorItem(FrostResistantArmorMaterials.FUR_LINED_CHAIN,
            EquipmentSlot.FEET, new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item FROSTOLOGY_CLOAK = new FrostologyCloakItem(new FabricItemSettings()
            .equipmentSlot(FrostologyCloakItem::getPreferredEquipmentSlot)
            .maxCount(1)
            .maxDamage(432)
            .group(FItemGroups.FROSTIFUL)
    );

    public static final Item POLAR_BEAR_FUR_TUFT = new Item(new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item WOLF_FUR_TUFT = new Item(new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item OCELOT_FUR_TUFT = new Item(new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item ICICLE = new IcicleItem(FBlocks.ICICLE, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item COLD_SUN_LICHEN = new BlockItem(FBlocks.COLD_SUN_LICHEN, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item COOL_SUN_LICHEN = new BlockItem(FBlocks.COOL_SUN_LICHEN, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item WARM_SUN_LICHEN = new BlockItem(FBlocks.WARM_SUN_LICHEN, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item HOT_SUN_LICHEN = new BlockItem(FBlocks.HOT_SUN_LICHEN, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item FROST_WAND = new FrostWandItem(new FabricItemSettings().group(FItemGroups.FROSTIFUL).maxCount(1).maxDamage(250));
    public static final Item FROST_TIPPED_ARROW = new FrostTippedArrowItem(new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item FROSTOLOGER_SPAWN_EGG = new SpawnEggItem(FEntityTypes.FROSTOLOGER, 0x473882, 0xBEB2EB, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CHILLAGER_SPAWN_EGG = new SpawnEggItem(FEntityTypes.CHILLAGER, 0x3432A8, 0xA2CCFC, new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item BITER_SPAWN_EGG = new SpawnEggItem(FEntityTypes.BITER, 0xEBFEFF, 0x2E64C3, new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item FROZEN_TORCH = new WallStandingBlockItem(
            FBlocks.FROZEN_TORCH,
            FBlocks.FROZEN_WALL_TORCH,
            new FabricItemSettings().group(FItemGroups.FROSTIFUL)
    );

    public static final Item PACKED_SNOW = new BlockItem(FBlocks.PACKED_SNOW, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOWBALL = new PackedSnowBallItem(new FabricItemSettings().maxCount(16).group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOW_BLOCK = new BlockItem(FBlocks.PACKED_SNOW_BLOCK, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOW_BRICKS = new BlockItem(FBlocks.PACKED_SNOW_BRICKS, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOW_BRICK_STAIRS = new BlockItem(FBlocks.PACKED_SNOW_BRICK_STAIRS, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOW_BRICK_SLAB = new BlockItem(FBlocks.PACKED_SNOW_BRICK_SLAB, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item PACKED_SNOW_BRICK_WALL = new BlockItem(FBlocks.PACKED_SNOW_BRICK_WALL, new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static final Item ICE_PANE = new BlockItem(FBlocks.ICE_PANE, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_PACKED_ICE = new BlockItem(FBlocks.CUT_PACKED_ICE, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_PACKED_ICE_STAIRS = new BlockItem(FBlocks.CUT_PACKED_ICE_STAIRS, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_PACKED_ICE_SLAB = new BlockItem(FBlocks.CUT_PACKED_ICE_SLAB, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_PACKED_ICE_WALL = new BlockItem(FBlocks.CUT_PACKED_ICE_WALL, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_BLUE_ICE = new BlockItem(FBlocks.CUT_BLUE_ICE, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_BLUE_ICE_STAIRS = new BlockItem(FBlocks.CUT_BLUE_ICE_STAIRS, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_BLUE_ICE_SLAB = new BlockItem(FBlocks.CUT_BLUE_ICE_SLAB, new FabricItemSettings().group(FItemGroups.FROSTIFUL));
    public static final Item CUT_BLUE_ICE_WALL = new BlockItem(FBlocks.CUT_BLUE_ICE_WALL, new FabricItemSettings().group(FItemGroups.FROSTIFUL));

    public static void registerItems() {
        register("fur_helmet", FUR_HELMET);
        register("fur_chestplate", FUR_CHESTPLATE);
        register("fur_leggings", FUR_LEGGINGS);
        register("fur_boots", FUR_BOOTS);

        register("fur_padded_chainmail_helmet", FUR_PADDED_CHAINMAIL_HELMET);
        register("fur_padded_chainmail_chestplate", FUR_PADDED_CHAINMAIL_CHESTPLATE);
        register("fur_padded_chainmail_leggings", FUR_PADDED_CHAINMAIL_LEGGINGS);
        register("fur_padded_chainmail_boots", FUR_PADDED_CHAINMAIL_BOOTS);

        register("frostology_cloak", FROSTOLOGY_CLOAK);

        register("polar_bear_fur_tuft", POLAR_BEAR_FUR_TUFT);
        register("wolf_fur_tuft", WOLF_FUR_TUFT);
        register("ocelot_fur_tuft", OCELOT_FUR_TUFT);

        register("icicle", ICICLE);
        register("cold_sun_lichen", COLD_SUN_LICHEN);
        register("cool_sun_lichen", COOL_SUN_LICHEN);
        register("warm_sun_lichen", WARM_SUN_LICHEN);
        register("hot_sun_lichen", HOT_SUN_LICHEN);
        register("frost_wand", FROST_WAND);
        register("frost_tipped_arrow", FROST_TIPPED_ARROW);
        register("frostologer_spawn_egg", FROSTOLOGER_SPAWN_EGG);
        register("chillager_spawn_egg", CHILLAGER_SPAWN_EGG);
        register("biter_spawn_egg", BITER_SPAWN_EGG);

        register("frozen_torch", FROZEN_TORCH);

        register("packed_snow", PACKED_SNOW);
        register("packed_snowball", PACKED_SNOWBALL);
        register("packed_snow_block", PACKED_SNOW_BLOCK);
        register("packed_snow_bricks", PACKED_SNOW_BRICKS);
        register("packed_snow_brick_stairs", PACKED_SNOW_BRICK_STAIRS);
        register("packed_snow_brick_slab", PACKED_SNOW_BRICK_SLAB);
        register("packed_snow_brick_wall", PACKED_SNOW_BRICK_WALL);

        register("ice_pane", ICE_PANE);
        register("cut_packed_ice", CUT_PACKED_ICE);
        register("cut_packed_ice_stairs", CUT_PACKED_ICE_STAIRS);
        register("cut_packed_ice_slab", CUT_PACKED_ICE_SLAB);
        register("cut_packed_ice_wall", CUT_PACKED_ICE_WALL);
        register("cut_blue_ice", CUT_BLUE_ICE);
        register("cut_blue_ice_stairs", CUT_BLUE_ICE_STAIRS);
        register("cut_blue_ice_slab", CUT_BLUE_ICE_SLAB);
        register("cut_blue_ice_wall", CUT_BLUE_ICE_WALL);}

    private static void register(String id, Item item) {
        Registry.register(Registry.ITEM, new Identifier(Frostiful.MODID, id), item);
    }

}
