package ruiseki.integratedcompat.modcompat.nei;

import net.minecraftforge.common.MinecraftForge;

import codechicken.nei.api.API;
import codechicken.nei.event.NEIConfigsLoadedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.integratedcompat.IntegratedCompat;
import ruiseki.integratedcompat.modcompat.nei.logicprogrammer.LogicProgrammerOverlayHandler;
import ruiseki.integratedcompat.modcompat.nei.logicprogrammer.LogicProgrammerPositioner;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammer;
import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerPortable;
import ruiseki.okcore.modcompat.IModCompat;

public class NEIModCompat implements IModCompat {

    /**
     * If the modcompat can be used.
     */
    public static boolean canBeUsed = false;

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            canBeUsed = IntegratedCompat._instance.getModCompatLoader()
                .shouldLoadModCompat(this);

            if (canBeUsed) {
                MinecraftForge.EVENT_BUS.register(this);
            }
        }
    }

    @SubscribeEvent
    public void onNEIConfigsLoaded(NEIConfigsLoadedEvent event) {
        IntegratedCompat.clog("NEIConfigsLoadedEvent fired! Registering NEI Overlays...");

        LogicProgrammerPositioner positioner = new LogicProgrammerPositioner();
        LogicProgrammerOverlayHandler overlayHandler = new LogicProgrammerOverlayHandler();

        API.registerGuiOverlay(GuiLogicProgrammer.class, "crafting", positioner);
        API.registerGuiOverlay(GuiLogicProgrammerPortable.class, "crafting", positioner);

        API.registerGuiOverlayHandler(GuiLogicProgrammer.class, overlayHandler, "crafting");
        API.registerGuiOverlayHandler(GuiLogicProgrammerPortable.class, overlayHandler, "crafting");

        IntegratedCompat.clog("NEI Overlays registered successfully via Event!");
    }

    @Override
    public String getModID() {
        return "NotEnoughItems";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integration for Integrated Dynamics recipes.";
    }

}
