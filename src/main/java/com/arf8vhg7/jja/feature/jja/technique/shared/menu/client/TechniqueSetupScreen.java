package com.arf8vhg7.jja.feature.jja.technique.shared.menu.client;

import com.arf8vhg7.jja.client.keymap.JjaSkillKeyMappings;
import com.arf8vhg7.jja.compat.jujutsucraft.JjaJujutsucraftCompat;
import com.arf8vhg7.jja.feature.jja.technique.shared.display.JjaTechniqueDisplayNameResolver;
import com.arf8vhg7.jja.feature.jja.domain.sd.AntiDomainTechniqueOption;
import com.arf8vhg7.jja.feature.jja.domain.de.DomainTypeOption;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupCategory;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupInputSlot;
import com.arf8vhg7.jja.feature.jja.technique.shared.registration.TechniqueSetupRegistrationCandidate;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.TechniqueSetupViewState;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueMenuRegisterMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueMenuRegistrationModeMessage;
import com.arf8vhg7.jja.feature.jja.technique.shared.menu.network.JjaTechniqueSetupCycleMessage;
import com.arf8vhg7.jja.feature.player.state.PlayerStateAccess;
import com.arf8vhg7.jja.feature.player.state.model.PlayerSkillState;
import com.arf8vhg7.jja.network.JjaNetwork;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public final class TechniqueSetupScreen extends Screen {
    private static final int CARD_WIDTH = 419;
    private static final int CARD_HEIGHT = 246;
    private static final int CARD_BACKGROUND = 0xE0171A20;
    private static final int CARD_BORDER = 0xFF41464F;
    private static final int CARD_BORDER_DARK = 0xFF1A1D23;
    private static final int CARD_ACCENT = 0xFF7F92A6;
    private static final int BACKGROUND_TOP = 0xE0090A0F;
    private static final int BACKGROUND_BOTTOM = 0xF004050A;
    private static final int TAB_BUTTON_WIDTH = 128;
    private static final int TAB_BUTTON_HEIGHT = 18;
    private static final int TAB_BUTTON_GAP = 8;
    private static final int TAB_BUTTON_Y = 24;
    private static final int REGISTRATION_SLOT_X_OFFSET = 14;
    private static final int REGISTRATION_SLOT_ROW_WIDTH = 170;
    private static final int REGISTRATION_SLOT_ROW_HEIGHT = 14;
    private static final int REGISTRATION_SLOT_ROW_STEP = 16;
    private static final int REGISTRATION_SLOT_START_Y = 66;
    private static final int REGISTRATION_SLOT_HEADER_Y = 54;
    private static final int REGISTRATION_HINT_Y = REGISTRATION_SLOT_HEADER_Y;
    private static final int REGISTRATION_TECHNIQUE_X_OFFSET = 198;
    private static final int REGISTRATION_TECHNIQUE_ROW_HEIGHT = 14;
    private static final int REGISTRATION_TECHNIQUE_ROW_STEP = 16;
    private static final int REGISTRATION_TECHNIQUE_START_Y = 66;
    private static final int REGISTRATION_TECHNIQUE_VISIBLE_ROWS = 10;
    private static final int REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH = 4;
    private static final int REGISTRATION_TECHNIQUE_SCROLLBAR_GAP = 4;
    private static final double REGISTRATION_CANDIDATE_DRAG_THRESHOLD_SQUARED = 16.0D;
    private static final int SETUP_LABEL_X_OFFSET = 16;
    private static final int SETUP_LABEL_WIDTH = 60;
    private static final int SETUP_BUTTON_WIDTH = 160;
    private static final int SETUP_BUTTON_HEIGHT = 20;
    private static final int SETUP_BUTTON_GAP = 8;
    private static final int SETUP_START_Y = 82;
    private static final int SETUP_ROW_STEP = 34;
    private static final int SETUP_COLUMN_HEADER_Y = 64;

    private static final TechniqueSetupCategory[] CATEGORIES = TechniqueSetupCategory.values();

    private TechniqueSetupViewState viewState;
    private MenuTab activeTab = MenuTab.REGISTRATION;
    private Button registrationTabButton;
    private Button setupTabButton;
    private final Button[] registrationButtons = new Button[10];
    private final Button[] setupNormalButtons = new Button[CATEGORIES.length];
    private final Button[] setupCrouchButtons = new Button[CATEGORIES.length];
    private int selectedRegistrationSlot;
    private int registrationScrollOffset;
    private boolean registrationScrollDragging;
    private TechniqueSetupRegistrationCandidate registrationPressedCandidate;
    private int registrationPressedButton = -1;
    private double registrationPressedMouseX;
    private double registrationPressedMouseY;
    private double registrationPressedGrabOffsetX;
    private double registrationPressedGrabOffsetY;
    private boolean registrationPressedCandidateDragged;
    private int registrationLastSlotClickSlot;
    private int registrationLastSlotClickButton = -1;
    private long registrationLastSlotClickTick = -1L;

    public TechniqueSetupScreen(TechniqueSetupViewState viewState) {
        super(Component.translatable("screen.jja.technique_menu.title"));
        this.viewState = viewState;
    }

    public void applyViewState(TechniqueSetupViewState viewState) {
        this.viewState = viewState;
        clampRegistrationScrollOffset();
        clearRegistrationPointerState();
        if (this.minecraft != null) {
            rebuildTechniqueMenuWidgets();
        }
    }

    @Override
    protected void init() {
        rebuildTechniqueMenuWidgets();
        syncRegistrationMode(this.activeTab == MenuTab.REGISTRATION);
    }

    @Override
    public void onClose() {
        syncRegistrationMode(false);
        clearRegistrationPointerState();
        super.onClose();
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawDimmedBackground(guiGraphics);
        drawPanel(guiGraphics);
        drawHeader(guiGraphics);
        if (this.activeTab == MenuTab.REGISTRATION) {
            drawRegistrationContent(guiGraphics, mouseX, mouseY);
        }
        refreshButtonLabels();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawActiveTabAccent(guiGraphics);
        if (this.activeTab == MenuTab.REGISTRATION) {
            drawDraggedRegistrationCandidate(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.activeTab == MenuTab.REGISTRATION && handleRegistrationMouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.activeTab == MenuTab.REGISTRATION && this.registrationScrollDragging && button == 0) {
            updateRegistrationScrollOffsetFromMouseY(mouseY);
            return true;
        }
        if (this.activeTab == MenuTab.REGISTRATION && handleRegistrationMouseDragged(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.activeTab == MenuTab.REGISTRATION && button == 0 && this.registrationScrollDragging) {
            this.registrationScrollDragging = false;
            clearRegistrationPointerState();
            return true;
        }
        if (this.activeTab == MenuTab.REGISTRATION && handleRegistrationMouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (this.activeTab == MenuTab.REGISTRATION && handleRegistrationMouseScrolled(mouseX, mouseY, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void rebuildTechniqueMenuWidgets() {
        this.clearWidgets();
        this.registrationButtonsFill(null);
        this.setupButtonsFill(null, null);
        clearRegistrationPointerState();
        clearRegistrationSlotClickState();

        int cardLeft = cardLeft();
        int cardTop = cardTop();
        int tabGroupWidth = TAB_BUTTON_WIDTH * 2 + TAB_BUTTON_GAP;
        int tabX = cardLeft + (CARD_WIDTH - tabGroupWidth) / 2;

        this.registrationTabButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> setActiveTab(MenuTab.REGISTRATION))
                .bounds(tabX, cardTop + TAB_BUTTON_Y, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT)
                .build()
        );
        this.setupTabButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> setActiveTab(MenuTab.SETUP))
                .bounds(tabX + TAB_BUTTON_WIDTH + TAB_BUTTON_GAP, cardTop + TAB_BUTTON_Y, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT)
                .build()
        );

        if (this.activeTab == MenuTab.REGISTRATION) {
            buildRegistrationWidgets(cardLeft, cardTop);
        } else {
            buildSetupWidgets(cardLeft, cardTop);
        }

        refreshButtonLabels();
    }

    private void buildRegistrationWidgets(int cardLeft, int cardTop) {
        clampRegistrationScrollOffset();
    }

    private void buildSetupWidgets(int cardLeft, int cardTop) {
        for (TechniqueSetupCategory category : CATEGORIES) {
            if (!this.viewState.isVisible(category)) {
                continue;
            }
            int columnX = setupColumnX(cardLeft, category);
            int normalRowY = cardTop + SETUP_START_Y + TechniqueSetupInputSlot.NORMAL.id() * SETUP_ROW_STEP;
            int crouchRowY = cardTop + SETUP_START_Y + TechniqueSetupInputSlot.CROUCH.id() * SETUP_ROW_STEP;
            this.setupNormalButtons[category.id()] = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> sendCycle(category, TechniqueSetupInputSlot.NORMAL))
                    .bounds(columnX, normalRowY, SETUP_BUTTON_WIDTH, SETUP_BUTTON_HEIGHT)
                    .build()
            );
            this.setupCrouchButtons[category.id()] = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> sendCycle(category, TechniqueSetupInputSlot.CROUCH))
                    .bounds(columnX, crouchRowY, SETUP_BUTTON_WIDTH, SETUP_BUTTON_HEIGHT)
                    .build()
            );
        }
    }

    private void refreshButtonLabels() {
        if (this.registrationTabButton != null) {
            this.registrationTabButton.setMessage(tabLabel(MenuTab.REGISTRATION));
        }
        if (this.setupTabButton != null) {
            this.setupTabButton.setMessage(tabLabel(MenuTab.SETUP));
        }
        if (this.activeTab == MenuTab.SETUP) {
            refreshSetupLabels();
        }
    }

    private void drawRegistrationContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        clampRegistrationScrollOffset();
        int left = cardLeft();
        int top = cardTop();
        drawRegistrationPaneBackground(guiGraphics, left, top);
        drawRegistrationSlotRows(guiGraphics, mouseX, mouseY, left, top);
        drawRegistrationTechniqueRows(guiGraphics, mouseX, mouseY, left, top);
    }

    private void drawRegistrationPaneBackground(GuiGraphics guiGraphics, int left, int top) {
        int paneTop = top + 60;
        int paneBottom = top + 224;
        guiGraphics.fill(left + 11, paneTop, left + 183, paneBottom, 0xAA0F141A);
        guiGraphics.fill(left + REGISTRATION_TECHNIQUE_X_OFFSET - 6, paneTop, left + CARD_WIDTH - 12, paneBottom, 0xAA0F141A);
        guiGraphics.fill(left + 188, paneTop + 1, left + 189, paneBottom - 1, 0xFF252A33);
    }

    private void drawRegistrationSlotRows(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top) {
        List<TechniqueSetupRegistrationCandidate> candidates = this.viewState.registrationCandidates();
        int dropTargetSlot = currentRegistrationDropTargetSlot(mouseX, mouseY, left, top);
        int highlightedSlot = dropTargetSlot != 0 ? dropTargetSlot : this.selectedRegistrationSlot;
        int rowX = left + REGISTRATION_SLOT_X_OFFSET;
        for (int slot = 1; slot <= 10; slot++) {
            int rowY = top + REGISTRATION_SLOT_START_Y + (slot - 1) * REGISTRATION_SLOT_ROW_STEP;
            boolean selected = highlightedSlot == slot;
            boolean hovered = isMouseOverArea(mouseX, mouseY, rowX, rowY, REGISTRATION_SLOT_ROW_WIDTH, REGISTRATION_SLOT_ROW_HEIGHT);
            drawRowBackground(guiGraphics, rowX, rowY, REGISTRATION_SLOT_ROW_WIDTH, REGISTRATION_SLOT_ROW_HEIGHT, selected, hovered);
            guiGraphics.enableScissor(rowX + 6, rowY, rowX + REGISTRATION_SLOT_ROW_WIDTH - 6, rowY + REGISTRATION_SLOT_ROW_HEIGHT);
            try {
                Component label = registrationSlotLabel(slot);
                guiGraphics.drawString(
                    this.font,
                    label,
                    rowX + 6,
                    rowY + 3,
                    selected ? 0xF3F4F6 : 0xE5E7EB,
                    true
                );
            } finally {
                guiGraphics.disableScissor();
            }
        }
        if (candidates.isEmpty()) {
            guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("screen.jja.technique_menu.registration.empty"),
                left + REGISTRATION_TECHNIQUE_X_OFFSET + registrationTechniqueRowWidth() / 2,
                top + REGISTRATION_TECHNIQUE_START_Y + 34,
                0x9CA3AF
            );
        }
    }

    private void drawRegistrationTechniqueRows(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top) {
        List<TechniqueSetupRegistrationCandidate> candidates = this.viewState.registrationCandidates();
        if (candidates.isEmpty()) {
            return;
        }
        int visibleRows = registrationVisibleRows(candidates.size());
        int maxScrollOffset = Math.max(0, candidates.size() - visibleRows);
        int startIndex = Math.min(this.registrationScrollOffset, maxScrollOffset);
        int rowX = left + REGISTRATION_TECHNIQUE_X_OFFSET;
        int rowWidth = registrationTechniqueRowWidth();
        int rowY = top + REGISTRATION_TECHNIQUE_START_Y;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int candidateIndex = startIndex + rowIndex;
            if (candidateIndex >= candidates.size()) {
                break;
            }
            int currentRowY = rowY + rowIndex * REGISTRATION_TECHNIQUE_ROW_STEP;
            TechniqueSetupRegistrationCandidate candidate = candidates.get(candidateIndex);
            boolean hovered = isMouseOverArea(mouseX, mouseY, rowX, currentRowY, rowWidth, REGISTRATION_TECHNIQUE_ROW_HEIGHT);
            drawRowBackground(guiGraphics, rowX, currentRowY, rowWidth, REGISTRATION_TECHNIQUE_ROW_HEIGHT, false, hovered);
            int textColor = this.selectedRegistrationSlot == 0 ? 0x7C8795 : hovered ? 0xF8FAFC : 0xE5E7EB;
            String truncatedName = this.font.plainSubstrByWidth(candidate.displayName(), rowWidth - 10);
            guiGraphics.drawString(this.font, truncatedName, rowX + 6, currentRowY + 3, textColor, true);
        }
        if (maxScrollOffset > 0) {
            drawRegistrationScrollBar(guiGraphics, left, top, visibleRows, candidates.size(), maxScrollOffset);
        }
    }

    private void drawRegistrationScrollBar(GuiGraphics guiGraphics, int left, int top, int visibleRows, int totalRows, int maxScrollOffset) {
        int trackLeft = left + CARD_WIDTH - REGISTRATION_TECHNIQUE_SCROLLBAR_GAP - REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH;
        int trackTop = top + REGISTRATION_TECHNIQUE_START_Y;
        int trackHeight = registrationTechniqueListHeight();
        guiGraphics.fill(trackLeft, trackTop, trackLeft + REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH, trackTop + trackHeight, 0x66111418);
        int thumbHeight = Math.max(12, trackHeight * visibleRows / totalRows);
        int thumbOffset = (trackHeight - thumbHeight) * this.registrationScrollOffset / maxScrollOffset;
        guiGraphics.fill(trackLeft, trackTop + thumbOffset, trackLeft + REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH, trackTop + thumbOffset + thumbHeight, CARD_ACCENT);
    }

    private boolean handleRegistrationMouseClicked(double mouseX, double mouseY, int button) {
        int left = cardLeft();
        int top = cardTop();
        if (button == 0 && isMouseOverRegistrationScrollBar(mouseX, mouseY)) {
            this.registrationScrollDragging = true;
            clearRegistrationPointerState();
            updateRegistrationScrollOffsetFromMouseY(mouseY);
            return true;
        }
        if (button == 0 || button == 1) {
            int slot = slotAt(mouseX, mouseY, left, top);
            if (slot != 0) {
                long currentTick = currentRegistrationGameTime();
                if (isRecentRegistrationSlotClick(slot, button, currentTick) && hasRegisteredTechniqueInSlot(slot)) {
                    clearRegisteredTechnique(slot);
                    clearRegistrationPointerState();
                    return true;
                }
                this.selectedRegistrationSlot = slot;
                this.registrationLastSlotClickSlot = slot;
                this.registrationLastSlotClickButton = button;
                this.registrationLastSlotClickTick = currentTick;
                clearRegistrationPointerState();
                return true;
            }
        }
        if (button == 0 || button == 1) {
            RegistrationCandidateHit candidateHit = candidateHitAt(mouseX, mouseY, left, top);
            if (candidateHit != null) {
                beginRegistrationCandidateDrag(candidateHit, button, mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    private boolean handleRegistrationMouseDragged(double mouseX, double mouseY, int button) {
        if (this.registrationPressedCandidate == null || button != this.registrationPressedButton) {
            return false;
        }
        if (!this.registrationPressedCandidateDragged && hasDraggedFarEnough(mouseX, mouseY)) {
            this.registrationPressedCandidateDragged = true;
        }
        return true;
    }

    private boolean handleRegistrationMouseReleased(double mouseX, double mouseY, int button) {
        if (this.registrationPressedCandidate == null || button != this.registrationPressedButton) {
            return false;
        }
        int left = cardLeft();
        int top = cardTop();
        int slot = slotAt(mouseX, mouseY, left, top);
        if (slot != 0) {
            registerCandidate(slot, this.registrationPressedCandidate);
            clearRegistrationPointerState();
            return true;
        }
        if (!this.registrationPressedCandidateDragged) {
            TechniqueSetupRegistrationCandidate candidate = candidateAt(mouseX, mouseY, left, top);
            if (candidate == this.registrationPressedCandidate && this.selectedRegistrationSlot != 0) {
                registerCandidate(this.selectedRegistrationSlot, candidate);
                clearRegistrationPointerState();
                return true;
            }
        }
        clearRegistrationPointerState();
        return true;
    }

    private boolean handleRegistrationMouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!isMouseOverRegistrationTechniqueList(mouseX, mouseY)) {
            return false;
        }
        if (scrollY == 0.0D) {
            return false;
        }
        int direction = scrollY > 0.0D ? -1 : 1;
        int newOffset = this.registrationScrollOffset + direction;
        int maxScrollOffset = registrationMaxScrollOffset();
        if (newOffset < 0) {
            newOffset = 0;
        } else if (newOffset > maxScrollOffset) {
            newOffset = maxScrollOffset;
        }
        if (newOffset == this.registrationScrollOffset) {
            return false;
        }
        this.registrationScrollOffset = newOffset;
        return true;
    }

    private int slotAt(double mouseX, double mouseY, int left, int top) {
        int rowX = left + REGISTRATION_SLOT_X_OFFSET;
        for (int slot = 1; slot <= 10; slot++) {
            int rowY = top + REGISTRATION_SLOT_START_Y + (slot - 1) * REGISTRATION_SLOT_ROW_STEP;
            if (isMouseOverArea(mouseX, mouseY, rowX, rowY, REGISTRATION_SLOT_ROW_WIDTH, REGISTRATION_SLOT_ROW_HEIGHT)) {
                return slot;
            }
        }
        return 0;
    }

    private TechniqueSetupRegistrationCandidate candidateAt(double mouseX, double mouseY, int left, int top) {
        RegistrationCandidateHit hit = candidateHitAt(mouseX, mouseY, left, top);
        if (hit == null) {
            return null;
        }
        return hit.candidate();
    }

    private RegistrationCandidateHit candidateHitAt(double mouseX, double mouseY, int left, int top) {
        List<TechniqueSetupRegistrationCandidate> candidates = this.viewState.registrationCandidates();
        if (candidates.isEmpty()) {
            return null;
        }
        int visibleRows = registrationVisibleRows(candidates.size());
        int maxScrollOffset = Math.max(0, candidates.size() - visibleRows);
        int startIndex = Math.min(this.registrationScrollOffset, maxScrollOffset);
        int rowX = left + REGISTRATION_TECHNIQUE_X_OFFSET;
        int rowWidth = registrationTechniqueRowWidth();
        int rowY = top + REGISTRATION_TECHNIQUE_START_Y;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int candidateIndex = startIndex + rowIndex;
            if (candidateIndex >= candidates.size()) {
                break;
            }
            int currentRowY = rowY + rowIndex * REGISTRATION_TECHNIQUE_ROW_STEP;
            if (isMouseOverArea(mouseX, mouseY, rowX, currentRowY, rowWidth, REGISTRATION_TECHNIQUE_ROW_HEIGHT)) {
                return new RegistrationCandidateHit(candidates.get(candidateIndex), rowX, currentRowY);
            }
        }
        return null;
    }

    private boolean isMouseOverRegistrationTechniqueList(double mouseX, double mouseY) {
        int left = cardLeft() + REGISTRATION_TECHNIQUE_X_OFFSET;
        int top = cardTop() + REGISTRATION_TECHNIQUE_START_Y;
        int width = registrationTechniqueRowWidth() + REGISTRATION_TECHNIQUE_SCROLLBAR_GAP + REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH;
        int height = registrationTechniqueListHeight();
        return isMouseOverArea(mouseX, mouseY, left, top, width, height);
    }

    private boolean isMouseOverRegistrationScrollBar(double mouseX, double mouseY) {
        if (registrationMaxScrollOffset() <= 0) {
            return false;
        }
        int left = cardLeft() + CARD_WIDTH - REGISTRATION_TECHNIQUE_SCROLLBAR_GAP - REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH;
        int top = cardTop() + REGISTRATION_TECHNIQUE_START_Y;
        int height = registrationTechniqueListHeight();
        return isMouseOverArea(mouseX, mouseY, left, top, REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH, height);
    }

    private void updateRegistrationScrollOffsetFromMouseY(double mouseY) {
        int maxScrollOffset = registrationMaxScrollOffset();
        if (maxScrollOffset <= 0) {
            this.registrationScrollOffset = 0;
            return;
        }
        List<TechniqueSetupRegistrationCandidate> candidates = this.viewState.registrationCandidates();
        int visibleRows = registrationVisibleRows(candidates.size());
        int trackHeight = registrationTechniqueListHeight();
        int thumbHeight = Math.max(12, trackHeight * visibleRows / candidates.size());
        int draggableHeight = Math.max(0, trackHeight - thumbHeight);
        if (draggableHeight == 0) {
            this.registrationScrollOffset = 0;
            return;
        }
        double trackTop = cardTop() + REGISTRATION_TECHNIQUE_START_Y;
        double relativeY = mouseY - trackTop - thumbHeight / 2.0D;
        int newOffset = (int) Math.round(relativeY * maxScrollOffset / draggableHeight);
        if (newOffset < 0) {
            newOffset = 0;
        } else if (newOffset > maxScrollOffset) {
            newOffset = maxScrollOffset;
        }
        this.registrationScrollOffset = newOffset;
    }

    private int currentRegistrationDropTargetSlot(double mouseX, double mouseY, int left, int top) {
        if (this.registrationPressedCandidate == null || !this.registrationPressedCandidateDragged) {
            return 0;
        }
        return slotAt(mouseX, mouseY, left, top);
    }

    private boolean isMouseOverArea(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private void drawRowBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean selected, boolean hovered) {
        int fill = selected ? 0xFF243041 : hovered ? 0xFF1C222B : 0xFF151920;
        int topBorder = selected ? CARD_ACCENT : CARD_BORDER_DARK;
        int bottomBorder = selected ? CARD_ACCENT : CARD_BORDER;
        guiGraphics.fill(x, y, x + width, y + height, fill);
        guiGraphics.fill(x, y, x + width, y + 1, topBorder);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, bottomBorder);
        guiGraphics.fill(x, y, x + 1, y + height, CARD_BORDER_DARK);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, CARD_BORDER);
    }

    private Component registrationSlotLabel(int slot) {
        return Component.translatable(
            "screen.jja.technique_menu.slot.entry",
            Component.translatable(
                "screen.jja.technique_menu.slot.label",
                String.format(Locale.ROOT, "%02d", slot),
                JjaSkillKeyMappings.getSkillSlotKeyLabel(slot)
            ),
            registrationSlotTechniqueLabel(slot)
        );
    }

    private void beginRegistrationCandidateDrag(RegistrationCandidateHit candidateHit, int button, double mouseX, double mouseY) {
        this.registrationPressedCandidate = candidateHit.candidate();
        this.registrationPressedButton = button;
        this.registrationPressedMouseX = mouseX;
        this.registrationPressedMouseY = mouseY;
        this.registrationPressedGrabOffsetX = mouseX - candidateHit.rowX();
        this.registrationPressedGrabOffsetY = mouseY - candidateHit.rowY();
        this.registrationPressedCandidateDragged = false;
    }

    private boolean hasDraggedFarEnough(double mouseX, double mouseY) {
        double deltaX = mouseX - this.registrationPressedMouseX;
        double deltaY = mouseY - this.registrationPressedMouseY;
        return deltaX * deltaX + deltaY * deltaY >= REGISTRATION_CANDIDATE_DRAG_THRESHOLD_SQUARED;
    }

    private void registerCandidate(int slot, TechniqueSetupRegistrationCandidate candidate) {
        JjaNetwork.CHANNEL.sendToServer(
            new JjaTechniqueMenuRegisterMessage(
                slot,
                candidate.selectTechniqueId(),
                candidate.canonicalName()
            )
        );
        this.selectedRegistrationSlot = 0;
        clearRegistrationSlotClickState();
    }

    private void clearRegisteredTechnique(int slot) {
        JjaNetwork.CHANNEL.sendToServer(new JjaTechniqueMenuRegisterMessage(slot, 0, ""));
        this.selectedRegistrationSlot = 0;
        clearRegistrationSlotClickState();
    }

    private boolean hasRegisteredTechniqueInSlot(int slot) {
        Player player = player();
        if (player == null) {
            return false;
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        if (skillState == null) {
            return false;
        }
        int ctId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        return ctId != 0 && skillState.hasRegisteredCurseTechnique(slot, ctId);
    }

    private boolean isRecentRegistrationSlotClick(int slot, int button, long currentTick) {
        return currentTick >= 0
            && this.registrationLastSlotClickTick >= 0
            && currentTick - this.registrationLastSlotClickTick <= 10L
            && this.registrationLastSlotClickSlot == slot
            && this.registrationLastSlotClickButton == button;
    }

    private long currentRegistrationGameTime() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return -1L;
        }
        return this.minecraft.level.getGameTime();
    }

    private void clearRegistrationPointerState() {
        this.registrationScrollDragging = false;
        this.registrationPressedCandidate = null;
        this.registrationPressedButton = -1;
        this.registrationPressedMouseX = 0.0D;
        this.registrationPressedMouseY = 0.0D;
        this.registrationPressedGrabOffsetX = 0.0D;
        this.registrationPressedGrabOffsetY = 0.0D;
        this.registrationPressedCandidateDragged = false;
    }

    private void clearRegistrationSlotClickState() {
        this.registrationLastSlotClickSlot = 0;
        this.registrationLastSlotClickButton = -1;
        this.registrationLastSlotClickTick = -1L;
    }

    private record RegistrationCandidateHit(TechniqueSetupRegistrationCandidate candidate, int rowX, int rowY) {
    }

    private Component registrationSlotTechniqueLabel(int slot) {
        Player player = player();
        if (player == null) {
            return Component.translatable("key.keyboard.unknown");
        }
        PlayerSkillState skillState = PlayerStateAccess.skill(player);
        if (skillState == null) {
            return Component.translatable("key.keyboard.unknown");
        }
        int ctId = JjaJujutsucraftCompat.jjaGetActiveCurseTechniqueId(player);
        if (ctId == 0 || !skillState.hasRegisteredCurseTechnique(slot, ctId)) {
            return Component.translatable("key.keyboard.unknown");
        }
        int selectTechniqueId = skillState.getRegisteredCurseTechnique(slot, ctId);
        String canonicalName = skillState.getRegisteredCurseTechniqueName(slot, ctId);
        if (canonicalName == null || canonicalName.isEmpty()) {
            return Component.translatable("key.keyboard.unknown");
        }
        String displayName = JjaTechniqueDisplayNameResolver.resolveDisplayName(player, ctId, selectTechniqueId, canonicalName);
        if (displayName == null || displayName.isEmpty()) {
            return Component.translatable("key.keyboard.unknown");
        }
        return Component.literal(displayName);
    }

    private int registrationTechniqueRowWidth() {
        return CARD_WIDTH - REGISTRATION_TECHNIQUE_X_OFFSET - REGISTRATION_TECHNIQUE_SCROLLBAR_GAP - REGISTRATION_TECHNIQUE_SCROLLBAR_WIDTH - 12;
    }

    private int registrationMaxScrollOffset() {
        return Math.max(0, this.viewState.registrationCandidates().size() - registrationVisibleRows(this.viewState.registrationCandidates().size()));
    }

    static int registrationVisibleRows(int candidateCount) {
        return Math.min(REGISTRATION_TECHNIQUE_VISIBLE_ROWS, Math.max(candidateCount, 0));
    }

    static int registrationTechniqueListHeight() {
        return REGISTRATION_TECHNIQUE_VISIBLE_ROWS * REGISTRATION_TECHNIQUE_ROW_STEP - 2;
    }

    private void clampRegistrationScrollOffset() {
        int maxScrollOffset = registrationMaxScrollOffset();
        if (this.registrationScrollOffset < 0) {
            this.registrationScrollOffset = 0;
        } else if (this.registrationScrollOffset > maxScrollOffset) {
            this.registrationScrollOffset = maxScrollOffset;
        }
    }

    private void refreshSetupLabels() {
        for (TechniqueSetupCategory category : CATEGORIES) {
            if (!this.viewState.isVisible(category)) {
                continue;
            }
            Button normalButton = this.setupNormalButtons[category.id()];
            Button crouchButton = this.setupCrouchButtons[category.id()];
            if (normalButton != null) {
                normalButton.setMessage(buildSetupLabel(category, TechniqueSetupInputSlot.NORMAL));
            }
            if (crouchButton != null) {
                crouchButton.setMessage(buildSetupLabel(category, TechniqueSetupInputSlot.CROUCH));
            }
        }
    }

    private Component buildSetupLabel(TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        return currentOptionLabel(category, slot);
    }

    private Component currentOptionLabel(TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        int selectionId = this.viewState.selectionId(category, slot);
        if (category == TechniqueSetupCategory.DOMAIN_TYPE) {
            return DomainTypeOption.fromId(selectionId).displayName();
        }
        return AntiDomainTechniqueOption.fromId(selectionId).displayName();
    }

    private void sendCycle(TechniqueSetupCategory category, TechniqueSetupInputSlot slot) {
        JjaNetwork.CHANNEL.sendToServer(new JjaTechniqueSetupCycleMessage(category, slot));
    }

    private void setActiveTab(MenuTab tab) {
        if (this.activeTab == tab) {
            return;
        }
        this.registrationScrollDragging = false;
        this.activeTab = tab;
        syncRegistrationMode(tab == MenuTab.REGISTRATION);
        if (this.minecraft != null) {
            rebuildTechniqueMenuWidgets();
        }
    }

    private void syncRegistrationMode(boolean enabled) {
        if (player() == null) {
            return;
        }
        JjaNetwork.CHANNEL.sendToServer(new JjaTechniqueMenuRegistrationModeMessage(enabled));
    }

    private void drawDimmedBackground(GuiGraphics guiGraphics) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, BACKGROUND_TOP, BACKGROUND_BOTTOM);
    }

    private void drawPanel(GuiGraphics guiGraphics) {
        int left = cardLeft();
        int top = cardTop();
        guiGraphics.fill(left, top, left + CARD_WIDTH, top + CARD_HEIGHT, CARD_BACKGROUND);
        guiGraphics.fill(left, top, left + CARD_WIDTH, top + 1, CARD_BORDER);
        guiGraphics.fill(left, top + CARD_HEIGHT - 1, left + CARD_WIDTH, top + CARD_HEIGHT, CARD_BORDER_DARK);
        guiGraphics.fill(left, top, left + 1, top + CARD_HEIGHT, CARD_BORDER_DARK);
        guiGraphics.fill(left + CARD_WIDTH - 1, top, left + CARD_WIDTH, top + CARD_HEIGHT, CARD_BORDER);
        guiGraphics.fill(left + 1, top + 1, left + CARD_WIDTH - 1, top + CARD_HEIGHT - 1, 0x18000000);
    }

    private void drawHeader(GuiGraphics guiGraphics) {
        int left = cardLeft();
        int top = cardTop();
        if (this.activeTab == MenuTab.REGISTRATION) {
            guiGraphics.drawString(
                this.font,
                Component.translatable("screen.jja.technique_menu.registration.hint"),
                left + 14,
                top + CARD_HEIGHT - 19,
                0x9CA3AF,
                true
            );
            guiGraphics.drawString(
                this.font,
                Component.translatable("screen.jja.technique_menu.registration.slot_header"),
                left + REGISTRATION_SLOT_X_OFFSET,
                top + REGISTRATION_SLOT_HEADER_Y,
                0xD1D5DB,
                true
            );
            guiGraphics.drawString(
                this.font,
                Component.translatable(
                    this.registrationPressedCandidate == null
                        ? "screen.jja.technique_menu.registration.select_slot_hint"
                        : "screen.jja.technique_menu.registration.select_technique_hint"
                ),
                left + REGISTRATION_TECHNIQUE_X_OFFSET,
                top + REGISTRATION_HINT_Y,
                0x9CA3AF,
                true
            );
            return;
        }
        guiGraphics.drawString(
            this.font,
            Component.translatable("screen.jja.technique_menu.setup.hint"),
            left + 14,
            top + CARD_HEIGHT - 19,
            0x9CA3AF,
            true
        );
        if (this.activeTab == MenuTab.SETUP) {
            for (TechniqueSetupInputSlot slot : TechniqueSetupInputSlot.values()) {
                int rowY = top + SETUP_START_Y + slot.id() * SETUP_ROW_STEP + 5;
                guiGraphics.drawString(this.font, slot.displayName(), left + SETUP_LABEL_X_OFFSET, rowY, 0xE5E7EB, true);
            }
            for (TechniqueSetupCategory category : CATEGORIES) {
                if (!this.viewState.isVisible(category)) {
                    continue;
                }
                int centerX = setupColumnX(left, category) + SETUP_BUTTON_WIDTH / 2;
                guiGraphics.drawCenteredString(this.font, category.displayName(), centerX, top + SETUP_COLUMN_HEADER_Y, 0xD1D5DB);
            }
        }
    }

    private void drawActiveTabAccent(GuiGraphics guiGraphics) {
        Button button = this.activeTab == MenuTab.REGISTRATION ? this.registrationTabButton : this.setupTabButton;
        if (button == null) {
            return;
        }
        int left = button.getX() + 3;
        int right = button.getX() + button.getWidth() - 3;
        int bottom = button.getY() + button.getHeight();
        guiGraphics.fill(left, bottom - 2, right, bottom, CARD_ACCENT);
    }

    private void drawDraggedRegistrationCandidate(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.registrationPressedCandidate == null || !this.registrationPressedCandidateDragged) {
            return;
        }
        String candidateLabel = this.registrationPressedCandidate.displayName();
        int textWidth = this.font.width(candidateLabel);
        int boxWidth = Math.min(Math.max(registrationTechniqueRowWidth(), textWidth + 12), this.width - 12);
        int boxHeight = REGISTRATION_TECHNIQUE_ROW_HEIGHT;
        int x = (int) Math.round(mouseX - this.registrationPressedGrabOffsetX);
        int y = (int) Math.round(mouseY - this.registrationPressedGrabOffsetY);
        if (x + boxWidth > this.width - 6) {
            x = this.width - 6 - boxWidth;
        }
        if (x < 6) {
            x = 6;
        }
        if (y + boxHeight > this.height - 6) {
            y = this.height - 6 - boxHeight;
        }
        if (y < 6) {
            y = 6;
        }
        drawRowBackground(guiGraphics, x, y, boxWidth, boxHeight, true, false);
        String visibleLabel = textWidth > boxWidth - 12 ? this.font.plainSubstrByWidth(candidateLabel, boxWidth - 12) : candidateLabel;
        guiGraphics.drawString(this.font, visibleLabel, x + 6, y + 3, 0xF8FAFC, true);
    }

    private Component tabLabel(MenuTab tab) {
        MutableComponent label = Component.translatable(
            tab == MenuTab.REGISTRATION
                ? "screen.jja.technique_menu.tab.registration"
                : "screen.jja.technique_menu.tab.setup"
        );
        if (tab == this.activeTab) {
            return label.copy().withStyle(ChatFormatting.BOLD);
        }
        return label.copy().withStyle(ChatFormatting.GRAY);
    }

    private int setupColumnIndex(TechniqueSetupCategory category) {
        int columnIndex = 0;
        for (TechniqueSetupCategory candidate : CATEGORIES) {
            if (!this.viewState.isVisible(candidate)) {
                continue;
            }
            if (candidate == category) {
                return columnIndex;
            }
            columnIndex++;
        }
        return 0;
    }

    private int setupColumnX(int cardLeft, TechniqueSetupCategory category) {
        return cardLeft + SETUP_LABEL_X_OFFSET + SETUP_LABEL_WIDTH + SETUP_BUTTON_GAP + setupColumnIndex(category) * (SETUP_BUTTON_WIDTH + SETUP_BUTTON_GAP);
    }

    private void registrationButtonsFill(Button button) {
        for (int i = 0; i < this.registrationButtons.length; i++) {
            this.registrationButtons[i] = button;
        }
    }

    private void setupButtonsFill(Button normalButton, Button crouchButton) {
        for (int i = 0; i < this.setupNormalButtons.length; i++) {
            this.setupNormalButtons[i] = normalButton;
            this.setupCrouchButtons[i] = crouchButton;
        }
    }

    private Player player() {
        return this.minecraft == null ? null : this.minecraft.player;
    }

    private int cardLeft() {
        return (this.width - CARD_WIDTH) / 2;
    }

    private int cardTop() {
        return (this.height - CARD_HEIGHT) / 2;
    }

    private enum MenuTab {
        REGISTRATION,
        SETUP
    }
}
