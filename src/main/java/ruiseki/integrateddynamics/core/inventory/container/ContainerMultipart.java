package ruiseki.integrateddynamics.core.inventory.container;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler;
import ruiseki.integrateddynamics.core.client.gui.container.GuiMultipart;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.inventory.container.InventoryContainer;
import ruiseki.okcore.inventory.container.button.IButtonActionServer;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * Container for parts.
 *
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class ContainerMultipart<P extends IPartType<P, S> & IGuiContainerProvider, S extends IPartState<P>>
    extends ExtendedInventoryContainer implements IDirtyMarkListener {

    public static final int BUTTON_SETTINGS = 1;
    private static final int PAGE_SIZE = 3;

    private final PartTarget target;
    private final IPartContainer partContainer;
    private final P partType;
    private final World world;
    private final BlockPos pos;
    private final Map<IAspect, Integer> aspectPropertyButtons = Maps.newHashMap();

    protected final EntityPlayer player;

    /**
     * Make a new instance.
     *
     * @param target        The target.
     * @param player        The player.
     * @param partContainer The part container.
     * @param partType      The part type.
     */
    public ContainerMultipart(EntityPlayer player, PartTarget target, IPartContainer partContainer, P partType) {
        super(player.inventory, partType);
        this.target = target;
        this.partContainer = partContainer;
        this.partType = partType;
        this.world = player.getEntityWorld();
        if (target != null && target.getCenter() != null) {
            this.pos = target.getCenter()
                .getPos()
                .getBlockPos();
        } else {
            this.pos = new BlockPos(
                (int) Math.floor(player.posX),
                (int) Math.floor(player.posY),
                (int) Math.floor(player.posZ));
        }

        this.player = player;

        putButtonAction(GuiMultipart.BUTTON_SETTINGS, new IButtonActionServer<InventoryContainer>() {

            @Override
            public void onAction(int buttonId, InventoryContainer container) {
                if (!MinecraftHelpers.isClientSide()) {
                    IGuiContainerProvider gui = ((PartTypeConfigurable<?, ?>) getPartType()).getSettingsGuiProvider();
                    IntegratedDynamics._instance.getGuiHandler()
                        .setTemporaryData(
                            ExtendedGuiHandler.PART,
                            getTarget().getCenter()
                                .getSide()); // Pass the side as extra data to the gui
                    BlockPos cPos = getTarget().getCenter()
                        .getPos()
                        .getBlockPos();
                    ContainerMultipart.this.player
                        .openGui(gui.getModGui(), gui.getGuiID(), world, cPos.getX(), cPos.getY(), cPos.getZ());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public S getPartState() {
        return (S) partContainer.getPartState(
            getTarget().getCenter()
                .getSide());
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return PartHelpers.canInteractWith(getTarget(), player, this.partContainer);
    }
}
