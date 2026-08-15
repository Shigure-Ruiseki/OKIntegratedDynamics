package ruiseki.integratedtunnels.core;

import java.util.UUID;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.integratedtunnels.IntegratedTunnels;

public class FakePlayerHelpers {

    private static GameProfile PROFILE = new GameProfile(
        UUID.fromString("41C82C87-7AfB-4024-BB57-13D2C99CAE77"),
        "[IntegratedTunnels]");

    public static FakePlayer initFakePlayer(WorldServer ws) {
        FakePlayer fakePlayer;
        try {
            fakePlayer = FakePlayerFactory.get(ws, PROFILE);
        } catch (Exception e) {
            IntegratedTunnels.clog(Level.ERROR, "Exception thrown trying to create fake player : ", e);
            fakePlayer = null;
        }

        if (fakePlayer == null) return null;

        fakePlayer.onGround = true;

        fakePlayer.playerNetServerHandler = new NetHandlerPlayServer(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance(),
            new NetworkManager(false),
            fakePlayer) {

            @Override
            public void sendPacket(Packet packetIn) {

            }
        };

        return fakePlayer;
    }
}
