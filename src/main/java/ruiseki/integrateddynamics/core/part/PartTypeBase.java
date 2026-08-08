package ruiseki.integrateddynamics.core.part;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

import lombok.Getter;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.PartTypeAdapter;
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
public abstract class PartTypeBase<P extends IPartType<P, S>, S extends IPartState<P>> extends PartTypeAdapter<P, S>
    implements IGuiContainerProvider {

    @Getter
    private final Item item;
    private ItemConfig itemConfig;
    @Getter
    private final int guiID;
    @Getter
    private final String name;
    @Getter
    private final PartRenderPosition partRenderPosition;
    private final Map<Class<? extends INetworkEvent>, IEventAction> networkEventActions;

    public PartTypeBase(String name, PartRenderPosition partRenderPosition) {
        if (hasGui()) {
            this.guiID = Helpers.getNewId(getMod(), Helpers.IDType.GUI);
            getMod().getGuiHandler()
                .registerGUI(this, ExtendedGuiHandler.PART);
        } else {
            this.guiID = -1;
        }
        this.name = name;
        this.item = registerItem();
        this.partRenderPosition = partRenderPosition;

        networkEventActions = constructNetworkEventActions();
    }

    /**
     * Get the part type class.
     * This is used for doing dynamic construction of guis.
     *
     * @return The actual class for this part type.
     */
    public abstract Class<? super P> getPartTypeClass();

    /**
     * Factory method for creating a item instance.
     *
     * @param itemConfig The config to register the item for.
     * @return The item instance.
     */
    protected Item createItem(ItemConfig itemConfig) {
        return new ItemPart<P, S>(itemConfig, this);
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
    public String getUnlocalizedNameBase() {
        return "parttype.parttypes." + getMod().getModId() + "." + getName();
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (MinecraftHelpers.isClientSide() && initStep == IInitListener.Step.INIT) {
            MinecraftForgeClient.registerItemRenderer(getItem(), new ItemPartRenderer());
        }
    }

    @Override
    public INetworkElement createNetworkElement(IPartContainer partContainer, DimPos pos, ForgeDirection side) {
        return new PartNetworkElement(this, PartTarget.fromCenter(pos, side));
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
                .setTemporaryData(ExtendedGuiHandler.PART, side); // Pass the side as extra data to the gui
            if (!world.isRemote && hasGui()) {
                player.openGui(getMod().getModId(), getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {
        if (!state.isEnabled()) {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_DISABLED));
        }
        lines.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, state.getId()));
    }

    /**
     * Override this to register your network event actions.
     *
     * @return The event actions.
     */
    protected Map<Class<? extends INetworkEvent>, IEventAction> constructNetworkEventActions() {
        return Maps.newHashMap();
    }

    @Override
    public final boolean hasEventSubscriptions() {
        return !networkEventActions.isEmpty();
    }

    @Override
    public final Set<Class<? extends INetworkEvent>> getSubscribedEvents() {
        return networkEventActions.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onEvent(INetworkEvent event, IPartNetworkElement<P, S> networkElement) {
        networkEventActions.get(event.getClass())
            .onAction(event.getNetwork(), networkElement.getTarget(), networkElement.getPartState(), event);
    }

    public interface IEventAction<P extends IPartType<P, S>, S extends IPartState<P>, E extends INetworkEvent> {

        public void onAction(INetwork network, PartTarget target, S state, E event);

    }

    public enum Status {
        INACTIVE,
        ACTIVE,
        ERROR
    }
}
