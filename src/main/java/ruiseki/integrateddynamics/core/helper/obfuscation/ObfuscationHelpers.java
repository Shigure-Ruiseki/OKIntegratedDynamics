package ruiseki.integrateddynamics.core.helper.obfuscation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;

import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * Helper for getting private fields or methods.
 * 
 * @author rubensworks
 *
 */
public class ObfuscationHelpers {

    /**
     * Call the the {@link EntityLivingBase#getHurtSound()}.
     * 
     * @param entity The entity.
     * @return The hurt sound.
     */
    public static String getEntityLivingBaseHurtSound(EntityLivingBase entity) {
        Method method = ReflectionHelper
            .findMethod(EntityLivingBase.class, entity, ObfuscationData.ENTITYLIVINGBASE_HURTSOUND);
        try {
            return (String) method.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Call the the {@link EntityLivingBase#getDeathSound()} ()}.
     * 
     * @param entity The entity.
     * @return The death sound.
     */
    public static String getEntityLivingBaseDeathSound(EntityLivingBase entity) {
        Method method = ReflectionHelper
            .findMethod(EntityLivingBase.class, entity, ObfuscationData.ENTITYLIVINGBASE_DEATHSOUND);
        try {
            return (String) method.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
