package ruiseki.integratedtunnels.core.part;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartOffset;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Base part for a tunnel.
 *
 * @author rubensworks
 */
public abstract class PartTypeTunnel<P extends IPartType<P, S>, S extends IPartState<P>> extends PartTypeBase<P, S> {

    public PartTypeTunnel(String name) {
        super(name, new PartRenderPosition(0.25F, 0.25F, 0.375F, 0.375F));
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartType.class;
    }

    @Override
    public ModBase getMod() {
        return IntegratedTunnels._instance;
    }

    @Override
    public Optional<IGuiConstructor> getContainerProviderSettings(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerInterfaceSettings(
                    playerInventory,
                    new SimpleInventory(0),
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    data.getMiddle());
            }
        });
    }

    @Override
    public Optional<IGuiConstructor> getContainerProviderOffsets(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerPartOffset(
                    playerInventory,
                    new SimpleInventory(0),
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    data.getMiddle());
            }
        });
    }

    @Override
    public void writeExtraGuiDataOffsets(ExtendedBuffer packetBuffer, PartPos pos, EntityPlayerMP player) {
        try {
            PacketCodec.getAction(PartPos.class)
                .encode(pos, packetBuffer);
            packetBuffer.writeString(
                this.getUniqueName()
                    .toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
