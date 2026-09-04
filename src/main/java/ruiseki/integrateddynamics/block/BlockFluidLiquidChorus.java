package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.fluid.FluidLiquidChorusConfig;
import ruiseki.okcore.fluid.BlockFluidBase;
import ruiseki.okcore.helper.MinecraftHelpers;

public class BlockFluidLiquidChorus extends BlockFluidBase {

    public BlockFluidLiquidChorus() {
        super(FluidLiquidChorusConfig._instance.getInstance(), Material.water);

        if (MinecraftHelpers.isClientSide()) this.setParticleColor(0.694117647F, 0.505882353F, 0.694117647F);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, x, y, z, entityIn);

        if (!worldIn.isRemote && entityIn instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entityIn;

            if (worldIn.rand.nextInt(20) == 0) {
                teleportEntity(worldIn, entityLiving);
            }
        }
    }

    private void teleportEntity(World world, EntityLivingBase entity) {
        double d0 = entity.posX;
        double d1 = entity.posY;
        double d2 = entity.posZ;

        for (int i = 0; i < 16; ++i) {
            double targetX = entity.posX + (entity.getRNG()
                .nextDouble() - 0.5D) * 16.0D;
            double targetY = Math.min(
                Math.max(
                    entity.posY + (double) (entity.getRNG()
                        .nextInt(16) - 8),
                    0.0D),
                (double) (world.getActualHeight() - 1));
            double targetZ = entity.posZ + (entity.getRNG()
                .nextDouble() - 0.5D) * 16.0D;

            if (entity.ridingEntity != null) {
                entity.mountEntity((Entity) null);
            }

            entity.setPositionAndUpdate(targetX, targetY, targetZ);

            if (world.checkNoEntityCollision(entity.boundingBox)
                && world.getCollidingBoundingBoxes(entity, entity.boundingBox)
                    .isEmpty()
                && !world.isAnyLiquid(entity.boundingBox)) {
                world.playSoundEffect(d0, d1, d2, "mob.endermen.portal", 1.0F, 1.0F);
                entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
                break;
            } else {
                entity.setPositionAndUpdate(d0, d1, d2);
            }
        }
    }
}
