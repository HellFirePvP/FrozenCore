package hellfirepvp.frozencore.common.integration.base;

import com.google.common.collect.Lists;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: JEIBaseWrapper
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:54
 */
public abstract class JEIBaseWrapper implements IRecipeWrapper {

    @Override
    @Deprecated
    public List getInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List getOutputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidInputs() {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public List<FluidStack> getFluidOutputs() {
        return Lists.newArrayList();
    }
}
