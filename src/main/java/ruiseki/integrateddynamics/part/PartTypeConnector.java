package ruiseki.integrateddynamics.part;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.core.block.IgnoredBlockStatus;
import ruiseki.integrateddynamics.core.part.PartStateBase;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * A base wireless connector part.
 *
 * @author rubensworks
 */
public abstract class PartTypeConnector<P extends PartTypeConnector<P, S>, S extends PartTypeConnector.State<P>>
    extends PartTypeBase<P, S> {

    public PartTypeConnector(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    @Override
    public boolean supportsOffsets() {
        return false;
    }

    @Override
    protected Block createBlock() {
        return new IgnoredBlockStatus();
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
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        state.setPosition(target.getCenter());
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        state.setPosition(target.getCenter());
    }

    public static abstract class State<P extends PartTypeConnector> extends PartStateBase<P> implements IPathElement {

        private PartPos partPos;

        protected PartPos getPartPos() {
            return partPos;
        }

        @Override
        public DimPos getPosition() {
            return this.partPos == null ? null : this.partPos.getPos();
        }

        public void setPosition(PartPos partPos) {
            this.partPos = partPos;
        }

        @Override
        public int compareTo(IPathElement o) {
            return getPosition().compareTo(o.getPosition());
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, INetwork network, IPartNetwork partNetwork,
            PartTarget target) {
            if (capability == PathElementConfig.CAPABILITY) {
                return LazyOptional.of(() -> this)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }
}
