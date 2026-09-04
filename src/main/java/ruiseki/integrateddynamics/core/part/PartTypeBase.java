package ruiseki.integrateddynamics.core.part;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import lombok.Getter;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.PartTypeAdapter;
import ruiseki.integrateddynamics.client.model.ItemPartRenderer;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.item.ItemPart;
import ruiseki.integrateddynamics.core.network.PartNetworkElement;
import ruiseki.integrateddynamics.item.ItemEnhancement;
import ruiseki.integrateddynamics.item.ItemEnhancementConfig;
import ruiseki.okcore.config.configurabletypeaction.BlockAction;
import ruiseki.okcore.config.configurabletypeaction.ItemAction;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemNBTHelpers;
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
    private final Block block;
    @Getter
    protected int guiID;
    @Getter
    private final String name;
    @Getter
    private final PartRenderPosition partRenderPosition;
    private final Map<Class<? extends INetworkEvent>, IEventAction> networkEventActions;

    public PartTypeBase(String name, PartRenderPosition partRenderPosition) {
        this.name = name;
        this.block = registerBlock();
        this.item = registerItem();
        this.partRenderPosition = partRenderPosition;

        networkEventActions = constructNetworkEventActions();
        registerGui();
    }

    protected void registerGui() {
        if (hasGui()) {
            this.guiID = Helpers.getNewId(getModGui(), Helpers.IDType.GUI);
            getModGui().getGuiHandler()
                .registerGUI(this, ExtendedGuiHandler.PART);
        } else {
            this.guiID = -1;
        }
    }

    protected ModBase getMod() {
        return IntegratedDynamics._instance;
    }

    /**
     * Get the part type class.
     * This is used for doing dynamic construction of guis.
     *
     * @return The actual class for this part type.
     */
    public abstract Class<? super P> getPartTypeClass();

    /**
     * Factory method for creating a block instance.
     *
     * @return The block instance.
     */
    protected Block createBlock() {
        return new IgnoredBlock();
    }

    /**
     * Creates and registers a block instance for this part type.
     * This is mainly used for the block model.
     *
     * @return The corresponding block.
     */
    protected Block registerBlock() {
        BlockConfig blockConfig = new BlockConfig(getMod(), true, "part_" + getName(), null, null) {

            @Override
            public boolean isDisableable() {
                return false;
            }

            @Override
            public Block getInstance() {
                return PartTypeBase.this.getBlock();
            }
        };
        Block block = createBlock();
        block.setBlockName(blockConfig.getNamedId());
        BlockAction.register(block, blockConfig, blockConfig.getTargetTab());
        return block;
    }

    /**
     * Factory method for creating a item instance.
     *
     * @return The item instance.
     */
    protected Item createItem() {
        return new ItemPart<P, S>(this);
    }

    /**
     * Creates and registers a item instance for this part type.
     * This is the item used to place the part with and obtained when broken.
     *
     * @return The corresponding item.
     */
    protected Item registerItem() {
        itemConfig = new ItemConfig(getMod(), true, "part_" + getName(), null, null) {

            @Override
            public boolean isDisableable() {
                return false;
            }

            @Override
            public String getFullUnlocalizedName() {
                return PartTypeBase.this.getUnlocalizedName();
            }
        };
        Item item = createItem();
        item.setUnlocalizedName(itemConfig.getUnlocalizedName());
        ItemAction.register(item, itemConfig, itemConfig.getTargetTab());
        return item;
    }

    @Override
    public String getBlockModelPath() {
        return getMod().getModId() + ":" + "part_" + getName();
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
        return new PartNetworkElement(this, PartPos.of(pos, side));
    }

    protected boolean hasGui() {
        return true;
    }

    @Override
    public ModBase getModGui() {
        return getMod();
    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, S partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        // Drop through if the player is sneaking
        if (player.isSneaking()) {
            return false;
        }

        // Consume enhancement
        if (heldItem != null) {
            if (heldItem.getItem() instanceof ItemEnhancement) {
                return ((ItemEnhancement) heldItem.getItem()).applyEnhancement(this, partState, heldItem, player);
            }
        }

        if (hasGui()) {
            openGui(world, pos, partState, player, heldItem, side, hitX, hitY, hitZ);
            return true;
        }
        return false;
    }

    protected void openGui(World world, BlockPos pos, S partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        getModGui().getGuiHandler()
            .setTemporaryData(ExtendedGuiHandler.PART, side); // Pass the side as extra data to the gui
        if (!world.isRemote && hasGui()) {
            player.openGui(getModGui().getModId(), getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void addDrops(PartTarget target, S state, List<ItemStack> itemStacks, boolean dropMainElement,
        boolean saveState) {
        super.addDrops(target, state, itemStacks, dropMainElement, saveState);
        // Save enhancements
        if (!saveState && state.getMaxOffset() > 0) {
            // Drop Part Offset items each with as maximum the GeneralConfig.enchancementOffsetPartDropValue offset
            // value.
            int remainingOffset = state.getMaxOffset();
            while (remainingOffset > 0) {
                int offset;
                if (remainingOffset < GeneralConfig.enchancementOffsetPartDropValue) {
                    offset = remainingOffset;
                } else {
                    offset = GeneralConfig.enchancementOffsetPartDropValue;
                }
                remainingOffset -= offset;

                ItemStack itemStack = new ItemStack(ItemEnhancementConfig._instance.getInstance());
                ((ItemEnhancement) ItemEnhancementConfig._instance.getInstance())
                    .setEnhancementValue(itemStack, offset);
                itemStacks.add(itemStack);
            }
            state.setMaxOffset(0);
        }
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, ForgeDirection side) {
        BlockState state = BlockStateHelpers.getState(getBlock(), 0);
        state.setPropertyValue(IgnoredBlock.FACING, side);
        return state;
    }

    @Override
    public void loadTooltip(S state, List<String> lines) {
        if (!state.isEnabled()) {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_DISABLED));
        }
        lines.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, state.getId()));

        if (state.getMaxOffset() > 0) {
            lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_MAXOFFSET, state.getMaxOffset()));
        }
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {
        if (itemStack.getTagCompound() != null) {
            NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
            if (tag.hasKey("id", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
                int id = tag.getInteger("id");
                lines.add(LangHelpers.localize(L10NValues.GENERAL_ITEM_ID, id));
            }
            if (tag.hasKey("maxOffset", MinecraftHelpers.NBTTag_Types.NBTTagInt.ordinal())) {
                int maxOffset = tag.getInteger("maxOffset");
                lines.add(LangHelpers.localize(L10NValues.PART_TOOLTIP_MAXOFFSET, maxOffset));
            }
        }

        super.loadTooltip(itemStack, lines);
    }

    /**
     * Override this to register your network event actions.
     *
     * @return The event actions.
     */
    protected Map<Class<? extends INetworkEvent>, IEventAction> constructNetworkEventActions() {
        return new IdentityHashMap<>();
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
        if (networkElement.getTarget()
            .getCenter()
            .getPos()
            .isLoaded()) {
            networkEventActions.get(event.getClass())
                .onAction(event.getNetwork(), networkElement.getTarget(), networkElement.getPartState(), event);
        }
    }

    @Override
    public boolean forceLightTransparency(S state) {
        return false;
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
