package ruiseki.integratedterminals.part;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.GeneralConfig;
import ruiseki.integratedterminals.core.part.PartTypeTerminal;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalCraftingJobs;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * A part that exposes a gui using which players can view and manage the active crafting jobs in the network.
 *
 * @author rubensworks
 */
public class PartTypeTerminalCraftingJob
    extends PartTypeTerminal<PartTypeTerminalCraftingJob, PartStateEmpty<PartTypeTerminalCraftingJob>> {

    public PartTypeTerminalCraftingJob(String name) {
        super(name);
    }

    @Override
    public int getConsumptionRate(PartStateEmpty<PartTypeTerminalCraftingJob> state) {
        return GeneralConfig.terminalCraftingBaseConsumption;
    }

    @Override
    protected PartStateEmpty<PartTypeTerminalCraftingJob> constructDefaultState() {
        return new PartStateEmpty<PartTypeTerminalCraftingJob>() {

            @Override
            public int getUpdateInterval() {
                return 1; // For enabling energy consumption
            }
        };
    }

    @Override
    public Optional<IGuiConstructor> getContainerProvider(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerTerminalCraftingJobs(
                    playerInventory,
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    (PartTypeTerminalCraftingJob) data.getMiddle());
            }
        });
    }

    @Override
    public void writeExtraGuiData(ExtendedBuffer packetBuffer, PartPos pos, EntityPlayerMP player) {
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(pos, packetBuffer);
            super.writeExtraGuiData(packetBuffer, pos, player);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadTooltip(ItemStack itemStack, List<String> lines) {
        super.loadTooltip(itemStack, lines);
        if (TerminalStorageTabIngredientCraftingHandlers.REGISTRY.getHandlers()
            .isEmpty()) {
            lines.add(
                EnumChatFormatting.GOLD + LangHelpers
                    .localize("parttype.parttypes.integratedterminals.terminal_crafting_job.tooltip.nohandlers"));
        }
    }
}
