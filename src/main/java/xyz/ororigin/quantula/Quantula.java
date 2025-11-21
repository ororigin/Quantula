// Quantula.java 修改后
package xyz.ororigin.quantula;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.ororigin.quantula.command.QuantulaCommand;
import xyz.ororigin.quantula.data.files.DataFileManager;
import xyz.ororigin.quantula.network.QuantumPacketHandler;

@Mod(Quantula.MODID)
public class Quantula {

    public static final String MODID = "quantula";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static QuantumPacketHandler packetHandler; // 新增：包处理器实例

    public Quantula() {
        new DataFileManager();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        packetHandler = new QuantumPacketHandler(MODID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            packetHandler.initialize();
            LOGGER.info("Quantum network system initialized");
        });
    }

    public static QuantumPacketHandler getPacketHandler() {
        return packetHandler;
    }

    private void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(QuantulaCommand.register());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}