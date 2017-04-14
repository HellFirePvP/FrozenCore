package hellfirepvp.frozencore.common.registry;

import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.block.BlockCentrifuge;
import hellfirepvp.frozencore.common.block.BlockDynamicColor;
import hellfirepvp.frozencore.common.block.BlockVariants;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlocks
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:49
 */
public class RegistryBlocks {

    public static BlockCentrifuge blockCentrifuge;

    public static List<Block> defaultItemBlocksToRegister = new LinkedList<>();
    public static List<Block> customNameItemBlocksToRegister = new LinkedList<>();
    public static List<BlockDynamicColor> pendingIBlockColorBlocks = new LinkedList<>();

    public static void init() {
        registerFluids();

        registerBlocks();

        registerTileEntities();
    }

    private static void registerFluids() {

    }

    //Blocks
    private static void registerBlocks() {
        blockCentrifuge = registerBlock(new BlockCentrifuge());
        queueDefaultItemBlock(blockCentrifuge);
    }

    //Called after items are registered.
    //Necessary for blocks that require different models/renders for different metadata values
    public static void initRenderRegistry() {

    }

    //Tiles
    private static void registerTileEntities() {
        registerTile(TileCentrifuge.class);
    }

    private static void queueCustomNameItemBlock(Block block) {
        customNameItemBlocksToRegister.add(block);
    }

    private static void queueDefaultItemBlock(Block block) {
        defaultItemBlocksToRegister.add(block);
    }

    private static <T extends Block> T registerBlock(T block, String name) {
        GameRegistry.register(block.setUnlocalizedName(name).setRegistryName(name));
        if(block instanceof BlockDynamicColor) {
            pendingIBlockColorBlocks.add((BlockDynamicColor) block);
        }
        return block;
    }

    private static <T extends Block> T registerBlock(T block) {
        return registerBlock(block, block.getClass().getSimpleName().toLowerCase());
    }

    private static void registerBlockRender(Block block) {
        if(block instanceof BlockVariants) {
            for (IBlockState state : ((BlockVariants) block).getValidStates()) {
                String unlocName = ((BlockVariants) block).getBlockName(state);
                String name = unlocName + "_" + ((BlockVariants) block).getStateName(state);
                FrozenCore.proxy.registerVariantName(Item.getItemFromBlock(block), name);
                FrozenCore.proxy.registerBlockRender(block, block.getMetaFromState(state), name);
            }
        } else {
            FrozenCore.proxy.registerVariantName(Item.getItemFromBlock(block), block.getUnlocalizedName());
            FrozenCore.proxy.registerBlockRender(block, 0, block.getUnlocalizedName());
        }
    }

    private static void registerTile(Class<? extends TileEntity> tile, String name) {
        GameRegistry.registerTileEntity(tile, name);
    }

    private static void registerTile(Class<? extends TileEntity> tile) {
        registerTile(tile, tile.getSimpleName().toLowerCase());
    }

    public static class FluidCustomModelMapper extends StateMapperBase implements ItemMeshDefinition {

        private final ModelResourceLocation res;

        public FluidCustomModelMapper(Fluid f) {
            this.res = new ModelResourceLocation(FrozenCore.MODID.toLowerCase() + ":blockfluids", f.getName());
        }

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return res;
        }

        @Override
        public ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return res;
        }

    }

}
