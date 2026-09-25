package ruiseki.integratedcrafting.inventory.container;

import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingVariableBase;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipart;
import ruiseki.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import ruiseki.integrateddynamics.core.inventory.container.slot.SlotVariable;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.ValueNotifierHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Container for the crafting interface.
 *
 * @author rubensworks
 */
public class ContainerPartInterfaceCrafting<P extends PartTypeInterfaceCraftingVariableBase<P, S>, S extends PartTypeInterfaceCraftingVariableBase.State<P, S>>
    extends ContainerMultipart<P, S> {

    public static final int GUI_WIDTH = 176;
    // Value of GuiHelpers.SLOT_SIZE, duplicated because GuiHelpers is client-only.
    public static final int SLOT_SIZE = 18;

    private final List<Integer> readSlotValidIds;
    private final List<Integer> readSlotErrorIds;

    public ContainerPartInterfaceCrafting(InventoryPlayer playerInventory, ExtendedBuffer packetBuffer) {
        this(
            playerInventory,
            new SimpleInventory(packetBuffer.readInt(), 1),
            Optional.empty(),
            Optional.empty(),
            (P) PartHelpers.readPart(packetBuffer));
    }

    public ContainerPartInterfaceCrafting(InventoryPlayer playerInventory, IInventory inventory,
        Optional<PartTarget> target, Optional<IPartContainer> partContainer, P partType) {
        super(
            ContainerPartInterfaceCraftingConfig._instance.getInstance(),
            playerInventory,
            inventory,
            target,
            partContainer,
            partType);

        addInventory(
            inventory,
            0,
            getVariableSlotsX(inventory.getSizeInventory()),
            22,
            1,
            inventory.getSizeInventory());
        addPlayerInventory(player.inventory, 8, 59);

        getPartState().ifPresent(p -> p.setLastPlayer(player));

        this.readSlotValidIds = Lists.newArrayList();
        this.readSlotErrorIds = Lists.newArrayList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            this.readSlotValidIds.add(getNextValueId());
            this.readSlotErrorIds.add(getNextValueId());
        }

        if (!player.worldObj.isRemote) {
            putButtonAction(
                ContainerMultipartAspects.BUTTON_SETTINGS,
                (s, containerExtended) -> {
                    PartHelpers.openContainerPartSettings(
                        (EntityPlayerMP) player,
                        target.get()
                            .getCenter(),
                        partType);
                });
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        getPartState().ifPresent(partState -> {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ValueNotifierHelpers.setValue(this, this.readSlotValidIds.get(i), partState.isRecipeSlotValid(i));
                ValueNotifierHelpers.setValueUnlocalizedString(
                    this,
                    this.readSlotErrorIds.get(i),
                    partState.getRecipeSlotUnlocalizedMessage(i));
            }
        });
    }

    public static int getVariableSlotsX(int slotCount) {
        return (GUI_WIDTH - slotCount * SLOT_SIZE) / 2 + 1;
    }

    public boolean isRecipeSlotValid(int slot) {
        return ValueNotifierHelpers.getValueBoolean(this, this.readSlotValidIds.get(slot));
    }

    @Nullable
    public LangHelpers.UnlocalizedString getRecipeSlotUnlocalizedMessage(int slot) {
        return ValueNotifierHelpers.getValueUnlocalizedString(this, this.readSlotErrorIds.get(slot));
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        if (inventory instanceof SimpleInventory) {
            return new SlotVariable(inventory, index, x, y) {

                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    IVariableFacade variableFacade = ((ItemVariable) ItemVariableConfig._instance.getInstance())
                        .getVariableFacade(itemStack);
                    return variableFacade != null
                        && ValueHelpers.correspondsTo(variableFacade.getOutputType(), ValueTypes.OBJECT_RECIPE)
                        && super.isItemValid(itemStack);
                }
            };
        }
        return super.createNewSlot(inventory, index, x, y);
    }

    @Override
    public void onDirty() {

    }
}
