package ruiseki.integrateddynamics.part;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.block.IDynamicLight;
import ruiseki.integrateddynamics.api.evaluate.InvalidValueTypeException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.block.BlockInvisibleLight;
import ruiseki.integrateddynamics.block.BlockInvisibleLightConfig;
import ruiseki.integrateddynamics.capability.DynamicLightConfig;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLightLevels;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanelVariableDriven;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A part that can display variables.
 *
 * @author rubensworks
 */
public class PartTypePanelLightDynamic
    extends PartTypePanelVariableDriven<PartTypePanelLightDynamic, PartTypePanelLightDynamic.State> {

    public PartTypePanelLightDynamic(String name) {
        super(name);
    }

    @Override
    public Class<? super PartTypePanelLightDynamic> getPartTypeClass() {
        return PartTypePanelLightDynamic.class;
    }

    @Override
    public PartTypePanelLightDynamic.State constructDefaultState() {
        return new PartTypePanelLightDynamic.State();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onValueChanged(IPartNetwork network, PartTarget target, State state, IValue lastValue,
        IValue newValue) {
        super.onValueChanged(network, target, state, lastValue, newValue);
        int lightLevel = 0;
        if (newValue != null) {
            lightLevel = getLightLevel(state, newValue);
        }
        setLightLevel(target, lightLevel);
        state.sendUpdate();
    }

    protected int getLightLevel(State state, IValue value) {
        try {
            return ValueTypeLightLevels.REGISTRY.getLightLevel(value);
        } catch (InvalidValueTypeException e) {
            state.addGlobalError(
                new LangHelpers.UnlocalizedString(
                    L10NValues.PART_PANEL_ERROR_INVALIDTYPE,
                    new LangHelpers.UnlocalizedString(
                        value.getType()
                            .getUnlocalizedName())));
        }
        return 0;
    }

    @Override
    public void onNetworkRemoval(IPartNetwork network, PartTarget target, State state) {
        super.onNetworkRemoval(network, target, state);
        PartTypePanelLightDynamic.setLightLevel(target, 0);
    }

    @Override
    public void onPostRemoved(IPartNetwork network, PartTarget target, State state) {
        super.onPostRemoved(network, target, state);
        setLightLevel(target, 0);
    }

    @Override
    public void onBlockNeighborChange(IPartNetwork network, PartTarget target, State state, IBlockAccess world,
        Block neighborBlock) {
        super.onBlockNeighborChange(network, target, state, world, neighborBlock);
        setLightLevel(target, state.getDisplayValue() == null ? 0 : getLightLevel(state, state.getDisplayValue()));
    }

    @Override
    public void postUpdate(IPartNetwork network, PartTarget target, State state, boolean updated) {
        boolean wasEnabled = isEnabled(state);
        super.postUpdate(network, target, state, updated);
        boolean isEnabled = isEnabled(state);
        if (wasEnabled != isEnabled) {
            setLightLevel(target, isEnabled ? getLightLevel(state, state.getDisplayValue()) : 0);
        }
    }

    public static void setLightLevel(PartTarget target, int lightLevel) {
        if (ConfigHandler.isEnabled(BlockInvisibleLightConfig.class)) {
            World world = target.getTarget()
                .getPos()
                .getWorld();
            BlockPos pos = target.getTarget()
                .getPos()
                .getBlockPos();

            Block currentBlock = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
            boolean isAir = world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
            boolean isLightBlock = currentBlock == BlockInvisibleLight.getInstance();

            if (isAir || isLightBlock) {
                if (lightLevel > 0) {
                    if (!isLightBlock) {
                        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), BlockInvisibleLight.getInstance(), 0, 2);
                    }
                    int currentLight = BlockStateHelpers
                        .get(world, pos.getX(), pos.getY(), pos.getZ(), BlockInvisibleLight.LIGHT);
                    if (currentLight != lightLevel) {
                        BlockStateHelpers
                            .set(world, pos.getX(), pos.getY(), pos.getZ(), BlockInvisibleLight.LIGHT, lightLevel);
                    }
                } else if (isLightBlock) {
                    world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                }
            }
        } else {
            IDynamicLight dynamicLight = CapabilityHelpers.getCapability(
                target.getCenter()
                    .getPos(),
                DynamicLightConfig.CAPABILITY,
                target.getCenter()
                    .getSide())
                .getOrNull();
            if (dynamicLight != null) {
                dynamicLight.setLightLevel(lightLevel);
            }
        }
    }

    public static class State
        extends PartTypePanelVariableDriven.State<PartTypePanelLightDynamic, PartTypePanelLightDynamic.State> {

        @Override
        public Class<? extends IPartState> getPartStateClass() {
            return PartTypePanelLightDynamic.State.class;
        }

    }

}
