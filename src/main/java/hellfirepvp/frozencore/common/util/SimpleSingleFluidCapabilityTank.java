package hellfirepvp.frozencore.common.util;

import hellfirepvp.frozencore.common.tile.TileEntitySynchronized;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleSingleFluidCapabilityTank
 * Created by HellFirePvP
 * Date: 14.04.2017 / 22:06
 */
public class SimpleSingleFluidCapabilityTank implements IFluidTank, IFluidTankProperties, IFluidHandler {

    private final TileEntitySynchronized owner;

    private int amount = 0;
    private Fluid fluid = null;
    private int maxCapacity;

    private boolean allowInput = true, allowOutput = true;

    public List<EnumFacing> accessibleSides = new ArrayList<>();
    private boolean acceptNullCapabilityAccess;

    private SimpleSingleFluidCapabilityTank(TileEntitySynchronized owner) {
        this.owner = owner;
    }

    public SimpleSingleFluidCapabilityTank(TileEntitySynchronized owner, int maxCapacity) {
        this(owner, maxCapacity, EnumFacing.VALUES);
    }

    public SimpleSingleFluidCapabilityTank(TileEntitySynchronized owner, int maxCapacity, boolean acceptNull) {
        this(owner, maxCapacity, acceptNull, EnumFacing.VALUES);
    }

    public SimpleSingleFluidCapabilityTank(TileEntitySynchronized owner, int capacity, EnumFacing... accessibleFrom) {
        this(owner, capacity, false, accessibleFrom);
    }

    public SimpleSingleFluidCapabilityTank(TileEntitySynchronized owner, int capacity, boolean acceptNull, EnumFacing... accessibleFrom) {
        this.owner = owner;
        this.maxCapacity = Math.max(0, capacity);
        this.accessibleSides = Arrays.asList(accessibleFrom);
        this.acceptNullCapabilityAccess = acceptNull;
    }

    public void setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
    }

    public void setAllowOutput(boolean allowOutput) {
        this.allowOutput = allowOutput;
    }

    //returns min(toAdd, what can be added at most)
    public int getMaxAddable(int toAdd) {
        return Math.min(toAdd, maxCapacity - toAdd);
    }

    public int getMaxDrainable(int toDrain) {
        return Math.min(toDrain, amount);
    }

    public TileEntitySynchronized getOwner() {
        return owner;
    }

    //leftover amount that could not be added
    public int addAmount(int amount) {
        if (this.fluid == null) return amount;
        int addable = getMaxAddable(amount);
        this.amount += addable;
        getOwner().markForUpdate();
        return amount - addable;
    }

    //returns amount drained
    public int drain(int amount) {
        if (this.fluid == null) return 0;
        int drainable = getMaxDrainable(amount);
        this.amount -= drainable;
        if (this.amount <= 0) {
            setFluid(null);
        }
        getOwner().markForUpdate();
        return drainable;
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        if (fluid == null) return null;
        return new FluidStack(fluid, amount);
    }

    @Nullable
    public Fluid getTankFluid() {
        return fluid;
    }

    public void setFluid(@Nullable Fluid fluid) {
        if (fluid != this.fluid) {
            this.amount = 0;
        }
        this.fluid = fluid;
        getOwner().markForUpdate();
    }

    @Override
    public int getFluidAmount() {
        return amount;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return getFluid();
    }

    @Override
    public int getCapacity() {
        return this.maxCapacity;
    }

    @Override
    public boolean canFill() {
        return this.allowInput && this.amount < this.maxCapacity;
    }

    @Override
    public boolean canDrain() {
        return this.allowOutput && this.amount > 0 && this.fluid != null;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill() && (this.fluid == null || fluidStack.getFluid().equals(this.fluid));
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain() && (this.fluid != null && fluidStack.getFluid().equals(this.fluid));
    }

    public float getPercentageFilled() {
        return (((float) amount) / ((float) maxCapacity));
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { this };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) return 0;
        int maxAdded = resource.amount;
        int addable = getMaxAddable(maxAdded);
        if(doFill) {
            addable = maxAdded - addAmount(addable);
        }
        return addable;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(resource)) return null;
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrain()) return null;
        int maxDrainable = getMaxDrainable(maxDrain);
        if (doDrain) {
            maxDrainable = drain(maxDrainable);
        }
        return new FluidStack(this.fluid, maxDrainable);
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("amt", this.amount);
        tag.setInteger("capacity", this.maxCapacity);
        tag.setBoolean("aIn", this.allowInput);
        tag.setBoolean("aOut", this.allowOutput);
        if(this.fluid != null) {
            tag.setString("fluid", this.fluid.getName());
        }
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
        this.amount = tag.getInteger("amt");
        this.maxCapacity = tag.getInteger("capacity");
        this.allowInput = tag.getBoolean("aIn");
        this.allowOutput = tag.getBoolean("aOut");
        if(tag.hasKey("fluid")) {
            this.fluid = FluidRegistry.getFluid(tag.getString("fluid"));
        } else {
            this.fluid = null;
        }
        this.acceptNullCapabilityAccess = tag.getBoolean("allowNull");
        int[] sides = tag.getIntArray("sides");
        for (int i : sides) {
            this.accessibleSides.add(EnumFacing.values()[i]);
        }
    }

    public static SimpleSingleFluidCapabilityTank deserialize(TileEntitySynchronized owner, NBTTagCompound tag) {
        SimpleSingleFluidCapabilityTank tank = new SimpleSingleFluidCapabilityTank(owner);
        tank.readNBT(tag);
        return tank;
    }

    public boolean hasCapability(EnumFacing facing) {
        return (facing == null && acceptNullCapabilityAccess) || accessibleSides.contains(facing);
    }

    public IFluidHandler getCapability(EnumFacing facing) {
        if(hasCapability(facing)) {
            return this;
        }
        return null;
    }

}
