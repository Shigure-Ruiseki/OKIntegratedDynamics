package ruiseki.integratedterminals.core.terminalstorage.button;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalButton;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabClient;
import ruiseki.integratedterminals.client.gui.image.Images;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentClient;
import ruiseki.integratedterminals.core.terminalstorage.TerminalStorageTabIngredientComponentItemStackCraftingCommon;
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemStackCraftingGridSetAutoRefill;
import ruiseki.okcore.client.gui.component.button.GuiButtonImage;
import ruiseki.okcore.client.gui.image.IImage;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A button for clearing the crafting grid.
 *
 * @author rubensworks
 */
public class TerminalButtonItemStackCraftingGridAutoRefill<T> implements
    ITerminalButton<TerminalStorageTabIngredientComponentClient<T, ?>, TerminalStorageTabIngredientComponentItemStackCraftingCommon, GuiButtonImage> {

    private final TerminalStorageState state;
    private final String buttonName;
    private final ITerminalStorageTabClient<?> clientTab;

    private AutoRefillType active;

    public TerminalButtonItemStackCraftingGridAutoRefill(TerminalStorageState state,
        ITerminalStorageTabClient<?> clientTab) {
        this.state = state;
        this.buttonName = "itemstack_grid_autorefill";
        this.clientTab = clientTab;

        reloadFromState();

        notifyServer((TerminalStorageTabIngredientComponentClient<T, ?>) clientTab);
    }

    @Override
    public void reloadFromState() {
        String tabName = clientTab.getTabSettingsName()
            .toString();

        if (state.hasButton(tabName, this.buttonName)) {
            NBTTagCompound data = (NBTTagCompound) state.getButton(tabName, this.buttonName);
            if (data != null && data.hasKey("active")) {
                int activeIndex = data.getInteger("active");
                AutoRefillType[] values = AutoRefillType.values();
                if (activeIndex >= 0 && activeIndex < values.length) {
                    this.active = values[activeIndex];
                } else {
                    this.active = AutoRefillType.STORAGE;
                }
            } else {
                this.active = AutoRefillType.STORAGE;
            }
        } else {
            this.active = AutoRefillType.STORAGE;
        }
    }

    protected void notifyServer(TerminalStorageTabIngredientComponentClient<T, ?> clientTab) {
        IntegratedTerminals._instance.getPacketHandler()
            .sendToServer(
                new TerminalStorageIngredientItemStackCraftingGridSetAutoRefill(
                    clientTab.getName()
                        .toString(),
                    this.active));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiButtonImage createButton(int x, int y) {
        return new GuiButtonImage(
            0,
            x,
            y,
            active == AutoRefillType.DISABLED ? Images.BUTTON_BACKGROUND_INACTIVE : Images.BUTTON_BACKGROUND_ACTIVE,
            active.getImage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClick(TerminalStorageTabIngredientComponentClient<T, ?> clientTab,
        TerminalStorageTabIngredientComponentItemStackCraftingCommon commomTab, GuiButtonImage guiButton, int channel,
        int mouseButton) {
        this.active = mouseButton == 0
            ? AutoRefillType.values()[(this.active.ordinal() + 1) % AutoRefillType.values().length]
            : AutoRefillType.DISABLED;

        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("active", active.ordinal());
        state.setButton(
            clientTab.getTabSettingsName()
                .toString(),
            this.buttonName,
            data);

        notifyServer(clientTab);
    }

    @Override
    public String getTranslationKey() {
        return "gui.integratedterminals.terminal_storage.craftinggrid.autorefill";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltip(EntityPlayer player, boolean tooltipFlag, List<String> lines) {
        lines.add(LangHelpers.localize("gui." + Reference.MOD_ID + ".terminal_storage.craftinggrid.autorefill.info"));
        lines.add(LangHelpers.localize(active.getLabel()));
    }

    public static enum AutoRefillType {

        DISABLED(Images.BUTTON_MIDDLE_AUTOREFILL_DISABLED,
            "gui.integratedterminals.terminal_storage.craftinggrid.autorefill.type.disabled"),
        STORAGE(Images.BUTTON_MIDDLE_AUTOREFILL_STORAGE,
            "gui.integratedterminals.terminal_storage.craftinggrid.autorefill.type.storage"),
        PLAYER(Images.BUTTON_MIDDLE_AUTOREFILL_PLAYER,
            "gui.integratedterminals.terminal_storage.craftinggrid.autorefill.type.player"),
        STORAGE_PLAYER(Images.BUTTON_MIDDLE_AUTOREFILL_STORAGEPLAYER,
            "gui.integratedterminals.terminal_storage.craftinggrid.autorefill.type.storage_player"),
        PLAYER_STORAGE(Images.BUTTON_MIDDLE_AUTOREFILL_PLAYERSTORAGE,
            "gui.integratedterminals.terminal_storage.craftinggrid.autorefill.type.player_storage");

        @Nullable
        private final IImage image;
        private final String label;

        AutoRefillType(@Nullable IImage image, String label) {
            this.image = image;
            this.label = label;
        }

        @Nullable
        public IImage getImage() {
            return image;
        }

        public String getLabel() {
            return label;
        }
    }
}
