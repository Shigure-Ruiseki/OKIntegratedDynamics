package ruiseki.integratedtunnels.part.aspect;

import java.util.List;

import net.minecraft.entity.Entity;

/**
 * @author rubensworks
 */
public class TunnelTransferEntities extends TunnelTransferComposite {

    public TunnelTransferEntities(List<? extends Entity> entities) {
        super(
            entities.stream()
                .map(TunnelTransferEntity::new)
                .toArray(TunnelTransferEntity[]::new));
    }
}
