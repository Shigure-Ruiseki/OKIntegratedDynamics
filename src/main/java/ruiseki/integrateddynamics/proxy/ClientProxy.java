package ruiseki.integrateddynamics.proxy;

import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.itemrendering.TexturedItemRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.network.diagnostics.NetworkDiagnosticsPartOverlayRenderer;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;
import ruiseki.okcore.client.key.IKeyRegistry;
import ruiseki.okcore.client.key.KeyBindingOK;
import ruiseki.okcore.client.key.KeyConflictContext;
import ruiseki.okcore.client.key.KeyModifier;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;

public class ClientProxy extends ClientProxyComponent {

    private static final String KEYBINDING_CATEGORY_NAME = "key.categories." + Reference.MOD_ID;

    public static final KeyBindingOK FOCUS_LP_SEARCH = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".logicProgrammerFocusSearch",
        KeyConflictContext.GUI,
        KeyModifier.ALT,
        Keyboard.KEY_F,
        KEYBINDING_CATEGORY_NAME);
    public static final KeyBindingOK FOCUS_LP_RENAME = new KeyBindingOK(
        "key." + Reference.MOD_ID + ".logicProgrammerOpenRename",
        KeyConflictContext.GUI,
        KeyModifier.ALT,
        Keyboard.KEY_R,
        KEYBINDING_CATEGORY_NAME);

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return IntegratedDynamics._instance;
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        MinecraftForge.EVENT_BUS.register(NetworkDiagnosticsPartOverlayRenderer.getInstance());
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {
        super.registerKeyBindings(keyRegistry);
        ClientRegistry.registerKeyBinding(FOCUS_LP_SEARCH);
        ClientRegistry.registerKeyBinding(FOCUS_LP_RENAME);
    }

    @Override
    public void registerRenderers() {
        TexturedItemRenderer.register((ItemVariable) ItemVariableConfig._instance.getInstance());
        super.registerRenderers();
    }
}
