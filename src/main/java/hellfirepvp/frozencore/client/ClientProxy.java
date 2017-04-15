package hellfirepvp.frozencore.client;

import com.google.common.collect.Lists;
import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.client.gui.GuiContainerCentrifuge;
import hellfirepvp.frozencore.client.util.MeshRegisterHelper;
import hellfirepvp.frozencore.common.CommonProxy;
import hellfirepvp.frozencore.common.block.BlockDynamicColor;
import hellfirepvp.frozencore.common.item.ItemDynamicColor;
import hellfirepvp.frozencore.common.registry.RegistryBlocks;
import hellfirepvp.frozencore.common.registry.RegistryItems;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:40
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();

        registerFluidRenderers();
    }

    @Override
    public void init() {
        super.init();

        registerDisplayInformationInit();

        registerTileRenderers();
    }

    private void registerPendingIBlockColorBlocks() {
        BlockColors colors = Minecraft.getMinecraft().getBlockColors();
        for (BlockDynamicColor b : RegistryBlocks.pendingIBlockColorBlocks) {
            colors.registerBlockColorHandler(b::getColorMultiplier, (Block) b);
        }
    }

    private void registerPendingIItemColorItems() {
        ItemColors colors = Minecraft.getMinecraft().getItemColors();
        for (ItemDynamicColor i : RegistryItems.pendingDynamicColorItems) {
            colors.registerItemColorHandler(i::getColorForItemStack, (Item) i);
        }
    }

    private void registerFluidRenderers() {
        //registerFluidRender(someFluidBlock);
    }

    private void registerFluidRender(Fluid f) {
        RegistryBlocks.FluidCustomModelMapper mapper = new RegistryBlocks.FluidCustomModelMapper(f);
        Block block = f.getBlock();
        if(block != null) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                ModelLoader.registerItemVariants(item);
                ModelLoader.setCustomMeshDefinition(item, mapper);
            } else {
                ModelLoader.setCustomStateMapper(block, mapper);
            }
        }
    }

    private void registerTileRenderers() {

    }

    private <T extends TileEntity> void registerTESR(Class<T> tile, TileEntitySpecialRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
                if(te != null && te instanceof TileCentrifuge) {
                    return new GuiContainerCentrifuge((TileCentrifuge) te);
                }
                break;
        }
        return null;
    }

    public void registerDisplayInformationInit() {
        ItemModelMesher imm = MeshRegisterHelper.getIMM();
        for (RenderInfoItem modelEntry : itemRegister) {
            if (modelEntry.variant) {
                registerVariantName(modelEntry.item, modelEntry.name);
            }
            imm.register(modelEntry.item, modelEntry.metadata,
                    new ModelResourceLocation(FrozenCore.MODID + ":" + modelEntry.name, "inventory"));
        }

        registerPendingIBlockColorBlocks();
        registerPendingIItemColorItems();

        for (RenderInfoBlock modelEntry : blockRegister) {
            MeshRegisterHelper.registerBlock(modelEntry.block, modelEntry.metadata, FrozenCore.MODID + ":" + modelEntry.name);
        }
    }

    public void registerVariantName(Item item, String name) {
        ModelBakery.registerItemVariants(item, new ResourceLocation(FrozenCore.MODID, name));
    }

    public void registerBlockRender(Block block, int metadata, String name) {
        blockRegister.add(new RenderInfoBlock(block, metadata, name));
    }

    public void registerItemRender(Item item, int metadata, String name) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, false));
    }

    public void registerItemRender(Item item, int metadata, String name, boolean variant) {
        itemRegister.add(new RenderInfoItem(item, metadata, name, variant));
    }

    public void registerFromSubItems(Item item, String name) {
        List<ItemStack> list = Lists.newArrayList();
        item.getSubItems(item, item.getCreativeTab(), list);
        if (list.size() > 0) {
            for (ItemStack i : list) {
                registerItemRender(item, i.getItemDamage(), name);
            }
        } else {
            registerItemRender(item, 0, name);
        }
    }

    private static List<RenderInfoBlock> blockRegister = new ArrayList<RenderInfoBlock>();
    private static List<RenderInfoItem> itemRegister = new ArrayList<RenderInfoItem>();

    private static class RenderInfoBlock {

        public Block block;
        public int metadata;
        public String name;

        public RenderInfoBlock(Block block, int metadata, String name) {
            this.block = block;
            this.metadata = metadata;
            this.name = name;
        }
    }

    private static class RenderInfoItem {

        public Item item;
        public int metadata;
        public String name;
        public boolean variant;

        public RenderInfoItem(Item item, int metadata, String name, boolean variant) {
            this.item = item;
            this.metadata = metadata;
            this.name = name;
            this.variant = variant;
        }
    }

}
