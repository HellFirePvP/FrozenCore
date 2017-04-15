package hellfirepvp.frozencore.common.integration;

import com.google.common.collect.Lists;
import hellfirepvp.frozencore.common.integration.base.JEIBaseWrapper;
import hellfirepvp.frozencore.common.recipe.CentrifugeRecipe;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CentrifugeRecipeWrapper
 * Created by HellFirePvP
 * Date: 15.04.2017 / 19:59
 */
public class CentrifugeRecipeWrapper extends JEIBaseWrapper implements ITooltipCallback<ItemStack> {

    private final CentrifugeRecipe recipe;

    public CentrifugeRecipeWrapper(CentrifugeRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, recipe.getInputsRequired());
        ingredients.setOutputs(ItemStack.class, recipe.getAllOutputs());

        ingredients.setInput(FluidStack.class, recipe.getFluidRequired());
    }

    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        if(input) return;

        int index = slotIndex - 3;
        float chance = recipe.getOutputChance(index);
        tooltip.add("Chance: " + (int) (chance * 100) + " %");
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {}

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Lists.newArrayList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

}
