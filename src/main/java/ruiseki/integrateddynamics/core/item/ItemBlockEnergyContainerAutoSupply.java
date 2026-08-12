package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class ItemBlockEnergyContainerAutoSupply extends ItemBlockEnergyContainer {

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockEnergyContainerAutoSupply(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        LangHelpers.addStatusInfo(list, isActivated(itemStack), getUnlocalizedName() + ".info.auto_supply");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player) {
        return toggleActivation(itemStackIn, world, player);
    }

    public static void autofill(IEnergyStorage source, World world, Entity entity) {
        if (entity instanceof EntityPlayer && !world.isRemote) {
            int tickAmount = source.extractEnergy(Integer.MAX_VALUE, true);
            if (tickAmount > 0) {
                EntityPlayer player = (EntityPlayer) entity;
                ItemStack held = player.getHeldItem();
                ItemStack filled = tryFillContainerForPlayer(source, held, tickAmount, player);
                if (filled != null) {
                    player.inventory.mainInventory[player.inventory.currentItem] = filled;
                }
            }
        }
    }

    public static ItemStack tryFillContainerForPlayer(IEnergyStorage source, ItemStack held, int tickAmount,
        EntityPlayer player) {
        if (CapabilityHelpers.getCapability(held, CapabilityEnergy.ENERGY)
            .isPresent()) {
            IEnergyStorage target = CapabilityHelpers.getCapability(held, CapabilityEnergy.ENERGY)
                .getOrNull();
            if (target != null) {
                int moved = target
                    .receiveEnergy(source.extractEnergy(target.receiveEnergy(tickAmount, true), false), false);
                if (moved > 0) {
                    return held;
                }
            }
        }
        return null;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean par5) {
        if (isActivated(itemStack) && CapabilityHelpers.getCapability(itemStack, CapabilityEnergy.ENERGY)
            .isPresent()) {
            autofill(
                CapabilityHelpers.getCapability(itemStack, CapabilityEnergy.ENERGY)
                    .getOrNull(),
                world,
                entity);
        }
        super.onUpdate(itemStack, world, entity, itemSlot, par5);
    }

    public ItemStack toggleActivation(ItemStack itemStack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            if (!world.isRemote) {
                ItemStack activated = itemStack.copy();
                activated.setItemDamage(1 - activated.getItemDamage());
                return activated;
            }
            return itemStack;
        }
        return itemStack;
    }

    public boolean isActivated(ItemStack itemStack) {
        return itemStack.getItemDamage() == 1;
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return isActivated(itemStack);
    }
}
