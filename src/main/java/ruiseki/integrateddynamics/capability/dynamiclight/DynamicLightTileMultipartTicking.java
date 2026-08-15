package ruiseki.integrateddynamics.capability.dynamiclight;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.IDynamicLight;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.EnumFacingMap;

/**
 * Default implementation of {@link IDynamicLight}.
 *
 * @author rubensworks
 */
public class DynamicLightTileMultipartTicking implements IDynamicLight {

    private final TileMultipartTicking tile;
    private final ForgeDirection side;

    public DynamicLightTileMultipartTicking(TileMultipartTicking tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    protected EnumFacingMap<Integer> getLightLevels() {
        return tile.getLightLevels();
    }

    @Override
    public void setLightLevel(int level) {
        if (!tile.getWorldObj().isRemote) {
            boolean sendUpdate = false;
            EnumFacingMap<Integer> lightLevels = getLightLevels();
            if (lightLevels.containsKey(side)) {
                if (lightLevels.get(side) != level) {
                    sendUpdate = true;
                    lightLevels.put(side, level);
                }
            } else {
                sendUpdate = true;
                lightLevels.put(side, level);
            }
            if (sendUpdate) {
                tile.updateLightInfo();
            }
        }
    }

    @Override
    public int getLightLevel() {
        EnumFacingMap<Integer> lightLevels = getLightLevels();
        if (lightLevels.containsKey(side)) {
            return lightLevels.get(side);
        }
        return 0;
    }
}
