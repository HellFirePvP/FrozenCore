package hellfirepvp.frozencore.common;

import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.container.ContainerCentrifuge;
import hellfirepvp.frozencore.common.registry.RegistryBlocks;
import hellfirepvp.frozencore.common.registry.RegistryItems;
import hellfirepvp.frozencore.common.registry.RegistryRecipes;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:40
 */
public class CommonProxy implements IGuiHandler {

    public void preInit() {
        RegistryItems.setupDefaults();

        RegistryBlocks.init();
        RegistryItems.init();

        RegistryBlocks.initRenderRegistry();

        RegistryRecipes.init();
    }

    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(FrozenCore.instance, this);
    }

    public void postInit() {

    }

    //Model registry garbage
    public void registerVariantName(Item item, String name) {}

    public void registerBlockRender(Block block, int metadata, String name) {}

    public void registerItemRender(Item item, int metadata, String name) {}

    public <T extends Item> void registerItemRender(T item, int metadata, String name, boolean variant) {}

    public void registerFromSubItems(Item item, String name) {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
                if(te != null && te instanceof TileCentrifuge) {
                    return new ContainerCentrifuge(player.inventory, (TileCentrifuge) te);
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
