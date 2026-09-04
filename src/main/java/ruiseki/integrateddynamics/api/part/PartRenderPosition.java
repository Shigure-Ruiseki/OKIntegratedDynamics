package ruiseki.integrateddynamics.api.part;

import java.util.Arrays;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.client.model.CableModel;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.helper.MatrixHelpers;

public class PartRenderPosition {

    public static final PartRenderPosition NONE = new PartRenderPosition(-1, -1, -1, -1);

    private final float depthFactor;
    private final float widthFactor;
    private final float heightFactor;
    private final float widthFactorSide;
    private final float heightFactorSide;
    private final EnumFacingMap<ImmutableAxisAlignedBB> sidedCableCollisionBoxes;
    private final EnumFacingMap<ImmutableAxisAlignedBB> collisionBoxes;

    public PartRenderPosition(float selectionDepthFactor, float depthFactor, float widthFactor, float heightFactor) {
        this(selectionDepthFactor, depthFactor, widthFactor, heightFactor, widthFactor, heightFactor);
    }

    public PartRenderPosition(float selectionDepthFactor, float depthFactor, float widthFactor, float heightFactor,
        float widthFactorSide, float heightFactorSide) {
        this.depthFactor = depthFactor;
        this.widthFactor = widthFactor;
        this.heightFactor = heightFactor;
        this.widthFactorSide = widthFactorSide;
        this.heightFactorSide = heightFactorSide;
        float[][] sidedCableCollisionBoxesRaw = new float[][] {
            { CableModel.MIN, selectionDepthFactor, CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX }, // DOWN
            { CableModel.MIN, CableModel.MAX, CableModel.MIN, CableModel.MAX, 1 - selectionDepthFactor,
                CableModel.MAX }, // UP
            { CableModel.MIN, CableModel.MIN, selectionDepthFactor, CableModel.MAX, CableModel.MAX, CableModel.MIN }, // NORTH
            { CableModel.MIN, CableModel.MAX, CableModel.MAX, CableModel.MAX, CableModel.MIN,
                1 - selectionDepthFactor }, // SOUTH
            { selectionDepthFactor, CableModel.MIN, CableModel.MIN, CableModel.MIN, CableModel.MAX, CableModel.MAX }, // WEST
            { CableModel.MAX, CableModel.MIN, CableModel.MIN, 1 - selectionDepthFactor, CableModel.MAX,
                CableModel.MAX }, // EAST
        };

        sidedCableCollisionBoxes = EnumFacingMap.newMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            float[] b = sidedCableCollisionBoxesRaw[side.ordinal()];
            sidedCableCollisionBoxes.put(side, ImmutableAxisAlignedBB.fromBounds(b[0], b[1], b[2], b[3], b[4], b[5]));
        }

        float min = (1 - widthFactor) / 2 + 0.0025F;
        float max = (1 - widthFactor) / 2 + widthFactor - 0.0025F;
        float[][] collisionBoxesRaw = new float[][] { { min, max }, { 0.005F, selectionDepthFactor }, { min, max } };
        collisionBoxes = EnumFacingMap.newMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            // Copy bounds
            float[][] bounds = new float[collisionBoxesRaw.length][collisionBoxesRaw[0].length];
            for (int i = 0; i < bounds.length; i++)
                bounds[i] = Arrays.copyOf(collisionBoxesRaw[i], collisionBoxesRaw[i].length);

            // Transform bounds
            MatrixHelpers.transform(bounds, side);
            collisionBoxes.put(
                side,
                ImmutableAxisAlignedBB
                    .fromBounds(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]));
        }
    }

    public float getDepthFactor() {
        return depthFactor;
    }

    public float getWidthFactor() {
        return widthFactor;
    }

    public float getHeightFactor() {
        return heightFactor;
    }

    public float getWidthFactorSide() {
        return widthFactorSide;
    }

    public float getHeightFactorSide() {
        return heightFactorSide;
    }

    public ImmutableAxisAlignedBB getSidedCableBoundingBox(ForgeDirection side) {
        return sidedCableCollisionBoxes.get(side);
    }

    public ImmutableAxisAlignedBB getBoundingBox(ForgeDirection side) {
        return collisionBoxes.get(side);
    }

    @Override
    public String toString() {
        return "PartRenderPosition{" + "depthFactor="
            + depthFactor
            + ", widthFactor="
            + widthFactor
            + ", heightFactor="
            + heightFactor
            + ", widthFactorSide="
            + widthFactorSide
            + ", heightFactorSide="
            + heightFactorSide
            + '}';
    }

    public String toCompactString() {
        return "df=" + depthFactor
            + ",wf="
            + widthFactor
            + ",hf="
            + heightFactor
            + ",wfs="
            + widthFactorSide
            + ",hfs="
            + heightFactorSide;
    }
}
