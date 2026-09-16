package ruiseki.integratednbt.evaluate.variable;

import java.util.Optional;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.item.IVariableFacadeHandler;
import ruiseki.integratednbt.Reference;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;

public class NbtExtractedVariableFacadeHandler implements IVariableFacadeHandler<NbtExtractedVariableFacade> {

    private static final String KEY_SOURCE_NBT_ID = "sourceNBTId";
    private static final String KEY_EXTRACTION_PATH = "extractionPath";
    private static final String KEY_DEFAULT_NBT_ID = "defaultNBTId";
    private static NbtExtractedVariableFacadeHandler instance;

    public static NbtExtractedVariableFacadeHandler getInstance() {
        if (instance == null) {
            instance = new NbtExtractedVariableFacadeHandler();
        }
        return instance;
    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "nbt_extracted");
    }

    @Override
    public NbtExtractedVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        int sourceNBTId = tag.getInteger(KEY_SOURCE_NBT_ID);
        Optional<SegmentedNbtPath> extractionPath = SegmentedNbtPath.fromNBT(tag.getCompoundTag(KEY_EXTRACTION_PATH));
        byte defaultNBTId = tag.getByte(KEY_DEFAULT_NBT_ID);
        return new NbtExtractedVariableFacade(id, sourceNBTId, extractionPath.orElse(null), defaultNBTId);
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, NbtExtractedVariableFacade facade) {
        tag.setInteger(KEY_SOURCE_NBT_ID, facade.getSourceNbtId());
        tag.setTag(
            KEY_EXTRACTION_PATH,
            facade.getExtractionPath()
                .toNBT());
        tag.setByte(KEY_DEFAULT_NBT_ID, facade.getDefaultNbtId());
    }
}
