package ruiseki.integratednbt.evaluate;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry.IVariableFacadeFactory;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeString;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeVariableFacade;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratednbt.client.gui.component.Texture;
import ruiseki.integratednbt.client.gui.component.TexturePart;
import ruiseki.integratednbt.evaluate.nbt.NbtValueConverter;
import ruiseki.integratednbt.evaluate.nbt.path.SegmentedNbtPath;
import ruiseki.integratednbt.evaluate.operator.NbtExtractionOperator;
import ruiseki.integratednbt.evaluate.variable.NbtExtractedVariableFacade;
import ruiseki.integratednbt.evaluate.variable.NbtExtractedVariableFacadeHandler;
import ruiseki.okcore.helper.LangHelpers;

public enum NbtExtractorOutputMode {

    REFERENCE("reference", EnumChatFormatting.YELLOW,
        Nest.GUI_TEXTURE.createPart(90, 0, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE),
        Nest.GUI_TEXTURE.createPart(90, 12, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE)) {

        @Override
        public ItemStack writeItemStack(Supplier<IVariableFacade> sourceVariableFacadeSupplier,
            ItemStack outputVariableItemStack, NBTBase currentNBT, SegmentedNbtPath extractionPath, byte defaultNBTId,
            World level, Block blockState, EntityPlayer player) {

            IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class);
            IVariableFacade variableFacade = sourceVariableFacadeSupplier.get();
            if (variableFacade != null) {
                int sourceNBTId = variableFacade.getId();
                IVariableFacadeFactory<NbtExtractedVariableFacade> factory = new IVariableFacadeFactory<NbtExtractedVariableFacade>() {

                    @Override
                    public NbtExtractedVariableFacade create(boolean generateId) {
                        return new NbtExtractedVariableFacade(generateId, sourceNBTId, extractionPath, defaultNBTId);
                    }

                    @Override
                    public NbtExtractedVariableFacade create(int id) {
                        return new NbtExtractedVariableFacade(id, sourceNBTId, extractionPath, defaultNBTId);
                    }
                };
                return registry.writeVariableFacadeItem(
                    true,
                    outputVariableItemStack,
                    NbtExtractedVariableFacadeHandler.getInstance(),
                    factory,
                    player,
                    blockState);
            } else {
                return null;
            }
        }
    },
    OPERATOR("operator", EnumChatFormatting.DARK_GREEN,
        Nest.GUI_TEXTURE.createPart(102, 0, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE),
        Nest.GUI_TEXTURE.createPart(102, 12, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE)) {

        @Override
        public ItemStack writeItemStack(Supplier<IVariableFacade> sourceVariableFacadeSupplier,
            ItemStack outputVariableItemStack, NBTBase currentNBT, SegmentedNbtPath extractionPath, byte defaultNBTId,
            World level, Block blockState, EntityPlayer player) {
            return getVariableUsingValue(
                ValueTypeOperator.ValueOperator.of(new NbtExtractionOperator(extractionPath, defaultNBTId)),
                outputVariableItemStack,
                level,
                blockState,
                player);
        }

    },
    VALUE("value", EnumChatFormatting.GOLD, Nest.GUI_TEXTURE.createPart(114, 0, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE),
        Nest.GUI_TEXTURE.createPart(114, 12, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE)) {

        @Override
        public ItemStack writeItemStack(Supplier<IVariableFacade> sourceVariableFacadeSupplier,
            ItemStack outputVariableItemStack, NBTBase currentNBT, SegmentedNbtPath extractionPath, byte defaultNBTId,
            World level, Block blockState, EntityPlayer player) {
            sourceVariableFacadeSupplier.get(); // Refresh variable
            NBTBase extractedNBT = extractionPath.extract(currentNBT);
            IValue value = extractedNBT == null ? NbtValueConverter.getDefaultValue(defaultNBTId)
                : NbtValueConverter.mapNBTToValue(extractedNBT);
            return getVariableUsingValue(value, outputVariableItemStack, level, blockState, player);
        }
    },
    NBT_PATH("nbt_path", EnumChatFormatting.RED,
        Nest.GUI_TEXTURE.createPart(150, 0, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE),
        Nest.GUI_TEXTURE.createPart(150, 12, Nest.BUTTON_SIZE, Nest.BUTTON_SIZE)) {

        @Override
        public ItemStack writeItemStack(Supplier<IVariableFacade> sourceVariableFacadeSupplier,
            ItemStack outputVariableItemStack, NBTBase currentNBT, SegmentedNbtPath extractionPath, byte defaultNBTId,
            World level, Block blockState, EntityPlayer player) {
            return getVariableUsingValue(
                ValueTypeString.ValueString.of(extractionPath.getCyclopsNBTPath()),
                outputVariableItemStack,
                level,
                blockState,
                player);
        }
    };

    private static class Nest {

        private static final Texture GUI_TEXTURE = new Texture("integratednbt", "textures/gui/nbt_extractor.png");
        private static final int BUTTON_SIZE = 12;
    }

    private String translationId;
    private EnumChatFormatting color;
    private TexturePart buttonTextureNormal;
    private TexturePart buttonTextureHover;

    NbtExtractorOutputMode(String translationId, EnumChatFormatting color, TexturePart buttonTextureNormal,
        TexturePart buttonTextureHover) {
        this.translationId = translationId;
        this.color = color;
        this.buttonTextureNormal = buttonTextureNormal;
        this.buttonTextureHover = buttonTextureHover;
    }

    @Nullable
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static ItemStack getVariableUsingValue(IValue value, ItemStack outputVariableItemStack, World level,
        Block blockState, EntityPlayer player) {
        IVariableFacadeHandlerRegistry registry = IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class);
        if (value == null) {
            return null;
        }
        return registry.writeVariableFacadeItem(
            true,
            outputVariableItemStack,
            ValueTypes.REGISTRY,
            new IVariableFacadeHandlerRegistry.IVariableFacadeFactory<IValueTypeVariableFacade>() {

                @Override
                public IValueTypeVariableFacade create(boolean generateId) {
                    return new ValueTypeVariableFacade(generateId, value.getType(), value);
                }

                @Override
                public IValueTypeVariableFacade create(int id) {
                    return new ValueTypeVariableFacade(id, value.getType(), value);
                }
            },
            player,
            blockState);
    }

    public TexturePart getButtonTextureNormal() {
        return this.buttonTextureNormal;
    }

    public TexturePart getButtonTextureHover() {
        return this.buttonTextureHover;
    }

    public abstract ItemStack writeItemStack(Supplier<IVariableFacade> sourceVariableFacadeSupplier,
        ItemStack outputVariableItemStack, NBTBase currentNBT, SegmentedNbtPath extractionPath, byte defaultNBTId,
        World level, Block blockState, EntityPlayer player);

    public String getDescription(boolean highlighted) {
        EnumChatFormatting color = highlighted ? EnumChatFormatting.GRAY : EnumChatFormatting.DARK_GRAY;
        return color + LangHelpers
            .localize("integratednbt:nbt_extractor.output_mode." + this.translationId + ".description", this.getName());
    }

    public String getName() {
        return this.color
            + LangHelpers.localize("integratednbt:nbt_extractor.output_mode." + this.translationId + ".name");
    }
}
