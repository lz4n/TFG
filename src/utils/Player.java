package utils;

import listener.MouseListener;
import main.Main;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import ui.container.Container;
import ui.container.Frame;
import ui.container.Inventory;
import ui.widget.*;
import ui.widget.buttonWidget.AbstractButtonWidget;
import ui.widget.buttonWidget.FrameButtonWidget;
import ui.widget.textWidgets.DateTimeTextBox;
import ui.widget.textWidgets.SmallTextBox;
import ui.widget.Tab;
import utils.render.Camera;
import utils.render.Window;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.feature.Feature;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Representa a un jugador.
 */
public class Player {

    /**
     * Pestañas que tiene el inventario del jugador.
     */
    private final static Tab GENERAL_TAB = new Tab(Textures.SELECTED_GENERAL_TAB_ICON, Textures.UNSELECTED_GENERAL_TAB_ICON),
        ZONING_TAB = new Tab(Textures.SELECTED_ZONING_TAB_ICON, Textures.UNSELECTED_ZONING_TAB_ICON),
        INDUSTRY_TAB = new Tab(Textures.SELECTED_INDUSTRY_TAB_ICON, Textures.UNSELECTED_INDUSTRY_TAB_ICON),
        SERVICES_TAB = new Tab(Textures.SELECTED_SERVICES_TAB_ICON, Textures.UNSELECTED_SERVICES_TAB_ICON),
        NATURE_TAB = new Tab(Textures.SELECTED_NATURE_TAB_ICON, Textures.UNSELECTED_NATURE_TAB_ICON),
        SPECIAL_TAB = new Tab(Textures.SELECTED_SPECIAL_TAB_ICON, Textures.UNSELECTED_SPECIAL_TAB_ICON);

    /**
     * Items por pestaña del inventario.
     */
    private final static HashMap<Tab, List<List<MenuItem>>> ITEMS = new HashMap<>() {{
        this.put(Player.GENERAL_TAB, new ArrayList<>());
        this.put(Player.ZONING_TAB, new ArrayList<>());
        this.put(Player.INDUSTRY_TAB, new ArrayList<>());
        this.put(Player.SERVICES_TAB, new ArrayList<>());
        this.put(Player.NATURE_TAB, Arrays.asList(
                Arrays.asList(
                        new MenuItem(Textures.BUSH, Feature.FeatureType.BUSH, 0),
                        new MenuItem(Textures.TREE1, Feature.FeatureType.TREE, 0),
                        new MenuItem(Textures.TREE2, Feature.FeatureType.TREE, 1)),
                Arrays.asList(
                        new MenuItem(Textures.TULIP, Feature.FeatureType.FLOWER, 0),
                        new MenuItem(Textures.TULIP_2, Feature.FeatureType.FLOWER, 1),
                        new MenuItem(Textures.BLUE_ORCHID, Feature.FeatureType.FLOWER, 2),
                        new MenuItem(Textures.DANDELION, Feature.FeatureType.FLOWER, 3),
                        new MenuItem(Textures.RED_LILY, Feature.FeatureType.FLOWER, 4)),
                List.of(
                        new MenuItem(Textures.ROCK, Feature.FeatureType.ROCK, 0))
        ));
        this.put(Player.SPECIAL_TAB, new ArrayList<>());

    }};

    /**
     * Inventario del jugador.
     */
    private final Inventory INVENTORY = new Inventory();

    /**
     * Menú de pausa del jugador.
     */
    private final Frame GAME_MENU = new Frame("Menu");

    /**
     * Mapa que almacena los slots correspondientes a cada item del inventario.
     */
    private final HashMap<MenuItem, SlotWidget> MENU_ITEMS_BY_SLOT = new HashMap<>();

    /**
     * Cámara del jugador.
     */
    private final Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    /**
     * Almacena el contenedor que está utilizando el jugador.
     */
    private Container container = this.INVENTORY;

    /**
     * Widget que muestra la fecha del mundo.
     */
    private DateTimeTextBox timeTextBox;

    /**
     * Widget para cambiar la velocidad de juego.
     */
    private AbstractButtonWidget gameSpeedButton;

    /**
     * Widget que indica que se está utilizando el bulldozer.
     */
    private ScreenIndicatorWidget bulldozerIndicator = new ScreenIndicatorWidget(20, 340, Textures.BULLDOZER_ICON);

    /**
     * Banderas del jugador, indican si está ocultando la interfaz, si está utilizando el bulldozer, si tiene el ratón
     * sobre el inventario y si está en pausa.
     */
    private boolean isHidingUI = false, isUsingBulldozer = false, isMouseOnInventory = false, isPaused = false;

    /**
     * Almacena la velocidad de juego anterior.
     */
    private int previousGameSpeed = Main.tickSpeed;

    /**
     * Indica el item que está seleccionado para ser colocado.
     */
    private MenuItem selectedFeatureToPlace;

    public Player() {
    }

    public void init() {
        //** INVENTARIO **//
        this.timeTextBox = new DateTimeTextBox(4, 4);

        this.gameSpeedButton = new AbstractButtonWidget(4, 24, Textures.HORIZONTAL_BUTTON, Textures.CLICKED_HORIZONTAL_BUTTON, new Vector2f(0, -1), new BoundingBox(36, 16)) {
            @Override
            public Texture getIcon() {
                return switch (Main.tickSpeed) {
                    case 1 -> Textures.X1_GAME_ICON;
                    case 2 -> Textures.X2_GAME_ICON;
                    case 3 -> Textures.X3_GAME_ICON;
                    case 5 -> Textures.X5_GAME_ICON;
                    default -> Textures.PAUSED_GAME_ICON;
                };
            }
        };
        this.gameSpeedButton.setOnClickEvent(() -> {
            switch (Main.tickSpeed) {
                case 1 -> Main.tickSpeed = 2;
                case 2 -> Main.tickSpeed = 3;
                case 3 -> Main.tickSpeed = 5;
                case 5 -> Main.tickSpeed = 0;
                default -> Main.tickSpeed = 1;
            };
        });

        this.bulldozerIndicator.setOnClickEvent(() -> {
            this.isUsingBulldozer = false;
        });

        this.INVENTORY.addTab(Player.GENERAL_TAB);
        this.INVENTORY.addTab(Player.ZONING_TAB);
        this.INVENTORY.addTab(Player.INDUSTRY_TAB);
        this.INVENTORY.addTab(Player.NATURE_TAB);
        this.INVENTORY.addTab(Player.SERVICES_TAB);
        this.INVENTORY.addTab(Player.SPECIAL_TAB);

        this.INVENTORY.addWidgetToAllTabs(this.timeTextBox);
        this.INVENTORY.addWidgetToAllTabs(this.gameSpeedButton);
        this.INVENTORY.addWidgetToAllTabs(new SeparatorWidget(44, 0));

        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 4));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 14));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 24));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 34));

        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 4));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 14));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 24));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 34));

        Player.ITEMS.keySet().forEach(tab -> this.addFeaturesWidgetToTab(130, tab));

        this.INVENTORY.addWidget(new SlotWidget(800, 24, Textures.DUCK), Player.SPECIAL_TAB);

        this.INVENTORY.setCurrentTab(Player.GENERAL_TAB);

        //** MENU DE JUEGO **//
        FrameButtonWidget returnGameButton = new FrameButtonWidget(4, 24, "Volver");
        returnGameButton.setOnClickEvent(this::openInventory);
        this.GAME_MENU.addWidget(returnGameButton);
        this.GAME_MENU.addWidget(new FrameButtonWidget(4, 56, "Abrir mundo"));
        this.GAME_MENU.addWidget(new FrameButtonWidget(4, 88, "Configuracion"));
        FrameButtonWidget leaveGameButton = new FrameButtonWidget(4, 120, "Guardar y salir");
        leaveGameButton.setOnClickEvent(() -> {
            File worldFile = new File(Main.installDirectory, "worlds/world.dat");
            worldFile.getParentFile().mkdirs();
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(worldFile))) {
                outputStream.writeObject(Main.world);
                outputStream.writeUTF(Main.world.getName());
            } catch (IOException exception) {
                Logger.sendMessage("Error guardando el mundo: %s.\n", Logger.LogMessageType.FATAL, exception.getMessage());
            }
            GLFW.glfwSetWindowShouldClose(Window.window, true);
        });
        this.GAME_MENU.addWidget(leaveGameButton);
    }

    private void addBulldozerToInventory(int posX, Tab tab) {
        this.INVENTORY.addWidget(new SeparatorWidget(posX, 0), tab);

        Widget bulldozerButton = new AbstractButtonWidget(posX + 6, 4, Textures.VERTICAL_BUTTON, Textures.CLICKED_VERTICAL_BUTTON, new Vector2f(1, 0), new BoundingBox(16, 36)) {
            @Override
            public Texture getIcon() {
                return Textures.BULLDOZER_BUTTON_ICON;
            }
        };
        bulldozerButton.setOnClickEvent(this::toggleBulldozer);

        this.INVENTORY.addWidget(bulldozerButton, tab);
    }

    private void addFeaturesWidgetToTab(int widgetXPos, Tab tab) {
        final int SLOT_SEPARATION = 20, WIDGET_POS_TOP = 4, WIDGET_POS_BOTTOM = 24;

        for (List<MenuItem> itemsCompound : Player.ITEMS.getOrDefault(tab, new ArrayList<>())) {
            this.INVENTORY.addWidget(new SeparatorWidget(widgetXPos, 0), tab);
            widgetXPos += 6;

            int widgetCounter = 0;
            for (MenuItem item : itemsCompound) {
                SlotWidget slotWidget;

                if (widgetCounter++ == 0) {
                    slotWidget = new SlotWidget(widgetXPos, WIDGET_POS_TOP, item.TEXTURE);
                } else {
                    slotWidget = new SlotWidget(widgetXPos, WIDGET_POS_BOTTOM, item.TEXTURE);
                    widgetCounter = 0;
                    widgetXPos += SLOT_SEPARATION;
                }

                this.MENU_ITEMS_BY_SLOT.put(item, slotWidget);
                slotWidget.setOnClickEvent(() -> {
                    this.INVENTORY.removeWidget(this.bulldozerIndicator);
                    this.isUsingBulldozer = false;

                    this.MENU_ITEMS_BY_SLOT.values().forEach(itemWidget -> itemWidget.setSelected(false));
                    slotWidget.setSelected(true);


                    ScreenIndicatorWidget screenIndicatorWidget = new ScreenIndicatorWidget(20, 340, item.TEXTURE);
                    screenIndicatorWidget.setOnClickEvent(() -> {
                        this.selectedFeatureToPlace = null;
                        slotWidget.setSelected(false);
                    });
                    this.INVENTORY.addWidget(screenIndicatorWidget, tab);

                    this.selectedFeatureToPlace = item;
                });
                this.INVENTORY.addWidget(slotWidget, tab);
            }

            widgetXPos += SLOT_SEPARATION;
        }

        this.addBulldozerToInventory(widgetXPos, tab);

        this.INVENTORY.onResizeWindowEvent(utils.render.Window.getWidth(), Window.getHeight());
    }

    public void toggleIsHidingUi() {
        this.isHidingUI = !this.isHidingUI;
    }

    public void toggleGameSpeed() {
        this.gameSpeedButton.onClickEvent();
        if (Main.tickSpeed == 0) Main.tickSpeed = 1;
    }

    public void togglePauseGame() {
        if (Main.tickSpeed != 0) {
            this.previousGameSpeed = Main.tickSpeed;
            this.gameSpeedButton.onClickEvent();
            Main.tickSpeed = 0;
        } else {
            this.gameSpeedButton.onClickEvent();
            Main.tickSpeed = this.previousGameSpeed;
        }
    }

    public void toggleBulldozer() {
        if (!this.isUsingBulldozer) {
            this.selectedFeatureToPlace = null;
            this.INVENTORY.addWidgetToAllTabs(this.bulldozerIndicator);
            this.MENU_ITEMS_BY_SLOT.values().stream().filter(SlotWidget::isSelected).forEach(slotWidget -> slotWidget.setSelected(false));
        } else {
            this.INVENTORY.removeWidget(this.bulldozerIndicator);
        }
        this.isUsingBulldozer = !this.isUsingBulldozer;
    }

    public Feature createSelectedFeature() {
        if (this.selectedFeatureToPlace == null) return null;
        return this.selectedFeatureToPlace.FEATURE_TO_PLACE.createFeature(MouseListener.getInGameLocation().truncate(), this.selectedFeatureToPlace.VARIANT);
    }

    public void updateDateTime(String date, String time) {
        this.timeTextBox.updateDateTime(date, time);
    }

    public Container getContainer() {
        return this.container;
    }

    public Camera getCamera() {
        return this.CAMERA;
    }

    public boolean isHidingUi() {
        return this.isHidingUI;
    }

    public boolean isUsingBulldozer() {
        return this.isUsingBulldozer;
    }

    public boolean isMouseOnInventory() {
        return this.isMouseOnInventory;
    }
    public boolean isPaused() {
        return this.isPaused;
    }

    public void setMouseOnInventory(boolean isMouseOnInventory) {
        this.isMouseOnInventory = isMouseOnInventory;
    }

    public void openMenu() {
        this.container = this.GAME_MENU;
        this.isPaused = true;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    public void openInventory() {
        this.container = this.INVENTORY;
        this.isPaused = false;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    public static class MenuItem {
        private final Texture TEXTURE;
        private final Feature.FeatureType FEATURE_TO_PLACE;
        private final int VARIANT;

        public MenuItem(Texture texture, Feature.FeatureType featureToPlace, int variant) {
            this.TEXTURE = texture;
            this.FEATURE_TO_PLACE = featureToPlace;
            this.VARIANT = variant;
        }
    }
}
