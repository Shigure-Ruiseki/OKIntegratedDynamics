package ruiseki.integrateddynamics.core.inventory.container;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * Container for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class ContainerMultipart<P extends IPartType<P, S>, S extends IPartState<P>> extends InventoryContainer
    implements IDirtyMarkListener {

    public static final String BUTTON_SETTINGS = "button_settings";
    public static final String BUTTON_OFFSETS = "button_offsets";

    private static final int PAGE_SIZE = 3;

    private final Optional<PartTarget> target;
    private final Optional<IPartContainer> partContainer;
    private final P partType;
    private final World world;

    public ContainerMultipart(@Nullable ContainerType<?> type, InventoryPlayer playerInventory, IInventory inventory,
        Optional<PartTarget> target, Optional<IPartContainer> partContainer, P partType) {
        super(type, playerInventory, inventory);
        this.target = target;
        this.partType = partType;
        this.partContainer = partContainer;
        this.world = player.worldObj;

        putButtonAction(ContainerMultipart.BUTTON_SETTINGS, (s, containerExtended) -> {
            if (!world.isRemote) {
                PartHelpers.openContainerPart(
                    (EntityPlayerMP) player,
                    target.get()
                        .getCenter(),
                    partType);
            }
        });
        putButtonAction(ContainerMultipart.BUTTON_OFFSETS, (s, containerExtended) -> {
            if (!world.isRemote) {
                PartHelpers.openContainerPartOffsets(
                    (EntityPlayerMP) player,
                    target.get()
                        .getCenter(),
                    partType);
            }
        });

    }

    public World getWorld() {
        return world;
    }

    public P getPartType() {
        return partType;
    }

    public Optional<PartTarget> getTarget() {
        return target;
    }

    public Optional<S> getPartState() {
        return partContainer.map(
            p -> (S) p.getPartState(
                getTarget().get()
                    .getCenter()
                    .getSide()));
    }

    public Optional<IPartContainer> getPartContainer() {
        return partContainer;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return PartHelpers.canInteractWith(getTarget().get(), player, this.partContainer.get());
    }

    @Override
    public void onDirty() {

    }
}
