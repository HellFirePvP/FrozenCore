package hellfirepvp.frozencore.common.integration;

import hellfirepvp.frozencore.common.recipe.CentrifugeRecipeRegistry;
import hellfirepvp.frozencore.common.registry.RegistryBlocks;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationJEI
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:52
 */
@JEIPlugin
public class IntegrationJEI implements IModPlugin {

    public static IStackHelper stackHelper;
    public static IJeiHelpers jeiHelpers;
    public static IRecipeRegistry recipeRegistry;

    public static final String idCentrifuge = "frozencore.centrifuge";

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {}

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        stackHelper = jeiHelpers.getStackHelper();
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new CategoryCentrifuge(guiHelper));

        registry.addRecipeHandlers(new CentrifugeRecipeHandler());

        registry.addRecipeCategoryCraftingItem(new ItemStack(RegistryBlocks.blockCentrifuge), idCentrifuge);

        registry.addRecipes(CentrifugeRecipeRegistry.getRecipeList());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        recipeRegistry = jeiRuntime.getRecipeRegistry();
    }

}
