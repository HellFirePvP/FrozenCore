package hellfirepvp.frozencore.common.tile;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import hellfirepvp.frozencore.common.util.IOInventory;
import hellfirepvp.frozencore.common.util.SimpleSingleFluidCapabilityTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: TileCentrifuge
 * Created by HellFirePvP
 * Date: 14.04.2017 / 21:59
 */
public class TileCentrifuge extends TileEntitySynchronized implements IEnergyConnection, IEnergyHandler, ITickable {

    private static int[] IN_SLOTS   = new int[] { 0 };
    private static int[] OUT_SLOTS  = new int[] { 1, 2, 3, 4, 5, 6 };
    private static int[] MISC_SLOTS = new int[] { 7 };

    private SimpleSingleFluidCapabilityTank tank;
    private IOInventory inventory;
    private int currentEnergy = 100;

    public TileCentrifuge() {
        this.tank = buildTank(this);
        this.inventory = buildInventory(this);
    }

    private IOInventory buildInventory(TileCentrifuge tile) {
        return new IOInventory(tile, IN_SLOTS, OUT_SLOTS, false,
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST)
                .setMiscSlots(MISC_SLOTS);
    }

    private SimpleSingleFluidCapabilityTank buildTank(TileCentrifuge tile) {
        return new SimpleSingleFluidCapabilityTank(tile, 4000, false,
                EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);
    }

    @Override
    public void update() {

    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("energy", currentEnergy);
        compound.setTag("tankInfo", tank.writeNBT());
        compound.setTag("inventory", inventory.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

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
    public boolean canConnectEnergy(EnumFacing from) {
        return from != null && from != EnumFacing.UP;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (from == null || from == EnumFacing.UP) ? 0 : currentEnergy;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return 20000;
    }

}
