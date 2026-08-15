package ruiseki.integrateddynamics;

import ruiseki.integrateddynamics.block.BlockCableConfig;
import ruiseki.integrateddynamics.block.BlockDelayConfig;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.block.BlockInvisibleLightConfig;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLeavesConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLogConfig;
import ruiseki.integrateddynamics.block.BlockMenrilPlanksConfig;
import ruiseki.integrateddynamics.block.BlockMenrilSaplingConfig;
import ruiseki.integrateddynamics.block.BlockProxyConfig;
import ruiseki.integrateddynamics.block.BlockVariablestoreConfig;
import ruiseki.integrateddynamics.capability.cable.CableConfig;
import ruiseki.integrateddynamics.capability.cable.CableFakeableConfig;
import ruiseki.integrateddynamics.capability.dynamiclight.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.integrateddynamics.capability.ingredient.IngredientComponentValueHandlerConfig;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.network.PartNetworkConfig;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.valueinterface.ValueInterfaceConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
import ruiseki.integrateddynamics.capability.variablefacade.VariableFacadeHolderConfig;
import ruiseki.integrateddynamics.item.ItemFacadeConfig;
import ruiseki.integrateddynamics.item.ItemLabellerConfig;
import ruiseki.integrateddynamics.item.ItemLogicDirectorConfig;
import ruiseki.integrateddynamics.item.ItemMenrilBerriesConfig;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.integrateddynamics.item.ItemVariableTransformerConfig;
import ruiseki.integrateddynamics.item.ItemWrenchConfig;
import ruiseki.integrateddynamics.world.biome.BiomeMeneglinConfig;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

public class Configs {

    public static void registerBlocks(ConfigHandler configHandler) {

        // Capabilities
        configHandler.add(new PartContainerConfig());
        configHandler.add(new NetworkElementProviderConfig());
        configHandler.add(new DynamicLightConfig());
        configHandler.add(new DynamicRedstoneConfig());
        configHandler.add(new FacadeableConfig());
        configHandler.add(new VariableContainerConfig());
        configHandler.add(new CableConfig());
        configHandler.add(new CableFakeableConfig());
        configHandler.add(new NetworkCarrierConfig());
        configHandler.add(new PathElementConfig());
        configHandler.add(new VariableFacadeHolderConfig());
        configHandler.add(new PartNetworkConfig());
        configHandler.add(new EnergyNetworkConfig());
        configHandler.add(new ValueInterfaceConfig());
        configHandler.add(new PositionedAddonsNetworkIngredientsHandlerConfig());
        configHandler.add(new IngredientComponentValueHandlerConfig());

        // Fluids
        // configHandler.add(new FluidMenrilResinConfig());

        // Blocks
        configHandler.add(new BlockCableConfig());
        configHandler.add(new BlockVariablestoreConfig());
        configHandler.add(new BlockLogicProgrammerConfig());
        configHandler.add(new BlockInvisibleLightConfig());
        configHandler.add(new BlockEnergyBatteryConfig());
        // configHandler.add(new BlockCreativeEnergyBatteryConfig());
        // configHandler.add(new BlockCoalGeneratorConfig());
        configHandler.add(new BlockProxyConfig());
        // configHandler.add(new BlockMaterializerConfig());
        configHandler.add(new BlockMenrilLogConfig());
        configHandler.add(new BlockMenrilLeavesConfig());
        configHandler.add(new BlockMenrilSaplingConfig());
        configHandler.add(new BlockMenrilPlanksConfig());
        // configHandler.add(new BlockCrystalizedMenrilBlockConfig());
        // configHandler.add(new BlockCrystalizedMenrilBrickConfig());
        // configHandler.add(new BlockFluidMenrilResinConfig());
        // configHandler.add(new BlockDryingBasinConfig());
        // configHandler.add(new BlockSqueezerConfig());
        // configHandler.add(new BlockMenrilTorchConfig());
        configHandler.add(new BlockDelayConfig());

        // Items
        configHandler.add(new ItemWrenchConfig());
        configHandler.add(new ItemVariableConfig());
        configHandler.add(new ItemLabellerConfig());
        configHandler.add(new ItemFacadeConfig());
        // configHandler.add(new ItemBucketMenrilResinConfig());
        // configHandler.add(new ItemCrystalizedMenrilChunkConfig());
        configHandler.add(new ItemVariableTransformerConfig());
        configHandler.add(new ItemMenrilBerriesConfig());
        configHandler.add(new ItemPortableLogicProgrammerConfig());
        configHandler.add(new ItemLogicDirectorConfig());

        // Biomes
        configHandler.add(new BiomeMeneglinConfig());
    }

    /**
     * A safe way to check if a {@link ruiseki.okcore.config.configurable.IConfigurable} is enabled. @see
     * ExtendedConfig#isEnabled()
     *
     * @param config The config to check.
     * @return If the given config is enabled.
     */
    public static boolean isEnabled(Class<? extends ExtendedConfig<?>> config) {
        return IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(config);
    }
}
