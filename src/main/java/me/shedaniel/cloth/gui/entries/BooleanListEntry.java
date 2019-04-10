package me.shedaniel.cloth.gui.entries;

import com.google.common.collect.Lists;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanListEntry extends ClothConfigScreen.ListEntry {
    
    private AtomicBoolean bool;
    private ButtonWidget buttonWidget, resetButton;
    private Consumer<Boolean> saveConsumer;
    private Supplier<Boolean> defaultValue;
    private List<Element> widgets;
    
    public BooleanListEntry(String fieldName, boolean bool, Consumer<Boolean> saveConsumer) {
        this(fieldName, bool, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    public BooleanListEntry(String fieldName, boolean bool, String resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer) {
        super(fieldName);
        this.defaultValue = defaultValue;
        this.bool = new AtomicBoolean(bool);
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, "", widget -> {
            BooleanListEntry.this.bool.set(!BooleanListEntry.this.bool.get());
            getScreen().setEdited(true);
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(resetButtonKey)) + 6, 20, I18n.translate(resetButtonKey), widget -> {
            BooleanListEntry.this.bool.set(defaultValue.get());
            getScreen().setEdited(true);
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getObject());
    }
    
    @Override
    public Boolean getObject() {
        return bool.get();
    }
    
    @Override
    public Optional<Object> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        Window window = MinecraftClient.getInstance().window;
        this.resetButton.active = getDefaultValue().isPresent() && defaultValue.get().booleanValue() != bool.get();
        this.resetButton.y = y;
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(getYesNoText(bool.get()));
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(getFieldName())), y + 5, 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), x, y + 5, 16777215);
            this.resetButton.x = window.getScaledWidth() - x - resetButton.getWidth();
            this.buttonWidget.x = window.getScaledWidth() - x - 150;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        }
        resetButton.render(mouseX, mouseY, delta);
        buttonWidget.render(mouseX, mouseY, delta);
    }
    
    public String getYesNoText(boolean bool) {
        return bool ? "§aYes" : "§cNo";
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
}
