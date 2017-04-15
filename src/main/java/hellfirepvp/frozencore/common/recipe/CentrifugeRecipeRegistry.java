package hellfirepvp.frozencore.common.recipe;

import com.google.common.collect.Lists;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CentrifugeRecipeRegistry
 * Created by HellFirePvP
 * Date: 15.04.2017 / 15:14
 */
public class CentrifugeRecipeRegistry {

    private static List<CentrifugeRecipe> recipeList = Lists.newLinkedList();

    public static CentrifugeRecipe registerRecipe(CentrifugeRecipe recipe) {
        recipeList.add(recipe);
        return recipe;
    }

    @Nullable
    public static CentrifugeRecipe findMatchingRecipe(TileCentrifuge centrifuge) {
        for (CentrifugeRecipe recipe : recipeList) {
            if(recipe.matches(centrifuge)) {
                return recipe;
            }
        }
        return null;
    }

}
