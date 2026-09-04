package ruiseki.integratedterminals.inventory.container;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import ruiseki.integratedterminals.Reference;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * @author rubensworks
 */
public class TerminalStorageState {

    static {
        PacketCodec.addCodedAction(TerminalStorageState.class, new PacketCodec.ICodecAction() {

            @Override
            public void encode(Object o, ExtendedBuffer extendedBuffer) throws IOException {
                ((TerminalStorageState) o).writeToPacketBuffer(extendedBuffer);
            }

            @Override
            public Object decode(ExtendedBuffer extendedBuffer) {
                try {
                    return TerminalStorageState.readFromPacketBuffer(extendedBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static final String SETTING_TAB = "tab";
    public static final String SETTING_SEARCH = "search";
    public static final String SETTING_BUTTON = "button";

    public static final String PLAYER_TAG_DEFAULT_KEY = Reference.MOD_ID + ":terminalStorageStateDefault";

    private NBTTagCompound tag;
    private IDirtyMarkListener dirtyMarkListener;

    public TerminalStorageState(IDirtyMarkListener dirtyMarkListener) {
        this(new NBTTagCompound(), dirtyMarkListener);
    }

    public TerminalStorageState(NBTTagCompound tag, IDirtyMarkListener dirtyMarkListener) {
        this.tag = tag;
        this.dirtyMarkListener = dirtyMarkListener;
    }

    public void setDirtyMarkListener(IDirtyMarkListener dirtyMarkListener) {
        this.dirtyMarkListener = dirtyMarkListener;
    }

    protected void markDirty() {
        this.dirtyMarkListener.onDirty();
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
        this.markDirty();
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
        this.markDirty();
    }

    public String getSearch(String tab, int channel) {
        return tag.getString(SETTING_SEARCH + "_" + tab + "_" + channel);
    }

    public boolean hasSearch(String tab, int channel) {
        return tag.hasKey(SETTING_SEARCH + "_" + tab + "_" + channel, Constants.NBT.TAG_STRING);
    }

    public void setSearch(String tab, int channel, @Nullable String search) {
        if (search != null) {
            tag.setString(SETTING_SEARCH + "_" + tab + "_" + channel, search);
        } else {
            tag.removeTag(SETTING_SEARCH + "_" + tab + "_" + channel);
        }
        this.markDirty();
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
            tag.removeTag(SETTING_BUTTON + "_" + tab + "_" + buttonName);
        }
        this.markDirty();
    }

    public static void setPlayerDefault(EntityPlayer playerEntity, TerminalStorageState state) {
        playerEntity.getEntityData()
            .setTag(
                TerminalStorageState.PLAYER_TAG_DEFAULT_KEY,
                state.getTag()
                    .copy());
    }

    public void writeToPacketBuffer(ExtendedBuffer packetBuffer) throws IOException {
        packetBuffer.writeNBTTagCompoundToBuffer(tag);
    }

    public static TerminalStorageState readFromPacketBuffer(ExtendedBuffer packetBuffer) throws IOException {
        return new TerminalStorageState(packetBuffer.readNBTTagCompoundFromBuffer(), () -> {});
    }

    public static TerminalStorageState getPlayerDefault(EntityPlayer playerEntity,
        IDirtyMarkListener dirtyMarkListener) {
        if (playerEntity.getEntityData()
            .hasKey(TerminalStorageState.PLAYER_TAG_DEFAULT_KEY)) {
            return new TerminalStorageState(
                playerEntity.getEntityData()
                    .getCompoundTag(TerminalStorageState.PLAYER_TAG_DEFAULT_KEY),
                dirtyMarkListener);
        }
        return new TerminalStorageState(dirtyMarkListener);
    }
}
