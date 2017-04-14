package hellfirepvp.frozencore.common.util;

import hellfirepvp.frozencore.common.tile.TileEntitySynchronized;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: IOInventory
 * Created by HellFirePvP
 * Date: 14.04.2017 / 22:52
 */
public class IOInventory implements IItemHandlerModifiable {

    private final TileEntitySynchronized owner;

    private Map<Integer, SlotStackHolder> inventory = new HashMap<>();
    private int[] inSlots = new int[0], outSlots = new int[0], miscSlots = new int[0];

    public List<EnumFacing> accessibleSides = new ArrayList<>();
    private boolean acceptNullCapabilityAccess;

    private IOInventory(TileEntitySynchronized owner) {
        this.owner = owner;
    }

    public IOInventory(TileEntitySynchronized owner, int[] inSlots, int[] outSlots) {
        this(owner, inSlots, outSlots, EnumFacing.VALUES);
    }

    public IOInventory(TileEntitySynchronized owner, int[] inSlots, int[] outSlots, boolean acceptNull) {
        this(owner, inSlots, outSlots, acceptNull, EnumFacing.VALUES);
    }

    public IOInventory(TileEntitySynchronized owner, int[] inSlots, int[] outSlots, EnumFacing... accessibleFrom) {
        this(owner, inSlots, outSlots, false, accessibleFrom);
    }

    public IOInventory(TileEntitySynchronized owner, int[] inSlots, int[] outSlots, boolean acceptNull, EnumFacing... accessibleFrom) {
        this.owner = owner;
        this.inSlots = inSlots;
        this.outSlots = outSlots;
        for (Integer slot : inSlots) {
            this.inventory.put(slot, new SlotStackHolder(slot));
        }
        for (Integer slot : outSlots) {
            this.inventory.put(slot, new SlotStackHolder(slot));
        }
        this.accessibleSides = Arrays.asList(accessibleFrom);
        this.acceptNullCapabilityAccess = acceptNull;
    }

    public IOInventory setMiscSlots(int[] miscSlots) {
        this.miscSlots = miscSlots;
        for (Integer slot : miscSlots) {
            this.inventory.put(slot, new SlotStackHolder(slot));
        }
        return this;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if(this.inventory.containsKey(slot)) {
            this.inventory.get(slot).itemStack = (stack == null ? null : stack.copy());
        }
    }

    @Override
    public int getSlots() {
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.containsKey(slot) ? inventory.get(slot).itemStack : null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(stack == null || stack.getItem() == null) return null;
        if (!arrayContains(inSlots, slot)) return null;
        if (!this.inventory.containsKey(slot)) return null; //Shouldn't happen anymore here tho
        SlotStackHolder holder = this.inventory.get(slot);
        ItemStack toInsert = copyWithClampedStackSize(stack, stack.stackSize);
        if(holder.itemStack == null) {
            ItemStack inserted = copyWithClampedStackSize(toInsert, Math.min(toInsert.stackSize, 64));
            if(!simulate) {
                holder.itemStack = copyWithClampedStackSize(inserted, inserted.stackSize);
            }
            return copyWithClampedStackSize(toInsert, toInsert.stackSize - inserted.stackSize);
        } else {
            if(ItemStack.areItemStacksEqual(holder.itemStack, toInsert) && ItemStack.areItemStackTagsEqual(holder.itemStack, toInsert)) {
                int remaining = 64 - holder.itemStack.stackSize;
                int toInsertAmt = MathHelper.clamp_int(toInsert.stackSize, 0, remaining);
                if(!simulate) {
                    holder.itemStack = copyWithClampedStackSize(holder.itemStack, holder.itemStack.stackSize + toInsertAmt);
                }
                return copyWithClampedStackSize(toInsert, toInsert.stackSize - toInsertAmt);
            }
        }
        return null;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!arrayContains(outSlots, slot)) return null;
        if (!this.inventory.containsKey(slot)) return null; //Shouldn't happen anymore here tho
        SlotStackHolder holder = this.inventory.get(slot);
        if(holder.itemStack == null) return null;
        ItemStack extract = copyWithClampedStackSize(holder.itemStack, amount);
        if(extract == null) return null;
        if(!simulate) {
            holder.itemStack = copyWithClampedStackSize(holder.itemStack, holder.itemStack.stackSize - extract.stackSize);
        }
        return extract;
    }

    @Nullable
    private ItemStack copyWithClampedStackSize(@Nullable ItemStack stack, int amount) {
        if (stack == null || stack.getItem() == null || amount <= 0) return null;
        ItemStack s = stack.copy();
        s.stackSize = MathHelper.clamp_int(amount, 1, stack.stackSize);
        return s;
    }

    private boolean arrayContains(int[] array, int i) {
        for (int id : array) {
            if(id == i) return true;
        }
        return false;
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray("inSlots", this.inSlots);
        tag.setIntArray("outSlots", this.outSlots);
        tag.setIntArray("miscSlots", this.miscSlots);

        NBTTagList inv = new NBTTagList();
        for (Integer slot : this.inventory.keySet()) {
            SlotStackHolder holder = this.inventory.get(slot);
            NBTTagCompound holderTag = new NBTTagCompound();
            holderTag.setBoolean("holderEmpty", holder.itemStack == null);
            holderTag.setInteger("holderId", slot);
            if(holder.itemStack != null) {
                holder.itemStack.writeToNBT(holderTag);
            }
            inv.appendTag(holderTag);
        }
        tag.setTag("inventoryArray", inv);

        tag.setBoolean("allowNull", this.acceptNullCapabilityAccess);
        int[] sides = new int[accessibleSides.size()];
        for (int i = 0; i < accessibleSides.size(); i++) {
            EnumFacing side = accessibleSides.get(i);
            sides[i] = side.ordinal();
        }
        tag.setIntArray("sides", sides);
        return tag;
    }

    public void readNBT(NBTTagCompound tag) {
        this.inSlots = tag.getIntArray("inSlots");
        this.outSlots = tag.getIntArray("outSlots");
        this.miscSlots = tag.getIntArray("miscSlots");

        this.inventory.clear();
        NBTTagList list = tag.getTagList("inventoryArray", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound holderTag = list.getCompoundTagAt(i);
            int slot = holderTag.getInteger("holderId");
            boolean isEmpty = holderTag.getBoolean("holderEmpty");
            ItemStack stack = null;
            if(!isEmpty) {
                stack = ItemStack.loadItemStackFromNBT(holderTag);
            }
            SlotStackHolder holder = new SlotStackHolder(slot);
            holder.itemStack = stack;
            this.inventory.put(slot, holder);
        }

        this.acceptNullCapabilityAccess = tag.getBoolean("allowNull");
        int[] sides = tag.getIntArray("sides");
        for (int i : sides) {
            this.accessibleSides.add(EnumFacing.values()[i]);
        }
    }

    public static IOInventory deserialize(TileEntitySynchronized owner, NBTTagCompound tag) {
        IOInventory inv = new IOInventory(owner);
        inv.readNBT(tag);
        return inv;
    }

    public boolean hasCapability(EnumFacing facing) {
        return (facing == null && acceptNullCapabilityAccess) || accessibleSides.contains(facing);
    }

    public IItemHandlerModifiable getCapability(EnumFacing facing) {
        if(hasCapability(facing)) {
            return this;
        }
        return null;
    }

    private static class SlotStackHolder {

        private final int slotId;
        private ItemStack itemStack;

        private SlotStackHolder(int slotId) {
            this.slotId = slotId;
        }

    }

}
