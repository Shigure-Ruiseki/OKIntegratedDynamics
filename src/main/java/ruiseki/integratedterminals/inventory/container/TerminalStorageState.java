package ruiseki.integratedterminals.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.Reference;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * @author rubensworks
 */
public class TerminalStorageState {

    public static final String SETTING_TAB = "tab";
    public static final String SETTING_SEARCH = "search";
    public static final String SETTING_BUTTON = "button";

    public static final String PLAYER_TAG_DEFAULT_KEY = Reference.MOD_ID + ":terminalStorageStateDefault";

    private NBTTagCompound tag;

    public TerminalStorageState() {
        this(new NBTTagCompound());
    }

    public TerminalStorageState(NBTTagCompound tag) {
        this.tag = tag;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
    }

    public String getTab() {
        return tag.getString(SETTING_TAB);
    }

    public boolean hasTab() {
        return tag.hasKey(SETTING_TAB, Constants.NBT.TAG_STRING);
    }

    public void setTab(@Nullable String tab) {
        if (tab != null) {
            tag.setString(SETTING_TAB, tab);
        } else {
            tag.removeTag(SETTING_TAB);
        }
    }

    public String getSearch(String tab, int channel) {
        return tag.getString(SETTING_SEARCH + "_" + tab + "_" + channel);
    }

    public boolean hasSearch(String tab, int channel) {
        return tag.hasKey(SETTING_SEARCH + "_" + tab + "_" + channel, Constants.NBT.TAG_STRING);
    }

    public void setSearch(String tab, int channel, @Nullable String search) {
        if (tab != null) {
            tag.setString(SETTING_SEARCH + "_" + tab + "_" + channel, search);
        } else {
            tag.removeTag(SETTING_SEARCH + "_" + tab + "_" + channel);
        }
    }

    public NBTBase getButton(String tab, String buttonName) {
        return tag.getTag(SETTING_BUTTON + "_" + tab + "_" + buttonName);
    }

    public boolean hasButton(String tab, String buttonName) {
        return tag.hasKey(SETTING_BUTTON + "_" + tab + "_" + buttonName);
    }

    public void setButton(String tab, String buttonName, @Nullable NBTBase button) {
        if (button != null) {
            tag.setTag(SETTING_BUTTON + "_" + tab + "_" + buttonName, button);
        } else {
            tag.removeTag(SETTING_TAB);
        }
    }

    public static void setPlayerDefault(EntityPlayer playerEntity, TerminalStorageState state) {
        playerEntity.getEntityData()
            .setTag(
                TerminalStorageState.PLAYER_TAG_DEFAULT_KEY,
                state.getTag()
                    .copy());
    }

    public static TerminalStorageState getPlayerDefault(EntityPlayer playerEntity) {
        if (playerEntity.getEntityData()
            .hasKey(TerminalStorageState.PLAYER_TAG_DEFAULT_KEY)) {
            return new TerminalStorageState(
                playerEntity.getEntityData()
                    .getCompoundTag(TerminalStorageState.PLAYER_TAG_DEFAULT_KEY));
        }
        return new TerminalStorageState();
    }
}
