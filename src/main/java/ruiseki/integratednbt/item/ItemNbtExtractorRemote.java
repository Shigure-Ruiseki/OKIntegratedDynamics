package ruiseki.integratednbt.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integratednbt.IntegratedNbt;
import ruiseki.integratednbt.Reference;
import ruiseki.integratednbt.block.BlockNbtExtractorConfig;
import ruiseki.integratednbt.network.packet.OpenNbtExtractorRemoteGuiPacket;
import ruiseki.integratednbt.tileentity.TileNbtExtractor;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.ItemBase;

public class ItemNbtExtractorRemote extends ItemBase {

    public ItemNbtExtractorRemote() {
        super();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            this.clientUse(itemStack, player);
        }
        return itemStack;
    }

    @SideOnly(Side.CLIENT)
    private void clientUse(ItemStack itemStack, EntityPlayer player) {
        NBTTagCompound nbt = this.getModNBT(itemStack);
        if (!nbt.hasKey("world")) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.need_bind"));
            return;
        }
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.invalid_bind"));
            return;
        }
        int dimId = nbt.getInteger("world");
        if (world.provider.dimensionId != dimId) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.require_dim"));
            return;
        }
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");

        if (!world.blockExists(x, y, z)) {
            player
                .addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.require_load_client"));
            return;
        }
        if (world.getBlock(x, y, z) != BlockNbtExtractorConfig._instance.getInstance()) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.invalid_bind"));
            return;
        }
        IntegratedNbt._instance.getPacketHandler()
            .sendToServer(new OpenNbtExtractorRemoteGuiPacket());
    }

    public NBTTagCompound getModNBT(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = itemStack.getTagCompound();
        if (!tag.hasKey(Reference.MOD_ID)) {
            tag.setTag(Reference.MOD_ID, new NBTTagCompound());
        }
        return tag.getCompoundTag(Reference.MOD_ID);
    }

    public void serverUse(ItemStack itemStack, EntityPlayerMP player) {
        NBTTagCompound nbt = this.getModNBT(itemStack);
        if (!nbt.hasKey("world")) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.need_bind"));
            return;
        }
        int dimId = nbt.getInteger("world");
        WorldServer world = player.mcServer.worldServerForDimension(dimId);
        if (world == null) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.invalid_bind"));
            return;
        }
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");

        if (!world.blockExists(x, y, z)) {
            player
                .addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.require_load_server"));
            return;
        }
        if (world.getBlock(x, y, z) != BlockNbtExtractorConfig._instance.getInstance()) {
            player.addChatMessage(new ChatComponentTranslation("integratednbt:nbt_extractor_remote.invalid_bind"));
            return;
        }
        playerAccess(world, x, y, z, player);
    }

    public void playerAccess(World world, int x, int y, int z, EntityPlayerMP playerMP) {
        TileNbtExtractor tile = TileHelpers.getSafeTile(world, x, y, z, TileNbtExtractor.class);
        if (tile != null) {
            tile.refreshVariables(true);
            playerMP.openGui(IntegratedNbt._instance, 0, world, x, y, z);
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (player == null) {
            return false;
        }
        if (world.getBlock(x, y, z) == BlockNbtExtractorConfig._instance.getInstance()) {
            if (!world.isRemote) {
                ((ItemNbtExtractorRemote) ItemNbtExtractorRemoteConfig._instance.getInstance())
                    .bindBlock(stack, world, x, y, z);
                player.addChatMessage(
                    new ChatComponentTranslation(
                        "integratednbt:nbt_extractor_remote.bind_successful",
                        String.valueOf(x),
                        String.valueOf(y),
                        String.valueOf(z)));
            }
        } else if (world.isRemote) {
            this.clientUse(stack, player);
        }
        return true;
    }

    public void bindBlock(ItemStack itemStack, World world, int x, int y, int z) {
        NBTTagCompound nbt = this.getModNBT(itemStack);
        nbt.setInteger("world", world.provider.dimensionId);
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("z", z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(itemStack, player, list, par4);
        NBTTagCompound nbt = this.getModNBT(itemStack);
        if (nbt.hasKey("world")) {
            list.add(
                EnumChatFormatting.GREEN + StatCollector.translateToLocalFormatted(
                    "integratednbt:nbt_extractor_remote.tooltip.bound",
                    String.valueOf(nbt.getInteger("x")),
                    String.valueOf(nbt.getInteger("y")),
                    String.valueOf(nbt.getInteger("z")),
                    String.valueOf(nbt.getInteger("world"))));
        } else {
            list.add(StatCollector.translateToLocal("integratednbt:nbt_extractor_remote.tooltip.not_bound"));
        }
        list.add(StatCollector.translateToLocal("integratednbt:nbt_extractor_remote.tooltip"));
    }
}
