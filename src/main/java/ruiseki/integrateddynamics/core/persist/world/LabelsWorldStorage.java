package ruiseki.integrateddynamics.core.persist.world;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.network.packet.ActionLabelPacket;
import ruiseki.integrateddynamics.core.network.packet.AllLabelsPacket;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.persist.world.WorldStorage;

/**
 * World NBT storage for variable labels.
 * Available client- and serverside and correctly synced.
 *
 * @author rubensworks
 */
public class LabelsWorldStorage extends WorldStorage {

    private static LabelsWorldStorage INSTANCE = null;

    @NBTPersist
    private Map<Integer, String> labels = Maps.newHashMap();

    private LabelsWorldStorage(ModBase mod) {
        super(mod);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    public static LabelsWorldStorage getInstance(ModBase mod) {
        if (INSTANCE == null) {
            INSTANCE = new LabelsWorldStorage(mod);
        }
        return INSTANCE;
    }

    @Override
    public void reset() {
        labels.clear();
    }

    @Override
    protected String getDataId() {
        return "Labels";
    }

    /**
     * Put a onLabelPacket mapping for a variable id getting a onLabelPacket.
     * Should only be called from within packets.
     *
     * @param variableId The variable id.
     * @param label      The onLabelPacket
     */
    public synchronized void putUnsafe(int variableId, @Nonnull String label) {
        Objects.requireNonNull(label);
        labels.put(variableId, label);
    }

    /**
     * Remove a onLabelPacket mapping by variable id.
     * Should only be called from within packets.
     *
     * @param variableId The variable id.
     */
    public synchronized void removeUnsafe(int variableId) {
        labels.remove(variableId);
    }

    /**
     * Put a onLabelPacket mapping for a variable id getting a onLabelPacket.
     *
     * @param variableId The variable id.
     * @param label      The onLabelPacket
     */
    public void put(int variableId, @Nonnull String label) {
        if (MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(new ActionLabelPacket(variableId, label));
        } else {
            putUnsafe(variableId, label);
            IntegratedDynamics._instance.getPacketHandler()
                .sendToAll(new ActionLabelPacket(variableId, label));
        }
    }

    /**
     * Remove a onLabelPacket mapping by variable id.
     *
     * @param variableId The variable id.
     */
    public void remove(int variableId) {
        if (MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToServer(new ActionLabelPacket(variableId, null));
        } else {
            removeUnsafe(variableId);
            IntegratedDynamics._instance.getPacketHandler()
                .sendToAll(new ActionLabelPacket(variableId, null));
        }
    }

    /**
     * Get a onLabelPacket by variable id.
     *
     * @param variableId The variable id.
     * @return The corresponding variable onLabelPacket or null.
     */
    public synchronized String getLabel(int variableId) {
        return labels.get(variableId);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!MinecraftHelpers.isClientSide()) {
            IntegratedDynamics._instance.getPacketHandler()
                .sendToPlayer(new AllLabelsPacket(this.labels), (EntityPlayerMP) event.player);
        }
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        // Fix all null labels (#1038)
        // This should not be able to occur, but it does, no idea why...
        labels.entrySet()
            .removeIf(integerStringEntry -> integerStringEntry.getValue() == null);
    }
}
