package ruiseki.integratedterminals.network.packet;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.core.client.gui.CraftingOptionGuiData;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingOption;
import ruiseki.integratedterminals.core.terminalstorage.crafting.HandlerWrappedTerminalCraftingPlan;
import ruiseki.integratedterminals.core.terminalstorage.location.TerminalStorageLocations;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a storage slot click event from client to server.
 *
 * @author rubensworks
 *
 */
public abstract class TerminalStorageIngredientCraftingOptionDataPacketAbstract<T, M, L> extends PacketCodec {

    @CodecField
    private String ingredientComponent;
    private ITerminalStorageLocation<L> location;
    private L locationInstance;
    @CodecField
    private String tabName;
    @CodecField
    private int channel;
    @CodecField
    private NBTTagCompound craftingOption;
    @CodecField
    private int amount;
    @CodecField
    private NBTTagCompound craftingPlan;

    public TerminalStorageIngredientCraftingOptionDataPacketAbstract() {}

    public TerminalStorageIngredientCraftingOptionDataPacketAbstract(
        CraftingOptionGuiData<T, M, L> craftingOptionData) {
        this.ingredientComponent = craftingOptionData.getComponent()
            .getName()
            .toString();
        this.location = craftingOptionData.getLocation();
        this.locationInstance = craftingOptionData.getLocationInstance();
        this.tabName = craftingOptionData.getTabName();
        this.channel = craftingOptionData.getChannel();
        this.craftingOption = craftingOptionData.getCraftingOption() != null
            ? HandlerWrappedTerminalCraftingOption.serialize(craftingOptionData.getCraftingOption())
            : new NBTTagCompound();
        this.amount = craftingOptionData.getAmount();
        this.craftingPlan = craftingOptionData.getCraftingPlan() != null
            ? HandlerWrappedTerminalCraftingPlan.serialize(craftingOptionData.getCraftingPlan())
            : new NBTTagCompound();
    }

    @Override
    public void encode(ExtendedBuffer output) {
        super.encode(output);
        output.writeResourceLocation(location.getName());
        location.writeToPacketBuffer(output, locationInstance);
    }

    @Override
    public void decode(ExtendedBuffer input) {
        super.decode(input);
        this.location = (ITerminalStorageLocation<L>) TerminalStorageLocations.REGISTRY
            .getLocation(input.readResourceLocation());
        this.locationInstance = this.location.readFromPacketBuffer(input);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {

    }

    @Nullable
    protected HandlerWrappedTerminalCraftingOption<T> getCraftingOption(IngredientComponent<T, M> ingredientComponent) {
        try {
            return HandlerWrappedTerminalCraftingOption.deserialize(ingredientComponent, this.craftingOption);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    protected HandlerWrappedTerminalCraftingPlan getCraftingPlan() {
        try {
            return HandlerWrappedTerminalCraftingPlan.deserialize(this.craftingPlan);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        IngredientComponent<?, ?> component = IngredientComponent.REGISTRY
            .getValue(new ResourceLocation(ingredientComponent));
        if (component == null) {
            throw new IllegalArgumentException("Could not find the ingredient component type " + ingredientComponent);
        }
        return (IngredientComponent<T, M>) component;
    }

    public int getChannel() {
        return channel;
    }

    public String getTabName() {
        return tabName;
    }

    public int getAmount() {
        return amount;
    }

    public CraftingOptionGuiData<T, M, L> getCraftingOptionData() {
        IngredientComponent<T, M> ingredientComponent = getIngredientComponent();
        return new CraftingOptionGuiData<>(
            ingredientComponent,
            tabName,
            channel,
            getCraftingOption(ingredientComponent),
            amount,
            getCraftingPlan(),
            location,
            locationInstance);
    }
}
