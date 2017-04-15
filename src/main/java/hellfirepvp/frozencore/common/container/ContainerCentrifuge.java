package hellfirepvp.frozencore.common.container;

import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import hellfirepvp.frozencore.common.util.IOInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerCentrifuge
 * Created by HellFirePvP
 * Date: 15.04.2017 / 10:45
 */
public class ContainerCentrifuge extends Container {

    private final TileCentrifuge centrifuge;

    public ContainerCentrifuge(InventoryPlayer playerInventory, TileCentrifuge centrifuge) {
        this.centrifuge = centrifuge;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        IOInventory inv = centrifuge.getInventory();
        this.addSlotToContainer(new SlotHandlerInput(inv, 0, 44, 15));
        this.addSlotToContainer(new SlotHandlerInput(inv, 1, 44, 15 + 18));
        this.addSlotToContainer(new SlotHandlerInput(inv, 2, 44, 15 + 36));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlotToContainer(new SlotHandlerOutput(inv, j + i * 2 + 3, 87 + j * 18, 24 + i * 18));
            }
        }

        this.addSlotToContainer(new SlotHandlerOverclocker(inv, 9, 149, 9));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index >= 0 && index < 36) {
                if (this.mergeItemStack(itemstack1, 36 + 0, 36 + 3, false)) {
                    return itemstack;
                }
            }
            if (index >= 36 + 3 && index < 36 + 8) {
                if (this.mergeItemStack(itemstack1, 0, 36, false)) {
                    return itemstack;
                }
            }
            if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return null;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    private static class SlotHandlerInput extends SlotItemHandler {

        private final int slotIndex;

        public SlotHandlerInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.slotIndex = index;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            ((IOInventory) getItemHandler()).allowAnySlots = true;
            boolean result = super.isItemValid(stack);
            ((IOInventory) getItemHandler()).allowAnySlots = false;
            return result;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return getItemHandler().getStackInSlot(slotIndex) != null;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            ((IOInventory) getItemHandler()).allowAnySlots = true;
            ItemStack result = super.decrStackSize(amount);
            ((IOInventory) getItemHandler()).allowAnySlots = false;
            return result;
        }

        @Override
        public void onSlotChanged() {
            ((IOInventory) getItemHandler()).getOwner().markForUpdate();
        }

    }

    private static class SlotHandlerOverclocker extends SlotItemHandler {

        private final int slotIndex;

        public SlotHandlerOverclocker(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.slotIndex = index;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null || stack.getItem() == null) return false;
            Item i = stack.getItem();
            ResourceLocation reg = i.getRegistryName();
            return reg.getResourceDomain().equals("ic2") && reg.getResourcePath().equals("upgrade") && stack.getItemDamage() == 0;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return getItemHandler().getStackInSlot(slotIndex) != null;
        }

        @Override
        public ItemStack decrStackSize(int amount) {
            ((IOInventory) getItemHandler()).allowAnySlots = true;
            ItemStack result = super.decrStackSize(amount);
            ((IOInventory) getItemHandler()).allowAnySlots = false;
            return result;
        }

        @Override
        public void onSlotChanged() {
            ((IOInventory) getItemHandler()).getOwner().markForUpdate();
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return 16;
        }

        @Override
        public int getSlotStackLimit() {
            return 16;
        }

    }

    private static class SlotHandlerOutput extends SlotItemHandler {

        private final int slotIndex;

        public SlotHandlerOutput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.slotIndex = index;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return getItemHandler().getStackInSlot(slotIndex) != null;
        }

        @Override
        public void onSlotChanged() {
            ((IOInventory) getItemHandler()).getOwner().markForUpdate();
        }

    }

}
