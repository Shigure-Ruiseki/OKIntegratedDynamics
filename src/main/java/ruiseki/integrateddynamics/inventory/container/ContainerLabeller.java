package ruiseki.integrateddynamics.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.client.gui.GuiLabeller;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.integrateddynamics.item.ItemLabeller;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ItemInventoryContainer;
import ruiseki.okcore.inventory.slot.SlotExtended;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * Container for the labeller.
 *
 * @author rubensworks
 */
public class ContainerLabeller extends ItemInventoryContainer<ItemLabeller> {

    private SimpleInventory temporaryInputSlots = null;

    @SideOnly(Side.CLIENT)
    private GuiLabeller gui;

    /**
     * Make a new instance.
     *
     * @param player    The player.
     * @param itemIndex The index of the item in use inside the player inventory.
     */
    public ContainerLabeller(EntityPlayer player, int itemIndex) {
        super(player.inventory, ItemLabeller.getInstance(), itemIndex);
        this.temporaryInputSlots = new SimpleInventory(1, "temporaryInput", 1);
        addSlotToContainer(new SlotExtended(temporaryInputSlots, 0, 8, 8));
        this.addPlayerInventory(player.inventory, 8, 31);

        if (MinecraftHelpers.isClientSide()) {
            temporaryInputSlots.addDirtyMarkListener(new IDirtyMarkListener() {

                @Override
                public void onDirty() {
                    ItemStack itemStack = temporaryInputSlots.getStackInSlot(0);
                    IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
                        .getRegistry(IVariableFacadeHandlerRegistry.class);
                    IVariableFacade variableFacade = registry.handle(itemStack);
                    String label = LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
                        .getLabel(variableFacade.getId());
                    if (label == null && itemStack != null && itemStack.hasDisplayName()) {
                        label = itemStack.getDisplayName();
                    }
                    if (label != null) {
                        ContainerLabeller.this.getGui()
                            .setText(label);
                    }
                }
            });
        }
    }

    @SideOnly(Side.CLIENT)
    public void setGui(GuiLabeller gui) {
        this.gui = gui;
    }

    @SideOnly(Side.CLIENT)
    public GuiLabeller getGui() {
        return this.gui;
    }

    public ItemStack getItemStack() {
        return temporaryInputSlots.getStackInSlot(0);
    }

    @Override
    protected int getSizeInventory() {
        return 1;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            ItemStack itemStack = temporaryInputSlots.getStackInSlot(0);
            if (itemStack != null) {
                player.dropPlayerItemWithRandomChoice(itemStack, false);
            }
        }
    }

    public void setItemStackName(String name) {
        ItemStack itemStack = getItemStack();
        if (itemStack != null) {
            if (StringUtils.isBlank(name)) {
                if (itemStack.hasTagCompound() && itemStack.getTagCompound()
                    .hasKey("display", 10)) {
                    NBTTagCompound displayTag = itemStack.getTagCompound()
                        .getCompoundTag("display");
                    displayTag.removeTag("Name");

                    if (displayTag.hasNoTags()) {
                        itemStack.getTagCompound()
                            .removeTag("display");
                    }

                    if (itemStack.getTagCompound()
                        .hasNoTags()) {
                        itemStack.setTagCompound(null);
                    }
                }
            } else {
                itemStack.setStackDisplayName(name);
            }
        }
    }
}
