package hellfirepvp.frozencore.common.block;

import hellfirepvp.frozencore.common.registry.RegistryItems;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
