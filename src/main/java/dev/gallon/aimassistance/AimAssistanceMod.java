package dev.gallon.aimassistance;

import dev.gallon.aimassistance.config.Config;
import dev.gallon.aimassistance.core.AimAssistance;
import dev.gallon.aimassistance.utils.MouseUtils;
import dev.gallon.aimassistance.utils.Wrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AimAssistanceMod.MOD_ID)
public class AimAssistanceMod
{
    // Ready if needed
    // private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "aimassistancemod";
    public static final String CONTROLLABLE_MOD_ID = "controllable";
    private AimAssistance aimAssistance;

    public AimAssistanceMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        aimAssistance = new AimAssistance();
        Config.bakeConfig(); // init config values
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        if(ModList.get().isLoaded(CONTROLLABLE_MOD_ID))
        {
            Wrapper.setSupportForControllable(ModList.get().isLoaded(CONTROLLABLE_MOD_ID));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent) {
        if (Wrapper.playerPlaying()) {
            aimAssistance.analyseBehaviour();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent clientTickEvent) {
        if (Wrapper.playerPlaying()) {
            MouseUtils.checkForMouseMove();
            aimAssistance.analyseEnvironment();
        }
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent renderTickEvent) {
        if (Wrapper.playerPlaying()) {
            aimAssistance.assistIfPossible();
        }
    }
}
