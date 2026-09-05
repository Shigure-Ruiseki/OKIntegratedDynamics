package ruiseki.integrateddynamics.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.api.capability.wrench.DefaultWrench;
import ruiseki.commoncapabilities.capability.wrench.WrenchConfig;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.item.ItemBase;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * The default wrench for this mod.
 *
 * @author rubensworks
 */
public class ItemWrench extends ItemBase {

    private static final Map<String, Mode> NAMED_MODES = new HashMap<String, Mode>();

    public ItemWrench() {
        super();
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new DefaultCapabilityProvider<>(() -> WrenchConfig.CAPABILITY, new DefaultWrench());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (player.isSneaking() && !world.isRemote) {
            incrementMode(itemStack);
            player.addChatMessage(
                new ChatComponentTranslation(
                    "item.items.integrateddynamics.wrench.mode",
                    new ChatComponentTranslation(getMode(itemStack).getLabel())));
            return itemStack;
        }
        return super.onItemRightClick(itemStack, world, player);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (player != null && player.isSneaking()) {
            switch (getMode(stack)) {
                case OFFSET: {
                    NBTTagCompound tag = getOrCreateTag(stack);
                    tag.setInteger("posX", x);
                    tag.setInteger("posY", y);
                    tag.setInteger("posZ", z);
                    if (!world.isRemote) {
                        player.addChatMessage(
                            new ChatComponentTranslation(
                                "item.items.integrateddynamics.wrench.mode.offset.saved",
                                x + ", " + y + ", " + z));
                    }
                    return true;
                }
                case OFFSET_SIDE: {
                    NBTTagCompound tag = getOrCreateTag(stack);
                    tag.setInteger("posX", x);
                    tag.setInteger("posY", y);
                    tag.setInteger("posZ", z);
                    tag.setInteger("side", side);
                    if (!world.isRemote) {
                        player.addChatMessage(
                            new ChatComponentTranslation(
                                "item.items.integrateddynamics.wrench.mode.offset_side.saved",
                                x + ", " + y + ", " + z,
                                ForgeDirection.getOrientation(side)
                                    .name()));
                    }
                    return true;
                }
                case DEFAULT: {
                    // Xử lý kiểm tra Cable hoặc bỏ qua trên client
                    break;
                }
            }
        } else {
            Mode mode = getMode(stack);
            if (mode == Mode.DEFAULT) {
                Block block = world.getBlock(x, y, z);
                if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
                    player.swingItem();
                    return true;
                }
            }
        }

        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    public Mode getMode(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            Mode mode = NAMED_MODES.get(
                itemStack.getTagCompound()
                    .getString("mode"));
            if (mode != null) {
                return mode;
            }
        }
        return Mode.DEFAULT;
    }

    public void setMode(ItemStack itemStack, Mode mode) {
        getOrCreateTag(itemStack).setString("mode", mode.getName());
    }

    public void incrementMode(ItemStack itemStack) {
        Mode mode = getMode(itemStack);
        int modeId = mode.ordinal();
        Mode nextMode = Mode.values()[(modeId + 1) % Mode.values().length];
        setMode(itemStack, nextMode);

        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            tag.removeTag("posX");
            tag.removeTag("posY");
            tag.removeTag("posZ");
            tag.removeTag("side");
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(itemStack, player, list, par4);

        Mode mode = getMode(itemStack);
        list.add(
            new ChatComponentTranslation(
                "item.items.integrateddynamics.wrench.mode",
                new ChatComponentTranslation(mode.getLabel())).getUnformattedText());

        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag.hasKey("posX")) {
                String posStr = tag.getInteger("posX") + ", " + tag.getInteger("posY") + ", " + tag.getInteger("posZ");
                list.add(
                    EnumChatFormatting.GRAY
                        + new ChatComponentTranslation("item.items.integrateddynamics.wrench.mode.offset.pos", posStr)
                            .getUnformattedText());
            }
            if (tag.hasKey("side")) {
                list.add(
                    EnumChatFormatting.GRAY + new ChatComponentTranslation(
                        "item.items.integrateddynamics.wrench.mode.offset_side.side",
                        ForgeDirection.getOrientation(tag.getInteger("side"))
                            .name()).getUnformattedText());
            }
        }
        list.add(
            EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC
                + new ChatComponentTranslation(mode.getLabel() + ".info").getUnformattedText());
    }

    private NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public enum Mode {

        DEFAULT("integrateddynamics:default", "item.items.integrateddynamics.wrench.mode.default.name"),
        OFFSET("integrateddynamics:offset", "item.items.integrateddynamics.wrench.mode.offset.name"),
        OFFSET_SIDE("integrateddynamics:offset_side", "item.items.integrateddynamics.wrench.mode.offset_side.name");

        private final String name;
        private final String label;

        Mode(String name, String label) {
            this.name = name;
            this.label = label;
            NAMED_MODES.put(name, this);
        }

        public String getName() {
            return name;
        }

        public String getLabel() {
            return label;
        }
    }
}
