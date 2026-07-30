package ruiseki.integrateddynamics.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Config for a variable item.
 * 
 * @author rubensworks
 */
public class ItemVariableConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemVariableConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemVariableConfig() {
        super(IntegratedDynamics._instance, true, "variable", null, ItemVariable.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    protected void validateModels() {
        // TODO: Add Model
        // ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        // VariableModel.addAdditionalModels(builder);
        // ImmutableSet<ResourceLocation> models = builder.build();
        // for(ResourceLocation model : models) {
        // if(!ModelLoaderRegistry.loaded(model)) {
        // //IntegratedDynamics.clog(Level.ERROR, String.format("Model file %s not found, it is required by the variable
        // item model.", model));
        // throw new RuntimeException(String.format("Model file %s not found, it is required by the variable item
        // model.", model));
        // }
        // }
    }

    @Override
    public void onInit(Step step) {
        super.onInit(step);
        if (step == Step.POSTINIT && MinecraftHelpers.isClientSide()) {
            validateModels();
        }
    }
}
