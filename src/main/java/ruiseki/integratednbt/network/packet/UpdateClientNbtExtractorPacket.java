package ruiseki.integratednbt.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.integratednbt.client.gui.container.GuiNbtExtractor;
import ruiseki.integratednbt.evaluate.NbtExtractorOutputMode;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for updating a live crafting plan gui.
 *
 * @author rubensworks
 */
public class UpdateClientNbtExtractorPacket extends PacketCodec {

    private static ByteMaskMaker maskMaker = new ByteMaskMaker();
    private static final byte MASK_NBT = maskMaker.nextMask();
    private static final byte MASK_ERROR_CODE = maskMaker.nextMask();
    private static final byte MASK_EXTRACTION_PATH = maskMaker.nextMask();
    private static final byte MASK_OUTPUT_MODE = maskMaker.nextMask();
    private static final byte MASK_ERROR_MESSAGE = maskMaker.nextMask();
    private static final byte MASK_AUTO_REFRESH = maskMaker.nextMask();

    private byte updated = 0;
    private UpdateClientNbtExtractorPacket.ErrorCode errorCode;
    private NBTBase nbt;
    private SegmentedNbtPath path;
    private NbtExtractorOutputMode outputMode;
    private LangHelpers.UnlocalizedString errorMessage;
    private boolean autoRefresh;

    public UpdateClientNbtExtractorPacket() {

    }

    public void updateNBT(NBTBase nbt) {
        this.nbt = nbt;
        this.updated |= MASK_NBT;
    }

    public void updateErrorCode(UpdateClientNbtExtractorPacket.ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.updated |= MASK_ERROR_CODE;
    }

    public void updateExtractionPath(SegmentedNbtPath path) {
        this.path = path;
        this.updated |= MASK_EXTRACTION_PATH;
    }

    public void updateOutputMode(NbtExtractorOutputMode outputMode) {
        this.outputMode = outputMode;
        this.updated |= MASK_OUTPUT_MODE;
    }

    public void updateErrorMessage(LangHelpers.UnlocalizedString errorMessage) {
        this.errorMessage = errorMessage;
        this.updated |= MASK_ERROR_MESSAGE;
    }

    public void updateAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        this.updated |= MASK_AUTO_REFRESH;
    }

    private boolean isUpdated(byte mask) {
        return (this.updated & mask) > 0;
    }

    public boolean isEmpty() {
        return this.updated == 0;
    }

    @Override
    public void encode(ExtendedBuffer buf) {
        super.encode(buf);

        buf.writeByte(this.updated);
        if (this.isUpdated(MASK_NBT)) {
            NBTTagCompound compound = new NBTTagCompound();
            if (this.nbt != null) {
                compound.setTag("nbt", this.nbt);
            }
            try {
                buf.writeNBTTagCompoundToBuffer(compound);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.isUpdated(MASK_ERROR_CODE)) {
            buf.writeByte(this.errorCode.ordinal());
        }
        if (this.isUpdated(MASK_EXTRACTION_PATH)) {
            try {
                buf.writeNBTTagCompoundToBuffer(this.path.toNBTCompound());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.isUpdated(MASK_OUTPUT_MODE)) {
            buf.writeByte(this.outputMode.ordinal());
        }
        if (this.isUpdated(MASK_ERROR_MESSAGE)) {
            if (this.errorMessage == null) { // Is null
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                try {
                    buf.writeNBTTagCompoundToBuffer(this.errorMessage.serializeNBT());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (this.isUpdated(MASK_AUTO_REFRESH)) {
            buf.writeBoolean(this.autoRefresh);
        }
    }

    @Override
    public void decode(ExtendedBuffer buf) {
        super.decode(buf);

        this.updated = buf.readByte();
        if (this.isUpdated(MASK_NBT)) {
            NBTTagCompound compound = null;
            try {
                compound = buf.readNBTTagCompoundFromBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assert compound != null;
            this.nbt = compound.getCompoundTag("nbt");
        }
        if (this.isUpdated(MASK_ERROR_CODE)) {
            this.errorCode = UpdateClientNbtExtractorPacket.ErrorCode.values()[buf.readByte()];
        }
        if (this.isUpdated(MASK_EXTRACTION_PATH)) {
            try {
                this.path = SegmentedNbtPath.fromNBT(buf.readNBTTagCompoundFromBuffer())
                    .orElse(new SegmentedNbtPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.isUpdated(MASK_OUTPUT_MODE)) {
            this.outputMode = NbtExtractorOutputMode.values()[buf.readByte()];
        }
        if (this.isUpdated(MASK_ERROR_MESSAGE)) {
            if (buf.readBoolean()) { // Is null
                this.errorMessage = null;
            } else {
                try {
                    this.errorMessage.deserializeNBT(buf.readNBTTagCompoundFromBuffer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (this.isUpdated(MASK_AUTO_REFRESH)) {
            this.autoRefresh = buf.readBoolean();
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        if (isUpdated(MASK_NBT)) {
            GuiNbtExtractor.updateNBT(nbt);
        }
        if (isUpdated(MASK_ERROR_CODE)) {
            GuiNbtExtractor.updateError(errorCode);
        }
        if (isUpdated(MASK_EXTRACTION_PATH)) {
            GuiNbtExtractor.updateExtractionPath(path);
        }
        if (isUpdated(MASK_OUTPUT_MODE)) {
            GuiNbtExtractor.updateOutputMode(outputMode);
        }
        if (isUpdated(MASK_ERROR_MESSAGE)) {
            GuiNbtExtractor.updateErrorMessage(errorMessage);
        }
        if (isUpdated(MASK_AUTO_REFRESH)) {
            GuiNbtExtractor.updateAutoRefresh(autoRefresh);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        // Do nothing
    }

    public enum ErrorCode {
        NO_ERROR,
        TYPE_ERROR,
        EVAL_ERROR,
        UNEXPECTED_ERROR,
    }

}
