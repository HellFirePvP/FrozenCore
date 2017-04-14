package hellfirepvp.frozencore.common.block;

import net.minecraft.block.state.IBlockState;

import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVariants
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:53
 */
public interface BlockVariants {

    public List<IBlockState> getValidStates();

    public String getStateName(IBlockState state);

    default public String getBlockName(IBlockState state) {
        return state.getBlock().getClass().getSimpleName();
    }

}
