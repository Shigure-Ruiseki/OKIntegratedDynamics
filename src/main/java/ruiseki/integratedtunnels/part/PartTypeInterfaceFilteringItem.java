package ruiseki.integratedtunnels.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.integratedtunnels.core.part.ContainerInterfaceSettings;
import ruiseki.integratedtunnels.core.part.GuiInterfaceSettings;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * Interface for filtering item handlers.
 *
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringItem extends
    PartTypeInterfacePositionedAddonFiltering<IItemNetwork, IItemHandler, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {

    public PartTypeInterfaceFilteringItem(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER,
                    TunnelAspects.Write.ItemFilter.ITEMSTACK_SET_FILTER,
                    TunnelAspects.Write.ItemFilter.LIST_SET_FILTER,
                    TunnelAspects.Write.ItemFilter.PREDICATE_SET_FILTER,
                    TunnelAspects.Write.ItemFilter.NBT_SET_FILTER));
    }

    @Override
    public Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IItemHandler> getTargetCapability() {
        return CapabilityItemHandler.ITEM_HANDLER;
    }

    @Override
    protected PartTypeInterfaceFilteringItem.State constructDefaultState() {
        return new PartTypeInterfaceFilteringItem.State(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    protected Class<? extends Container> getSettingsContainer() {
        return ContainerInterfaceSettings.class;
    }

    @Override
    protected Class<? extends GuiScreen> getSettingsGui() {
        return GuiInterfaceSettings.class;
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends
        PartTypeInterfacePositionedAddonFiltering.State<IItemNetwork, IItemHandler, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IItemHandler> getTargetCapability() {
            return CapabilityItemHandler.ITEM_HANDLER;
        }

        @Override
        public IItemHandler getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network,
            IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == SlotlessItemHandlerConfig.CAPABILITY) {
                return LazyOptional.of(this::getCapabilityInstance)
                    .cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }
}
