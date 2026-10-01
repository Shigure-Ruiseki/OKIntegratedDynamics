package ruiseki.integratedterminals.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.integratedterminals.item.ItemTerminalStoragePortable;
import ruiseki.integratedterminals.network.packet.TerminalStorageIngredientItemOpenGenericPacket;
import ruiseki.okcore.client.key.IKeyRegistry;
import ruiseki.okcore.client.key.KeyBindingOK;
import ruiseki.okcore.client.key.KeyConflictContext;
import ruiseki.okcore.client.key.KeyModifier;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.inventory.PlayerExtendedInventoryIterator;
import ruiseki.okcore.proxy.ClientProxyComponent;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxy extends ClientProxyComponent {

    private static final String KEYBINDING_CATEGORY_NAME = "key.categories." + Reference.MOD_ID;

    public static final KeyBindingOK TERMINAL_TAB_NEXT = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.tab.next",
        KeyConflictContext.GUI,
        Keyboard.KEY_TAB,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK TERMINAL_TAB_PREVIOUS = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.tab.previous",
        KeyConflictContext.GUI,
        KeyModifier.SHIFT,
        Keyboard.KEY_TAB,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK TERMINAL_CRAFTINGGRID_CLEARPLAYER = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.craftinggrid.clearplayer",
        KeyConflictContext.GUI,
        KeyModifier.SHIFT,
        Keyboard.KEY_C,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK TERMINAL_CRAFTINGGRID_CLEARSTORAGE = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.craftinggrid.clearstorage",
        KeyConflictContext.GUI,
        Keyboard.KEY_C,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK TERMINAL_CRAFTINGGRID_BALANCE = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.craftinggrid.balance",
        KeyConflictContext.GUI,
        Keyboard.KEY_B,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK TERMINAL_STORAGE_PORTABLE_OPEN = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".terminal.portable.open",
        KeyConflictContext.IN_GAME,
        KeyModifier.SHIFT,
        Keyboard.KEY_C,
        KEYBINDING_CATEGORY_NAME);

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return IntegratedTerminals._instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {
        ClientRegistry.registerKeyBinding(TERMINAL_TAB_NEXT);
        ClientRegistry.registerKeyBinding(TERMINAL_TAB_PREVIOUS);
        ClientRegistry.registerKeyBinding(TERMINAL_CRAFTINGGRID_CLEARPLAYER);
        ClientRegistry.registerKeyBinding(TERMINAL_CRAFTINGGRID_CLEARSTORAGE);
        ClientRegistry.registerKeyBinding(TERMINAL_CRAFTINGGRID_BALANCE);

        keyRegistry.addKeyHandler(TERMINAL_STORAGE_PORTABLE_OPEN, (kb) -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            ItemLocation found = null;
            PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player);
            while (it.hasNext() && found == null) {
                ItemLocation pair = it.nextIndexed();
                if (pair.getItemStack(player)
                    .getItem() instanceof ItemTerminalStoragePortable) {
                    found = pair;
                }
            }
            if (found != null) {
                TerminalStorageIngredientItemOpenGenericPacket.send(found);
            }
        });
    }

}
