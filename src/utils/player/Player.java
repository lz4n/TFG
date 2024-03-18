package utils.player;

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
import utils.BoundingBox;
import utils.Logger;
import utils.player.inventory.FeatureItem;
import utils.player.inventory.Item;
import utils.player.inventory.TerrainItem;
import utils.render.Camera;
import utils.render.Window;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.feature.Feature;
import world.particle.BulldozerParticle;
import world.particle.NegativeParticle;
import world.particle.PositiveParticle;
import world.terrain.Terrain;

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
    private final static HashMap<Tab, List<List<Item>>> ITEMS = new HashMap<>() {{
        this.put(Player.GENERAL_TAB,Arrays.asList(
                Arrays.asList(
                        new TerrainItem(Textures.WATER, Terrain.TerrainType.WATER),
                        new TerrainItem(Textures.GRASS, Terrain.TerrainType.GRASS),
                        new TerrainItem(Textures.SAND, Terrain.TerrainType.SAND),
                        new TerrainItem(Textures.GRAVEL, Terrain.TerrainType.GRAVEL),
                        new TerrainItem(Textures.STONE, Terrain.TerrainType.STONE),
                        new TerrainItem(Textures.SNOW, Terrain.TerrainType.SNOW)
                ),
                Arrays.asList(
                        new TerrainItem(Textures.PATH, Terrain.TerrainType.PATH),
                        new TerrainItem(Textures.STONE_TILE, Terrain.TerrainType.STONE_TILE)
                )
        ));
        this.put(Player.ZONING_TAB, Arrays.asList(
                Arrays.asList(
                        new FeatureItem(Textures.HOUSE_LVL1, Feature.FeatureType.HOUSE, 0),
                        new FeatureItem(Textures.HOUSE_LVL2, Feature.FeatureType.HOUSE, 1),
                        new FeatureItem(Textures.HOUSE_LVL3, Feature.FeatureType.HOUSE, 2),
                        new FeatureItem(Textures.HOUSE_LVL4, Feature.FeatureType.HOUSE, 3),
                        new FeatureItem(Textures.HOUSE_LVL5, Feature.FeatureType.HOUSE, 4),
                        new FeatureItem(Textures.HOUSE_LVL6, Feature.FeatureType.HOUSE, 5),
                        new FeatureItem(Textures.HOUSE_LVL7, Feature.FeatureType.HOUSE, 6)
                )
        ));
        this.put(Player.INDUSTRY_TAB, new ArrayList<>());
        this.put(Player.SERVICES_TAB, new ArrayList<>());
        this.put(Player.NATURE_TAB, Arrays.asList(
                Arrays.asList(
                        new FeatureItem(Textures.BUSH, Feature.FeatureType.BUSH, 0),
                        new FeatureItem(Textures.TREE1, Feature.FeatureType.TREE, 0),
                        new FeatureItem(Textures.TREE2, Feature.FeatureType.TREE, 1)),
                Arrays.asList(
                        new FeatureItem(Textures.TULIP, Feature.FeatureType.FLOWER, 0),
                        new FeatureItem(Textures.TULIP_2, Feature.FeatureType.FLOWER, 1),
                        new FeatureItem(Textures.BLUE_ORCHID, Feature.FeatureType.FLOWER, 2),
                        new FeatureItem(Textures.DANDELION, Feature.FeatureType.FLOWER, 3),
                        new FeatureItem(Textures.RED_LILY, Feature.FeatureType.FLOWER, 4)),
                List.of(
                        new FeatureItem(Textures.ROCK, Feature.FeatureType.ROCK, 0))
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
     * Menú de configuración.
     */
    private final Frame CONFIG_MENU = new Frame("Config");

    /**
     * Mapa que almacena los slots correspondientes a cada item del inventario.
     */
    private final HashMap<Item, SlotWidget> MENU_ITEMS_BY_SLOT = new HashMap<>();

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
    private Item selectedItem;

    /**
     * Inicializa los datos del jugador. Crea el inventario y el menú.
     */
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
        FrameButtonWidget returnGameButton = new FrameButtonWidget(8, 24, "Volver");
        returnGameButton.setOnClickEvent(this::openInventory);
        this.GAME_MENU.addWidget(returnGameButton);
        this.GAME_MENU.addWidget(new FrameButtonWidget(8, 56, "Abrir mundo"));

        FrameButtonWidget configButton = new FrameButtonWidget(8, 88, "Configuracion");
        configButton.setOnClickEvent(this::openConfig);
        this.GAME_MENU.addWidget(configButton);

        FrameButtonWidget leaveGameButton = new FrameButtonWidget(8, 120, "Guardar y salir");
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

        //** MENU DE CONFIGURACIÓN **//
        for (int i = 0; i < 10; i++) {
            FrameButtonWidget a = new FrameButtonWidget(6, 24 + 32*i, "test " + i);
            this.CONFIG_MENU.addWidget(a);
        }
    }

    /**
     * Añade el widget del bulldozer al inventario.
     * @param posX Posición en el eje X del inventario donde se va a colocar el botón del bulldozer.
     * @param tab Pestaña donde se colocará el botón.
     */
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

    /**
     * Añade los items de las features a la pestaña correspondiente.
     * @param widgetXPos Posición en el eje X del inventario donde se quieren colocar los items.
     * @param tab Pestaña donde se van a colocar los items.
     */
    private void addFeaturesWidgetToTab(int widgetXPos, Tab tab) {
        final int SLOT_SEPARATION = 20, WIDGET_POS_TOP = 4, WIDGET_POS_BOTTOM = 24;

        for (List<Item> itemsCompound : Player.ITEMS.getOrDefault(tab, new ArrayList<>())) {
            this.INVENTORY.addWidget(new SeparatorWidget(widgetXPos, 0), tab);
            widgetXPos += 6;

            int widgetCounter = 0;
            for (Item item : itemsCompound) {
                SlotWidget slotWidget;

                if (widgetCounter++ == 0) {
                    slotWidget = new SlotWidget(widgetXPos, WIDGET_POS_TOP, item.getTexture());
                } else {
                    slotWidget = new SlotWidget(widgetXPos, WIDGET_POS_BOTTOM, item.getTexture());
                    widgetCounter = 0;
                    widgetXPos += SLOT_SEPARATION;
                }

                this.MENU_ITEMS_BY_SLOT.put(item, slotWidget);
                slotWidget.setOnClickEvent(() -> {
                    this.INVENTORY.removeWidget(this.bulldozerIndicator);
                    this.isUsingBulldozer = false;

                    this.MENU_ITEMS_BY_SLOT.values().forEach(itemWidget -> itemWidget.setSelected(false));
                    slotWidget.setSelected(true);


                    //Cuando haces clic se abre un indicador en la pantalla, que cuando lo cierras se desselecciona el slot.
                    ScreenIndicatorWidget screenIndicatorWidget = new ScreenIndicatorWidget(20, 340, item.getTexture());
                    screenIndicatorWidget.setOnClickEvent(() -> {
                        this.selectedItem = null;
                        slotWidget.setSelected(false);
                    });
                    this.INVENTORY.addWidget(screenIndicatorWidget, tab);

                    this.selectedItem = item;
                });
                this.INVENTORY.addWidget(slotWidget, tab);
            }

            widgetXPos += SLOT_SEPARATION;
        }

        this.addBulldozerToInventory(widgetXPos, tab);

        this.INVENTORY.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    /**
     * Activa/desactiva la ocultación del inventario.
     */
    public void toggleIsHidingUi() {
        this.isHidingUI = !this.isHidingUI;
    }

    /**
     * Pasa a la siguiente velocidad, si llega al máximo pausa el juego.
     */
    public void toggleGameSpeed() {
        this.gameSpeedButton.onClickEvent();
        if (Main.tickSpeed == 0) Main.tickSpeed = 1;
    }

    /**
     * Pausa/despausa el juego.
     */
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

    /**
     * Activa/desactiva el bulldozer.
     */
    public void toggleBulldozer() {
        if (!this.isUsingBulldozer) {
            this.selectedItem = null;
            this.INVENTORY.addWidgetToAllTabs(this.bulldozerIndicator);
            this.MENU_ITEMS_BY_SLOT.values().stream().filter(SlotWidget::isSelected).forEach(slotWidget -> slotWidget.setSelected(false));
        } else {
            this.INVENTORY.removeWidget(this.bulldozerIndicator);
        }
        this.isUsingBulldozer = !this.isUsingBulldozer;
    }

    /**
     * Crea una feature del tipo que tenga seleccionado el jugador, pero no la coloca en el mundo.
     * @return Nueva feature del tipo que tenga seleccionado el jugaador.
     */
    public Feature createSelectedFeature() {
        if (this.selectedItem != null && this.selectedItem instanceof FeatureItem featureItem) {
            return featureItem.getFeatureToPlace().createFeature(MouseListener.getInGameLocation().truncate(), featureItem.getVariantToPlace());
        }
        return null;
    }

    public Terrain createSelectedTerrain() {
        if (this.selectedItem != null && this.selectedItem instanceof TerrainItem terrainItem) {
            Terrain currentTerrain = MouseListener.getInGameLocation().getTerrain();
            return new Terrain(
                    terrainItem.getTerrainToPlace(),
                    currentTerrain.getBiome(),
                    currentTerrain.getContinentalityNoise(),
                    currentTerrain.getWeirdnessNoise(),
                    currentTerrain.getRiversNoise()
            );
        }
        return null;
    }

    /**
     * Actualiza la fecha y hora en el <code>timeTextBox</code>.
     * @param date nueva fecha.
     * @param time nueva hora.
     * @see Player#timeTextBox
     */
    public void updateDateTime(String date, String time) {
        this.timeTextBox.updateDateTime(date, time);
    }

    /**
     * @return Contenedor que está utilizando el jugador, el menú o el inventario.
     */
    public Container getContainer() {
        return this.container;
    }

    /**
     * @return Cámara del jugador.
     */
    public Camera getCamera() {
        return this.CAMERA;
    }

    /**
     * @return Si está ocultando el inventario o no.
     */
    public boolean isHidingUi() {
        return this.isHidingUI;
    }

    /**
     * @return Si está utilizando el bulldozer o no.
     */
    public boolean isUsingBulldozer() {
        return this.isUsingBulldozer;
    }

    /**
     * @return Si está el ratón sobre el inventario o no.
     */
    public boolean isMouseOnInventory() {
        return this.isMouseOnInventory;
    }

    /**
     * @return Si el juego está en pausa o no.
     */
    public boolean isPaused() {
        return this.isPaused;
    }

    /**
     * Establece si el ratón está sobre el inventario.
     * @param isMouseOnInventory Valor boleano que indica si el ratón está sobre el inventario.
     */
    public void setMouseOnInventory(boolean isMouseOnInventory) {
        this.isMouseOnInventory = isMouseOnInventory;
    }

    /**
     * Cierra el contenedor anterior y abre el menú de juego.
     */
    public void openMenu() {
        this.container = this.GAME_MENU;
        this.isPaused = true;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    /**
     * Cierra el contenedor anterior y abre el menú de configuración.
     */
    public void openConfig() {
        this.container = this.CONFIG_MENU;
        this.isPaused = true;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    /**
     * Cierra el contenedor anterior y abre el inventario.
     */
    public void openInventory() {
        this.container = this.INVENTORY;
        this.isPaused = false;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    /**
     * Elimina la feature que esté seleccionando el jugador.
     */
    public void destroySelectedFeature() {
        Feature selectedFeature = MouseListener.getInGameLocation().getFeature();
        if (selectedFeature != null) {
            Main.world.removeFeature(selectedFeature);
            for (int particles = 0; particles < selectedFeature.getSize().x() * selectedFeature.getSize().y() * 5; particles++) {
                Main.world.spawnParticle(new BulldozerParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                        Main.RANDOM.nextFloat(0, selectedFeature.getSize().x()),
                        Main.RANDOM.nextFloat(0, selectedFeature.getSize().y())
                )));
            }
        }
    }

    public void placeSelectedItem() {
        if (this.selectedItem instanceof FeatureItem) {
            Feature selectedFeature = this.createSelectedFeature();
            if (selectedFeature != null) {
                if (selectedFeature.canBePlaced()) {
                    Main.world.addFeature(selectedFeature);
                    for (int particles = 0; particles < selectedFeature.getSize().x() * selectedFeature.getSize().y() * 5; particles++) {
                        Main.world.spawnParticle(new PositiveParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                                Main.RANDOM.nextFloat(0, selectedFeature.getSize().x()),
                                Main.RANDOM.nextFloat(0, selectedFeature.getSize().y())
                        )));
                    }
                } else {
                    Main.world.spawnParticle(new NegativeParticle(MouseListener.getInGameLocation().add(-.5f, .5f)));
                }
            }
        }
        if (this.selectedItem instanceof TerrainItem) {
            Terrain selectedTerrain = this.createSelectedTerrain();
            Terrain.TerrainType oldTerrainType = MouseListener.getInGameLocation().getTerrain().getType();
            if (selectedTerrain != null && !selectedTerrain.getType().equals(oldTerrainType)) {
                Main.world.setTerrain(
                        (int) MouseListener.getInGameLocation().getX(),
                        (int) MouseListener.getInGameLocation().getY(),
                        selectedTerrain
                );

                Feature feature = Main.world.getFeature(
                        (int) MouseListener.getInGameLocation().getX(),
                        (int) MouseListener.getInGameLocation().getY()
                );
                if (feature != null && !feature.checkSpecificConditions()) {
                    Main.world.removeFeature(feature);
                    for (int particles = 0; particles < feature.getSize().x() * feature.getSize().y() * 5; particles++) {
                        Main.world.spawnParticle(new BulldozerParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                                Main.RANDOM.nextFloat(0, feature.getSize().x()),
                                Main.RANDOM.nextFloat(0, feature.getSize().y())
                        )));
                    }
                }

                for (int particles = 0; particles < 5; particles++) {
                    Main.world.spawnParticle(new PositiveParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                            Main.RANDOM.nextFloat(0, 1),
                            Main.RANDOM.nextFloat(0, 1)
                    )));
                }
            }
        }
    }
}
