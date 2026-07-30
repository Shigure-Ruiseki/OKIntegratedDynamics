package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.core.persist.world.LabelsWorldStorage;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Base implementation of {@link IVariableFacade}
 *
 * @author rubensworks
 */
public abstract class VariableFacadeBase implements IVariableFacade {

    private final int id;

    public VariableFacadeBase(boolean generateId) {
        this.id = generateId ? generateId() : -1;
    }

    public VariableFacadeBase(int id) {
        this.id = id;
    }

    /**
     * @return A unique new variable id.
     */
    public static int generateId() {
        return IntegratedDynamics.globalCounters.getNext("variable");
    }

    @Override
    public final int getId() {
        return this.id;
    }

    @Override
    public String getLabel() {
        return LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
            .getLabel(getId());
    }

    protected String getReferenceDisplay(int variableId) {
        String label = LabelsWorldStorage.getInstance(IntegratedDynamics._instance)
            .getLabel(variableId);
        if (label == null) {
            return String.valueOf(variableId);
        } else {
            return String.format("%s:%s", label, variableId);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(List<String> list, EntityPlayer entityPlayer) {
        list.add(LangHelpers.localize("item.items.integrateddynamics.variable.id", getId() == -1 ? "..." : getId()));
    }

}
