package hellfirepvp.frozencore.common.integration;

import hellfirepvp.frozencore.FrozenCore;
import hellfirepvp.frozencore.common.integration.base.JEIBaseCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CategoryCentrifuge
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:59
 */
public class CategoryCentrifuge extends JEIBaseCategory<CentrifugeRecipeWrapper> {

    private final IDrawable background;

    public CategoryCentrifuge(IGuiHelper guiHelper) {
        super("jei.category.centrifuge", IntegrationJEI.idCentrifuge);
        ResourceLocation location = new ResourceLocation(FrozenCore.MODID, "textures/gui/jei/centrifuge.png");
        background = guiHelper.createDrawable(location, 0, 0, 169, 76);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {}

    @Override
    public void drawAnimations(Minecraft minecraft) {}

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidGroup = recipeLayout.getFluidStacks();

        group.init(0, true, 40, 11);
        group.init(1, true, 40, 11 + 18);
        group.init(2, true, 40, 11 + 36);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                group.init(j + i * 2 + 3, false, 83 + j * 18, 20 + i * 18);
            }
        }

        fluidGroup.init(0, true, 24, 8, 11, 60, 4000, true, null);

        fluidGroup.set(ingredients);
        group.set(ingredients);

        group.addTooltipCallback(recipeWrapper);
    }


}
