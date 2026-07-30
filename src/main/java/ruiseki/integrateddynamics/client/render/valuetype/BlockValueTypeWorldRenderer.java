package ruiseki.integrateddynamics.client.render.valuetype;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.base.Optional;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * A value type world renderer for blocks.
 * 
 * @author rubensworks
 */
public class BlockValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    @Override
    public void renderValue(IPartContainer partContainer, double x, double y, double z, float partialTick,
        int destroyStage, ForgeDirection direction, IPartType partType, IValue value,
        TileEntityRendererDispatcher rendererDispatcher, float alpha) {
        Optional<BlockState> blockOptional = ((ValueObjectTypeBlock.ValueBlock) value).getRawValue();
        if (blockOptional.isPresent()) {
            // ItemStack
            ItemStack itemStack = BlockHelpers.getItemStackFromBlockState(blockOptional.get());
            if (itemStack != null) {
                ItemValueTypeWorldRenderer.renderItemStack(itemStack, alpha);
            }
        }
    }
}
