package hellfirepvp.frozencore.common.registry;

import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.item.ItemBlockCustomName;
import hellfirepvp.frozencore.common.item.ItemDynamicColor;
import hellfirepvp.frozencore.common.item.ItemVariants;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryItems
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:49
 */
public class RegistryItems {

    public static CreativeTabs creativeTabFrozenTweaks;

    public static List<ItemDynamicColor> pendingDynamicColorItems = new LinkedList<>();

    public static void setupDefaults() {
        creativeTabFrozenTweaks = new CreativeTabs(FrozenCore.MODID) {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(Blocks.ICE);
            }
        };
    }

    public static void init() {
        registerItems();

        registerBlockItems();
    }

    //"Normal" items
    private static void registerItems() {

    }

    //Items associated to blocks/itemblocks
    private static void registerBlockItems() {
        RegistryBlocks.defaultItemBlocksToRegister.forEach(RegistryItems::registerDefaultItemBlock);
        RegistryBlocks.customNameItemBlocksToRegister.forEach(RegistryItems::registerCustomNameItemBlock);
    }

    private static <T extends Block> void registerCustomNameItemBlock(T block) {
        registerItem(new ItemBlockCustomName(block), block.getClass().getSimpleName().toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block) {
        registerDefaultItemBlock(block, block.getClass().getSimpleName().toLowerCase());
    }

    private static <T extends Block> void registerDefaultItemBlock(T block, String name) {
        registerItem(new ItemBlock(block), name);
    }

    private static <T extends Item> T registerItem(T item, String name) {
        item.setUnlocalizedName(name);
        item.setRegistryName(name);
        register(item, name);
        return item;
    }

    private static <T extends Item> T registerItem(T item) {
        String simpleName = item.getClass().getSimpleName().toLowerCase();
        if (item instanceof ItemBlock) {
            simpleName = ((ItemBlock) item).getBlock().getClass().getSimpleName().toLowerCase();
        }
        return registerItem(item, simpleName);
    }

    private static <T extends IForgeRegistryEntry> void register(T item, String name) {
        GameRegistry.register(item);

        if (item instanceof Item) {
            registerItemInformations((Item) item, name);
            if(item instanceof ItemDynamicColor) {
                pendingDynamicColorItems.add((ItemDynamicColor) item);
            }
        }
    }

    private static <T extends Item> void registerItemInformations(T item, String name) {
        if (item instanceof ItemVariants) {
            for (int i = 0; i < ((ItemVariants) item).getVariants().length; i++) {
                int m = i;
                if (((ItemVariants) item).getVariantMetadatas() != null) {
                    m = ((ItemVariants) item).getVariantMetadatas()[i];
                }
                String vName = name + "_" + ((ItemVariants) item).getVariants()[i];
                if (((ItemVariants) item).getVariants()[i].equals("*")) {
                    vName = name;
                }
                FrozenCore.proxy.registerItemRender(item, m, vName, true);
            }
        } else {
            FrozenCore.proxy.registerFromSubItems(item, name);
        }
    }

}
