package hellfirepvp.frozencore.common.recipe;

import com.google.common.collect.Lists;
import hellfirepvp.frozencore.common.tile.TileCentrifuge;
import hellfirepvp.frozencore.common.util.IOInventory;
import hellfirepvp.frozencore.common.util.ItemUtils;
import hellfirepvp.frozencore.common.util.SimpleSingleFluidCapabilityTank;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the FrozenCore Core/Tweaker-mod
 * The complete source code for this mod can be found on github.
 * Class: CentrifugeRecipe
 * Created by HellFirePvP
 * Date: 15.04.2017 / 15:14
 */
public class CentrifugeRecipe {

    private int tickTime = 100;
    private int rfCost, rfCostPerTick;
    private FluidStack fluidRequired = null;
    private List<ItemStack> inputsRequired = Lists.newArrayList();
    private List<CentrifugeOutput> outputs;

    public CentrifugeRecipe(int rfPerTick, CentrifugeOutput... outputs) {
        this.outputs = Lists.newArrayList(outputs);
        this.rfCostPerTick = rfPerTick;
        this.rfCost = tickTime * rfCostPerTick;
    }

    public CentrifugeRecipe addRequiredInput(ItemStack stack) {
        if(inputsRequired.size() >= 3) {
            throw new IllegalArgumentException("Trying to add more than 3 inputs for mixer-centrifuge recipe.");
        }
        inputsRequired.add(stack);
        return this;
    }

    public CentrifugeRecipe setRequiredFluid(Fluid f, int mbAmount) {
        this.fluidRequired = new FluidStack(f, mbAmount);
        return this;
    }

    public CentrifugeRecipe setCraftingTickTime(int tickTime) {
        this.tickTime = tickTime;
        this.rfCost = tickTime * rfCostPerTick;
        return this;
    }

    public int getCraftingTickTime() {
        return tickTime;
    }

    public int getRfCostPerTick() {
        return rfCostPerTick;
    }

    public FluidStack getFluidRequired() {
        return fluidRequired;
    }

    public List<ItemStack> getInputsRequired() {
        return inputsRequired;
    }

    public boolean matches(TileCentrifuge centrifuge) {
        return matches(centrifuge, TileCentrifuge.getOverclockerMultiplier(centrifuge), getCraftingTickTime());
    }

    public boolean matches(TileCentrifuge centrifuge, float powerMultiplier, int ticksLeft) {
        int rfCost = ticksLeft * MathHelper.ceiling_float_int(rfCostPerTick * powerMultiplier);
        if(centrifuge.getCurrentEnergy() < rfCost) return false;

        IOInventory inventory = centrifuge.getInventory();
        if(fluidRequired != null) {
            SimpleSingleFluidCapabilityTank tank = centrifuge.getTank();

            //Fluid checks first.
            FluidStack stored = tank.getFluid();
            if(stored == null || stored.amount <= 0) return false;
            if(!fluidRequired.getFluid().equals(stored.getFluid())) return false;
            if(fluidRequired.amount > stored.amount) return false;

            if(inputsRequired.isEmpty()) {
                ItemStack in = inventory.getStackInSlot(0);
                if(in != null) return false;
                in = inventory.getStackInSlot(1);
                if(in != null) return false;
                in = inventory.getStackInSlot(2);
                return in == null;
            } else {
                for (ItemStack inpRequired : inputsRequired) {
                    boolean foundInput = false;
                    for (int i = 0; i < 3; i++) {
                        ItemStack in = inventory.getStackInSlot(i);
                        if(ItemUtils.areItemsEqualCrafting(inpRequired, in)) {
                            foundInput = true;
                        }
                    }
                    if(!foundInput) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            if(inputsRequired.isEmpty()) {
                throw new IllegalStateException("Recipe without fluid or item cost!");
            } else {
                for (ItemStack inpRequired : inputsRequired) {
                    boolean foundInput = false;
                    for (int i = 0; i < 3; i++) {
                        ItemStack in = inventory.getStackInSlot(i);
                        if(ItemUtils.areItemsEqualCrafting(inpRequired, in)) {
                            foundInput = true;
                        }
                    }
                    if(!foundInput) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public List<ItemStack> getAllOutputs() {
        return outputs.stream().map((c) -> c.outItem).collect(Collectors.toList());
    }

    public List<ItemStack> getOutputs(Random random) {
        List<ItemStack> out = Lists.newLinkedList();
        for (CentrifugeOutput output : outputs) {
            if(random.nextFloat() <= output.chance) {
                out.add(output.outItem.copy());
            }
        }
        return out;
    }

    public static class CentrifugeOutput {

        public final float chance;
        public final ItemStack outItem;

        public CentrifugeOutput(float chance, ItemStack outItem) {
            this.chance = MathHelper.clamp_float(chance, 0F, 1F);
            this.outItem = outItem.copy();
            this.outItem.stackSize = 1;
        }

    }

}
