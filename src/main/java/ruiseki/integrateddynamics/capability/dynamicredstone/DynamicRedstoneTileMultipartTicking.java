package ruiseki.integrateddynamics.capability.dynamicredstone;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.EnumFacingMap;

/**
 * Default implementation of {@link IDynamicRedstone}.
 *
 * @author rubensworks
 */
public class DynamicRedstoneTileMultipartTicking implements IDynamicRedstone {

    private final TileMultipartTicking tile;
    private final ForgeDirection side;

    public DynamicRedstoneTileMultipartTicking(TileMultipartTicking tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    protected EnumFacingMap<Integer> getRedstoneLevels() {
        return tile.getRedstoneLevels();
    }

    protected EnumFacingMap<Boolean> getRedstoneInputs() {
        return tile.getRedstoneInputs();
    }

    protected EnumFacingMap<Boolean> getRedstoneStrong() {
        return tile.getRedstoneStrong();
    }

    @Override
    public void setRedstoneLevel(int level, boolean strongPower) {
        if (!tile.getWorldObj().isRemote) {
            EnumFacingMap<Integer> redstoneLevels = getRedstoneLevels();
            EnumFacingMap<Boolean> redstoneStrongs = getRedstoneStrong();
            boolean sendUpdate = false;
            if (redstoneLevels.containsKey(side)) {
                if (redstoneLevels.get(side) != level) {
                    sendUpdate = true;
                    redstoneLevels.put(side, level);
                }
            } else {
                sendUpdate = true;
                redstoneLevels.put(side, level);
            }
            if (redstoneStrongs.containsKey(side)) {
                if (redstoneStrongs.get(side) != strongPower) {
                    sendUpdate = true;
                    redstoneStrongs.put(side, strongPower);
                }
            } else {
                sendUpdate = true;
                redstoneStrongs.put(side, strongPower);
            }
            if (sendUpdate) {
                tile.updateRedstoneInfo(side, strongPower);
            }
        }
    }

    @Override
    public int getRedstoneLevel() {
        EnumFacingMap<Integer> redstoneLevels = getRedstoneLevels();
        if (redstoneLevels.containsKey(side)) {
            return redstoneLevels.get(side);
        }
        return -1;
    }

    @Override
    public boolean isStrong() {
        EnumFacingMap<Boolean> redstoneStrongs = getRedstoneStrong();
        if (redstoneStrongs.containsKey(side)) {
            return redstoneStrongs.get(side);
        }
        return false;
    }

    @Override
    public void setAllowRedstoneInput(boolean allow) {
        EnumFacingMap<Boolean> redstoneInputs = getRedstoneInputs();
        redstoneInputs.put(side, allow);
    }

    @Override
    public boolean isAllowRedstoneInput() {
        EnumFacingMap<Boolean> redstoneInputs = getRedstoneInputs();
        if (redstoneInputs.containsKey(side)) {
            return redstoneInputs.get(side);
        }
        return false;
    }
}
