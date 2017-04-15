package hellfirepvp.frozencore.common.integration.base;

import mezz.jei.api.recipe.IRecipeHandler;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: JEIBaseHandler
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:54
 */
public abstract class JEIBaseHandler<T> implements IRecipeHandler<T> {

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return null;
    }

}
