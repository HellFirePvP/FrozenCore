package hellfirepvp.frozencore.common.item;

import hellfirepvp.frozencore.common.block.BlockCustomName;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCustomName
 * Created by HellFirePvP
 * Date: 14.04.2017 / 16:55
 */
public class ItemBlockCustomName extends ItemBlock {

    public ItemBlockCustomName(Block block) {
        super(block);
        setHasSubtypes(true); //Normally the case if you're using multi-type blocks.
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Block b = getBlock();
        if (b instanceof BlockCustomName) {
            String identifier = ((BlockCustomName) b).getIdentifierForMeta(stack.getItemDamage());
            return super.getUnlocalizedName(stack) + "." + identifier;
        }
        return super.getUnlocalizedName(stack);
    }

}
