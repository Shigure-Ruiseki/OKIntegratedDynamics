package ruiseki.integratedcompat.modcompat.jjfmuy;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integratedcompat.IntegratedCompat;
import ruiseki.integratedcompat.network.packet.LPPacketJEIDragging;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.jfmuy.api.gui.IGhostIngredientHandler;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.inventory.SimpleInventory;

public class LPGhostIngredientHandler<T extends GuiLogicProgrammerBase> implements IGhostIngredientHandler<T> {

    @Override
    public <I> @Nonnull List<Target<I>> getTargets(@Nonnull T gui, @Nonnull I ingredient, boolean doStart) {
        List<IGhostIngredientHandler.Target<I>> targets = new ArrayList<>();
        if (ingredient instanceof ItemStack || ingredient instanceof FluidStack) {
            int size = gui.getContainer().inventorySlots.size();
            for (int i = 4; i < size; i++) {
                Slot slot = gui.getContainer().inventorySlots.get(i);
                if (slot.inventory instanceof SimpleInventory) {
                    targets.add(new IGhostIngredientHandler.Target<I>() {

                        @Override
                        public @Nonnull Rectangle getArea() {
                            return new Rectangle(
                                gui.guiLeft + slot.xDisplayPosition,
                                gui.guiTop + slot.yDisplayPosition,
                                16,
                                16);
                        }

                        @Override
                        public void accept(@Nonnull I ingredient) {
                            if (ingredient instanceof ItemStack) {
                                IntegratedCompat._instance.getPacketHandler()
                                    .sendToServer(new LPPacketJEIDragging(slot.getSlotIndex(), (ItemStack) ingredient));
                            } else if (ingredient instanceof FluidStack) {
                                ItemStack s = FluidHelpers.getFilledBucket((FluidStack) ingredient);
                                IntegratedCompat._instance.getPacketHandler()
                                    .sendToServer(new LPPacketJEIDragging(slot.getSlotIndex(), s));
                            }
                        }
                    });
                }
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {

    }
}
