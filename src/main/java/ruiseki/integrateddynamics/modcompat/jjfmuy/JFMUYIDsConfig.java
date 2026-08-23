package ruiseki.integrateddynamics.modcompat.jjfmuy;

import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.jfmuy.api.IModPlugin;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.JFMUYPlugin;

@JFMUYPlugin
public class JFMUYIDsConfig implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        if (!JFMUYModCompat.canBeUsed) return;
        registry.addGhostIngredientHandler(GuiLogicProgrammerBase.class, new LPGhostIngredientHandler<>());
    }
}
