package hellfirepvp.frozencore.common.integration.base;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: JEIBaseCategory
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:54
 */
public abstract class JEIBaseCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    private final String locTitle, uid;

    public JEIBaseCategory(String unlocTitle, String uid) {
        this.locTitle = I18n.format(unlocTitle);
        this.uid = uid;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return locTitle;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    @Deprecated
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper) {}
}
