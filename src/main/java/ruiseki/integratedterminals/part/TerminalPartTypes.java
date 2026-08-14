package ruiseki.integratedterminals.part;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.IPartTypeRegistry;

/**
 * @author rubensworks
 */
public class TerminalPartTypes {

    public static final IPartTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IPartTypeRegistry.class);

    public static void load() {}

    public static final PartTypeTerminalStorage TERMINAL_STORAGE = REGISTRY
        .register(new PartTypeTerminalStorage("terminal_storage"));
    public static final PartTypeTerminalCraftingJob TERMINAL_CRAFTING_JOB = REGISTRY
        .register(new PartTypeTerminalCraftingJob("terminal_crafting_job"));

}
