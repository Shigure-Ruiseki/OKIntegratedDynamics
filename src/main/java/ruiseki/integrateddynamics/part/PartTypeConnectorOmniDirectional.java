package ruiseki.integrateddynamics.part;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.api.path.ISidedPathElement;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.SidedPathElement;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;

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
        MinecraftForge.EVENT_BUS.register(this);
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
    public ItemStack getItemStack(State state, boolean saveState) {
        ItemStack itemStack = super.getItemStack(state, saveState);
        if (state.hasConnectorId()) {
            NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
            tag.setInteger(NBT_KEY_ID, state.getGroupId());
        }
        return itemStack;
    }

    @Override
    public State getState(ItemStack itemStack) {
        State state = super.getState(itemStack);
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag != null && tag.hasKey(NBT_KEY_ID, MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
            state.setGroupId(tag.getInteger(NBT_KEY_ID));
        } else {
            state.setGroupId(PartTypeConnectorOmniDirectional.generateGroupId());
        }
        return state;
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
        if (!PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions() && !state.isAddedToGroup()) {
            state.setAddedToGroup(true);
            PartTypeConnectorOmniDirectional.LOADED_GROUPS
                .addPosition(state.getGroupId(), pos, network.isInitialized());
        }
    }

    protected void removePosition(INetwork network, State state, PartPos pos) {
        if (!PartTypeConnectorOmniDirectional.LOADED_GROUPS.isModifyingPositions() && state.isAddedToGroup()) {
            if (state.hasConnectorId()) {
                state.setAddedToGroup(false);
                PartTypeConnectorOmniDirectional.LOADED_GROUPS
                    .removePosition(state.getGroupId(), pos, network.isInitialized());
            }
        }
    }

    public static int generateGroupId() {
        return IntegratedDynamics.globalCounters.getNext("omnidir-connectors");
    }

    @Override
    public void loadTooltip(State state, List<String> lines) {
        super.loadTooltip(state, lines);
        lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP, state.getGroupId()));
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

    protected IgnoredBlockStatus.Status getStatus(PartTypeConnectorOmniDirectional.State state) {
        return state != null && state.hasConnectorId() ? IgnoredBlockStatus.Status.ACTIVE
            : IgnoredBlockStatus.Status.INACTIVE;
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, ForgeDirection side) {
        BlockState state = BlockStateHelpers.getState(getBlock(), 0);
        IgnoredBlockStatus.Status status = getStatus(
            partContainer != null ? (PartTypeConnectorOmniDirectional.State) partContainer.getPartState(side) : null);
        state.setPropertyValue(IgnoredBlock.FACING, side);
        state.setPropertyValue(IgnoredBlockStatus.STATUS, status);
        return state;
    }

    @SubscribeEvent
    public void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        // When crafting the item, either copy the group id from the existing item or generate a new id.
        if (event.crafting.getItem() == this.getItem()) {
            int groupId = -1, stackCount = 0;
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
                ItemStack slotStack = event.craftMatrix.getStackInSlot(i);
                if (slotStack != null) {
                    ++stackCount;
                    if (slotStack.getItem() == this.getItem() && slotStack.hasTagCompound()) {
                        NBTTagCompound tag = slotStack.getTagCompound();
                        if (tag.hasKey(NBT_KEY_ID, MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
                            groupId = tag.getInteger(NBT_KEY_ID);
                            break;
                        }
                    }
                }
            }
            if (stackCount == 1) {
                groupId = -1; // If we're resetting a connector, give it a new ID
            }

            if (!MinecraftHelpers.isClientSide()) {
                if (groupId < 0) {
                    groupId = generateGroupId();
                }
                NBTTagCompound tag = ItemNBTHelpers.getNBT(event.crafting);
                tag.setInteger(NBT_KEY_ID, groupId);
            }
        }
    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, State partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        // Drop through if the player is sneaking
        if (player.isSneaking() || !partState.isEnabled()) {
            return false;
        }
        if (world.isRemote) {
            player.addChatComponentMessage(
                new ChatComponentTranslation(
                    L10NValues.PART_TOOLTIP_MONODIRECTIONALCONNECTOR_GROUP,
                    partState.getGroupId()));
        }

        return true;
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
        public Set<ISidedPathElement> getReachableElements() {
            if (hasConnectorId()) {
                Set<ISidedPathElement> pathElements = Sets.newTreeSet();
                for (PartPos pos : PartTypeConnectorOmniDirectional.LOADED_GROUPS.getPositions(getGroupId())) {
                    if (!pos.equals(this.getPartPos())) {
                        IPathElement pathElement = CapabilityHelpers
                            .getCapability(pos.getPos(), PathElementConfig.CAPABILITY, pos.getSide())
                            .getOrNull();
                        if (pathElement != null) {
                            pathElements.add(SidedPathElement.of(pathElement, pos.getSide()));
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
                            .getBlockPos(),
                        position.getSide());
                }
            }
        }

        public void addPosition(int group, PartPos pos, boolean initNetwork) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.add(pos);

            if (initNetwork) {
                modifyingPositions = true;
                initNetworkGroup(positions);
                modifyingPositions = false;
            }
        }

        public void removePosition(int group, PartPos pos, boolean initNetwork) {
            Set<PartPos> positions = groupPositions.get(group);
            if (positions == null) {
                groupPositions.put(group, positions = Sets.newTreeSet());
            }
            positions.remove(pos);

            if (initNetwork) {
                modifyingPositions = true;
                initNetworkGroup(positions);
                if (pos.getPos()
                    .isLoaded()) {
                    NetworkHelpers.initNetwork(
                        pos.getPos()
                            .getWorld(),
                        pos.getPos()
                            .getBlockPos(),
                        pos.getSide());
                }
                modifyingPositions = false;
            }
        }

        public boolean isModifyingPositions() {
            return modifyingPositions;
        }
    }
}
