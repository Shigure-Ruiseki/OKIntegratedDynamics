package ruiseki.integrateddynamics.core.network.diagnostics;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.integrateddynamics.network.packet.NetworkDiagnosticsNetworkPacket;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class NetworkDiagnostics {

    private static final NetworkDiagnostics _INSTANCE = new NetworkDiagnostics();

    private final List<UUID> players = Lists.newArrayList();

    private NetworkDiagnostics() {

    }

    public static NetworkDiagnostics getInstance() {
        return _INSTANCE;
    }

    protected EntityPlayerMP getPlayerByName(String username) {
        if (username == null || username.isEmpty()) return null;

        return FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getConfigurationManager()
            .func_152612_a(username);
    }

    protected EntityPlayerMP getPlayerByUUID(UUID uuid) {
        if (uuid == null) return null;

        List<EntityPlayerMP> players = FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getConfigurationManager().playerEntityList;

        for (EntityPlayerMP player : players) {
            if (uuid.equals(player.getPersistentID())) {
                return player;
            }
        }
        return null;
    }

    public synchronized void registerPlayer(EntityPlayerMP player) {
        if (!players.contains(player.getPersistentID())) {
            players.add(player.getPersistentID());
            for (INetwork<?> network : NetworkWorldStorage.getInstance(IntegratedDynamics._instance)
                .getNetworks()) {
                sendNetworkUpdateToPlayer(player, network);
            }
        }
    }

    public synchronized void unRegisterPlayer(EntityPlayerMP player) {
        players.remove(player.getPersistentID());
    }

    public void sendNetworkUpdateToPlayer(EntityPlayerMP player, INetwork<?> network) {
        List<RawPartData> rawParts = Lists.newArrayList();
        for (INetworkElement networkElement : network.getElements()) {
            if (networkElement instanceof IPartNetworkElement) {
                IPartNetworkElement partNetworkElement = (IPartNetworkElement) networkElement;
                PartPos pos = partNetworkElement.getTarget()
                    .getCenter();
                long lastSecondDuration = network.getLastSecondDuration(networkElement);
                rawParts.add(
                    new RawPartData(
                        pos.getPos()
                            .getWorld().provider.dimensionId,
                        pos.getPos()
                            .getBlockPos(),
                        pos.getSide(),
                        LangHelpers.localize(
                            partNetworkElement.getPart()
                                .getUnlocalizedName()),
                        lastSecondDuration));
            } else {
                // If needed, we can send the other part types later on as well
            }
        }
        RawNetworkData rawNetworkData = new RawNetworkData(
            network.isKilled(),
            network.hashCode(),
            network.getCablesCount(),
            rawParts);
        IntegratedDynamics._instance.getPacketHandler()
            .sendToPlayer(new NetworkDiagnosticsNetworkPacket(rawNetworkData.toNbt()), player);
    }

    public synchronized void sendNetworkUpdate(INetwork network) {
        for (Iterator<UUID> it = players.iterator(); it.hasNext();) {
            UUID uuid = it.next();
            EntityPlayerMP player = getPlayerByUUID(uuid);
            if (player != null) {
                sendNetworkUpdateToPlayer(player, network);
            } else {
                it.remove();
            }
        }
    }

    public synchronized boolean isBeingDiagnozed() {
        return !players.isEmpty();
    }

}
