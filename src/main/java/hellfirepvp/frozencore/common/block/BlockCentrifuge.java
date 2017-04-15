package hellfirepvp.frozencore.common.block;

import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.registry.RegistryItems;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import hellfirepvp.frozencore.common.util.IOInventory;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCentrifuge
 * Created by HellFirePvP
 * Date: 14.04.2017 / 22:17
 */
public class BlockCentrifuge extends BlockContainer {

    public BlockCentrifuge() {
        super(Material.IRON, MapColor.SILVER);
        setHardness(3F);
        setResistance(6F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabFrozenTweaks);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tc = worldIn.getTileEntity(pos);
        if(tc != null && tc instanceof TileCentrifuge) {
            IOInventory inv = (IOInventory) tc.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if(stack != null) {
                    EntityItem ei = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                    ei.setPickupDelay(0); //Lulz.
                    worldIn.spawnEntityInWorld(ei);
                }
                inv.setStackInSlot(i, null);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            playerIn.openGui(FrozenCore.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCentrifuge();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCentrifuge();
    }

}
