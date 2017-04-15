package hellfirepvp.frozencore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUtils
 * Created by HellFirePvP
 * Date: 15.04.2017 / 09:43
 */
public class ItemUtils {

    public static boolean canMergeItemStacks(ItemStack stack, ItemStack other) {
        if(stack == null || other == null || !stack.isStackable() || !other.isStackable()) {
            return false;
        }
        if(!stack.isItemEqual(other)) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(stack, other);
    }

    public static boolean areItemsEqualCrafting(ItemStack stack, ItemStack other) {
        return OreDictionary.itemMatches(stack, other, false);
    }

}
