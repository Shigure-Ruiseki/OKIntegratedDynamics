package ruiseki.integrateddynamics.part.aspect;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.block.BlockLiquid;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fluids.IFluidBlock;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.IValueInterface;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.AspectUpdateType;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRegistry;
import ruiseki.integrateddynamics.api.part.aspect.IAspectWrite;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.capability.valueinterface.ValueInterfaceConfig;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperator;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerInputs;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerOutput;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerRecipeByInput;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerRecipeByOutput;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerRecipesByInput;
import ruiseki.integrateddynamics.core.evaluate.operator.PositionedOperatorRecipeHandlerRecipesByOutput;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeEntity;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeCategoryAny;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeDouble;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositionedRecipes;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeNbt;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeString;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.part.aspect.build.AspectBuilder;
import ruiseki.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import ruiseki.integrateddynamics.part.aspect.read.AspectReadBuilders;
import ruiseki.integrateddynamics.part.aspect.write.AspectWriteBuilders;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.capability.wrapper.BlockLiquidWrapper;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Collection of all aspects.
 *
 * @author rubensworks
 */
public class Aspects {

    public static final IAspectRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IAspectRegistry.class);

    public static void load() {}

    public static final class Read {

        public static final class Audio {

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_PIANO_NOTE = AspectReadBuilders.Audio
                .forInstrument(NoteBlockEvent.Instrument.PIANO)
                .handle(AspectReadBuilders.PROP_GET_INTEGER)
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_BASSDRUM_NOTE = AspectReadBuilders.Audio
                .forInstrument(NoteBlockEvent.Instrument.BASSDRUM)
                .handle(AspectReadBuilders.PROP_GET_INTEGER)
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SNARE_NOTE = AspectReadBuilders.Audio
                .forInstrument(NoteBlockEvent.Instrument.SNARE)
                .handle(AspectReadBuilders.PROP_GET_INTEGER)
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_CLICKS_NOTE = AspectReadBuilders.Audio
                .forInstrument(NoteBlockEvent.Instrument.CLICKS)
                .handle(AspectReadBuilders.PROP_GET_INTEGER)
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_BASSGUITAR_NOTE = AspectReadBuilders.Audio
                .forInstrument(NoteBlockEvent.Instrument.BASSGUITAR)
                .handle(AspectReadBuilders.PROP_GET_INTEGER)
                .buildRead();

        }

        public static final class Block {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_BLOCK = AspectReadBuilders.Block.BUILDER_BOOLEAN
                .handle((dimPos) -> {
                    net.minecraft.block.Block block = dimPos.getBlockPos()
                        .getBlock(dimPos.getWorld());
                    return block != Blocks.air;
                })
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "block")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_DIMENSION = AspectReadBuilders.Block.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> world.provider.dimensionId)
                .withUpdateType(AspectUpdateType.NEVER)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "dimension")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSX = AspectReadBuilders.Block.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_POS)
                .handle(BlockPos::getX)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "posx")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSY = AspectReadBuilders.Block.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_POS)
                .handle(BlockPos::getY)
                .withUpdateType(AspectUpdateType.NEVER)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "posy")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_POSZ = AspectReadBuilders.Block.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_POS)
                .handle(BlockPos::getZ)
                .withUpdateType(AspectUpdateType.NEVER)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "posz")
                .buildRead();

            public static final IAspectRead<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock> BLOCK = AspectReadBuilders.Block.BUILDER_BLOCK
                .handle((dimPos) -> BlockStateHelpers.getState(dimPos.getWorld(), dimPos.getBlockPos()))
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_BLOCK)
                .buildRead();

            public static final IAspectRead<ValueTypeNbt.ValueNbt, ValueTypeNbt> NBT = AspectReadBuilders.Block.BUILDER_NBT
                .handle((dimPos) -> {
                    TileEntity tile = dimPos.getBlockPos()
                        .getTileEntity(dimPos.getWorld());
                    try {
                        if (tile != null) {
                            NBTTagCompound tagCompound = new NBTTagCompound();
                            tile.writeToNBT(tagCompound);
                            return tagCompound;
                        }
                    } catch (Exception e) {
                        // Catch possible errors
                    }
                    return null;
                })
                .handle(AspectReadBuilders.PROP_GET_NBT, "tile")
                .buildRead();
            public static final IAspectRead<ValueTypeString.ValueString, ValueTypeString> STRING_BIOME = AspectReadBuilders.Block.BUILDER_STRING
                .handle(
                    dimPos -> dimPos.getWorld()
                        .getBiomeGenForCoords(
                            dimPos.getBlockPos()
                                .getX(),
                            dimPos.getBlockPos()
                                .getZ()).biomeName)
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_STRING, "biome")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_LIGHT = AspectReadBuilders.Block.BUILDER_INTEGER
                .handle(
                    dimPos -> dimPos.getWorld()
                        .getBlockLightValue(dimPos.getX(), dimPos.getY(), dimPos.getZ()))
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "light")
                .buildRead();
        }

        public static final class Entity {

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ITEMFRAMEROTATION = AspectReadBuilders.Entity.BUILDER_INTEGER_ALL
                .handle(AspectReadBuilders.World.PROP_GET_ITEMFRAME)
                .handle((itemFrame) -> itemFrame != null ? itemFrame.getRotation() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "itemframerotation")
                .buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_ENTITIES = AspectReadBuilders.Entity.BUILDER_LIST
                .handle((dimPos) -> {
                    int x = dimPos.getX();
                    int y = dimPos.getY();
                    int z = dimPos.getZ();

                    List<net.minecraft.entity.Entity> entities = dimPos.getWorld()
                        .selectEntitiesWithinAABB(
                            net.minecraft.entity.Entity.class,
                            ImmutableAxisAlignedBB.fromBounds(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D),
                            IEntitySelector.selectAnything);
                    return ValueTypeList.ValueList.ofList(
                        ValueTypes.OBJECT_ENTITY,
                        Lists.transform(
                            entities,
                            new Function<net.minecraft.entity.Entity, ValueObjectTypeEntity.ValueEntity>() {

                                @Nullable
                                @Override
                                public ValueObjectTypeEntity.ValueEntity apply(net.minecraft.entity.Entity input) {
                                    return ValueObjectTypeEntity.ValueEntity.of(input);
                                }
                            }));
                })
                .appendKind("entities")
                .buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_PLAYERS = AspectReadBuilders.Entity.BUILDER_LIST
                .handle((dimPos) -> {
                    return ValueTypeList.ValueList.ofList(
                        ValueTypes.OBJECT_ENTITY,
                        Lists.transform(
                            dimPos.getWorld().playerEntities,
                            new Function<EntityPlayer, ValueObjectTypeEntity.ValueEntity>() {

                                @Nullable
                                @Override
                                public ValueObjectTypeEntity.ValueEntity apply(EntityPlayer input) {
                                    return ValueObjectTypeEntity.ValueEntity.of(input);
                                }
                            }));
                })
                .appendKind("players")
                .buildRead();

            public static final IAspectRead<ValueObjectTypeEntity.ValueEntity, ValueObjectTypeEntity> ENTITY = AspectReadBuilders.Entity.BUILDER_ENTITY
                .withProperties(AspectReadBuilders.LIST_PROPERTIES)
                .handle(input -> {
                    int i = input.getRight()
                        .getValue(AspectReadBuilders.PROPERTY_LISTINDEX)
                        .getRawValue();
                    DimPos dimPos = input.getLeft()
                        .getTarget()
                        .getPos();
                    int x = dimPos.getX();
                    int y = dimPos.getY();
                    int z = dimPos.getZ();

                    List<net.minecraft.entity.Entity> entities = dimPos.getWorld()
                        .selectEntitiesWithinAABB(
                            net.minecraft.entity.Entity.class,
                            ImmutableAxisAlignedBB.fromBounds(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D),
                            IEntitySelector.selectAnything);
                    return ValueObjectTypeEntity.ValueEntity.of(i < entities.size() ? entities.get(i) : null);
                })
                .buildRead();

            public static final IAspectRead<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_ITEMFRAMECONTENTS = AspectReadBuilders.Entity.BUILDER_ITEMSTACK
                .handle(AspectReadBuilders.World.PROP_GET_ITEMFRAME)
                .handle(itemFrame -> itemFrame != null ? itemFrame.getDisplayedItem() : null)
                .handle(AspectReadBuilders.PROP_GET_ITEMSTACK, "itemframecontents")
                .buildRead();
        }

        public static final class ExtraDimensional {

            private static final Random RANDOM = new Random();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_RANDOM = AspectReadBuilders.ExtraDimensional.BUILDER_INTEGER
                .handle(input -> RANDOM.nextInt())
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "random")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_PLAYERCOUNT = AspectReadBuilders.ExtraDimensional.BUILDER_INTEGER
                .handle(MinecraftServer::getCurrentPlayerCount)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "playercount")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_TICKTIME = AspectReadBuilders.ExtraDimensional.BUILDER_INTEGER
                .handle(input -> (int) DoubleMath.mean(input.tickTimeArray))
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "ticktime")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_TPS = AspectReadBuilders.ExtraDimensional.BUILDER_DOUBLE
                .handle(minecraft -> {
                    long[] times = minecraft.tickTimeArray;
                    if (times == null || times.length == 0) return 20.0D;

                    long totalTime = 0;
                    for (long time : times) {
                        totalTime += time;
                    }

                    double meanTickTimeMs = (totalTime / (double) times.length) * 1.0E-6D;
                    if (meanTickTimeMs <= 0) return 20.0D;

                    double tps = 1000.0D / meanTickTimeMs;
                    return Math.min(20.0D, tps);
                })
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "tps")
                .buildRead();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_PLAYERS = AspectReadBuilders.ExtraDimensional.BUILDER_LIST
                .handle(
                    input -> ValueTypeList.ValueList.ofList(
                        ValueTypes.OBJECT_ENTITY,
                        Lists.transform(
                            input.getConfigurationManager().playerEntityList,
                            new Function<EntityPlayerMP, ValueObjectTypeEntity.ValueEntity>() {

                                @Nullable
                                @Override
                                public ValueObjectTypeEntity.ValueEntity apply(EntityPlayerMP input) {
                                    return ValueObjectTypeEntity.ValueEntity.of(input);
                                }
                            })))
                .appendKind("players")
                .buildRead();

        }

        public static final class Fluid {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_FULL = AspectReadBuilders.Fluid.BUILDER_BOOLEAN
                .handle(tankInfo -> {
                    boolean allFull = true;
                    for (IFluidTankProperties tank : tankInfo) {
                        if (tank.getContents() == null && tank.getCapacity() > 0
                            || (tank.getContents() != null && tank.getContents().amount < tank.getCapacity())) {
                            allFull = false;
                        }
                    }
                    return allFull;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "full")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EMPTY = AspectReadBuilders.Fluid.BUILDER_BOOLEAN
                .handle(tankInfo -> {
                    for (IFluidTankProperties tank : tankInfo) {
                        if (tank.getContents() != null && tank.getCapacity() > 0
                            || (tank.getContents() != null && tank.getContents().amount < tank.getCapacity())) {
                            return false;
                        }
                    }
                    return true;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "empty")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONEMPTY = AspectReadBuilders.Fluid.BUILDER_BOOLEAN
                .handle(tankInfo -> {
                    boolean hasFluid = false;
                    for (IFluidTankProperties tank : tankInfo) {
                        if (tank.getContents() != null && tank.getContents().amount > 0) {
                            hasFluid = true;
                        }
                    }
                    return hasFluid;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonempty")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_APPLICABLE = AspectReadBuilders.Fluid.BUILDER_BOOLEAN
                .handle(tankInfo -> tankInfo.length > 0)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_AMOUNT = AspectReadBuilders.Fluid.BUILDER_INTEGER_ACTIVATABLE
                .handle(AspectReadBuilders.Fluid.PROP_GET_FLUIDSTACK)
                .handle(fluidStack -> fluidStack != null ? fluidStack.amount : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "amount")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_AMOUNTTOTAL = AspectReadBuilders.Fluid.BUILDER_INTEGER
                .handle(tankInfo -> {
                    int amount = 0;
                    for (IFluidTankProperties tank : tankInfo) {
                        if (tank.getContents() != null) {
                            amount += tank.getContents().amount;
                        }
                    }
                    return amount;
                })
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "totalamount")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_CAPACITY = AspectReadBuilders.Fluid.BUILDER_INTEGER_ACTIVATABLE
                .handle(tankInfo -> tankInfo != null ? tankInfo.getCapacity() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "capacity")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_CAPACITYTOTAL = AspectReadBuilders.Fluid.BUILDER_INTEGER
                .handle(tankInfo -> {
                    int capacity = 0;
                    for (IFluidTankProperties tank : tankInfo) {
                        capacity += tank.getCapacity();
                    }
                    return capacity;
                })
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "totalcapacity")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_TANKS = AspectReadBuilders.Fluid.BUILDER_INTEGER
                .handle(tankInfo -> tankInfo.length)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "tanks")
                .buildRead();

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FILLRATIO = AspectReadBuilders.Fluid.BUILDER_DOUBLE_ACTIVATABLE
                .handle(tankInfo -> {
                    if (tankInfo == null) {
                        return 0D;
                    }
                    double amount = tankInfo.getContents() == null ? 0D : tankInfo.getContents().amount;
                    return amount / (double) tankInfo.getCapacity();
                })
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "fillratio")
                .buildRead();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_TANKFLUIDS = AspectReadBuilders.BUILDER_LIST
                .appendKind("fluid")
                .handle(AspectReadBuilders.Fluid.PROP_GET_LIST_FLUIDSTACKS, "fluidstacks")
                .buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_TANKCAPACITIES = AspectReadBuilders.BUILDER_LIST
                .appendKind("fluid")
                .handle(AspectReadBuilders.Fluid.PROP_GET_LIST_CAPACITIES, "capacities")
                .buildRead();

            public static final IAspectRead<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUIDSTACK = AspectReadBuilders.BUILDER_OBJECT_FLUIDSTACK
                .handle(AspectReadBuilders.Fluid.PROP_GET_ACTIVATABLE, "fluid")
                .withProperties(AspectReadBuilders.Fluid.PROPERTIES)
                .handle(AspectReadBuilders.Fluid.PROP_GET_FLUIDSTACK)
                .handle(AspectReadBuilders.PROP_GET_FLUIDSTACK)
                .buildRead();

            public static final IAspectRead<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> BLOCK = AspectReadBuilders.BUILDER_OBJECT_FLUIDSTACK
                .handle(AspectReadBuilders.Block.PROP_GET, "block")
                .handle(dimPos -> {
                    BlockState blockState = BlockStateHelpers.getState(dimPos.getWorld(), dimPos.getBlockPos());
                    net.minecraft.block.Block block = blockState.getBlock();
                    if (block instanceof IFluidBlock) {
                        return ((IFluidBlock) block)
                            .drain(dimPos.getWorld(), dimPos.getX(), dimPos.getY(), dimPos.getZ(), false);
                    }
                    if (block instanceof BlockLiquid) {
                        return new BlockLiquidWrapper((BlockLiquid) block, dimPos.getWorld(), dimPos.getBlockPos())
                            .drain(Integer.MAX_VALUE, false);
                    }
                    return null;
                })
                .handle(AspectReadBuilders.PROP_GET_FLUIDSTACK)
                .buildRead();

        }

        public static final class Inventory {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_FULL = AspectReadBuilders.Inventory.BUILDER_BOOLEAN
                .handle(inventory -> {
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack == null) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "full")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EMPTY = AspectReadBuilders.Inventory.BUILDER_BOOLEAN
                .handle(inventory -> {
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "empty")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONEMPTY = AspectReadBuilders.Inventory.BUILDER_BOOLEAN
                .handle(inventory -> {
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonempty")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_APPLICABLE = AspectReadBuilders.Inventory.BUILDER_BOOLEAN
                .handle(Objects::nonNull)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_COUNT = AspectReadBuilders.Inventory.BUILDER_INTEGER
                .handle(inventory -> {
                    int count = 0;
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null) {
                                count += itemStack.stackSize;
                            }
                        }
                    }
                    return count;
                })
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "count")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SLOTS = AspectReadBuilders.Inventory.BUILDER_INTEGER
                .handle(inventory -> inventory != null ? inventory.getSlots() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "slots")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SLOTSFILLED = AspectReadBuilders.Inventory.BUILDER_INTEGER
                .handle(inventory -> {
                    int count = 0;
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null) {
                                count++;
                            }
                        }
                    }
                    return count;
                })
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "slotsfilled")
                .buildRead();

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FILLRATIO = AspectReadBuilders.Inventory.BUILDER_DOUBLE
                .handle(inventory -> {
                    int count = 0;
                    if (inventory != null) {
                        for (int i = 0; i < inventory.getSlots(); i++) {
                            ItemStack itemStack = inventory.getStackInSlot(i);
                            if (itemStack != null) {
                                count++;
                            }
                        }
                    }
                    return ((double) count) / (double) (inventory != null ? inventory.getSlots() : 1);
                })
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "fillratio")
                .buildRead();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_ITEMSTACKS = AspectReadBuilders.BUILDER_LIST
                .appendKind("inventory")
                .handle(AspectReadBuilders.Inventory.PROP_GET_LIST, "itemstacks")
                .buildRead();

            public static final IAspectRead<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> OBJECT_ITEM_STACK_SLOT = AspectReadBuilders.Inventory.BUILDER_ITEMSTACK
                .handle(AspectReadBuilders.PROP_GET_ITEMSTACK)
                .buildRead();

        }

        public static final class Machine {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISWORKER = AspectReadBuilders.Machine.BUILDER_WORKER_BOOLEAN
                .handle(Objects::nonNull)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isworker")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_HASWORK = AspectReadBuilders.Machine.BUILDER_WORKER_BOOLEAN
                .handle(worker -> worker != null && worker.hasWork())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "haswork")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_CANWORK = AspectReadBuilders.Machine.BUILDER_WORKER_BOOLEAN
                .handle(worker -> worker != null && worker.canWork())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "canwork")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISWORKING = AspectReadBuilders.Machine.BUILDER_WORKER_BOOLEAN
                .handle(worker -> worker != null && worker.canWork() && worker.hasWork())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isworking")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISTEMPERATURE = AspectReadBuilders.Machine.BUILDER_TEMPERATURE_BOOLEAN
                .handle(Objects::nonNull)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "istemperature")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_TEMPERATURE = AspectReadBuilders.Machine.BUILDER_TEMPERATURE_DOUBLE
                .handle(temperature -> temperature != null ? temperature.getTemperature() : 0)
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "temperature")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_MAXTEMPERATURE = AspectReadBuilders.Machine.BUILDER_TEMPERATURE_DOUBLE
                .handle(temperature -> temperature != null ? temperature.getMaximumTemperature() : 0)
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "maxtemperature")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_MINTEMPERATURE = AspectReadBuilders.Machine.BUILDER_TEMPERATURE_DOUBLE
                .handle(temperature -> temperature != null ? temperature.getMinimumTemperature() : 0)
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "mintemperature")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_DEFAULTTEMPERATURE = AspectReadBuilders.Machine.BUILDER_TEMPERATURE_DOUBLE
                .handle(temperature -> temperature != null ? temperature.getDefaultTemperature() : 0)
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "defaulttemperature")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISRECIPEHANDLER = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_BOOLEAN
                .handle(Objects::nonNull)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable")
                .buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_GETRECIPES = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_LIST
                .handle(
                    input -> ValueTypeList.ValueList.ofFactory(
                        new ValueTypeListProxyPositionedRecipes(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipes")
                .buildRead();
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPEOUTPUT = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerOutput<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipeoutputbyinput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerOutput.class,
                        "positionedRecipeHandlerOutput"));
            }
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPEINPUTS = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerInputs<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipeinputsbyoutput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerInputs.class,
                        "positionedRecipeHandlerInputs"));
            }
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPESBYINPUT = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerRecipesByInput<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipesbyinput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerRecipesByInput.class,
                        "positionedRecipeHandlerRecipesByInput"));
            }
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPESBYOUTPUT = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerRecipesByOutput<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipesbyoutput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerRecipesByOutput.class,
                        "positionedRecipeHandlerRecipesByOutput"));
            }
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPEBYINPUT = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerRecipeByInput<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipebyinput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerRecipeByInput.class,
                        "positionedRecipeHandlerRecipeByInput"));
            }
            public static final IAspectRead<ValueTypeOperator.ValueOperator, ValueTypeOperator> OPERATOR_GETRECIPEBYOUTPUT = AspectReadBuilders.Machine.BUILDER_RECIPE_HANDLER_OPERATOR
                .handle(
                    input -> ValueTypeOperator.ValueOperator.of(
                        new PositionedOperatorRecipeHandlerRecipeByOutput<>(
                            input.getLeft()
                                .getTarget()
                                .getPos(),
                            input.getLeft()
                                .getTarget()
                                .getSide())))
                .appendKind("recipebyoutput")
                .buildRead();
            static {
                Operators.REGISTRY.registerSerializer(
                    new PositionedOperator.Serializer(
                        PositionedOperatorRecipeHandlerRecipeByOutput.class,
                        "positionedRecipeHandlerRecipeByOutput"));
            }

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IEnergyStorage> PROP_GET = input -> EnergyHelpers
                .getEnergyStorage(
                    input.getLeft()
                        .getTarget())
                .getOrNull();

            public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, IEnergyStorage> BUILDER_BOOLEAN = AspectReadBuilders.BUILDER_BOOLEAN
                .handle(PROP_GET, "fe");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, IEnergyStorage> BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER
                .handle(PROP_GET, "fe");
            public static final AspectBuilder<ValueTypeDouble.ValueDouble, ValueTypeDouble, IEnergyStorage> BUILDER_DOUBLE = AspectReadBuilders.BUILDER_DOUBLE
                .handle(PROP_GET, "fe");

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISENERGY = BUILDER_BOOLEAN
                .handle(Objects::nonNull)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_CANEXTRACTENERGY = BUILDER_BOOLEAN
                .handle(data -> data != null && data.extractEnergy(1, true) == 1)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "canextract")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_CANINSERTENERGY = BUILDER_BOOLEAN
                .handle(data -> data != null && data.receiveEnergy(1, true) == 1)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "caninsert")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISENERGYFULL = BUILDER_BOOLEAN
                .handle(data -> data != null && data.getEnergyStored() == data.getMaxEnergyStored())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isfull")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISENERGYEMPTY = BUILDER_BOOLEAN
                .handle(data -> data != null && data.getEnergyStored() == 0)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isempty")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISENERGYNONEMPTY = BUILDER_BOOLEAN
                .handle(data -> data != null && data.getEnergyStored() != 0)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isnonempty")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGYSTORED = BUILDER_INTEGER
                .handle(data -> data != null ? data.getEnergyStored() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "amount")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGYCAPACITY = BUILDER_INTEGER
                .handle(data -> data != null ? data.getMaxEnergyStored() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "capacity")
                .buildRead();

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_ENERGYFILLRATIO = BUILDER_DOUBLE
                .handle(data -> {
                    if (data != null) {
                        double capacity = (double) data.getMaxEnergyStored();
                        if (capacity == 0.0D) {
                            return 0.0D;
                        }
                        return ((double) data.getEnergyStored()) / capacity;
                    }
                    return 0.0D;
                })
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "fillratio")
                .buildRead();

        }

        public static final class Network {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_APPLICABLE = AspectReadBuilders.Network.BUILDER_BOOLEAN
                .handle((network) -> network != null)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ELEMENT_COUNT = AspectReadBuilders.Network.BUILDER_INTEGER
                .handle(
                    (network) -> network != null ? network.getElements()
                        .size() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "elementcount")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGY_BATTERY_COUNT = AspectReadBuilders.Network.BUILDER_INTEGER
                .handle(
                    (network) -> network != null && network.getCapability(EnergyNetworkConfig.CAPABILITY)
                        .isPresent() ? network.getCapability(EnergyNetworkConfig.CAPABILITY)
                            .getOrNull()
                            .getPrioritizedPositions()
                            .size() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "energy")
                .appendKind("batterycount")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGY_STORED = AspectReadBuilders.Network.ENERGY_BUILDER
                .handle(storage -> storage != null ? storage.getEnergyStored() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "energy")
                .appendKind("stored")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGY_MAX = AspectReadBuilders.Network.ENERGY_BUILDER
                .handle(storage -> storage != null ? storage.getMaxEnergyStored() : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "energy")
                .appendKind("max")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_ENERGY_CONSUMPTION_RATE = AspectReadBuilders.Network.BUILDER_INTEGER
                .handle(
                    network -> network != null && GeneralConfig.energyConsumptionMultiplier > 0 ? network.getElements()
                        .stream()
                        .mapToInt(
                            (e) -> e instanceof IEnergyConsumingNetworkElement
                                ? ((IEnergyConsumingNetworkElement) e).getConsumptionRate()
                                : 0)
                        .sum() * GeneralConfig.energyConsumptionMultiplier : 0)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "energy")
                .appendKind("consumptionrate")
                .buildRead();
            public static final IAspectRead<IValue, ValueTypeCategoryAny> ANY_VALUE = AspectReadBuilders.BUILDER_ANY
                .appendKind("network")
                .handle(data -> {
                    PartPos target = data.getLeft()
                        .getTarget();
                    IValueInterface valueInterface = CapabilityHelpers
                        .getCapability(target.getPos(), ValueInterfaceConfig.CAPABILITY, target.getSide())
                        .orElseThrow(() -> {
                            EvaluationException error = new EvaluationException(
                                LangHelpers.localize(L10NValues.ASPECT_ERROR_NOVALUEINTERFACE));
                            error.setRetryEvaluation(true);
                            return error;
                        });
                    return valueInterface.getValue()
                        .orElseThrow(
                            () -> new EvaluationException(
                                LangHelpers.localize(L10NValues.ASPECT_ERROR_NOVALUEINTERFACE)));
                })
                .appendKind("value")
                .buildRead();
        }

        public static final class Redstone {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_LOW = AspectReadBuilders.Redstone.BUILDER_BOOLEAN
                .handle(input -> input == 0)
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "low")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONLOW = AspectReadBuilders.Redstone.BUILDER_BOOLEAN
                .handle(input -> input > 0)
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonlow")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_HIGH = AspectReadBuilders.Redstone.BUILDER_BOOLEAN
                .handle(input -> input == 15)
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "high")
                .buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_CLOCK = AspectReadBuilders.Redstone.BUILDER_BOOLEAN_CLOCK
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "clock")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_VALUE = AspectReadBuilders.Redstone.BUILDER_INTEGER
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "value")
                .buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_COMPARATOR = AspectReadBuilders.Redstone.BUILDER_INTEGER_COMPARATOR
                .withUpdateType(AspectUpdateType.BLOCK_UPDATE)
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "comparator")
                .buildRead();
        }

        public static final class World {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_CLEAR = AspectReadBuilders.World.BUILDER_BOOLEAN
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> !world.isRaining())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather")
                .appendKind("clear")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_RAINING = AspectReadBuilders.World.BUILDER_BOOLEAN
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> world.isRaining())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather")
                .appendKind("raining")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_WEATHER_THUNDER = AspectReadBuilders.World.BUILDER_BOOLEAN
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> world.isThundering())
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "weather")
                .appendKind("thunder")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISDAY = AspectReadBuilders.World.BUILDER_BOOLEAN
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> MinecraftHelpers.isDay(world))
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isday")
                .buildRead();

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_ISNIGHT = AspectReadBuilders.World.BUILDER_BOOLEAN
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> !MinecraftHelpers.isDay(world))
                .handle(AspectReadBuilders.PROP_GET_BOOLEAN, "isnight")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_RAINCOUNTDOWN = AspectReadBuilders.World.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle(
                    (world) -> world.getWorldInfo()
                        .getRainTime())
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "raincountdown")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_TICKTIME = AspectReadBuilders.World.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle(
                    (world) -> (int) DoubleMath.mean(
                        FMLCommonHandler.instance()
                            .getMinecraftServerInstance().worldTickTimes.get(world.provider.dimensionId)))
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "ticktime")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_DAYTIME = AspectReadBuilders.World.BUILDER_INTEGER
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> (int) (world.getWorldTime() % MinecraftHelpers.MINECRAFT_DAY))
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "daytime")
                .buildRead();

            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_LIGHTLEVEL = AspectReadBuilders.World.BUILDER_INTEGER
                .handle(
                    (dimPos) -> dimPos.getWorld()
                        .getBlockLightValue(
                            dimPos.getBlockPos()
                                .getX(),
                            dimPos.getBlockPos()
                                .getY(),
                            dimPos.getBlockPos()
                                .getZ()))
                .handle(AspectReadBuilders.PROP_GET_INTEGER, "lightlevel")
                .buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_TPS = AspectReadBuilders.World.BUILDER_DOUBLE
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle(world -> {
                    if (world == null || FMLCommonHandler.instance()
                        .getMinecraftServerInstance() == null) {
                        return 20.0D;
                    }

                    int dimId = world.provider.dimensionId;
                    long[] times = FMLCommonHandler.instance()
                        .getMinecraftServerInstance().worldTickTimes.get(dimId);

                    if (times == null || times.length == 0) {
                        return 20.0D;
                    }

                    long totalTime = 0;
                    for (long time : times) {
                        totalTime += time;
                    }

                    double meanTickTimeMs = (totalTime / (double) times.length) * 1.0E-6D;
                    if (meanTickTimeMs <= 0) return 20.0D;

                    double tps = 1000.0D / meanTickTimeMs;
                    return Math.min(20.0D, tps);
                })
                .handle(AspectReadBuilders.PROP_GET_DOUBLE, "tps")
                .buildRead();

            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_TIME = AspectReadBuilders.World.BUILDER_LONG
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> world.getWorldTime())
                .handle(AspectReadBuilders.PROP_GET_LONG, "time")
                .buildRead();

            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_TOTALTIME = AspectReadBuilders.World.BUILDER_LONG
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle((world) -> world.getTotalWorldTime())
                .handle(AspectReadBuilders.PROP_GET_LONG, "totaltime")
                .buildRead();

            public static final IAspectRead<ValueTypeString.ValueString, ValueTypeString> STRING_NAME = AspectReadBuilders.World.BUILDER_STRING
                .handle(AspectReadBuilders.World.PROP_GET_WORLD)
                .handle(
                    (world) -> world.getWorldInfo()
                        .getWorldName())
                .handle(AspectReadBuilders.PROP_GET_STRING, "worldname")
                .buildRead();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_PLAYERS = AspectReadBuilders.World.BUILDER_LIST
                .handle(
                    (dimPos) -> ValueTypeList.ValueList.ofList(
                        ValueTypes.OBJECT_ENTITY,
                        Lists.transform(
                            dimPos.getWorld().playerEntities,
                            (input) -> ValueObjectTypeEntity.ValueEntity.of(input))))
                .appendKind("players")
                .buildRead();

        }

    }

    public static final class Write {

        public static final class Audio {

            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_PIANO_NOTE = AspectWriteBuilders.Audio.BUILDER_INTEGER_INSTRUMENT
                .handle(AspectWriteBuilders.Audio.propWithInstrument(NoteBlockEvent.Instrument.PIANO), "piano")
                .handle(AspectWriteBuilders.Audio.PROP_SET)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_BASSDRUM_NOTE = AspectWriteBuilders.Audio.BUILDER_INTEGER_INSTRUMENT
                .handle(AspectWriteBuilders.Audio.propWithInstrument(NoteBlockEvent.Instrument.BASSDRUM), "bassdrum")
                .handle(AspectWriteBuilders.Audio.PROP_SET)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SNARE_NOTE = AspectWriteBuilders.Audio.BUILDER_INTEGER_INSTRUMENT
                .handle(AspectWriteBuilders.Audio.propWithInstrument(NoteBlockEvent.Instrument.SNARE), "snare")
                .handle(AspectWriteBuilders.Audio.PROP_SET)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_CLICKS_NOTE = AspectWriteBuilders.Audio.BUILDER_INTEGER_INSTRUMENT
                .handle(AspectWriteBuilders.Audio.propWithInstrument(NoteBlockEvent.Instrument.CLICKS), "clicks")
                .handle(AspectWriteBuilders.Audio.PROP_SET)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_BASSGUITAR_NOTE = AspectWriteBuilders.Audio.BUILDER_INTEGER_INSTRUMENT
                .handle(
                    AspectWriteBuilders.Audio.propWithInstrument(NoteBlockEvent.Instrument.BASSGUITAR),
                    "bassguitar")
                .handle(AspectWriteBuilders.Audio.PROP_SET)
                .buildWrite();

            public static final IAspectWrite<ValueTypeString.ValueString, ValueTypeString> STRING_SOUND = AspectWriteBuilders.Audio.BUILDER_STRING
                .withProperties(AspectWriteBuilders.Audio.PROPERTIES_SOUND)
                .handle(input -> {
                    IAspectProperties properties = input.getMiddle();
                    BlockPos pos = input.getLeft()
                        .getTarget()
                        .getPos()
                        .getBlockPos();
                    if (!StringUtils.isNullOrEmpty(input.getRight())) {
                        float f = (float) properties.getValue(AspectWriteBuilders.Audio.PROP_FREQUENCY)
                            .getRawValue();
                        float volume = (float) properties.getValue(AspectWriteBuilders.Audio.PROP_VOLUME)
                            .getRawValue();

                        IntegratedDynamics.proxy.sendSoundMinecraft(
                            (double) pos.getX() + 0.5D,
                            (double) pos.getY() + 0.5D,
                            (double) pos.getZ() + 0.5D,
                            input.getRight(),
                            volume,
                            f);
                    }
                    return null;
                }, "sound")
                .buildWrite();
        }

        public static final class Effect {

            public static IAspectWrite<ValueTypeDouble.ValueDouble, ValueTypeDouble> createForParticle(
                final String particleName) {
                return AspectWriteBuilders.Effect.BUILDER_DOUBLE_PARTICLE.appendKind("particle")
                    .appendKind(particleName)
                    .handle(input -> {
                        double velocity = input.getRight();
                        if (velocity < 0) {
                            return null;
                        }
                        IAspectProperties properties = input.getMiddle();
                        PartPos pos = input.getLeft()
                            .getTarget();

                        // Retrieve base block coordinates directly from 1.7.10 DimPos / PartPos
                        DimPos dimPos = pos.getPos();
                        double x = dimPos.getX() + properties.getValue(AspectWriteBuilders.Effect.PROP_OFFSET_X)
                            .getRawValue();
                        double y = dimPos.getY() + properties.getValue(AspectWriteBuilders.Effect.PROP_OFFSET_Y)
                            .getRawValue();
                        double z = dimPos.getZ() + properties.getValue(AspectWriteBuilders.Effect.PROP_OFFSET_Z)
                            .getRawValue();

                        int numberOfParticles = properties.getValue(AspectWriteBuilders.Effect.PROP_PARTICLES)
                            .getRawValue();

                        double xDir = properties.getValue(AspectWriteBuilders.Effect.PROP_SPREAD_X)
                            .getRawValue();
                        double yDir = properties.getValue(AspectWriteBuilders.Effect.PROP_SPREAD_Y)
                            .getRawValue();
                        double zDir = properties.getValue(AspectWriteBuilders.Effect.PROP_SPREAD_Z)
                            .getRawValue();
                        World world = pos.getPos()
                            .getWorld();

                        if (!world.isRemote && world instanceof WorldServer worldServer) {
                            worldServer
                                .func_147487_a(particleName, x, y, z, numberOfParticles, xDir, yDir, zDir, velocity);
                        }

                        return null;
                    })
                    .buildWrite();
            }
        }

        public static final class Redstone {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN = AspectWriteBuilders.Redstone.BUILDER_BOOLEAN
                .handle((input) -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? 15 : 0))
                .handle(AspectWriteBuilders.Redstone.PROP_SET)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER = AspectWriteBuilders.Redstone.BUILDER_INTEGER
                .handle(AspectWriteBuilders.Redstone.PROP_SET)
                .buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_PULSE = AspectWriteBuilders.Redstone.BUILDER_BOOLEAN
                .withProperties(AspectWriteBuilders.Redstone.PROPERTIES_REDSTONE_PULSE)
                .appendKind("pulse")
                .handle(input -> Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? 15 : 0))
                .handle(AspectWriteBuilders.Redstone.PROP_SET_PULSE)
                .buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_PULSE = AspectWriteBuilders.Redstone.BUILDER_INTEGER
                .withProperties(AspectWriteBuilders.Redstone.PROPERTIES_REDSTONE_PULSE)
                .appendKind("pulse")
                .handle(AspectWriteBuilders.Redstone.PROP_SET_PULSE)
                .buildWrite();
        }

    }

}
