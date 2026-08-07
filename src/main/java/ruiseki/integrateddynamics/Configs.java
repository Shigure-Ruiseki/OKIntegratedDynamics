package ruiseki.integrateddynamics;

import ruiseki.integrateddynamics.block.BlockCableConfig;
import ruiseki.integrateddynamics.block.BlockInvisibleLightConfig;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLeavesConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLogConfig;
import ruiseki.integrateddynamics.block.BlockMenrilPlanksConfig;
import ruiseki.integrateddynamics.block.BlockMenrilSaplingConfig;
import ruiseki.integrateddynamics.block.BlockVariablestoreConfig;
import ruiseki.integrateddynamics.capability.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.PartContainerConfig;
import ruiseki.integrateddynamics.item.ItemFacadeConfig;
import ruiseki.integrateddynamics.item.ItemLabellerConfig;
import ruiseki.integrateddynamics.item.ItemMenrilBerriesConfig;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.integrateddynamics.item.ItemVariableTransformerConfig;
import ruiseki.integrateddynamics.item.ItemWrenchConfig;
import ruiseki.integrateddynamics.world.biome.BiomeMeneglinConfig;
import ruiseki.okcore.config.ConfigHandler;

public class Configs {

    public static void registerBlocks(ConfigHandler configHandler) {

        // Capabilities
        configHandler.add(new PartContainerConfig());
        configHandler.add(new NetworkElementProviderConfig());
        configHandler.add(new DynamicLightConfig());
        configHandler.add(new DynamicRedstoneConfig());

        // Fluids
        // configHandler.add(new FluidMenrilResinConfig());

        // Blocks
        configHandler.add(new BlockCableConfig());
        configHandler.add(new BlockVariablestoreConfig());
        configHandler.add(new BlockLogicProgrammerConfig());
        configHandler.add(new BlockInvisibleLightConfig());
        // configHandler.add(new BlockEnergyBatteryConfig());
        // configHandler.add(new BlockCreativeEnergyBatteryConfig());
        // configHandler.add(new BlockCoalGeneratorConfig());
        // configHandler.add(new BlockProxyConfig());
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

        // Items
        configHandler.add(new ItemWrenchConfig());
        configHandler.add(new ItemVariableConfig());
        configHandler.add(new ItemLabellerConfig());
        configHandler.add(new ItemFacadeConfig());
        // configHandler.add(new ItemBucketMenrilResinConfig());
        // configHandler.add(new ItemCrystalizedMenrilChunkConfig());
        configHandler.add(new ItemVariableTransformerConfig());
        configHandler.add(new ItemMenrilBerriesConfig());

        // Biomes
        configHandler.add(new BiomeMeneglinConfig());
    }
}
