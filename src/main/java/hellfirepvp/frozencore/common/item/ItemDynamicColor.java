package hellfirepvp.frozencore.common.item;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ItemDynamicColor
 * Created by HellFirePvP
 * Date: 14.04.2017 / 17:07
 */
public interface ItemDynamicColor {

    public int getColorForItemStack(ItemStack stack, int tintIndex);

}
