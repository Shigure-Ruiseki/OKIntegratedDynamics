package ruiseki.integrateddynamics;

import ruiseki.integrateddynamics.block.BlockCableConfig;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.block.BlockInvisibleLightConfig;
import ruiseki.integrateddynamics.block.BlockLogicProgrammerConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLeavesConfig;
import ruiseki.integrateddynamics.block.BlockMenrilLogConfig;
import ruiseki.integrateddynamics.block.BlockMenrilPlanksConfig;
import ruiseki.integrateddynamics.block.BlockMenrilSaplingConfig;
import ruiseki.integrateddynamics.block.BlockVariablestoreConfig;
import ruiseki.integrateddynamics.capability.cable.CableConfig;
import ruiseki.integrateddynamics.capability.cable.CableFakeableConfig;
import ruiseki.integrateddynamics.capability.dynamiclight.DynamicLightConfig;
import ruiseki.integrateddynamics.capability.dynamicredstone.DynamicRedstoneConfig;
import ruiseki.integrateddynamics.capability.energybattery.EnergyBatteryConfig;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
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
        configHandler.add(new FacadeableConfig());
        configHandler.add(new VariableContainerConfig());
        configHandler.add(new EnergyBatteryConfig());
        configHandler.add(new CableConfig());
        configHandler.add(new CableFakeableConfig());
        configHandler.add(new NetworkCarrierConfig());
        configHandler.add(new PathElementConfig());

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
