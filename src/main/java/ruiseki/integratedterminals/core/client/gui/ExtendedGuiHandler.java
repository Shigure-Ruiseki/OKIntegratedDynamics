package ruiseki.integratedterminals.core.client.gui;

import static ruiseki.integrateddynamics.core.client.gui.ExtendedGuiHandler.getPartConstructionData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
import ruiseki.integratedterminals.inventory.container.ContainerTerminalStorageBase;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * An extension of the default cyclops gui handler with support for some more gui types.
 *
 * @author rubensworks
 */
public class ExtendedGuiHandler extends GuiHandler {

    /**
     * Gui type for guis for selecting crafting options.
     */
    public static final GuiType<Pair<ForgeDirection, CraftingOptionGuiData<?, ?, ?>>> CRAFTING_OPTION = GuiType
        .create(true);
    /**
     * Gui type for storage terminals with a preselected tab and channel.
     */
    public static final GuiType<Pair<ForgeDirection, ContainerTerminalStorageBase.InitTabData>> TERMINAL_STORAGE = GuiType
        .create(true);
    /**
     * Gui type for guis for selecting crafting options.
     */
    public static final GuiType<Pair<ForgeDirection, CraftingJobGuiData>> CRAFTING_PLAN = GuiType.create(true);

    static {
        CRAFTING_OPTION.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
            try {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                    world,
                    new BlockPos(x, y, z),
                    dataIn.getLeft());
                if (data == null) return null;
                Constructor<? extends Container> containerConstructor;
                try {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        data.getMiddle()
                            .getClass(),
                        CraftingOptionGuiData.class);
                } catch (NoSuchMethodException e) {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        IPartType.class,
                        CraftingOptionGuiData.class);
                }
                return containerConstructor
                    .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), dataIn.getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (MinecraftHelpers.isClientSide()) {
            CRAFTING_OPTION.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
                try {
                    Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                        world,
                        new BlockPos(x, y, z),
                        dataIn.getLeft());
                    if (data == null) return null;
                    Constructor<? extends GuiScreen> guiConstructor;
                    try {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            data.getMiddle()
                                .getClass(),
                            CraftingOptionGuiData.class);
                    } catch (NoSuchMethodException e) {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class,
                            CraftingOptionGuiData.class);
                    }
                    return guiConstructor
                        .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), dataIn.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }

        TERMINAL_STORAGE.setContainerConstructor((id, player, world, x, y, z, containerClass, in) -> {
            try {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                    world,
                    new BlockPos(x, y, z),
                    in.getLeft());
                if (data == null) return null;
                Constructor<? extends Container> containerConstructor;
                try {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        data.getMiddle()
                            .getClass(),
                        ContainerTerminalStorageBase.InitTabData.class);
                } catch (NoSuchMethodException e) {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        IPartType.class,
                        ContainerTerminalStorageBase.InitTabData.class);
                }
                return containerConstructor
                    .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), in.getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (MinecraftHelpers.isClientSide()) {
            TERMINAL_STORAGE.setGuiConstructor((id, player, world, x, y, z, guiClass, in) -> {
                try {
                    Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                        world,
                        new BlockPos(x, y, z),
                        in.getLeft());
                    if (data == null) return null;
                    Constructor<? extends GuiScreen> guiConstructor;
                    try {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            data.getMiddle()
                                .getClass(),
                            ContainerTerminalStorageBase.InitTabData.class);
                    } catch (NoSuchMethodException e) {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class,
                            ContainerTerminalStorageBase.InitTabData.class);
                    }
                    return guiConstructor
                        .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), in.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }

        CRAFTING_PLAN.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
            try {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                    world,
                    new BlockPos(x, y, z),
                    dataIn.getLeft());
                if (data == null) return null;
                Constructor<? extends Container> containerConstructor;
                try {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        data.getMiddle()
                            .getClass(),
                        CraftingJobGuiData.class);
                } catch (NoSuchMethodException e) {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        IPartType.class,
                        CraftingJobGuiData.class);
                }
                return containerConstructor
                    .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), dataIn.getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (MinecraftHelpers.isClientSide()) {
            CRAFTING_PLAN.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
                try {
                    Triple<IPartContainer, PartTypeBase, PartTarget> data = getPartConstructionData(
                        world,
                        new BlockPos(x, y, z),
                        dataIn.getLeft());
                    if (data == null) return null;
                    Constructor<? extends GuiScreen> guiConstructor;
                    try {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            data.getMiddle()
                                .getClass(),
                            CraftingJobGuiData.class);
                    } catch (NoSuchMethodException e) {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class,
                            CraftingJobGuiData.class);
                    }
                    return guiConstructor
                        .newInstance(player, data.getRight(), data.getLeft(), data.getMiddle(), dataIn.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
    }

    public ExtendedGuiHandler(ModBase mod) {
        super(mod);
    }
}
