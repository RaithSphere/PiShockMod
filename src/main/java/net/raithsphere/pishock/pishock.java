package net.raithsphere.pishock;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.raithsphere.pishock.config.CommonConfig;
import net.raithsphere.pishock.message.MessageHandler;
import org.slf4j.Logger;

import java.util.function.BiFunction;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(pishock.MOD_ID)
public class pishock
{
    public static final String NAME = "Pishock Mod";
    public static final String MOD_ID = "pishock";
    public static final Logger LOGGER = LogUtils.getLogger();
    public pishock()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "pishock-common.toml");

        FMLJavaModLoadingContext.get().getModEventBus()
                .addListener(this::clientSetup);
    }
    private void clientSetup(FMLClientSetupEvent event) {
        
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

        event.enqueueWork(() -> {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>() {
                @Override
                public Screen apply(Minecraft minecraft, Screen screen) {
                    return new ConfigScreen(screen);
                }
            }));
        });
    }

    //Fired when player dies
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e){
        if (!e.getEntity().level.isClientSide() && e.getEntity() instanceof ServerPlayer p) {

            //Getting the different health/damage values
            float now = p.getHealth();
            float max = p.getMaxHealth();

            MessageHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> p), new MessageHandler.MessageDamageToClient(0, now, max, 0));
        }
    }
    //Fired when an entity is hit/takes damage
    @SubscribeEvent
    public void onDamage(LivingDamageEvent e) {
         if (!e.getEntity().level.isClientSide() && e.getEntity() instanceof ServerPlayer p) {

             //Getting the different health/damage values
            float damage = e.getAmount();
            float now = p.getHealth();
            float max = p.getMaxHealth();

            //Sending a message/packet to the player with the values
            MessageHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> p), new MessageHandler.MessageDamageToClient(damage, now, max, 1));
        }
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MessageHandler.register();
        }
    }
}
