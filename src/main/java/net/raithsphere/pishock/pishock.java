package net.raithsphere.pishock;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.raithsphere.pishock.config.PiShockModClientConfigs;
import net.raithsphere.pishock.config.PiShockModCommonConfigs;
import net.raithsphere.pishock.message.MessageHandler;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(pishock.MOD_ID)
public class pishock
{
    public static final String MOD_ID = "pishock";
    public static final Logger LOGGER = LogUtils.getLogger();
    public pishock()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PiShockModClientConfigs.SPEC, "pishock.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PiShockModCommonConfigs.SPEC, "pishock-common.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        event.enqueueWork(() -> {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(pishock::createConfigScreen));
        });
    }

    public static Screen createConfigScreen(Minecraft minecraft, Screen mainConfigScreen) {
        return new ErrorScreen(Component.literal("PiShock Controller"), Component.literal("Sadly this needs to be configured in the toml located at config/pishock-common.toml "));
    }
    //Fired when player dies
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e){
        if (!e.getEntity().level().isClientSide() && e.getEntity() instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer) e.getEntity();

            //Getting the different health/damage values
            float now = p.getHealth();
            float max = p.getMaxHealth();

            MessageHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> p), new MessageHandler.MessageDamageToClient(0, now, max, 0));
        }
    }
    //Fired when an entity is hit/takes damage
    @SubscribeEvent
    public void onDamage(LivingDamageEvent e) {
        if (!e.getEntity().level().isClientSide() && e.getEntity() instanceof ServerPlayer) {

            //Getting the entity
            ServerPlayer p = (ServerPlayer) e.getEntity();

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
