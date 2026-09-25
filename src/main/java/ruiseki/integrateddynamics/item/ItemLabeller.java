package ruiseki.integrateddynamics.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.inventory.container.ContainerLabeller;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.inventory.container.NamedContainerProviderItem;
import ruiseki.okcore.item.ItemGui;

/**
 * A labeller for variables.
 *
 * @author rubensworks
 */
public class ItemLabeller extends ItemGui {

    /**
     * Make a new item instance.
     */
    public ItemLabeller() {
        super();
    }

    @Override
    public @Nullable IGuiConstructor getGuiProvider(World world, EntityPlayer player, int itemIndex) {
        return new NamedContainerProviderItem(
            itemIndex,
            (id, inventoryPlayer, index) -> new ContainerLabeller(inventoryPlayer, index));
    }

    @Override
    public Class<? extends ContainerExtended> getContainerClass(World world, EntityPlayer player, ItemStack itemStack) {
        return ContainerLabeller.class;
    }
}
