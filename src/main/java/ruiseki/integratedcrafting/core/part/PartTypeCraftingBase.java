package ruiseki.integratedcrafting.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartRenderPosition;
import ruiseki.integrateddynamics.core.client.gui.container.GuiPartSettings;
import ruiseki.integrateddynamics.core.inventory.container.ContainerPartSettings;
import ruiseki.integrateddynamics.core.part.PartTypeConfigurable;
import ruiseki.okcore.init.ModBase;

/**
 * Base part for a crafting part.
 *
 * @author rubensworks
 */
public abstract class PartTypeCraftingBase<P extends IPartType<P, S>, S extends IPartState<P>>
    extends PartTypeConfigurable<P, S> {

    public PartTypeCraftingBase(String name) {
        super(name, new PartRenderPosition(0.1875F, 0.1875F, 0.625F, 0.625F));
    }

    @Override
    public ModBase getMod() {
        return IntegratedCrafting._instance;
    }

    @Override
    public ModBase getModGui() {
        return IntegratedDynamics._instance;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartType.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartSettings.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiPartSettings.class;
    }
}
