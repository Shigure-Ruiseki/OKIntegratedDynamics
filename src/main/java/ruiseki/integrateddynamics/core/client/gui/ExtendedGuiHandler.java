package ruiseki.integrateddynamics.core.client.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.core.part.PartTypeBase;
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
     * Gui type for parts
     */
    public static final GuiType<ForgeDirection> PART = GuiType.create(true);
    /**
     * Gui type for part aspects
     */
    public static final GuiType<Pair<ForgeDirection, IAspect>> ASPECT = GuiType.create(true);

    static {
        PART.setContainerConstructor(new IContainerConstructor<ForgeDirection>() {

            @Override
            public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                Class<? extends Container> containerClass, ForgeDirection side) {
                try {
                    Pair<IPartContainer, PartTypeBase> data = getPartConstructionData(
                        world,
                        new BlockPos(x, y, z),
                        side);
                    if (data == null) return null;
                    Constructor<? extends Container> containerConstructor;
                    try {
                        containerConstructor = containerClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            data.getRight()
                                .getPartTypeClass());
                    } catch (NoSuchMethodException e) {
                        containerConstructor = containerClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class);
                    }
                    return containerConstructor.newInstance(
                        player,
                        PartTarget.fromCenter(world, new BlockPos(x, y, z), side),
                        data.getLeft(),
                        data.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        if (MinecraftHelpers.isClientSide()) {
            PART.setGuiConstructor(new IGuiConstructor<ForgeDirection>() {

                @Override
                public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                    Class<? extends GuiScreen> guiClass, ForgeDirection side) {
                    try {
                        Pair<IPartContainer, PartTypeBase> data = getPartConstructionData(
                            world,
                            new BlockPos(x, y, z),
                            side);
                        if (data == null) return null;
                        Constructor<? extends GuiScreen> guiConstructor;
                        try {
                            guiConstructor = guiClass.getConstructor(
                                EntityPlayer.class,
                                PartTarget.class,
                                IPartContainer.class,
                                data.getRight()
                                    .getPartTypeClass());
                        } catch (NoSuchMethodException e) {
                            guiConstructor = guiClass.getConstructor(
                                EntityPlayer.class,
                                PartTarget.class,
                                IPartContainer.class,
                                IPartType.class);
                        }
                        return guiConstructor.newInstance(
                            player,
                            PartTarget.fromCenter(world, new BlockPos(x, y, z), side),
                            data.getLeft(),
                            data.getRight());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }

        ASPECT.setContainerConstructor(new IContainerConstructor<Pair<ForgeDirection, IAspect>>() {

            @Override
            public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                Class<? extends Container> containerClass, Pair<ForgeDirection, IAspect> dataIn) {
                try {
                    if (dataIn == null) return null;
                    Pair<IPartContainer, PartTypeBase> data = getPartConstructionData(
                        world,
                        new BlockPos(x, y, z),
                        dataIn.getLeft());
                    if (data == null) return null;
                    Constructor<? extends Container> containerConstructor = containerClass.getConstructor(
                        EntityPlayer.class,
                        PartTarget.class,
                        IPartContainer.class,
                        IPartType.class,
                        IAspect.class);
                    return containerConstructor.newInstance(
                        player,
                        PartTarget.fromCenter(world, new BlockPos(x, y, z), dataIn.getLeft()),
                        data.getLeft(),
                        data.getRight(),
                        dataIn.getRight());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        if (MinecraftHelpers.isClientSide()) {
            ASPECT.setGuiConstructor(new IGuiConstructor<Pair<ForgeDirection, IAspect>>() {

                @Override
                public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z,
                    Class<? extends GuiScreen> guiClass, Pair<ForgeDirection, IAspect> dataIn) {
                    try {
                        if (dataIn == null) return null;
                        Pair<IPartContainer, PartTypeBase> data = getPartConstructionData(
                            world,
                            new BlockPos(x, y, z),
                            dataIn.getLeft());
                        if (data == null) return null;
                        Constructor<? extends GuiScreen> guiConstructor = guiClass.getConstructor(
                            EntityPlayer.class,
                            PartTarget.class,
                            IPartContainer.class,
                            IPartType.class,
                            IAspect.class);
                        return guiConstructor.newInstance(
                            player,
                            PartTarget.fromCenter(world, new BlockPos(x, y, z), dataIn.getLeft()),
                            data.getLeft(),
                            data.getRight(),
                            dataIn.getRight());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }

    private static Pair<IPartContainer, PartTypeBase> getPartConstructionData(World world, BlockPos pos,
        ForgeDirection side) {
        IPartContainer partContainer = PartContainerConfig.get(world, pos);
        if (partContainer == null) {
            IntegratedDynamics.clog(Level.WARN, String.format("The tile at %s is not a valid part container.", pos));
            return null;
        }

        IPartType partType = partContainer.getPart(side);
        if (partType == null || !(partType instanceof PartTypeBase)) {
            IntegratedDynamics.clog(
                Level.WARN,
                String.format("The part container at %s side %s does not have a valid part.", pos, side));
            return null;
        }

        return Pair.of(partContainer, (PartTypeBase) partType);
    }

    public ExtendedGuiHandler(ModBase mod) {
        super(mod);
    }
}
