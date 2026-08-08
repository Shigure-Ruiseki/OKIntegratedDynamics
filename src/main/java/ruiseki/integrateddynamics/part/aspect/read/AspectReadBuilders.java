package ruiseki.integrateddynamics.part.aspect.read;

import java.util.List;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.api.capability.temperature.ITemperature;
import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.commoncapabilities.capability.temperature.TemperatureConfig;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositionedInventory;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositionedTankCapacities;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositionedTankFluidStacks;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeString;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.part.aspect.build.AspectBuilder;
import ruiseki.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import ruiseki.integrateddynamics.core.part.aspect.property.AspectProperties;
import ruiseki.integrateddynamics.core.part.aspect.property.AspectPropertyTypeInstance;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * Collection of aspect read builders and value propagators.
 *
 * @author rubensworks
 */
public class AspectReadBuilders {

    // --------------- Value type builders ---------------
    public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Pair<PartTarget, IAspectProperties>> BUILDER_BOOLEAN = AspectBuilder
        .forReadType(ValueTypes.BOOLEAN);
    public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>> BUILDER_INTEGER = AspectBuilder
        .forReadType(ValueTypes.INTEGER);
    public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, Pair<PartTarget, IAspectProperties>> BUILDER_DOUBLE = AspectBuilder
        .forReadType(ValueTypes.DOUBLE);
    public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>> BUILDER_LONG = AspectBuilder
        .forReadType(ValueTypes.LONG);
    public static final AspectBuilder<ValueTypeString.ValueString, ValueTypeString, Pair<PartTarget, IAspectProperties>> BUILDER_STRING = AspectBuilder
        .forReadType(ValueTypes.STRING);
    public static final AspectBuilder<ValueObjectTypeEntity.ValueEntity, ValueObjectTypeEntity, Pair<PartTarget, IAspectProperties>> BUILDER_ENTITY = AspectBuilder
        .forReadType(ValueTypes.OBJECT_ENTITY);
    public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>> BUILDER_LIST = AspectBuilder
        .forReadType(ValueTypes.LIST);

    public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, Pair<PartTarget, IAspectProperties>> BUILDER_OBJECT_ITEMSTACK = AspectBuilder
        .forReadType(ValueTypes.OBJECT_ITEMSTACK);
    public static final AspectBuilder<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock, Pair<PartTarget, IAspectProperties>> BUILDER_OBJECT_BLOCK = AspectBuilder
        .forReadType(ValueTypes.OBJECT_BLOCK);
    public static final AspectBuilder<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack, Pair<PartTarget, IAspectProperties>> BUILDER_OBJECT_FLUIDSTACK = AspectBuilder
        .forReadType(ValueTypes.OBJECT_FLUIDSTACK);

    // --------------- Value type propagators ---------------
    public static final IAspectValuePropagator<Boolean, ValueTypeBoolean.ValueBoolean> PROP_GET_BOOLEAN = new IAspectValuePropagator<Boolean, ValueTypeBoolean.ValueBoolean>() {

        @Override
        public ValueTypeBoolean.ValueBoolean getOutput(Boolean input) {
            return ValueTypeBoolean.ValueBoolean.of(input);
        }
    };
    public static final IAspectValuePropagator<Integer, ValueTypeInteger.ValueInteger> PROP_GET_INTEGER = new IAspectValuePropagator<Integer, ValueTypeInteger.ValueInteger>() {

        @Override
        public ValueTypeInteger.ValueInteger getOutput(Integer input) {
            return ValueTypeInteger.ValueInteger.of(input);
        }
    };
    public static final IAspectValuePropagator<Double, ValueTypeDouble.ValueDouble> PROP_GET_DOUBLE = new IAspectValuePropagator<Double, ValueTypeDouble.ValueDouble>() {

        @Override
        public ValueTypeDouble.ValueDouble getOutput(Double input) {
            return ValueTypeDouble.ValueDouble.of(input);
        }
    };
    public static final IAspectValuePropagator<Long, ValueTypeLong.ValueLong> PROP_GET_LONG = new IAspectValuePropagator<Long, ValueTypeLong.ValueLong>() {

        @Override
        public ValueTypeLong.ValueLong getOutput(Long input) {
            return ValueTypeLong.ValueLong.of(input);
        }
    };
    public static final IAspectValuePropagator<ItemStack, ValueObjectTypeItemStack.ValueItemStack> PROP_GET_ITEMSTACK = new IAspectValuePropagator<ItemStack, ValueObjectTypeItemStack.ValueItemStack>() {

        @Override
        public ValueObjectTypeItemStack.ValueItemStack getOutput(ItemStack input) {
            return ValueObjectTypeItemStack.ValueItemStack.of(input);
        }
    };
    public static final IAspectValuePropagator<String, ValueTypeString.ValueString> PROP_GET_STRING = new IAspectValuePropagator<String, ValueTypeString.ValueString>() {

        @Override
        public ValueTypeString.ValueString getOutput(String input) {
            return ValueTypeString.ValueString.of(input);
        }
    };
    public static final IAspectValuePropagator<BlockState, ValueObjectTypeBlock.ValueBlock> PROP_GET_BLOCK = new IAspectValuePropagator<BlockState, ValueObjectTypeBlock.ValueBlock>() {

        @Override
        public ValueObjectTypeBlock.ValueBlock getOutput(BlockState input) {
            return ValueObjectTypeBlock.ValueBlock.of(input);
        }
    };
    public static final IAspectValuePropagator<FluidStack, ValueObjectTypeFluidStack.ValueFluidStack> PROP_GET_FLUIDSTACK = new IAspectValuePropagator<FluidStack, ValueObjectTypeFluidStack.ValueFluidStack>() {

        @Override
        public ValueObjectTypeFluidStack.ValueFluidStack getOutput(FluidStack input) {
            return ValueObjectTypeFluidStack.ValueFluidStack.of(input);
        }
    };

    // --------------- Generic properties ---------------
    public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROPERTY_LISTINDEX = new AspectPropertyTypeInstance<>(
        ValueTypes.INTEGER,
        "aspect.aspecttypes.integrateddynamics.integer.listindex.name");
    public static final IAspectProperties LIST_PROPERTIES = new AspectProperties(
        ImmutableList.<IAspectPropertyTypeInstance>of(PROPERTY_LISTINDEX));
    static {
        LIST_PROPERTIES.setValue(PROPERTY_LISTINDEX, ValueTypeInteger.ValueInteger.of(0));
    }

    public static final class Audio {

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROPERTY_RANGE = new AspectPropertyTypeInstance<>(
            ValueTypes.INTEGER,
            "aspect.aspecttypes.integrateddynamics.integer.range.name");
        public static final IAspectProperties NOTE_PROPERTIES = new AspectProperties(
            ImmutableList.<IAspectPropertyTypeInstance>of(PROPERTY_RANGE));
        static {
            NOTE_PROPERTIES.setValue(PROPERTY_RANGE, ValueTypeInteger.ValueInteger.of(64));
        }

        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .appendKind("audio");
        // TODO: Add distanceSq
        // public static AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Integer> forInstrument(
        // final NoteBlockEvent.Instrument instrument) {
        // return BUILDER_INTEGER.appendKind("instrument")
        // .handle(new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Integer>() {
        //
        // @Override
        // public Integer getOutput(Pair<PartTarget, IAspectProperties> input) throws EvaluationException {
        // for (NoteBlockEvent.Play event : NoteBlockEventReceiver.getInstance()
        // .getEvents()
        // .get(instrument)) {
        // net.minecraft.world.World world = input.getLeft()
        // .getTarget()
        // .getPos()
        // .getWorld();
        // BlockPos pos = input.getLeft()
        // .getTarget()
        // .getPos()
        // .getBlockPos();
        // int range = input.getRight()
        // .getValue(PROPERTY_RANGE)
        // .getRawValue();
        // if (world.provider.dimensionId == event.world.provider.dimensionId
        // && pos.distanceSq(event.pos) <= range * range) {
        // return event.getVanillaNoteId();
        // }
        // }
        // return -1;
        // }
        // },
        // instrument.name()
        // .toLowerCase(Locale.ENGLISH))
        // .withProperties(NOTE_PROPERTIES);
        // }

    }

    public static final class Block {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos>() {

            @Override
            public DimPos getOutput(Pair<PartTarget, IAspectProperties> input) {
                return input.getLeft()
                    .getTarget()
                    .getPos();
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, DimPos> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET, "block");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, DimPos> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "block");
        public static final AspectBuilder<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock, DimPos> BUILDER_BLOCK = AspectReadBuilders.BUILDER_OBJECT_BLOCK
            .handle(PROP_GET, "block");

    }

    public static final class Entity {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos>() {

            @Override
            public DimPos getOutput(Pair<PartTarget, IAspectProperties> input) {
                return input.getLeft()
                    .getTarget()
                    .getPos();
            }
        };

        public static final AspectBuilder<ValueObjectTypeEntity.ValueEntity, ValueObjectTypeEntity, Pair<PartTarget, IAspectProperties>> BUILDER_ENTITY = AspectReadBuilders.BUILDER_ENTITY
            .appendKind("entity");
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, DimPos> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
            .handle(PROP_GET, "entity");
        public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, Pair<PartTarget, IAspectProperties>> BUILDER_ITEMSTACK = AspectReadBuilders.BUILDER_OBJECT_ITEMSTACK
            .appendKind("entity");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>> BUILDER_INTEGER_ALL = AspectReadBuilders.BUILDER_INTEGER
            .appendKind("entity");

    }

    public static final class ExtraDimensional {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, MinecraftServer> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, MinecraftServer>() {

            @Override
            public MinecraftServer getOutput(Pair<PartTarget, IAspectProperties> input) {
                return MinecraftServer.getServer();
            }
        };

        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, MinecraftServer> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "extradimensional");
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, MinecraftServer> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
            .handle(PROP_GET, "extradimensional");

    }

    public static final class Fluid {

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_TANKID = new AspectPropertyTypeInstance<>(
            ValueTypes.INTEGER,
            "aspect.aspecttypes.integrateddynamics.integer.tankid.name");
        public static final IAspectProperties PROPERTIES = new AspectProperties(
            ImmutableList.<IAspectPropertyTypeInstance>of(PROP_TANKID));
        static {
            PROPERTIES.setValue(PROP_TANKID, ValueTypeInteger.ValueInteger.of(0)); // Not required in this case, but we
                                                                                   // do this here just as an example on
                                                                                   // how to set default values.
        }

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IFluidTankProperties[]> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IFluidTankProperties[]>() {

            @Override
            public IFluidTankProperties[] getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();
                IFluidHandler fluidHandler = CapabilityHelpers
                    .getCapability(
                        dimPos,
                        CapabilityFluidHandler.FLUID_HANDLER,
                        input.getLeft()
                            .getTarget()
                            .getSide())
                    .getOrNull();
                if (fluidHandler != null) {
                    return fluidHandler.getTankProperties();
                }
                return new IFluidTankProperties[0];
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IFluidTankProperties> PROP_GET_ACTIVATABLE = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IFluidTankProperties>() {

            @Override
            public IFluidTankProperties getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();
                IFluidHandler fluidHandler = CapabilityHelpers
                    .getCapability(
                        dimPos,
                        CapabilityFluidHandler.FLUID_HANDLER,
                        input.getLeft()
                            .getTarget()
                            .getSide())
                    .getOrNull();
                if (fluidHandler != null) {
                    IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
                    int i = input.getRight()
                        .getValue(PROP_TANKID)
                        .getRawValue();
                    if (tankInfo != null && i < tankInfo.length) {
                        return tankInfo[i];
                    }
                }
                return null;
            }
        };
        public static final IAspectValuePropagator<IFluidTankProperties, FluidStack> PROP_GET_FLUIDSTACK = new IAspectValuePropagator<IFluidTankProperties, FluidStack>() {

            @Override
            public FluidStack getOutput(IFluidTankProperties tankInfo) {
                return tankInfo != null ? tankInfo.getContents() : null;
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList> PROP_GET_LIST_FLUIDSTACKS = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>() {

            @Override
            public ValueTypeList.ValueList getOutput(Pair<PartTarget, IAspectProperties> input) {
                return ValueTypeList.ValueList.ofFactory(
                    new ValueTypeListProxyPositionedTankFluidStacks(
                        input.getLeft()
                            .getTarget()
                            .getPos(),
                        input.getLeft()
                            .getTarget()
                            .getSide()));
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList> PROP_GET_LIST_CAPACITIES = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>() {

            @Override
            public ValueTypeList.ValueList getOutput(Pair<PartTarget, IAspectProperties> input) {
                return ValueTypeList.ValueList.ofFactory(
                    new ValueTypeListProxyPositionedTankCapacities(
                        input.getLeft()
                            .getTarget()
                            .getPos(),
                        input.getLeft()
                            .getTarget()
                            .getSide()));
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, IFluidTankProperties[]> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET, "fluid");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, IFluidTankProperties[]> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "fluid");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, IFluidTankProperties> BUILDER_INTEGER_ACTIVATABLE = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET_ACTIVATABLE, "fluid")
            .withProperties(PROPERTIES);
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, IFluidTankProperties> BUILDER_DOUBLE_ACTIVATABLE = AspectReadBuilders.BUILDER_DOUBLE
            .handle(PROP_GET_ACTIVATABLE, "fluid")
            .withProperties(PROPERTIES);

    }

    public static final class Inventory {

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROPERTY_SLOTID = new AspectPropertyTypeInstance<>(
            ValueTypes.INTEGER,
            "aspect.aspecttypes.integrateddynamics.integer.slotid.name");
        public static final IAspectProperties PROPERTIES = new AspectProperties(
            ImmutableList.<IAspectPropertyTypeInstance>of(PROPERTY_SLOTID));
        static {
            PROPERTIES.setValue(PROPERTY_SLOTID, ValueTypeInteger.ValueInteger.of(0)); // Not required in this case, but
                                                                                       // we do this here just as an
                                                                                       // example on how to set default
                                                                                       // values.
        }

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IItemHandler> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IItemHandler>() {

            @Override
            public IItemHandler getOutput(Pair<PartTarget, IAspectProperties> input) {
                PartPos target = input.getLeft()
                    .getTarget();
                return CapabilityHelpers.getCapability(
                    target.getPos()
                        .getWorld(),
                    target.getPos()
                        .getBlockPos(),
                    CapabilityItemHandler.ITEM_HANDLER,
                    target.getSide())
                    .getOrNull();
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ItemStack> PROP_GET_SLOT = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ItemStack>() {

            @Override
            public ItemStack getOutput(Pair<PartTarget, IAspectProperties> input) {
                PartPos target = input.getLeft()
                    .getTarget();
                IItemHandler itemHandler = CapabilityHelpers.getCapability(
                    target.getPos()
                        .getWorld(),
                    target.getPos()
                        .getBlockPos(),
                    CapabilityItemHandler.ITEM_HANDLER,
                    target.getSide())
                    .getOrNull();
                int slotId = input.getRight()
                    .getValue(PROPERTY_SLOTID)
                    .getRawValue();
                if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots()) {
                    return itemHandler.getStackInSlot(slotId);
                }
                return null;
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList> PROP_GET_LIST = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>() {

            @Override
            public ValueTypeList.ValueList getOutput(Pair<PartTarget, IAspectProperties> input) {
                return ValueTypeList.ValueList.ofFactory(
                    new ValueTypeListProxyPositionedInventory(
                        input.getLeft()
                            .getTarget()
                            .getPos(),
                        input.getLeft()
                            .getTarget()
                            .getSide()));
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, IItemHandler> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET, "inventory");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, IItemHandler> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "inventory");
        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, IItemHandler> BUILDER_DOUBLE = AspectReadBuilders.BUILDER_DOUBLE
            .handle(PROP_GET, "inventory");
        public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, ItemStack> BUILDER_ITEMSTACK = BUILDER_OBJECT_ITEMSTACK
            .handle(PROP_GET_SLOT, "inventory")
            .withProperties(PROPERTIES);

    }

    public static final class Machine {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IWorker> PROP_GET_WORKER = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IWorker>() {

            @Override
            public IWorker getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();

                return CapabilityHelpers
                    .getCapability(
                        dimPos.getWorld(),
                        dimPos.getBlockPos(),
                        WorkerConfig.CAPABILITY,
                        input.getLeft()
                            .getTarget()
                            .getSide())
                    .getOrNull();
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ITemperature> PROP_GET_TEMPERATURE = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ITemperature>() {

            @Override
            public ITemperature getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();
                return CapabilityHelpers
                    .getCapability(
                        dimPos.getWorld(),
                        dimPos.getBlockPos(),
                        TemperatureConfig.CAPABILITY,
                        input.getLeft()
                            .getTarget()
                            .getSide())
                    .getOrNull();
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, IWorker> BUILDER_WORKER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET_WORKER, "machine");
        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, ITemperature> BUILDER_TEMPERATURE_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET_TEMPERATURE, "temperature");

        public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, ITemperature> BUILDER_TEMPERATURE_DOUBLE = AspectReadBuilders.BUILDER_DOUBLE
            .handle(PROP_GET_TEMPERATURE, "temperature");
    }

    public static final class Network {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, INetwork> PROP_GET_NETWORK = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, INetwork>() {

            @Override
            public INetwork getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();
                return NetworkHelpers.getNetwork(dimPos.getWorld(), dimPos.getBlockPos());
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, INetwork> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET_NETWORK, "network");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, INetwork> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET_NETWORK, "network");

    }

    public static final class Redstone {

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROPERTY_INTERVAL = new AspectPropertyTypeInstance<>(
            ValueTypes.INTEGER,
            "aspect.aspecttypes.integrateddynamics.integer.interval.name");
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROPERTY_LENGTH = new AspectPropertyTypeInstance<>(
            ValueTypes.INTEGER,
            "aspect.aspecttypes.integrateddynamics.integer.length.name");
        public static final IAspectProperties PROPERTIES_CLOCK = new AspectProperties(
            ImmutableList.<IAspectPropertyTypeInstance>of(PROPERTY_INTERVAL, PROPERTY_LENGTH));
        static {
            PROPERTIES_CLOCK.setValue(PROPERTY_INTERVAL, ValueTypeInteger.ValueInteger.of(20));
            PROPERTIES_CLOCK.setValue(PROPERTY_LENGTH, ValueTypeInteger.ValueInteger.of(1));
        }

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Integer> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Integer>() {

            @Override
            public Integer getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();

                net.minecraft.world.World world = dimPos.getWorld();
                int x = dimPos.getX();
                int y = dimPos.getY();
                int z = dimPos.getZ();
                ForgeDirection side = input.getLeft()
                    .getCenter()
                    .getSide();

                int power = world.getIndirectPowerLevelTo(x, y, z, side.ordinal());

                if (power == 0) {
                    net.minecraft.block.Block block = world.getBlock(x, y, z);
                    if (block == Blocks.redstone_wire) {
                        power = world.getBlockMetadata(x, y, z);
                    }
                }

                return power;
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Integer> PROP_GET_COMPARATOR = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Integer>() {

            @Override
            public Integer getOutput(Pair<PartTarget, IAspectProperties> input) {
                DimPos dimPos = input.getLeft()
                    .getTarget()
                    .getPos();
                return dimPos.getBlockPos()
                    .getBlock(dimPos.getWorld())
                    .getComparatorInputOverride(
                        dimPos.getWorld(),
                        dimPos.getBlockPos()
                            .getX(),
                        dimPos.getBlockPos()
                            .getY(),
                        dimPos.getBlockPos()
                            .getZ(),
                        input.getLeft()
                            .getCenter()
                            .getSide()
                            .ordinal());
            }
        };
        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Boolean> PROP_GET_CLOCK = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, Boolean>() {

            @Override
            public Boolean getOutput(Pair<PartTarget, IAspectProperties> input) {
                int interval = Math.max(
                    1,
                    input.getRight()
                        .getValue(PROPERTY_INTERVAL)
                        .getRawValue());
                int length = Math.max(
                    1,
                    input.getRight()
                        .getValue(PROPERTY_LENGTH)
                        .getRawValue());
                /*
                 * if(length * 2 > interval) {
                 * throw new EvaluationException(String.format("A true and false pulse of length %s do not " +
                 * "fit into an interval of %s.", length, interval));
                 * }
                 */
                return (input.getLeft()
                    .getTarget()
                    .getPos()
                    .getWorld()
                    .getTotalWorldTime() / length) % Math.max(1, interval / length) == 0;
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Integer> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET, "redstone");
        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Boolean> BUILDER_BOOLEAN_CLOCK = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET_CLOCK, "redstone")
            .withProperties(PROPERTIES_CLOCK);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Integer> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "redstone");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Integer> BUILDER_INTEGER_COMPARATOR = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET_COMPARATOR, "redstone");

    }

    public static final class World {

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos> PROP_GET = new IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, DimPos>() {

            @Override
            public DimPos getOutput(Pair<PartTarget, IAspectProperties> input) {
                return input.getLeft()
                    .getTarget()
                    .getPos();
            }
        };
        public static final IAspectValuePropagator<DimPos, net.minecraft.world.World> PROP_GET_WORLD = new IAspectValuePropagator<DimPos, net.minecraft.world.World>() {

            @Override
            public net.minecraft.world.World getOutput(DimPos input) {
                return input.getWorld();
            }
        };
        public static final IAspectValuePropagator<DimPos, BlockPos> PROP_GET_POS = new IAspectValuePropagator<DimPos, BlockPos>() {

            @Override
            public BlockPos getOutput(DimPos input) {
                return input.getBlockPos();
            }
        };
        private static final Predicate<net.minecraft.entity.Entity> ENTITY_SELECTOR_ITEMFRAME = new Predicate<net.minecraft.entity.Entity>() {

            @Override
            public boolean apply(@Nullable net.minecraft.entity.Entity entity) {
                return entity instanceof EntityItemFrame;
            }
        };

        public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, EntityItemFrame> PROP_GET_ITEMFRAME = pair -> {
            if (pair == null || pair.getLeft() == null) {
                return null;
            }

            PartTarget target = pair.getLeft();
            if (target == null) {
                return null;
            }

            DimPos dimPos = target.getCenter()
                .getPos();
            ForgeDirection facing = target.getTarget()
                .getSide();
            if (dimPos == null || dimPos.getWorld() == null || facing == null) {
                return null;
            }

            int x = dimPos.getX();
            int y = dimPos.getY();
            int z = dimPos.getZ();

            ImmutableAxisAlignedBB box = ImmutableAxisAlignedBB.fromBounds(x, y, z, x + 1, y + 1, z + 1);

            List<EntityItemFrame> entities = dimPos.getWorld()
                .getEntitiesWithinAABB(EntityItemFrame.class, box);

            for (EntityItemFrame entity : entities) {
                ForgeDirection frameFacing = getDirectionFromHanging(entity.hangingDirection);

                if (frameFacing == facing.getOpposite()) {
                    return entity;
                }
            }
            return null;
        };

        private static ForgeDirection getDirectionFromHanging(int hangingDirection) {
            switch (hangingDirection) {
                case 0:
                    return ForgeDirection.SOUTH;
                case 1:
                    return ForgeDirection.WEST;
                case 2:
                    return ForgeDirection.NORTH;
                case 3:
                    return ForgeDirection.EAST;
                default:
                    return ForgeDirection.UNKNOWN;
            }
        }

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, DimPos> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
            .handle(PROP_GET, "world");
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, DimPos> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
            .handle(PROP_GET, "world");
        public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, DimPos> BUILDER_LONG = AspectReadBuilders.BUILDER_LONG
            .handle(PROP_GET, "world");
        public static final AspectBuilder<ValueTypeString.ValueString, ValueTypeString, DimPos> BUILDER_STRING = AspectReadBuilders.BUILDER_STRING
            .handle(PROP_GET, "world");
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, DimPos> BUILDER_LIST = AspectReadBuilders.BUILDER_LIST
            .handle(PROP_GET, "world");

    }

}
