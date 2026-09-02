package ruiseki.integratedterminals.core.terminalstorage.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlan;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalCraftingPlanFlat;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;

/**
 * Data holder for {@link ITerminalCraftingPlan} wrapped with its handler.
 *
 * @author rubensworks
 */
public class HandlerWrappedTerminalCraftingPlan {

    private final ITerminalStorageTabIngredientCraftingHandler handler;
    private final ITerminalCraftingPlanFlat craftingPlanFlat;

    public HandlerWrappedTerminalCraftingPlan(ITerminalStorageTabIngredientCraftingHandler handler,
        ITerminalCraftingPlanFlat craftingPlanFlat) {
        this.handler = handler;
        this.craftingPlanFlat = craftingPlanFlat;
    }

    public ITerminalStorageTabIngredientCraftingHandler getHandler() {
        return handler;
    }

    public ITerminalCraftingPlanFlat getCraftingPlanFlat() {
        return craftingPlanFlat;
    }

    public static NBTTagCompound serialize(HandlerWrappedTerminalCraftingPlan craftingPlan) {
        ITerminalStorageTabIngredientCraftingHandler handler = craftingPlan.getHandler();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(
            "craftingPlanHandler",
            handler.getId()
                .toString());
        tag.setTag("flatPlan", handler.serializeCraftingPlanFlat(craftingPlan.getCraftingPlanFlat()));

        return tag;
    }

    public static HandlerWrappedTerminalCraftingPlan deserialize(NBTTagCompound tag) {
        if (!tag.hasKey("craftingPlanHandler", Constants.NBT.TAG_STRING)) {
            throw new IllegalArgumentException("Could not find a craftingPlanHandler entry in the given tag");
        }
        String handlerId = tag.getString("craftingPlanHandler");
        ITerminalStorageTabIngredientCraftingHandler handler = TerminalStorageTabIngredientCraftingHandlers.REGISTRY
            .getHandler(new ResourceLocation(handlerId));

        ITerminalCraftingPlan craftingPlan = null;
        if (tag.hasKey("treePlan", Constants.NBT.TAG_COMPOUND)) {
            craftingPlan = handler.deserializeCraftingPlan(tag.getCompoundTag("treePlan"));
        }
        ITerminalCraftingPlanFlat craftingPlanFlat = handler
            .deserializeCraftingPlanFlat(tag.getCompoundTag("flatPlan"));

        return new HandlerWrappedTerminalCraftingPlan(handler, craftingPlanFlat);
    }
}
