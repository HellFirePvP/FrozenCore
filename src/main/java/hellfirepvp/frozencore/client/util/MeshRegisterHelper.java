package hellfirepvp.frozencore.client.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: MeshRegisterHelper
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:56
 */
public class MeshRegisterHelper {

    public static void registerItem(Item item, int metadata, String itemName) {
        getIMM().register(item, metadata, new ModelResourceLocation(itemName, "inventory"));
    }

    public static void registerBlock(Block block, int metadata, String blockName) {
        registerItem(Item.getItemFromBlock(block), metadata, blockName);
    }

    public static ItemModelMesher getIMM() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    }

    public static BlockModelShapes getBMShapes() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    }

}
