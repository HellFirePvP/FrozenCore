package hellfirepvp.frozencore.common.registry;

import hellfirepvp.frozencore.common.recipe.CentrifugeRecipe;
import hellfirepvp.frozencore.common.recipe.CentrifugeRecipeRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 15.04.2017 / 17:24
 */
public class RegistryRecipes {

    public static void init() {

        CentrifugeRecipe recipe = CentrifugeRecipeRegistry.registerRecipe(new CentrifugeRecipe(100,
                new CentrifugeRecipe.CentrifugeOutput(0.2F, new ItemStack(Blocks.ICE)),
                new CentrifugeRecipe.CentrifugeOutput(0.7F, new ItemStack(Blocks.SNOW))));
        recipe.setCraftingTickTime(100);
        recipe.setRequiredFluid(FluidRegistry.WATER, 500);

    }

}
