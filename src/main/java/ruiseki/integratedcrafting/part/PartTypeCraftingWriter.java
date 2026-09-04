package ruiseki.integratedcrafting.part;

import com.google.common.collect.Lists;

import ruiseki.integratedcrafting.GeneralConfig;
import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.part.aspect.CraftingAspects;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.core.part.write.PartStateWriterBase;
import ruiseki.integrateddynamics.core.part.write.PartTypeWriteBase;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.okcore.init.ModBase;

/**
 * @author rubensworks
 */
public class PartTypeCraftingWriter
    extends PartTypeWriteBase<PartTypeCraftingWriter, PartStateWriterBase<PartTypeCraftingWriter>> {

    public PartTypeCraftingWriter(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    CraftingAspects.Write.RECIPE_CRAFT,
                    CraftingAspects.Write.ITEMSTACK_CRAFT,
                    CraftingAspects.Write.FLUIDSTACK_CRAFT,
                    CraftingAspects.Write.ENERGY_CRAFT));
    }

    @Override
    public PartStateWriterBase<PartTypeCraftingWriter> constructDefaultState() {
        return new State(
            Aspects.REGISTRY.getAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeCraftingWriter> state) {
        return GeneralConfig.craftingWriterBaseConsumption;
    }

    @Override
    public ModBase getMod() {
        return IntegratedCrafting._instance;
    }

    @Override
    public ModBase getModGui() {
        return IntegratedDynamics._instance;
    }

    public static class State extends PartStateWriterBase<PartTypeCraftingWriter> {

        protected long initialTickCraftingTrigger = -1;

        public State(int inventorySize) {
            super(inventorySize);
        }

        public long getInitialTickCraftingTrigger() {
            return initialTickCraftingTrigger;
        }

        public void setInitialTickCraftingTrigger(long initialTickCraftingTrigger) {
            this.initialTickCraftingTrigger = initialTickCraftingTrigger;
        }
    }
}
