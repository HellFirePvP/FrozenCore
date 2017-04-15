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

    public boolean allowAnySlots = false;
    private final TileEntitySynchronized owner;

    private Map<Integer, SlotStackHolder> inventory = new HashMap<>();
    private int[] inSlots = new int[0], outSlots = new int[0], miscSlots = new int[0];

    private UpdateListener listener = null;
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

    public IOInventory setListener(UpdateListener listener) {
        this.listener = listener;
        return this;
    }

    public TileEntitySynchronized getOwner() {
        return owner;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if(this.inventory.containsKey(slot)) {
            this.inventory.get(slot).itemStack = stack;
            getOwner().markForUpdate();
            if(listener != null) {
                listener.onChange();
            }
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
        if(stack == null || stack.getItem() == null) return stack;
        if (!allowAnySlots) {
            if (!arrayContains(inSlots, slot)) return stack;
        }
        if (!this.inventory.containsKey(slot)) return stack; //Shouldn't happen anymore here tho

        SlotStackHolder holder = this.inventory.get(slot);
        ItemStack toInsert = copyWithSize(stack, stack.stackSize);
        if(holder.itemStack != null) {
            ItemStack existing = copyWithSize(holder.itemStack, holder.itemStack.stackSize);
            int max = Math.min(existing.getMaxStackSize(), 64);
            if (existing.stackSize >= max || !ItemUtils.canMergeItemStacks(existing, toInsert)) {
                return stack;
            }
            int movable = Math.min(max - existing.stackSize, stack.stackSize);
            if (!simulate) {
                holder.itemStack.stackSize += movable;
                getOwner().markForUpdate();
                if(listener != null) {
                    listener.onChange();
                }
            }
            if (movable >= stack.stackSize) {
                return null;
            } else {
                ItemStack copy = stack.copy();
                copy.stackSize -= movable;
                return copy;
            }
        } else {
            int max = Math.min(stack.getMaxStackSize(), 64);
            if (max >= stack.stackSize) {
                if (!simulate) {
                    holder.itemStack = stack.copy();
                    getOwner().markForUpdate();
                    if(listener != null) {
                        listener.onChange();
                    }
                }
                return null;
            } else {
                ItemStack copy = stack.copy();
                copy.stackSize = max;
                if (!simulate) {
                    holder.itemStack = copy;
                    getOwner().markForUpdate();
                    if(listener != null) {
                        listener.onChange();
                    }
                }
                copy = stack.copy();
                copy.stackSize -= max;
                return copy;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!allowAnySlots) {
            if (!arrayContains(outSlots, slot)) return null;
        }
        if (!this.inventory.containsKey(slot)) return null; //Shouldn't happen anymore here tho
        SlotStackHolder holder = this.inventory.get(slot);
        if(holder.itemStack == null) return null;

        ItemStack extract = copyWithSize(holder.itemStack, Math.min(amount, holder.itemStack.stackSize));
        if(extract == null) return null;
        if(!simulate) {
            holder.itemStack = copyWithSize(holder.itemStack, holder.itemStack.stackSize - extract.stackSize);
            if(listener != null) {
                listener.onChange();
            }
        }
        getOwner().markForUpdate();
        return extract;
    }

    @Nullable
    private ItemStack copyWithSize(@Nullable ItemStack stack, int amount) {
        if (stack == null || stack.getItem() == null || amount <= 0) return null;
        ItemStack s = stack.copy();
        s.stackSize = Math.min(amount, 64);
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

        if(listener != null) {
            listener.onChange();
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
