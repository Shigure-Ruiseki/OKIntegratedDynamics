package ruiseki.integrateddynamics.core.part;

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
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * An abstract {@link IPartType} that can have settings.
 *
 * @author rubensworks
 */
public abstract class PartTypeConfigurable<P extends IPartType<P, S>, S extends IPartState<P>>
    extends PartTypeBase<P, S> {

    public PartTypeConfigurable(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    @Override
    public Optional<IGuiConstructor> getContainerProviderSettings(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerPartSettings(
                    playerInventory,
                    new SimpleInventory(0),
                    data.getRight(),
                    Optional.of(data.getLeft()),
                    data.getMiddle());
            }
        });
    }

    @Override
    public void writeExtraGuiDataSettings(ExtendedBuffer packetBuffer, PartPos pos, EntityPlayerMP player) {
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

    @Override
    public Optional<IGuiConstructor> getContainerProviderOffsets(PartPos pos) {
        return Optional.of(new IGuiConstructor() {

            @Override
            public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
                EntityPlayer player) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers
                    .getContainerPartConstructionData(pos);
                return new ContainerPartSettings(
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
