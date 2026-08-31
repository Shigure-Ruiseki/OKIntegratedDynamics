package ruiseki.integrateddynamics.part;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.world.IBlockAccess;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.block.IgnoredBlock;
import ruiseki.integrateddynamics.core.part.PartStateEmpty;
import ruiseki.integrateddynamics.core.part.panel.PartTypePanel;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * A panel part that simply emits light.
 *
 * @author rubensworks
 */
public class PartTypePanelLightStatic
    extends PartTypePanel<PartTypePanelLightStatic, PartStateEmpty<PartTypePanelLightStatic>> {

    public static final int LIGHT_LEVEL = 15;

    public PartTypePanelLightStatic(String name) {
        super(name);
    }

    @Override
    public boolean supportsOffsets() {
        return false;
    }

    @Override
    protected Block createBlock() {
        return new IgnoredBlock();
    }

    @Override
    public Class<? super PartTypePanelLightStatic> getPartTypeClass() {
        return PartTypePanelLightStatic.class;
    }

    @Override
    public PartStateEmpty<PartTypePanelLightStatic> constructDefaultState() {
        return new PartStateEmpty<PartTypePanelLightStatic>();
    }

    @Override
    public boolean isUpdate(PartStateEmpty<PartTypePanelLightStatic> state) {
        return getConsumptionRate(state) > 0 || super.isUpdate(state);
    }

    @Override
    public int getConsumptionRate(PartStateEmpty<PartTypePanelLightStatic> state) {
        return GeneralConfig.panelLightStaticBaseConsumption;
    }

    @Override
    protected boolean hasGui() {
        return false;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return null;
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target,
        PartStateEmpty<PartTypePanelLightStatic> state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        PartTypePanelLightDynamic.setLightLevel(target, LIGHT_LEVEL);
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target,
        PartStateEmpty<PartTypePanelLightStatic> state, IBlockAccess world, Block neighbourBlock,
        BlockPos neighbourPos) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighbourBlock, neighbourPos);
        PartTypePanelLightDynamic.setLightLevel(target, LIGHT_LEVEL);
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target,
        PartStateEmpty<PartTypePanelLightStatic> state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        PartTypePanelLightDynamic.setLightLevel(target, 0);
    }

    @Override
    public void onPostRemoved(INetwork network, IPartNetwork partNetwork, PartTarget target,
        PartStateEmpty<PartTypePanelLightStatic> state) {
        super.onPostRemoved(network, partNetwork, target, state);
        PartTypePanelLightDynamic.setLightLevel(target, 0);
    }

    @Override
    public void postUpdate(INetwork network, IPartNetwork partNetwork, PartTarget target,
        PartStateEmpty<PartTypePanelLightStatic> state, boolean updated) {
        boolean wasEnabled = isEnabled(state);
        super.postUpdate(network, partNetwork, target, state, updated);
        boolean isEnabled = isEnabled(state);
        if (wasEnabled != isEnabled) {
            PartTypePanelLightDynamic.setLightLevel(target, isEnabled ? LIGHT_LEVEL : 0);
        }
    }
}
