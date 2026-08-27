package ruiseki.integratedterminals.core.terminalstorage;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabCommon;
import ruiseki.integratedterminals.core.client.gui.GuiTerminalStorage;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridAutoRefill;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridBalance;
import ruiseki.integratedterminals.core.terminalstorage.button.TerminalButtonItemStackCraftingGridClear;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridShiftClickOutput;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * A client-side storage terminal ingredient tab for crafting with {@link ItemStack} instances.
 *
 * @author rubensworks
 */
public class TerminalStorageTabIngredientComponentItemStackCraftingClient
    extends TerminalStorageTabIngredientComponentClient<ItemStack, Integer> {

    private final ItemStack icon;

    public TerminalStorageTabIngredientComponentItemStackCraftingClient(ContainerTerminalStorageBase container,
        ResourceLocation name, IngredientComponent<?, ?> ingredientComponent) {
        super(container, name, ingredientComponent);
        this.icon = new ItemStack(Blocks.crafting_table);
    }

    @Override
    public ResourceLocation getTabSettingsName() {
        return GeneralConfig.syncItemStorageAndCraftingTabStates ? ingredientComponent.getName() : getName();
    }

    @Override
    protected void loadButtons(List<ITerminalButton<?, ?, ?>> buttons) {
        super.loadButtons(buttons);

        buttons.add(new TerminalButtonItemStackCraftingGridAutoRefill<>(container.getGuiState(), this));
        buttons.add(new TerminalButtonItemStackCraftingGridClear<>());
        buttons.add(new TerminalButtonItemStackCraftingGridBalance<>());
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public List<String> getTooltip() {
        return Lists.newArrayList(
            LangHelpers.localize(
                "gui.integratedterminals.terminal_storage.crafting_name",
                LangHelpers.localize(this.ingredientComponent.getTranslationKey())));
    }

    @Override
    public int getSlotOffsetX() {
        return ITerminalStorageTabClient.DEFAULT_SLOT_OFFSET_X + 108;
    }

    @Override
    public int getSlotRowLength() {
        return 3;
    }

    @Nullable
    @Override
    public ResourceLocation getBackgroundTexture() {
        return new ResourceLocation(
            Reference.MOD_ID,
            IntegratedTerminals._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                + "part_terminal_storage_crafting.png");
    }

    @Override
    public boolean handleClick(Container container, int channel, int hoveringStorageSlot, int mouseButton,
        boolean hasClickedOutside, boolean hasClickedInStorage, int hoveredContainerSlot) {
        int craftingResultSlotIndex = TerminalStorageTabIngredientComponentItemStackCraftingCommon
            .getCraftingResultSlotIndex(container, getName());
        boolean shift = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
        if (hoveredContainerSlot == craftingResultSlotIndex && shift) {
            IntegratedTerminals._instance.getPacketHandler()
                .sendToServer(
                    new TerminalStorageIngredientItemStackCraftingGridShiftClickOutput(getName().toString(), channel));
            return true;
        }
        if (hoveredContainerSlot > craftingResultSlotIndex && hoveredContainerSlot <= craftingResultSlotIndex + 9
            && getActiveSlotId() < 0) {
            return false;
        }
        return super.handleClick(
            container,
            channel,
            hoveringStorageSlot,
            mouseButton,
            hasClickedOutside,
            hasClickedInStorage,
            hoveredContainerSlot);
    }

    @Override
    public void onCommonSlotRender(GuiContainer gui, GuiTerminalStorage.DrawLayer layer, float partialTick, int x,
        int y, int mouseX, int mouseY, int slot, ITerminalStorageTabCommon tabCommon) {
        // Delegate to regular itemstack tab
        String name = ingredientComponent.getName()
            .toString();
        ITerminalStorageTabClient<?> tabClient = container.getTabClient(name);
        tabCommon = container.getTabCommon(name);
        tabClient.onCommonSlotRender(gui, layer, partialTick, x, y, mouseX, mouseY, slot, tabCommon);
    }
}
