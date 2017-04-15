package hellfirepvp.frozencore.common.tile;

import cofh.api.energy.IEnergyReceiver;
import com.google.common.collect.Lists;
import hellfirepvp.frozencore.common.recipe.CentrifugeRecipe;
import hellfirepvp.frozencore.common.recipe.CentrifugeRecipeRegistry;
import hellfirepvp.frozencore.common.util.IOInventory;
import hellfirepvp.frozencore.common.util.ItemUtils;
import hellfirepvp.frozencore.common.util.SimpleSingleFluidCapabilityTank;
import hellfirepvp.frozencore.common.util.UpdateListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: TileCentrifuge
 * Created by HellFirePvP
 * Date: 14.04.2017 / 21:59
 */
public class TileCentrifuge extends TileEntitySynchronized implements IEnergyReceiver, ITickable, UpdateListener {

    private static final int MAX_ENERGY = 20000;
    private static final int[] IN_SLOTS   = new int[] { 0, 1, 2 };
    private static final int[] OUT_SLOTS  = new int[] { 3, 4, 5, 6, 7, 8 };
    private static final int[] MISC_SLOTS = new int[] { 9 };

    private SimpleSingleFluidCapabilityTank tank;
    private IOInventory inventory;
    private int currentEnergy = 0;

    private int activeCraftingTick = -1, maxRecipeTick = -1;
    private CentrifugeRecipe activeRecipe = null;

    public TileCentrifuge() {
        this.tank = buildTank(this);
        this.inventory = buildInventory(this);
    }

    private IOInventory buildInventory(TileCentrifuge tile) {
        return new IOInventory(tile, IN_SLOTS, OUT_SLOTS, false,
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST)
                .setMiscSlots(MISC_SLOTS).setListener(tile);
    }

    private SimpleSingleFluidCapabilityTank buildTank(TileCentrifuge tile) {
        SimpleSingleFluidCapabilityTank tank = new SimpleSingleFluidCapabilityTank(tile, 4000, false,
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);
        tank.setAllowOutput(false);
        return tank;
    }

    public float getCurrentCraftingProcess() {
        if(activeCraftingTick == -1 || maxRecipeTick == -1) return 0F;
        return 1F - ((float) activeCraftingTick) / ((float) maxRecipeTick);
    }

    @Override
    public void update() {
        if(worldObj.isRemote) {
            spawnSmoke();
        } else {
            if(activeRecipe != null) {
                recipeTick();
                ItemStack inOverclockers = getInventory().getStackInSlot(9);
                if(inOverclockers != null) {
                    for (int i = 0; i < 2 * inOverclockers.stackSize; i++) {
                        recipeTick();
                    }
                }
            }
        }
    }

    public static float getOverclockerMultiplier(TileCentrifuge centrifuge) {
        float mul = 1F;
        ItemStack inOverclockers = centrifuge.getInventory().getStackInSlot(9);
        if(inOverclockers != null) {
            for (int i = 0; i < inOverclockers.stackSize; i++) {
                mul *= 1.175F;
            }
        }
        return mul;
    }

    @SideOnly(Side.CLIENT)
    private void spawnSmoke() {
        if(isCrafting()) {
            worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 0.3 + worldObj.rand.nextFloat() * 0.4,
                    pos.getY() + 0.7 + worldObj.rand.nextFloat() * 0.1,
                    pos.getZ() + 0.3 + worldObj.rand.nextFloat() * 0.4,
                    0, worldObj.rand.nextFloat() * 0.01F, 0);
        }
    }

    private void recipeTick() {
        if(activeRecipe != null) {
            float ovMul = getOverclockerMultiplier(this);
            if(activeRecipe.matches(this, ovMul, activeCraftingTick) && hasSpaceInOutputSlots(activeRecipe)) {
                activeCraftingTick--;
                currentEnergy -= activeRecipe.getRfCostPerTick();
                markForUpdate();
                if(activeCraftingTick <= 0) {
                    finishCrafting();
                }
            } else {
                this.activeRecipe = null;
                this.activeCraftingTick = -1;
                this.maxRecipeTick = -1;
                markForUpdate();
            }
        }
    }

    private void finishCrafting() {
        if(activeRecipe != null) {
            this.activeCraftingTick = -1;
            this.maxRecipeTick = -1;

            List<Runnable> callComplete = Lists.newLinkedList();
            boolean grantOutput = true;
            if(activeRecipe.getFluidRequired() != null) {
                SimpleSingleFluidCapabilityTank tank = getTank();
                FluidStack req = activeRecipe.getFluidRequired();
                FluidStack stored = tank.getFluid();
                if(stored != null && req.getFluid().equals(stored.getFluid()) && stored.amount >= req.amount) {
                    callComplete.add(() -> tank.drain(req.amount));
                } else {
                    grantOutput = false;
                }
            }

            if(!activeRecipe.getInputsRequired().isEmpty()) {
                for (ItemStack req : activeRecipe.getInputsRequired()) {
                    boolean foundInput = false;
                    int foundIndex = -1;
                    for (int i = 0; i < 3; i++) {
                        ItemStack in = inventory.getStackInSlot(i);
                        if(ItemUtils.areItemsEqualCrafting(req, in)) {
                            foundInput = true;
                            foundIndex = i;
                        }
                    }
                    if(!foundInput) {
                        grantOutput = false;
                    } else {
                        int finalFoundIndex = foundIndex;
                        callComplete.add(() -> {
                            ItemStack inSlot = getInventory().getStackInSlot(finalFoundIndex);
                            if(inSlot != null) {
                                inSlot.stackSize--;
                                if(inSlot.stackSize <= 0) {
                                    getInventory().setStackInSlot(finalFoundIndex, null);
                                }
                            }
                        }); //Add itemstack reduction.
                    }
                }
            }

            if(grantOutput) {
                List<ItemStack> outputs = activeRecipe.getOutputs(worldObj.rand);

                lblOutputs: for (ItemStack stack : outputs) {
                    for (int i = 3; i < 9; i++) {
                        ItemStack inSlot = inventory.getStackInSlot(i);
                        if(inSlot == null) {
                            inventory.setStackInSlot(i, stack.copy());
                            continue lblOutputs;
                        } else {
                            if(ItemUtils.canMergeItemStacks(inSlot, stack)) {
                                inSlot.stackSize += stack.stackSize;
                                continue lblOutputs;
                            }
                        }
                    }
                }

                for (Runnable r : callComplete) {
                    r.run();
                }
            }
            this.activeRecipe = null;
            onChange(); //Trigger new recipe calc.
            markForUpdate();
        }
    }

    private boolean hasSpaceInOutputSlots(CentrifugeRecipe activeRecipe) {
        List<ItemStack> outputs = activeRecipe.getAllOutputs();

        boolean hasSpace = true;
        for (ItemStack out : outputs) {
            boolean hasSpaceSomewhere = false;
            for (int i = 3; i < 9; i++) {
                ItemStack inSlot = inventory.getStackInSlot(i);
                if(inSlot == null) {
                    hasSpaceSomewhere = true;
                } else {
                    if(ItemUtils.canMergeItemStacks(inSlot, out) && (inSlot.stackSize + out.stackSize) <= 64) {
                        hasSpaceSomewhere = true;
                    }
                }
            }
            if(!hasSpaceSomewhere) {
                hasSpace = false;
            }
        }
        return hasSpace;
    }

    @Override
    public void onChange() {
        if(worldObj.isRemote) return;

        if(activeRecipe != null) {
            float ovMul = getOverclockerMultiplier(this);
            if(!activeRecipe.matches(this, ovMul, activeCraftingTick) || !hasSpaceInOutputSlots(activeRecipe)) {
                this.activeRecipe = null;
                this.activeCraftingTick = -1;
                this.maxRecipeTick = -1;
                markForUpdate();
            }
        } else {
            CentrifugeRecipe recipe = CentrifugeRecipeRegistry.findMatchingRecipe(this);
            if(recipe != null) {
                this.activeRecipe = recipe;
                this.activeCraftingTick = recipe.getCraftingTickTime();
                this.maxRecipeTick = recipe.getCraftingTickTime();
                markForUpdate();
            }
        }
    }

    public boolean isCrafting() {
        return maxRecipeTick >= 0;
    }

    public SimpleSingleFluidCapabilityTank getTank() {
        return tank;
    }

    public IOInventory getInventory() {
        return inventory;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    @Override
    public void onLoad() {
        if(worldObj != null) {
            if(!worldObj.isRemote && activeRecipe == null && (this.maxRecipeTick >= 0)) {
                this.activeCraftingTick = -1;
                this.maxRecipeTick = -1;
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("craftingTick", activeCraftingTick);
        compound.setInteger("maxCraftingTick", maxRecipeTick);
        compound.setInteger("energy", currentEnergy);
        compound.setTag("tankInfo", tank.writeNBT());
        compound.setTag("inventory", inventory.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.activeCraftingTick = compound.getInteger("craftingTick");
        this.maxRecipeTick = compound.getInteger("maxCraftingTick");
        this.currentEnergy = compound.getInteger("energy");
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(this, compound.getCompoundTag("tankInfo"));
        this.inventory = IOInventory.deserialize(this, compound.getCompoundTag("inventory"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.tank.hasCapability(facing);
        }
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.inventory.hasCapability(facing);
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.tank.getCapability(facing);
        }
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory.getCapability(facing);
        }
        return null;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if(from == null || from == EnumFacing.UP) return 0;
        int receiveable = Math.min(MAX_ENERGY - currentEnergy, maxReceive);
        if(!simulate) {
            this.currentEnergy += receiveable;
            markForUpdate();
            onChange();
        }
        return receiveable;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return from != null && from != EnumFacing.UP;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (from == null || from == EnumFacing.UP) ? 0 : currentEnergy;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return MAX_ENERGY;
    }

}
