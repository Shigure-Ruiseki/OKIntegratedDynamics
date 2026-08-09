package ruiseki.integrateddynamics.part;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An omnidirectional wireless connector part that can connect to
 * all other monodirectional connectors of the same group anywhere in any dimension.
 *
 * @author rubensworks
 */
public class PartTypeConnectorOmniDirectional
    extends PartTypeConnector<PartTypeConnectorOmniDirectional, PartTypeConnectorOmniDirectional.State> {

    public static LoadedGroups LOADED_GROUPS = new LoadedGroups();
    private static String NBT_KEY_ID = "omnidir-group-key";

    public PartTypeConnectorOmniDirectional(String name) {
        super(name, new PartRenderPosition(0.25F, 0.3125F, 0.625F, 0.625F));
    }

    @Override
    public int getConsumptionRate(State state) {
        return 128;
    }

    @Override
    public PartTypeConnectorOmniDirectional.State constructDefaultState() {
        return new PartTypeConnectorOmniDirectional.State();
    }

    @Override
    public Class<? super PartTypeConnectorOmniDirectional> getPartTypeClass() {
        return PartTypeConnectorOmniDirectional.class;
    }

    @Override
    public ItemStack getItemStack(State state) {
        ItemStack itemStack = super.getItemStack(state);
        if (state.hasConnectorId()) {
            NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
            tag.setInteger(NBT_KEY_ID, state.getGroupId());
        }
        return itemStack;
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addPosition(network, state, target.getCenter());
    }

    @Override
    public void onPostRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target, State state) {
        super.onPostRemoved(network, partNetwork, target, state);
        removePosition(network, state, target.getCenter());
    }

    protected void addPosition(INetwork network, State state, PartPos pos) {
        if (network.isInitialized() && !PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions()
            && !state.isAddedToGroup()) {
            state.setAddedToGroup(true);
            PartTypeConnectorOmniDirectional.LOADED_GROUPS.addPosition(state.getGroupId(), pos);
        }
    }

    protected void removePosition(INetwork network, State state, PartPos pos) {
        if (network.isInitialized() && !PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions()
            && state.isAddedToGroup()) {
            if (state.hasConnectorId()) {
                state.setAddedToGroup(false);
                PartTypeConnectorOmniDirectional.LOADED_GROUPS.removePosition(state.getGroupId(), pos);
            }
        }
    }

    public static int generateGroupId() {
        // return IntegratedDynamics.globalCounters.getNext("omnidir-connectors");
        return 100; // TODO: change when implemented recipes
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {
        super.loadTooltip(itemStack, lines);
        if (itemStack.hasTagCompound()) {
            lines.add(
                LangHelpers.localize(
                    L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP,
                    itemStack.getTagCompound()
                        .getInteger(NBT_KEY_ID)));
        }
    }

    @Override
    public void loadTooltip(State state, List<String> lines) {
        super.loadTooltip(state, lines);
        lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP, state.getGroupId()));
    }

    public static class State extends PartTypeConnector.State<PartTypeConnectorOmniDirectional> {

        private int groupId = -1;
        private boolean addedToGroup = false;

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setInteger(NBT_KEY_ID, groupId);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            this.groupId = tag.getInteger(NBT_KEY_ID);
        }

        @Override
        public Set<IPathElement> getReachableElements() {
            if (hasConnectorId()) {
                Set<IPathElement> pathElements = Sets.newTreeSet();
                for (PartPos pos : PartTypeConnectorOmniDirectional.LOADED_GROUPS.getPositions(getGroupId())) {
                    if (!pos.equals(this.getPartPos())) {
                        IPathElement pathElement = CapabilityHelpers
                            .getCapability(pos.getPos(), PathElementConfig.CAPABILITY, pos.getSide())
                            .getOrNull();
                        if (pathElement != null) {
                            pathElements.add(pathElement);
                        }
                    }
                }
                return pathElements;
            }
            return Collections.emptySet();
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
            sendUpdate();
        }

        public boolean hasConnectorId() {
            return this.groupId >= 0;
        }

        public boolean isAddedToGroup() {
            return addedToGroup;
        }

        public void setAddedToGroup(boolean addedToGroup) {
            this.addedToGroup = addedToGroup;
        }
    }

    public static class LoadedGroups {

        private TIntObjectMap<Set<PartPos>> groupPositions = new TIntObjectHashMap<>();
        private boolean modifyingPositions = false;

        public void onStartedEvent(FMLServerStartedEvent event) {
            // Reset to avoid ghost-groups on world-change.
            groupPositions.clear();
        }

        public Set<PartPos> getPositions(int group) {
            Set<PartPos> positions = groupPositions.get(group);
            return positions != null ? positions : Collections.<PartPos>emptySet();
        }

        protected void initNetworkGroup(Set<PartPos> positions) {
            for (PartPos position : positions) {
                if (position.getPos()
                    .isLoaded()) {
                    NetworkHelpers.initNetwork(
                        position.getPos()
                            .getWorld(),
                        position.getPos()
                            .getBlockPos());
                }
            }
        }

        public void addPosition(int group, PartPos pos) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.add(pos);

            modifyingPositions = true;
            initNetworkGroup(positions);
            modifyingPositions = false;
        }

        public void removePosition(int group, PartPos pos) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.remove(pos);

            modifyingPositions = true;
            initNetworkGroup(positions);
            if (pos.getPos()
                .isLoaded()) {
                NetworkHelpers.initNetwork(
                    pos.getPos()
                        .getWorld(),
                    pos.getPos()
                        .getBlockPos());
            }
            modifyingPositions = false;
        }

        public boolean isModifyingPositions() {
            return modifyingPositions;
        }
    }
}
