package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.FMLCommonHandler;
import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Value type with values that are entities.
 *
 * @author rubensworks
 */
public class ValueObjectTypeEntity extends ValueObjectTypeBase<ValueObjectTypeEntity.ValueEntity>
    implements IValueTypeNamed<ValueObjectTypeEntity.ValueEntity>,
    IValueTypeUniquelyNamed<ValueObjectTypeEntity.ValueEntity>, IValueTypeNullable<ValueObjectTypeEntity.ValueEntity> {

    private static final String DELIMITER = ";";

    public ValueObjectTypeEntity() {
        super("entity", ValueObjectTypeEntity.ValueEntity.class);
    }

    @Override
    public ValueEntity getDefault() {
        return ValueEntity.of(null);
    }

    @Override
    public String toCompactString(ValueEntity value) {
        Optional<Entity> entity = value.getRawValue();
        if (entity.isPresent()) {
            Entity e = entity.get();
            if (e instanceof EntityItem) {
                return ((EntityItem) e).getEntityItem()
                    .getDisplayName();
            } else {
                return e.getCommandSenderName();
            }
        }
        return "";
    }

    @Override
    public String serialize(ValueEntity value) {
        Optional<Entity> entity = value.getRawValue();
        if (entity.isPresent()) {
            int world = entity.get().worldObj.provider.dimensionId;
            int id = entity.get()
                .getEntityId();
            return world + DELIMITER + id;
        }
        return "";
    }

    @Override
    public ValueEntity deserialize(String value) {
        String[] split = value.split(DELIMITER);
        Entity entity = null;
        if (split.length == 2) {
            try {
                int worldId = Integer.parseInt(split[0]);
                int entityId = Integer.parseInt(split[1]);
                entity = getEntityByID(worldId, entityId);
            } catch (NumberFormatException ignored) {}
        }
        return ValueEntity.of(entity);
    }

    @Override
    public String getName(ValueEntity a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueEntity a) {
        return !a.getRawValue()
            .isPresent();
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return null;
    }

    @Override
    public String getUniqueName(ValueEntity value) {
        Optional<Entity> entity = value.getRawValue();
        if (entity.isPresent()) {
            Entity e = entity.get();
            return EntityList.getEntityString(e) + " " + e.getUniqueID();
        }
        return "";
    }

    public static Entity getEntityByUUID(UUID uuid) {
        if (uuid == null) return null;

        if (MinecraftHelpers.isClientSide()) {
            if (Minecraft.getMinecraft().theWorld != null) {
                for (Object obj : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                    if (obj instanceof Entity && uuid.equals(((Entity) obj).getUniqueID())) {
                        return (Entity) obj;
                    }
                }
            }
        } else {
            MinecraftServer server = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
            if (server != null && server.worldServers != null) {
                for (WorldServer world : server.worldServers) {
                    if (world != null) {
                        for (Object obj : world.loadedEntityList) {
                            if (obj instanceof Entity && uuid.equals(((Entity) obj).getUniqueID())) {
                                return (Entity) obj;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Entity getEntityByID(int dimensionId, int entityId) {
        if (MinecraftHelpers.isClientSide()) {
            if (Minecraft.getMinecraft().theWorld != null
                && Minecraft.getMinecraft().theWorld.provider.dimensionId == dimensionId) {
                return Minecraft.getMinecraft().theWorld.getEntityByID(entityId);
            }
        } else {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.worldServers != null) {
                for (WorldServer world : server.worldServers) {
                    if (world != null && world.provider.dimensionId == dimensionId) {
                        return world.getEntityByID(entityId);
                    }
                }
            }
        }
        return null;
    }

    @ToString
    public static class ValueEntity extends ValueOptionalBase<Entity> {

        protected ValueEntity(Entity entity) {
            super(ValueTypes.OBJECT_ENTITY, entity);
        }

        public static ValueEntity of(Entity entity) {
            return new ValueEntity(entity);
        }

        @Override
        protected boolean isEqual(Entity a, Entity b) {
            return a.getEntityId() == b.getEntityId();
        }
    }
}
