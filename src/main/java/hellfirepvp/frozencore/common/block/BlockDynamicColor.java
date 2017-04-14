package hellfirepvp.frozencore.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDynamicColor
 * Created by HellFirePvP
 * Date: 14.04.2017 / 17:06
 */
public interface BlockDynamicColor {

    //Return -1 for no color multiplication
    public int getColorMultiplier(IBlockState state, @Nullable IBlockAccess access, @Nullable BlockPos pos, int renderPass);

}
