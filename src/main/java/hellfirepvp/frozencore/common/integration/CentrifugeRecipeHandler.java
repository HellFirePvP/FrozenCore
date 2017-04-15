package hellfirepvp.frozencore.common.integration;

import hellfirepvp.frozencore.common.integration.base.JEIBaseHandler;
import hellfirepvp.frozencore.common.recipe.CentrifugeRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CentrifugeRecipeHandler
 * Created by HellFirePvP
 * Date: 15.04.2017 / 21:29
 */
public class CentrifugeRecipeHandler extends JEIBaseHandler<CentrifugeRecipe> {

    @Override
    public Class<CentrifugeRecipe> getRecipeClass() {
        return CentrifugeRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(CentrifugeRecipe recipe) {
        return IntegrationJEI.idCentrifuge;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(CentrifugeRecipe recipe) {
        return new CentrifugeRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(CentrifugeRecipe recipe) {
        return true;

    }
}
