package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.persist.nbt.INBTProvider;

import java.util.Objects;

/**
 * A list proxy for the fluid handler fluids of an entity.
 */
public class ValueTypeListProxyEntityFluids extends
    ValueTypeListProxyEntityCapability<IFluidHandler, ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack>
    implements INBTProvider {

    public ValueTypeListProxyEntityFluids(World world, Entity entity, @Nullable ForgeDirection side) {
        super(
            ValueTypeListProxyFactories.ENTITY_CAPABILITY_FLUIDS.getName(),
            ValueTypes.OBJECT_FLUIDSTACK,
            world,
            entity,
            CapabilityFluidHandler.FLUID_HANDLER,
            side);
    }

    public ValueTypeListProxyEntityFluids() {
        this(null, null, null);
    }

    @Override
    public int getLength() {
        return getCapability().map(handler -> handler.getTankProperties().length)
            .orElse(0);
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(
            getCapability().map(handler -> Objects.requireNonNull(handler.getTankProperties()[index].getContents()))
                .orElse(null));
    }
}
