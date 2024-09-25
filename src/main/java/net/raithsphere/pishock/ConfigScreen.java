package net.raithsphere.pishock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.raithsphere.pishock.config.CommonConfig;
import org.jetbrains.annotations.NotNull;

public final class ConfigScreen extends Screen {
    /** Distance from top of the screen to this GUI's title */
    private static final int TITLE_HEIGHT = 8;
    private final Screen parentScreen;
    private EditBox username, code, key;

    protected void init() {

        this.username = new EditBox(this.font, this.width / 2 - 75, 40, 200, 20, Component.literal("Username"));
        this.username.setValue(CommonConfig.USERNAME.get());
        this.username.setResponder(CommonConfig.USERNAME::set);

        this.code = new EditBox(this.font, this.width / 2 - 75, 65, 200, 20, Component.literal("Code"));
        this.code.setValue(CommonConfig.CODE.get());
        this.code.setResponder(CommonConfig.CODE::set);

        this.key = new EditBox(this.font, this.width / 2 - 75, 90, 200, 20, Component.literal("ApiKey"));
        this.key.setValue(CommonConfig.API_KEY.get());
        this.key.setResponder(CommonConfig.API_KEY::set);

        this.addRenderableWidget(this.username);
        this.addRenderableWidget(this.code);
        this.addRenderableWidget(this.key);

        this.addRenderableWidget(CycleButton.onOffBuilder(CommonConfig.TRIGGER.get()).create(this.width / 2 - 75, 115, 150, 20, Component.nullToEmpty("Triggers on Death"),
                (var1x, var2x) -> {
                    CommonConfig.TRIGGER.set(var2x);
                }));

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var2x) -> {
            CommonConfig.save();
            assert this.minecraft != null;
            this.minecraft.setScreen(this.parentScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());

    }
    public ConfigScreen(Screen parentScreen) {
        super(Component.nullToEmpty("PiShock Configuration"));
        this.parentScreen = parentScreen;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(poseStack);

        drawCenteredString(poseStack, this.font, "Username", this.width / 2 - 110, 45, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "Code", this.width / 2 - 100, 70, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, "API Key", this.width / 2 - 110, 95, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, TITLE_HEIGHT, 0xFFFFFF);

        super.render(poseStack, mouseX, mouseY, partialTicks);

    }
    @Override
    public void tick() {
        this.key.tick();
        this.code.tick();
        this.username.tick();
        super.tick();
    }
    @Override
    public void onClose() {
        CommonConfig.save();
        if (this.parentScreen != null) {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.parentScreen);
        }
    }
}