package ruiseki.integrateddynamics;

import ruiseki.integrateddynamics.block.BlockCableConfig;
import ruiseki.integrateddynamics.block.BlockCoalGeneratorConfig;
import ruiseki.integrateddynamics.block.BlockCreativeEnergyBatteryConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedChorusBlockConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedChorusBlockStairsConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedChorusBrickConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedChorusBrickStairsConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedMenrilBlockConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedMenrilBlockStairsConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedMenrilBrickConfig;
import ruiseki.integrateddynamics.block.BlockCrystalizedMenrilBrickStairsConfig;
import ruiseki.integrateddynamics.block.BlockDelayConfig;
import ruiseki.integrateddynamics.block.BlockDryingBasinConfig;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.block.BlockFluidLiquidChorusConfig;
import ruiseki.integrateddynamics.block.BlockFluidMenrilResinConfig;
import ruiseki.integrateddynamics.block.BlockInvisibleLightConfig;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.block.BlockMaterializerConfig;
import ruiseki.integrateddynamics.block.BlockMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.block.BlockMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.block.BlockMenrilDoorConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLeavesConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLogConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLogFilledConfig;
import ruiseki.integrateddynamics.block.BlockMenrilPlanksConfig;
import ruiseki.integrateddynamics.block.BlockMenrilPlanksStairsConfig;
import ruiseki.integrateddynamics.block.BlockMenrilSaplingConfig;
import ruiseki.integrateddynamics.block.BlockMenrilTorchConfig;
import ruiseki.integrateddynamics.block.BlockMenrilTorchStoneConfig;
import ruiseki.integrateddynamics.block.BlockProxyConfig;
import ruiseki.integrateddynamics.block.BlockSqueezerConfig;
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
import ruiseki.integrateddynamics.core.recipe.type.RecipeEnergyContainerCombinationConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSerializerDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSerializerMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSerializerMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSerializerNbtClearConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSerializerSqueezerConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeSqueezerConfig;
import ruiseki.integrateddynamics.fluid.FluidLiquidChorusConfig;
import ruiseki.integrateddynamics.fluid.FluidMenrilResinConfig;
import ruiseki.integrateddynamics.item.ItemBucketLiquidChorusConfig;
import ruiseki.integrateddynamics.item.ItemBucketMenrilResinConfig;
import ruiseki.integrateddynamics.item.ItemCrystalizedChorusChunkConfig;
import ruiseki.integrateddynamics.item.ItemCrystalizedMenrilChunkConfig;
import ruiseki.integrateddynamics.item.ItemEnhancementConfig;
import ruiseki.integrateddynamics.item.ItemFacadeConfig;
import ruiseki.integrateddynamics.item.ItemLabellerConfig;
import ruiseki.integrateddynamics.item.ItemLogicDirectorConfig;
import ruiseki.integrateddynamics.item.ItemMenrilBerriesConfig;
import ruiseki.integrateddynamics.item.ItemPortableLogicProgrammerConfig;
import ruiseki.integrateddynamics.item.ItemProtoChorusConfig;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.integrateddynamics.item.ItemVariableTransformerConfig;
import ruiseki.integrateddynamics.item.ItemWrenchConfig;
import ruiseki.integrateddynamics.recipe.ItemFacadeRecipeConfig;
import ruiseki.integrateddynamics.recipe.ItemVariableCopyRecipeConfig;
import ruiseki.integrateddynamics.recipe.RecipeSerializerCraftingSpecialShapedOmniDirectional3Config;
import ruiseki.integrateddynamics.recipe.RecipeSerializerCraftingSpecialShapedOmniDirectionalConfig;
import ruiseki.integrateddynamics.recipe.RecipeSerializerCraftingSpecialShapelessOmniDirectionalConfig;
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
        configHandler.add(new FluidMenrilResinConfig());
        configHandler.add(new FluidLiquidChorusConfig());

        // Blocks
        configHandler.add(new BlockCableConfig());
        configHandler.add(new BlockVariablestoreConfig());
        configHandler.add(new BlockLogicProgrammerConfig());
        configHandler.add(new BlockInvisibleLightConfig());
        configHandler.add(new BlockEnergyBatteryConfig());
        configHandler.add(new BlockCreativeEnergyBatteryConfig());
        configHandler.add(new BlockCoalGeneratorConfig());
        configHandler.add(new BlockProxyConfig());
        configHandler.add(new BlockMaterializerConfig());
        configHandler.add(new BlockMenrilLogConfig());
        configHandler.add(new BlockMenrilLogFilledConfig());
        configHandler.add(new BlockMenrilLeavesConfig());
        configHandler.add(new BlockMenrilSaplingConfig());
        configHandler.add(new BlockMenrilPlanksConfig());
        configHandler.add(new BlockCrystalizedMenrilBlockConfig());
        configHandler.add(new BlockCrystalizedMenrilBrickConfig());
        configHandler.add(new BlockFluidMenrilResinConfig());
        configHandler.add(new BlockDryingBasinConfig());
        configHandler.add(new BlockSqueezerConfig());
        configHandler.add(new BlockMenrilDoorConfig());
        configHandler.add(new BlockMenrilTorchConfig());
        configHandler.add(new BlockMenrilTorchStoneConfig());
        configHandler.add(new BlockMenrilPlanksStairsConfig());
        configHandler.add(new BlockCrystalizedMenrilBlockStairsConfig());
        configHandler.add(new BlockCrystalizedMenrilBrickStairsConfig());
        configHandler.add(new BlockDelayConfig());
        configHandler.add(new BlockFluidLiquidChorusConfig());
        configHandler.add(new BlockCrystalizedChorusBlockConfig());
        configHandler.add(new BlockCrystalizedChorusBrickConfig());
        configHandler.add(new BlockCrystalizedChorusBlockStairsConfig());
        configHandler.add(new BlockCrystalizedChorusBrickStairsConfig());
        configHandler.add(new BlockMechanicalSqueezerConfig());
        configHandler.add(new BlockMechanicalDryingBasinConfig());

        // Items
        configHandler.add(new ItemBucketLiquidChorusConfig());
        configHandler.add(new ItemBucketMenrilResinConfig());
        configHandler.add(new ItemWrenchConfig());
        configHandler.add(new ItemVariableConfig());
        configHandler.add(new ItemLabellerConfig());
        configHandler.add(new ItemFacadeConfig());
        configHandler.add(new ItemCrystalizedMenrilChunkConfig());
        configHandler.add(new ItemVariableTransformerConfig(true));
        configHandler.add(new ItemVariableTransformerConfig(false));
        configHandler.add(new ItemMenrilBerriesConfig());
        configHandler.add(new ItemPortableLogicProgrammerConfig());
        // configHandler.add(new ItemOnTheDynamicsOfIntegrationConfig());
        configHandler.add(new ItemCrystalizedChorusChunkConfig());
        configHandler.add(new ItemLogicDirectorConfig());
        configHandler.add(new ItemProtoChorusConfig());
        configHandler.add(new ItemEnhancementConfig());

        // Biomes
        configHandler.add(new BiomeMeneglinConfig());

        // Entities
        // configHandler.add(new EntityItemTargettedConfig());

        // Recipe types
        configHandler.add(new RecipeTypeDryingBasinConfig());
        configHandler.add(new RecipeTypeMechanicalDryingBasinConfig());
        configHandler.add(new RecipeTypeSqueezerConfig());
        configHandler.add(new RecipeTypeMechanicalSqueezerConfig());

        // Recipes
        configHandler.add(new RecipeSerializerDryingBasinConfig());
        configHandler.add(new RecipeSerializerMechanicalDryingBasinConfig());
        configHandler.add(new RecipeSerializerSqueezerConfig());
        configHandler.add(new RecipeSerializerMechanicalSqueezerConfig());
        configHandler.add(new RecipeSerializerNbtClearConfig());
        configHandler.add(new RecipeEnergyContainerCombinationConfig());
        configHandler.add(new ItemVariableCopyRecipeConfig());
        configHandler.add(new ItemFacadeRecipeConfig());
        configHandler.add(new RecipeSerializerCraftingSpecialShapedOmniDirectional3Config());
        configHandler.add(new RecipeSerializerCraftingSpecialShapedOmniDirectionalConfig());
        configHandler.add(new RecipeSerializerCraftingSpecialShapelessOmniDirectionalConfig());
    }

    /**
     * @param config The config to check.
     * @return If the given config is enabled.
     */
    public static boolean isEnabled(Class<? extends ExtendedConfig<?, ?>> config) {
        return IntegratedDynamics._instance.getConfigHandler()
            .isConfigEnabled(config);
    }
}
