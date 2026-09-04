package ruiseki.integratedterminals.core.part;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanel;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.init.ModBase;

/**
 * Base part for a terminal.
 *
 * @author rubensworks
 */
public abstract class PartTypeTerminal<P extends PartTypeTerminal<P, S>, S extends IPartState<P>>
    extends PartTypePanel<P, S> {

    public PartTypeTerminal(String name) {
        super(name);
    }

    @Override
    public boolean onPartActivated(World world, BlockPos pos, S partState, EntityPlayer player, ItemStack heldItem,
        ForgeDirection side, float hitX, float hitY, float hitZ) {
        if (isUpdate(partState) && !partState.isEnabled()) {
            player.addChatComponentMessage(new ChatComponentTranslation(L10NValues.PART_ERROR_LOWENERGY));
            return false;
        }
        return super.onPartActivated(world, pos, partState, player, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    protected Block createBlock() {
        return new IgnoredBlock();
    }

    @Override
    public ModBase getMod() {
        return IntegratedTerminals._instance;
    }

    @Override
    public ModBase getModGui() {
        return IntegratedDynamics._instance;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartType.class;
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public boolean isUpdate(S state) {
        return getConsumptionRate(state) > 0 && GeneralConfig.energyConsumptionMultiplier > 0;
    }

}
