package ruiseki.integratedtunnels.core;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * @author rubensworks
 */
public class PlayerHelpers {

    private static final Map<WorldServer, FakePlayer> FAKE_PLAYERS = new WeakHashMap<>();

    private static final float[] DIRECTION_YAW = new float[] { 0.0F, // DOWN
        0.0F, // UP
        180.0F, // NORTH
        0.0F, // SOUTH
        90.0F, // WEST
        270.0F // EAST
    };

    public static FakePlayer getFakePlayer(WorldServer world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = FakePlayerHelpers.initFakePlayer(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void setPlayerState(EntityPlayer player, BlockPos pos, float offsetX, float offsetY, float offsetZ,
        ForgeDirection side, boolean sneaking) {
        offsetY = side == ForgeDirection.DOWN ? -offsetY : offsetY;
        player.setPosition(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ);
        player.prevPosX = player.posX;
        player.prevPosY = player.posY;
        player.prevPosZ = player.posZ;

        ForgeDirection opposite = side.getOpposite();
        player.rotationYaw = DIRECTION_YAW[opposite.ordinal()];

        player.rotationPitch = side == ForgeDirection.UP ? 90F : (side == ForgeDirection.DOWN ? -90F : 0F);
        player.eyeHeight = 0F;
        player.setSneaking(sneaking);

        setHeldItemSilent(player, null);
        player.onUpdate();
        player.onGround = true;
    }

    public static void setHeldItemSilent(EntityPlayer player, ItemStack itemStack) {
        if (player != null && player.inventory != null) {
            player.inventory.mainInventory[player.inventory.currentItem] = itemStack;
        }
    }

}
