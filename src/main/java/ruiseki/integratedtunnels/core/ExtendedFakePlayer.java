package ruiseki.integratedtunnels.core;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

import ruiseki.okcore.helper.ItemHelpers;

/**
 * An extended fake player with more capabilities.
 *
 * @author rubensworks
 */
public class ExtendedFakePlayer extends FakePlayer {

    private static final GameProfile PROFILE = new GameProfile(
        UUID.fromString("41C82C87-7AfB-4024-BB57-13D2C99CAE77"),
        "[IntegratedTunnels]");

    private long lastUpdateTick = 0;
    private int ticksSinceLastTick = 0;

    public ExtendedFakePlayer(WorldServer world) {
        super(world, PROFILE);
        this.theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);
        this.playerNetServerHandler = new FakeNetHandlerPlayServer(world.func_73046_m(), this);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        int toTick = (int) (this.worldObj.getTotalWorldTime() - this.lastUpdateTick);
        if (toTick > 0) {
            this.ticksSinceLastTick = toTick;
        }
        this.lastUpdateTick = this.worldObj.getTotalWorldTime();

        this.inventory.decrementAnimations();
    }

    public void updateActiveHandSimulated() {
        if (this.isUsingItem()) {
            for (int i = 0; i < this.ticksSinceLastTick; i++) {
                if (this.isUsingItem()) {
                    ItemStack itemstack = this.getHeldItem();
                    ItemStack itemInUse = this.getItemInUse();

                    // Modern "canContinueUsing" check equivalence in 1.7.10
                    if (!ItemHelpers.isEmpty(itemInUse) && !ItemHelpers.isEmpty(itemstack)
                        && itemInUse.getItem() == itemstack.getItem()) {

                        // Equivalent to LivingEntity#updateItemInUse in 1.7.10
                        int itemInUseCount = this.getItemInUseCount();
                        itemstack.getItem()
                            .onUsingTick(itemstack, this, itemInUseCount);

                        if (itemInUseCount <= 25 && itemInUseCount % 4 == 0) {
                            // Sound/particle effects on item use in 1.7.10
                            this.worldObj.playSoundAtEntity(
                                this,
                                "random.eat",
                                0.5F + 0.5F * (float) this.rand.nextInt(2),
                                (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        }

                        // Decrement item in use counter (equivalent to --activeItemStackUseCount)
                        itemInUseCount--;
                        setItemInUseCount(itemInUseCount);

                        if (itemInUseCount <= 0 && !this.worldObj.isRemote) {
                            this.onItemUseFinish();
                            break;
                        }
                    } else {
                        this.clearItemInUse();
                        break;
                    }
                }
            }
        } else {
            this.clearItemInUse();
        }
    }

    // Helper setter for itemInUseCount via reflection or access transformer if needed
    private void setItemInUseCount(int count) {
        try {
            java.lang.reflect.Field field = EntityPlayerMP.class.getSuperclass()
                .getDeclaredField("itemInUseCount");
            field.setAccessible(true);
            field.setInt(this, count);
        } catch (Exception ignored) {}
    }
}
