package ruiseki.integratedcrafting.part;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.integratedcrafting.GeneralConfig;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.integratedcrafting.client.gui.GuiPartInterfaceCraftingSettings;
import ruiseki.integratedcrafting.core.part.PartTypeInterfaceCraftingBase;
import ruiseki.integratedcrafting.inventory.container.ContainerPartInterfaceCraftingSettings;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Interface for auto crafting that reads out all available target machine recipes.
 *
 * @author rubensworks
 */
public class PartTypeInterfaceCraftingAttuned
    extends PartTypeInterfaceCraftingBase<PartTypeInterfaceCraftingAttuned, PartTypeInterfaceCraftingAttuned.State> {

    public PartTypeInterfaceCraftingAttuned(String name) {
        super(name);
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiPartInterfaceCraftingSettings.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartInterfaceCraftingSettings.class;
    }

    @Override
    public int getConsumptionRate(PartTypeInterfaceCraftingAttuned.State state) {
        return state.getCraftingJobHandler()
            .getProcessingCraftingJobs()
            .size() * GeneralConfig.interfaceCraftingAttunedBaseConsumption;
    }

    @Override
    protected PartTypeInterfaceCraftingAttuned.State constructDefaultState() {
        return new PartTypeInterfaceCraftingAttuned.State();
    }

    @Override
    protected Block createBlock() {
        return new IgnoredBlockStatus();
    }

    protected IgnoredBlockStatus.Status getStatus(State state) {
        IgnoredBlockStatus.Status status = IgnoredBlockStatus.Status.INACTIVE;
        if (state != null) {
            if (state.hasValidTarget()) {
                status = IgnoredBlockStatus.Status.ACTIVE;
            } else {
                status = IgnoredBlockStatus.Status.ERROR;
            }
        }

        return status;
    }

    @Override
    public BlockState getBlockState(IPartContainer partContainer, ForgeDirection side) {
        BlockState state = BlockStateHelpers.getState(getBlock(), 0);
        IgnoredBlockStatus.Status status = this
            .getStatus(partContainer != null ? (State) partContainer.getPartState(side) : null);
        state.setPropertyValue(IgnoredBlock.FACING, side);
        state.setPropertyValue(IgnoredBlockStatus.STATUS, status);
        return state;
    }

    @Override
    public void loadTooltip(State state, List<String> lines) {
        super.loadTooltip(state, lines);

        if (!state.hasValidTarget()) {
            lines.add(
                EnumChatFormatting.RED + LangHelpers
                    .localize("parttype.parttypes.integratedcrafting.interface_crafting_attuned.unsupported"));
        }
    }

    @Override
    public boolean shouldTriggerBlockRenderUpdate(@Nullable State oldPartState, @Nullable State newPartState) {
        return super.shouldTriggerBlockRenderUpdate(oldPartState, newPartState)
            || this.getStatus(oldPartState) != this.getStatus(newPartState);
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, State state,
        IBlockAccess world, Block neighbourBlock, BlockPos neighbourBlockPos) {
        boolean hadValidTarget = state.hasValidTarget();
        removeTargetFromNetwork(network, target.getTarget(), state);

        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighbourBlock, neighbourBlockPos);

        addTargetToNetwork(network, target, state, false);
        if (hadValidTarget != state.hasValidTarget()) {
            BlockHelpers.markForUpdate(
                target.getCenter()
                    .getPos()
                    .getWorld(),
                target.getCenter()
                    .getPos()
                    .getBlockPos());
        }
    }

    public static class State extends
        PartTypeInterfaceCraftingBase.State<PartTypeInterfaceCraftingAttuned, PartTypeInterfaceCraftingAttuned.State> {

        protected boolean hasValidTarget = false;
        private Collection<IRecipeDefinition> recipes;

        protected LazyOptional<IRecipeHandler> getTargetRecipeHandler() {
            PartPos target = getTarget().getTarget();
            return Helpers.getTileOrBlockCapability(
                target.getPos()
                    .getWorld(),
                target.getPos()
                    .getBlockPos(),
                RecipeHandlerConfig.CAPABILITY,
                target.getSide());
        }

        @Override
        public void setNetworks(@Nullable INetwork network, @Nullable ICraftingNetwork craftingNetwork,
            @Nullable IPartNetwork partNetwork, int channel, boolean initialize) {
            super.setNetworks(network, craftingNetwork, partNetwork, channel, initialize);
            this.hasValidTarget = getTargetRecipeHandler().isPresent();
            this.recipes = getTargetRecipeHandler().map(IRecipeHandler::getRecipes)
                .orElse(Collections.emptyList());
            markDirty();
        }

        public boolean hasValidTarget() {
            return this.hasValidTarget;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setBoolean("hasValidTarget", hasValidTarget);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            this.hasValidTarget = tag.getBoolean("hasValidTarget");
        }

        @Override
        public Collection<IRecipeDefinition> getRecipes() {
            return this.recipes;
        }
    }

}
