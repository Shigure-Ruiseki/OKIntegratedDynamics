package ruiseki.integratedtunnels.core.part;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.okcore.init.ModBase;

/**
 * Base part for a tunnels with aspects.
 * 
 * @author rubensworks
 */
public abstract class PartTypeTunnelAspects<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>>
    extends PartTypeWriteBase<P, S> {

    public PartTypeTunnelAspects(String name) {
        this(name, new PartRenderPosition(0.25F, 0.25F, 0.375F, 0.375F));
    }

    protected PartTypeTunnelAspects(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    @Override
    public ModBase getMod() {
        return IntegratedTunnels._instance;
    }

    @Override
    public ModBase getModGui() {
        return IntegratedDynamics._instance;
    }

}
