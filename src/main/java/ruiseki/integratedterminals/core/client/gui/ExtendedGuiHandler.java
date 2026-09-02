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
import ruiseki.integratedterminals.inventory.container.TerminalStorageState;
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
     * Gui type for guis for selecting crafting options (PART).
     */
    public static final GuiType<Pair<ForgeDirection, CraftingOptionGuiData<?, ?, ?>>> CRAFTING_OPTION_PART = GuiType
        .create(true);
    /**
     * Gui type for storage terminals with a preselected tab and channel (PART).
     */
    public static final GuiType<Pair<ForgeDirection, Pair<ContainerTerminalStorageBase.InitTabData, TerminalStorageState>>> TERMINAL_STORAGE_PART = GuiType
        .create(true);
    /**
     * Gui type for crafting plans (PART).
     */
    public static final GuiType<Pair<ForgeDirection, CraftingJobGuiData>> CRAFTING_PLAN_PART = GuiType.create(true);

    /**
     * Gui type for guis for selecting crafting options (ITEM).
     */
    public static final GuiType<Pair<Integer, CraftingOptionGuiData<?, ?, ?>>> CRAFTING_OPTION_ITEM = GuiType
        .create(true);
    /**
     * Gui type for storage terminals with a preselected tab and channel (ITEM).
     */
    public static final GuiType<Pair<Integer, Pair<ContainerTerminalStorageBase.InitTabData, TerminalStorageState>>> TERMINAL_STORAGE_ITEM = GuiType
        .create(true);

    static {
        CRAFTING_OPTION_PART.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
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
            CRAFTING_OPTION_PART.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
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

        TERMINAL_STORAGE_PART.setContainerConstructor((id, player, world, x, y, z, containerClass, in) -> {
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
                        ContainerTerminalStorageBase.InitTabData.class,
                        TerminalStorageState.class);
                } catch (NoSuchMethodException e) {
                    containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        IPartType.class,
                        ContainerTerminalStorageBase.InitTabData.class,
                        TerminalStorageState.class);
                }
                return containerConstructor.newInstance(
                    player,
                    data.getRight(),
                    data.getLeft(),
                    data.getMiddle(),
                    in.getRight()
                        .getLeft(),
                    in.getRight()
                        .getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (MinecraftHelpers.isClientSide()) {
            TERMINAL_STORAGE_PART.setGuiConstructor((id, player, world, x, y, z, guiClass, in) -> {
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
                            ContainerTerminalStorageBase.InitTabData.class,
                            TerminalStorageState.class);
                    } catch (NoSuchMethodException e) {
                        guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class,
                            ContainerTerminalStorageBase.InitTabData.class,
                            TerminalStorageState.class);
                    }
                    return guiConstructor.newInstance(
                        player,
                        data.getRight(),
                        data.getLeft(),
                        data.getMiddle(),
                        in.getRight()
                            .getLeft(),
                        in.getRight()
                            .getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }

        CRAFTING_PLAN_PART.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
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
            CRAFTING_PLAN_PART.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
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

        CRAFTING_OPTION_ITEM.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
            try {
                Constructor<? extends Container> containerConstructor = containerClass
                    .getConstructor(EntityPlayer.class, Integer.TYPE, CraftingOptionGuiData.class);
                return containerConstructor.newInstance(player, dataIn.getLeft(), dataIn.getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });

        if (MinecraftHelpers.isClientSide()) {
            CRAFTING_OPTION_ITEM.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
                try {
                    Constructor<? extends GuiScreen> guiConstructor = guiClass
                        .getConstructor(EntityPlayer.class, Integer.TYPE, CraftingOptionGuiData.class);
                    return guiConstructor.newInstance(player, dataIn.getLeft(), dataIn.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }

        TERMINAL_STORAGE_ITEM.setContainerConstructor((id, player, world, x, y, z, containerClass, dataIn) -> {
            try {
                Constructor<? extends Container> containerConstructor = containerClass.getConstructor(
                    EntityPlayer.class,
                    Integer.TYPE,
                    ContainerTerminalStorageBase.InitTabData.class,
                    TerminalStorageState.class);
                return containerConstructor.newInstance(
                    player,
                    dataIn.getLeft(),
                    dataIn.getRight()
                        .getLeft(),
                    dataIn.getRight()
                        .getRight());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        });

        if (MinecraftHelpers.isClientSide()) {
            TERMINAL_STORAGE_ITEM.setGuiConstructor((id, player, world, x, y, z, guiClass, dataIn) -> {
                try {
                    Constructor<? extends GuiScreen> guiConstructor = guiClass.getConstructor(
                        EntityPlayer.class,
                        Integer.TYPE,
                        ContainerTerminalStorageBase.InitTabData.class,
                        TerminalStorageState.class);
                    return guiConstructor.newInstance(
                        player,
                        dataIn.getLeft(),
                        dataIn.getRight()
                            .getLeft(),
                        dataIn.getRight()
                            .getRight());
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
