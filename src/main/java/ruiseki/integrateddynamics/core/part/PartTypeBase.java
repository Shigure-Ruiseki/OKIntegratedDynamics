package ruiseki.integrateddynamics.core.part;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

import lombok.Getter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartContainerFacade;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.client.model.ItemPartRenderer;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.item.ItemPart;
import ruiseki.integrateddynamics.core.network.PartNetworkElement;
import ruiseki.okcore.config.configurabletypeaction.ItemAction;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * An abstract {@link IPartType} with a default implementation for creating
 * network elements.
 *
 * @author rubensworks
 */
public abstract class PartTypeBase<P extends IPartType<P, S>, S extends IPartState<P>>
    implements IPartType<P, S>, IGuiContainerProvider {

    @Getter
    private final Item item;
    private ItemConfig itemConfig;
    @Getter
    private final int guiID;
    @Getter
    private final String name;
    @Getter
    private final RenderPosition renderPosition;
    private final Map<Class<? extends INetworkEvent<IPartNetwork>>, IEventAction> networkEventActions;

    public PartTypeBase(String name, RenderPosition renderPosition) {
        if (hasGui()) {
            this.guiID = Helpers.getNewId(getMod(), Helpers.IDType.GUI);
            getMod().getGuiHandler()
                .registerGUI(this, ExtendedGuiHandler.PART);
        } else {
            this.guiID = -1;
        }
        this.name = name;
        this.item = registerItem();
        this.renderPosition = renderPosition;

        networkEventActions = constructNetworkEventActions();
    }

    /**
     * Factory method for creating a item instance.
     *
     * @param itemConfig The config to register the item for.
     * @return The item instance.
     */
    protected Item createItem(ItemConfig itemConfig) {
        return new ItemPart<>(itemConfig, this);
    }

    /**
     * Creates and registers a item instance for this part type.
     * This is the item used to place the part with and obtained when broken.
     *
     * @return The corresponding item.
     */
    protected Item registerItem() {
        itemConfig = new ItemConfig(getMod(), true, "part_" + getName() + "Item", null, null) {

            @Override
            public boolean isDisableable() {
                return false;
            }
        };
        Item item = createItem(itemConfig);
        ItemAction.register(item, itemConfig.getSubUniqueName(), itemConfig.getTargetTab());
        return item;
    }

    @Override
    public String getBlockModelPath(IPartContainer partContainer, ForgeDirection side) {
        return getMod().getModId() + ":" + "part/part_" + getName() + "Block";
    }

    @Override
    public String getItemModelPath() {
        return getMod().getModId() + ":" + "part/part_" + getName() + "Block";
    }

    /**
     * Override this to register your network event actions.
     *
     * @return The event actions.
     */
    protected Map<Class<? extends INetworkEvent<IPartNetwork>>, IEventAction> constructNetworkEventActions() {
        return Maps.newHashMap();
    }

    @Override
    public final boolean hasEventSubscriptions() {
        return !networkEventActions.isEmpty();
    }

    @Override
    public final Set<Class<? extends INetworkEvent<IPartNetwork>>> getSubscribedEvents() {
        return networkEventActions.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onEvent(INetworkEvent<IPartNetwork> event, IPartNetworkElement<P, S> networkElement) {
        networkEventActions.get(event.getClass())
            .onAction(event.getNetwork(), networkElement.getTarget(), networkElement.getPartState(), event);
    }

    @Override
    public boolean isSolid(S state) {
        return false;
    }

    @Override
    public String getUnlocalizedNameBase() {
        return "parttype.parttypes." + getMod().getModId() + "." + getName();
    }

    @Override
    public String getUnlocalizedName() {
        return getUnlocalizedNameBase() + ".name";
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (MinecraftHelpers.isClientSide() && initStep == IInitListener.Step.INIT) {
            MinecraftForgeClient.registerItemRenderer(getItem(), new ItemPartRenderer());
        }
    }

    @Override
    public INetworkElement<IPartNetwork> createNetworkElement(IPartContainerFacade partContainerFacade, DimPos pos,
        ForgeDirection side) {
        return new PartNetworkElement(this, PartTarget.fromCenter(pos, side));
    }

    @Override
    public ItemStack getItemStack(S state) {
        NBTTagCompound tag = new NBTTagCompound();
        toNBT(tag, state);
        ItemStack itemStack = new ItemStack(getItem());
        itemStack.setTagCompound(tag);
        return itemStack;
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, S state) {
        return getItemStack(state);
    }

    @Override
    public boolean isUpdate(S state) {
        return false;
    }

    @Override
    public void update(IPartNetwork network, PartTarget target, S state) {

    }

    @Override
    public S getState(ItemStack itemStack) {
        S partState = null;
        if (itemStack != null && itemStack.getTagCompound() != null) {
            partState = fromNBT(itemStack.getTagCompound());
        }
        if (partState == null) {
            partState = getDefaultState();
        }
        return partState;
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement) {
        if (dropMainElement) {
            itemStacks.add(getItemStack(state));
        }
    }

    @Override
    public void toNBT(NBTTagCompound tag, S partState) {
        partState.writeToNBT(tag);
    }

    @Override
    public S fromNBT(NBTTagCompound tag) {
        S partState = constructDefaultState();
        partState.readFromNBT(tag);
        partState.gatherCapabilities((P) this);
        return partState;
    }

    /**
     * @return Constructor call for a new default state for this part type.
     */
    protected abstract S constructDefaultState();

    @Override
    public final S getDefaultState() {
        S defaultState = constructDefaultState();
        defaultState.generateId();
        defaultState.gatherCapabilities((P) this);
        return defaultState;
    }

    @Override
    public void setUpdateInterval(S state, int updateInterval) {
        state.setUpdateInterval(updateInterval);
    }

    @Override
    public int getUpdateInterval(S state) {
        return state.getUpdateInterval();
    }

    @Override
    public void beforeNetworkKill(IPartNetwork network, PartTarget target, S state) {
        System.out.println("killing " + state);
    }

    @Override
    public void afterNetworkAlive(IPartNetwork network, PartTarget target, S state) {
        System.out.println("alive " + state);
    }

    @Override
    public void afterNetworkReAlive(IPartNetwork network, PartTarget target, S state) {

    }

    @Override
    public void onNetworkAddition(IPartNetwork network, PartTarget target, S state) {

    }

    @Override
    public void onNetworkRemoval(IPartNetwork network, PartTarget target, S state) {

    }

    protected boolean hasGui() {
        return true;
    }

    @Override
    public ModBase getMod() {
        return IntegratedDynamics._instance;
    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, S partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        // Drop through if the player is sneaking
        if (player.isSneaking() || !partState.isEnabled()) {
            return false;
        }

        if (hasGui()) {
            getMod().getGuiHandler()
                .setTemporaryData(ExtendedGuiHandler.PART, side);

            if (!world.isRemote) {
                player.openGui(getMod().getModId(), getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPreRemoved(IPartNetwork network, PartTarget target, S state) {

    }

    @Override
    public void onBlockNeighborChange(IPartNetwork network, PartTarget target, S state, IBlockAccess world,
        Block neighborBlock) {

    }

    @Override
    public int getConsumptionRate(S state) {
        return 0;
    }

    @Override
    public void postUpdate(IPartNetwork network, PartTarget target, S state, boolean updated) {
        setEnabled(state, updated);
    }

    @Override
    public boolean isEnabled(S state) {
        return state.isEnabled();
    }

    @Override
    public void setEnabled(S state, boolean enabled) {
        state.setEnabled(enabled);
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {
        if (!state.isEnabled()) {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_DISABLED));
        }
        lines.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, state.getId()));
    }

    public interface IEventAction<P extends IPartType<P, S>, S extends IPartState<P>, E extends INetworkEvent> {

        public void onAction(IPartNetwork network, PartTarget target, S state, E event);

    }

}
