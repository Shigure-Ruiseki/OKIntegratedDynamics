package ruiseki.integrateddynamics.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.inventory.container.ContainerLogicProgrammerPortable;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.inventory.container.NamedContainerProviderItem;
import ruiseki.okcore.item.ItemGui;

/**
 * A portable logic programmer.
 *
 * @author rubensworks
 */
public class ItemPortableLogicProgrammer extends ItemGui {

    /**
     * Make a new item instance.
     */
    public ItemPortableLogicProgrammer() {
        super();
    }

    @Override
    public @Nullable IGuiConstructor getGuiProvider(World world, EntityPlayer player, int itemIndex) {
        return new NamedContainerProviderItem(
            itemIndex,
            (i, inventoryPlayer, itemIndex1) -> new ContainerLogicProgrammerPortable(inventoryPlayer, itemIndex1));
    }

    @Override
    public Class<? extends ContainerExtended> getContainerClass(World world, EntityPlayer player, ItemStack itemStack) {
        return ContainerLogicProgrammerPortable.class;
    }
}
