package hellfirepvp.frozencore.common;

import hellfirepvp.frozencore.common.registry.RegistryBlocks;
import hellfirepvp.frozencore.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:40
 */
public class CommonProxy {

    public void preInit() {
        RegistryItems.setupDefaults();

        RegistryBlocks.init();
        RegistryItems.init();

        RegistryBlocks.initRenderRegistry();
    }

    public void init() {

    }

    public void postInit() {

    }

    //Model registry garbage
    public void registerVariantName(Item item, String name) {}

    public void registerBlockRender(Block block, int metadata, String name) {}

    public void registerItemRender(Item item, int metadata, String name) {}

    public <T extends Item> void registerItemRender(T item, int metadata, String name, boolean variant) {}

    public void registerFromSubItems(Item item, String name) {}

}
