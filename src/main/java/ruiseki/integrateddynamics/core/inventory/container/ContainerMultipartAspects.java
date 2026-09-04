package ruiseki.integrateddynamics.core.inventory.container;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IAspectVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipartAspects;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.item.AspectVariableFacade;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.ScrollingInventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * Container for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class ContainerMultipartAspects<P extends IPartType<P, S> & IGuiContainerProvider, S extends IPartState<P>, A extends IAspect>
    extends ScrollingInventoryContainer<A> implements IDirtyMarkListener {

    public static final int BUTTON_SETTINGS = 1;
    public static final int BUTTON_OFFSETS = 2;
    public static final int BUTTON_ASPECT_PROPERTIES_START = 3;
    private static final int PAGE_SIZE = 3;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final P partType;
    private final World world;
    private final BlockPos pos;
    private final Map<IAspect, Integer> aspectPropertyButtons = Maps.newHashMap();
    private final Map<IAspect, Integer> aspectPropertyValueIds = Maps.newIdentityHashMap();

    protected final IInventory inputSlots;
    protected final EntityPlayer player;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     * @param items         The items.
     */
    public ContainerMultipartAspects(EntityPlayer player, PartTarget target, IPartContainer partContainer, P partType,
        List<A> items) {
        super(player.inventory, partType, items, new IItemPredicate<A>() {

            @Override
            public boolean apply(A item, Pattern pattern) {
                // We could cache this if this would prove to be a bottleneck.
                // But we have a small amount of aspects, so this shouldn't be a problem.
                return pattern.matcher(
                    LangHelpers.localize(item.getUnlocalizedName())
                        .toLowerCase(Locale.ENGLISH))
                    .matches();
            }
        });
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();
        if (target != null && target.getCenter() != null) {
            this.pos = target.getCenter()
                .getPos()
                .getBlockPos();
        } else {
            this.pos = new BlockPos(
                (int) Math.floor(player.posX),
                (int) Math.floor(player.posY),
                (int) Math.floor(player.posZ));
        }

        this.inputSlots = constructInputSlotsInventory();
        this.player = player;
        putButtonAction(GuiMultipartAspects.BUTTON_SETTINGS, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!world.isRemote) {
                    IGuiContainerProvider gui = ((PartTypeConfigurable) getPartType()).getSettingsGuiProvider();
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide());
                    BlockPos cPos = getTarget().getCenter()
                        .getPos()
                        .getBlockPos();
                    ContainerMultipartAspects.this.player
                        .openGui(gui.getModGui(), gui.getGuiID(), world, cPos.getX(), cPos.getY(), cPos.getZ());
                }
            }
        });

        putButtonAction(GuiMultipartAspects.BUTTON_OFFSETS, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!world.isRemote) {
                    IGuiContainerProvider gui = ((PartTypeConfigurable<?, ?>) getPartType()).getOffsetsGuiProvider();
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide()); // Pass the side as extra data to the gui
                    BlockPos cPos = getTarget().getCenter()
                        .getPos()
                        .getBlockPos();
                    ContainerMultipartAspects.this.player
                        .openGui(gui.getModGui(), gui.getGuiID(), world, cPos.getX(), cPos.getY(), cPos.getZ());
                }
            }
        });

        int nextButtonId = BUTTON_ASPECT_PROPERTIES_START;
        for (final IAspect aspect : getUnfilteredItems()) {
            if (aspect.hasProperties()) {
                aspectPropertyButtons.put(aspect, nextButtonId);
                aspectPropertyValueIds.put(aspect, getNextValueId());
                putButtonAction(nextButtonId, new IButtonActionServer<InventoryContainer>() {

                    @Override
                    public void onAction(int buttonId, InventoryContainer container) {
                        IGuiContainerProvider gui = aspect.getPropertiesGuiProvider();
                        ForgeDirection side = getTarget().getCenter()
                            .getSide();

                        IntegratedDynamics._instance.getGuiHandler()
                            .setTemporaryData(ExtendedGuiHandler.ASPECT, Pair.of(side, aspect));

                        if (!world.isRemote) {
                            BlockPos cPos = getTarget().getCenter()
                                .getPos()
                                .getBlockPos();
                            ContainerMultipartAspects.this.player
                                .openGui(gui.getModGui(), gui.getGuiID(), world, cPos.getX(), cPos.getY(), cPos.getZ());
                        }
                    }
                });
                nextButtonId++;
            }
        }
    }

    public Map<IAspect, Integer> getAspectPropertyButtons() {
        return Collections.unmodifiableMap(this.aspectPropertyButtons);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!world.isRemote) {
            for (Map.Entry<IAspect, Integer> entry : this.aspectPropertyValueIds.entrySet()) {
                ValueNotifierHelpers.setValue(this, entry.getValue(), getModifiedAspectPropertyValues(entry.getKey()));
            }
        }
    }

    /**
     * Determine the values of all properties of the given aspect that deviate from their default value.
     *
     * The returned list has one entry for each of the aspect's property types,
     * in the order of {@link IAspect#getPropertyTypes()}.
     * Properties that still have their default value are represented by "EMPTY".
     *
     * @param aspect An aspect that has properties.
     * @return The modified property values, in the order of the aspect's property types.
     */
    @SuppressWarnings("unchecked")
    protected List<String> getModifiedAspectPropertyValues(IAspect aspect) {
        IAspectProperties defaultProperties = aspect.getDefaultProperties();
        IAspectProperties properties = getPartState().getAspectProperties(aspect);
        if (properties == null) {
            properties = defaultProperties;
        }

        List<String> values = Lists.newArrayList();
        for (IAspectPropertyTypeInstance property : (Collection<IAspectPropertyTypeInstance>) aspect
            .getPropertyTypes()) {
            IValue value = properties.getValue(property);
            IValue defaultValue = defaultProperties.getValue(property);
            if (value == null || ValueHelpers.areValuesEqual(value, defaultValue)) {
                values.add("");
            } else {
                IValueType valueType = value.getType();
                String compactValue = valueType.toCompactString(value);
                if (compactValue == null || compactValue.isEmpty()) {
                    values.add("");
                } else {
                    values.add(valueType.getDisplayColorFormat() + compactValue);
                }
            }
        }
        return values;
    }

    /**
     * Get the modified property values of the given aspect, as synced from the server.
     *
     * @param aspect An aspect.
     * @return The modified property values, in the order of {@link IAspect#getPropertyTypes()},
     *         or null if they are unknown.
     */
    @Nullable
    public List<String> getModifiedAspectPropertyValuesSynced(IAspect aspect) {
        Integer valueId = this.aspectPropertyValueIds.get(aspect);
        if (valueId == null) {
            return null;
        }
        return ValueNotifierHelpers.getValueStringList(this, valueId);
    }

    @SuppressWarnings("unchecked")
    public S getPartState() {
        return (S) partContainer.getPartState(
            getTarget().getCenter()
                .getSide());
    }

    public abstract int getAspectBoxHeight();

    protected IInventory constructInputSlotsInventory() {
        SimpleInventory inventory = new SimpleInventory(getUnfilteredItemCount(), "temporaryInputSlots", 1);
        inventory.addDirtyMarkListener(this);
        return inventory;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (inputSlots instanceof SimpleInventory) {
            ((SimpleInventory) inputSlots).removeDirtyMarkListener(this);
        }
    }

    protected void disableSlot(int slotIndex) {
        Slot slot = getSlot(slotIndex);
        // Yes I know this is ugly.
        // If you are reading this and know a better way, please tell me.
        slot.xDisplayPosition = Integer.MIN_VALUE;
        slot.yDisplayPosition = Integer.MIN_VALUE;
    }

    protected abstract void enableSlot(int slotIndex, int row);

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    protected void onScroll() {
        for (int i = 0; i < getUnfilteredItemCount(); i++) {
            disableSlot(i);
        }
    }

    @Override
    protected void enableElementAt(int row, int elementIndex, A element) {
        super.enableElementAt(row, elementIndex, element);
        enableSlot(elementIndex, row);
    }

    @Override
    protected int getSizeInventory() {
        return getUnfilteredItemCount(); // Input and output slots per item
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return PartHelpers.canInteractWith(getTarget(), player, this.partContainer);
    }

    public ItemStack writeAspectInfo(boolean generateId, ItemStack itemStack, final IAspect aspect) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        return registry.writeVariableFacadeItem(
            generateId,
            itemStack,
            Aspects.REGISTRY,
            new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IAspectVariableFacade>() {

                @Override
                public IAspectVariableFacade create(boolean generateId) {
                    return new AspectVariableFacade(generateId, getPartState().getId(), aspect);
                }

                @Override
                public IAspectVariableFacade create(int id) {
                    return new AspectVariableFacade(id, getPartState().getId(), aspect);
                }
            },
            null,
            null);
    }

}
