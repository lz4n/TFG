package utils.player;

import listener.MouseListener;
import main.Main;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import ui.widget.buttonWidget.OptionsFrameButtonWidgets;
import utils.Setting;
import utils.sounds.SoundCategory;
import utils.sounds.SoundEffect;
import ui.container.Container;
import ui.container.Frame;
import ui.container.Inventory;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
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
import utils.render.GameFont;
import utils.render.Window;
import utils.render.texture.Colors;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.World;
import world.feature.*;
import world.feature.building.Fountain;
import world.feature.building.Hospital;
import world.feature.building.House;
import world.feature.building.PoliceStation;
import world.feature.building.jobSite.Industry;
import world.feature.building.jobSite.Port;
import world.location.Location;
import world.particle.BulldozerParticle;
import world.particle.CursorParticle;
import world.particle.NegativeParticle;
import world.particle.PositiveParticle;
import world.terrain.Terrain;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class Player {
    private final static Tab GENERAL_TAB = new Tab(Textures.SELECTED_GENERAL_TAB_ICON, Textures.UNSELECTED_GENERAL_TAB_ICON),
        ZONING_TAB = new Tab(Textures.SELECTED_ZONING_TAB_ICON, Textures.UNSELECTED_ZONING_TAB_ICON),
        INDUSTRY_TAB = new Tab(Textures.SELECTED_INDUSTRY_TAB_ICON, Textures.UNSELECTED_INDUSTRY_TAB_ICON),
        SERVICES_TAB = new Tab(Textures.SELECTED_SERVICES_TAB_ICON, Textures.UNSELECTED_SERVICES_TAB_ICON),
        NATURE_TAB = new Tab(Textures.SELECTED_NATURE_TAB_ICON, Textures.UNSELECTED_NATURE_TAB_ICON),
        SPECIAL_TAB = new Tab(Textures.SELECTED_SPECIAL_TAB_ICON, Textures.UNSELECTED_SPECIAL_TAB_ICON);

    private final static HashMap<Tab, List<List<Item>>> ITEMS = new HashMap<>() {{
        this.put(Player.GENERAL_TAB,Arrays.asList(
                Arrays.asList(
                        new TerrainItem(Textures.WATER, Terrain.TerrainType.WATER, "Agua", null, 0),
                        new TerrainItem(Textures.GRASS, Terrain.TerrainType.GRASS, "Césped", null, 0),
                        new TerrainItem(Textures.SAND, Terrain.TerrainType.SAND, "Arena", null, 0),
                        new TerrainItem(Textures.GRAVEL, Terrain.TerrainType.GRAVEL, "Grava", null, 0),
                        new TerrainItem(Textures.STONE, Terrain.TerrainType.STONE, "Piedra", null, 0),
                        new TerrainItem(Textures.SNOW, Terrain.TerrainType.SNOW, "Nieve", null, 0)
                ),
                Arrays.asList(
                        new TerrainItem(Textures.PATH, Terrain.TerrainType.PATH, "Camino", null, 0),
                        new TerrainItem(Textures.STONE_TILE, Terrain.TerrainType.STONE_TILE, "Camino empedrado", null, 0)
                )
        ));
        this.put(Player.ZONING_TAB, List.of(
                Arrays.asList(
                        new FeatureItem(Textures.HOUSE_LVL1, location -> new House(location, 0), "Casa (nvl. I)", "Casa para que habiten 1 pato.", 100),
                        new FeatureItem(Textures.HOUSE_LVL2, location -> new House(location, 1),"Casa (nvl. II)", "Casa para que habiten 3 patos.", 400),
                        new FeatureItem(Textures.HOUSE_LVL3, location -> new House(location, 2), "Casa (nvl. III)", "Casa para que habiten 6 patos.", 800),
                        new FeatureItem(Textures.HOUSE_LVL4, location -> new House(location, 3), "Casa (nvl. IV)", "Casa para que habiten 9 patos.", 1200),
                        new FeatureItem(Textures.HOUSE_LVL5, location -> new House(location, 4), "Casa (nvl. V)", "Casa para que habiten 12 patos.", 1600),
                        new FeatureItem(Textures.HOUSE_LVL6, location -> new House(location, 5), "Casa (nvl. VI)", "Casa para que habiten 15 patos.", 2000),
                        new FeatureItem(Textures.HOUSE_LVL7, location -> new House(location, 6), "Casa (nvl. VII)", "Casa para que habiten 20 patos.", 10000)
                )
        ));
        this.put(Player.INDUSTRY_TAB, List.of(
                Arrays.asList(
                        new FeatureItem(Textures.INDUSTRY_LVL1, location -> new Industry(location, 1), "Industria (nvl. I)", "Da trabajo a 6 patos.", 200),
                        new FeatureItem(Textures.INDUSTRY_LVL2, location -> new Industry(location, 2), "Industria (nvl. II)", "Da trabajo a 9 patos.", 1100),
                        new FeatureItem(Textures.INDUSTRY_LVL3, location -> new Industry(location, 3), "Industria (nvl. III)", "Da trabajo a 12 patos.", 2200),
                        new FeatureItem(Textures.INDUSTRY_LVL4, location -> new Industry(location, 4), "Industria (nvl. IV)", "Da trabajo a 15 patos.", 3300),
                        new FeatureItem(Textures.INDUSTRY_LVL5, location -> new Industry(location, 5), "Industria (nvl. V)", "Da trabajo a 21 patos.", 4400),
                        new FeatureItem(Textures.PORT, Port::new, "Puerto", "Da trabajo a 50 patos. Se tiene que colocar en la costa.", 15000)
                )
        ));
        this.put(Player.SERVICES_TAB, List.of(
                Arrays.asList(
                        new FeatureItem(Textures.HOSPITAL, Hospital::new, "Hospital", "Incrementa la salud de un máximo de 25 patos.", 10000),
                        new FeatureItem(Textures.POLICE_STATION, PoliceStation::new, "Estación de Policía", "Comisaría para dar servicio a 10 patos.", 5000)
                )
        ));
        this.put(Player.NATURE_TAB, Arrays.asList(
                Arrays.asList(
                        new FeatureItem(Textures.BUSH, Bush::new, "Hierba", null, 25),
                        new FeatureItem(Textures.TREE1, location -> new Tree(location, 0), "Arbol de roble", null, 50),
                        new FeatureItem(Textures.TREE2, location -> new Tree(location, 1), "Árbol de abedúl", null, 50)),
                Arrays.asList(
                        new FeatureItem(Textures.TULIP, location -> new Flower(location, 0), "Tulipanes", null, 75),
                        new FeatureItem(Textures.TULIP_2, location -> new Flower(location, 1), "Más tulipanes", null, 75),
                        new FeatureItem(Textures.BLUE_ORCHID, location -> new Flower(location, 2), "Orquidea azul", null, 75),
                        new FeatureItem(Textures.DANDELION, location -> new Flower(location, 3), "Diente de león", null, 75),
                        new FeatureItem(Textures.RED_LILY, location -> new Flower(location, 4), "Lirio rojo", null, 75)),
                List.of(
                        new FeatureItem(Textures.ROCK, Rock::new, "Una piedra", null, 0))
        ));
        this.put(Player.SPECIAL_TAB, List.of(
                List.of(
                        new FeatureItem(Textures.FOUNTAIN, Fountain::new, "La fuente", "Hace bonito.", 25000))
        ));

    }};

    private final SmallTextBox MONEY_TEXT_BOX = new SmallTextBox(50, 4),
        INCOMES_TEXT_BOX = new SmallTextBox(50, 14),
        POPULATION_TEXT_BOX = new SmallTextBox(50, 24),
        HAPPINESS_TEXT_BOX = new SmallTextBox(50, 34),
        UNEMPLOYMENT_TEXT_BOX = new SmallTextBox(90, 4),
        SECURITY_TEXT_BOX = new SmallTextBox(90, 14),
        HEALTH_TEXT_BOX = new SmallTextBox(90, 24);

    private final Inventory INVENTORY = new Inventory();
    private final Frame GAME_MENU = new Frame("Menu");
    private final Frame CONFIG_MENU = new Frame("Config");
    private final Frame AUDIO_MENU = new Frame("Audio");
    private final Frame VIDEO_MENU = new Frame("Video");
    private final HashMap<Item, SlotWidget> MENU_ITEMS_BY_SLOT = new HashMap<>();
    private final Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0
    private Container container = this.INVENTORY;
    private DateTimeTextBox timeTextBox;
    private AbstractButtonWidget gameSpeedButton;
    private ScreenIndicatorWidget bulldozerIndicator;
    private boolean isHidingUI = false, isUsingBulldozer = false, isMouseOnInventory = false, isPaused = false;
    private int previousGameSpeed = Main.tickSpeed;
    private Item selectedItem;
    private Tooltip tooltip = null;
    private LinkedHashSet<Location> terrainSelection = null;

    /**
     * Inicializa los datos del jugador. Crea el inventario y el menú.
     */
    public void init() {
        //** INVENTARIO **//

        Player.GENERAL_TAB.setTooltip(
                new TextTooltip().addLine("Terreno", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)

        );

        Player.ZONING_TAB.setTooltip(
                new TextTooltip().addLine("Edificios", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)
        );

        Player.INDUSTRY_TAB.setTooltip(
                new TextTooltip().addLine("Industria", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)
        );

        Player.SERVICES_TAB.setTooltip(
                new TextTooltip().addLine("Servicios", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)
        );

        Player.NATURE_TAB.setTooltip(
                new TextTooltip().addLine("Naturaleza", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)
        );

        Player.SPECIAL_TAB.setTooltip(
                new TextTooltip().addLine("Edificios especiales", GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE)
        );

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

        this.INVENTORY.addTab(Player.GENERAL_TAB);
        this.INVENTORY.addTab(Player.ZONING_TAB);
        this.INVENTORY.addTab(Player.INDUSTRY_TAB);
        this.INVENTORY.addTab(Player.NATURE_TAB);
        this.INVENTORY.addTab(Player.SERVICES_TAB);
        this.INVENTORY.addTab(Player.SPECIAL_TAB);

        this.INVENTORY.addWidgetToAllTabs(this.timeTextBox);
        this.INVENTORY.addWidgetToAllTabs(this.gameSpeedButton);
        this.INVENTORY.addWidgetToAllTabs(new SeparatorWidget(44, 0));

        this.INVENTORY.addWidgetToAllTabs(this.MONEY_TEXT_BOX);
        this.MONEY_TEXT_BOX.setTooltip(new TextTooltip().addLine("Cantidad de dinero que tienes disponible", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.INCOMES_TEXT_BOX);
        this.INCOMES_TEXT_BOX.setTooltip(new TextTooltip().addLine("Ingresos diarios", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.POPULATION_TEXT_BOX);
        this.POPULATION_TEXT_BOX.setTooltip(new TextTooltip().addLine("Habitantes totales de la ciudad", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.HAPPINESS_TEXT_BOX);
        this.HAPPINESS_TEXT_BOX.setTooltip(new TextTooltip().addLine("Felicidad media de todos los patos", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.UNEMPLOYMENT_TEXT_BOX);
        this.UNEMPLOYMENT_TEXT_BOX.setTooltip(new TextTooltip().addLine("Índice de paro de la ciudad", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.SECURITY_TEXT_BOX);
        this.SECURITY_TEXT_BOX.setTooltip(new TextTooltip().addLine("Índice de seguridad de la ciudad", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));
        this.INVENTORY.addWidgetToAllTabs(this.HEALTH_TEXT_BOX);
        this.HEALTH_TEXT_BOX.setTooltip(new TextTooltip().addLine("Índice de salud de la ciudad", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE));


        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 34));

        for (Tab tab : Player.ITEMS.keySet()) {
            this.addFeaturesWidgetToTab(130, tab);
        }

        this.INVENTORY.addWidget(new SlotWidget(800, 24, Textures.DUCK_IDLE), Player.SPECIAL_TAB);

        this.INVENTORY.setCurrentTab(Player.GENERAL_TAB);


        this.createGameMenu();
        this.createConfigMenu();
        this.createVideoMenu();
        this.createAudioMenu();

        this.updateMoney(Main.world.getMoney());
        this.updateIncomes(Main.world.getIncomes());
        this.updatePopulation(Main.world.getPopulation());
        this.updateHappiness(Main.world.getHappiness());
        this.updateUnemployment(Main.world.getUnemployment());
        this.updateSecurity(Main.world.getSecurity());
        this.updateHealth(Main.world.getHealth());
    }

    private void createGameMenu() {
        this.GAME_MENU.setOnBackClickEvent(this::openInventory);

        Frame.FrameLayoutBuilder layoutBuilder = new Frame.FrameLayoutBuilder(16);

        FrameButtonWidget returnGameButton = new FrameButtonWidget(8, layoutBuilder.nextTo(), "Volver");
        returnGameButton.setOnClickEvent(this::openInventory);
        this.GAME_MENU.addWidget(returnGameButton);

        FrameButtonWidget newWorldButton = new FrameButtonWidget(8, layoutBuilder.nextTo(returnGameButton), "Nuevo mundo");
        newWorldButton.setOnClickEvent(this::openNewWorld);
        this.GAME_MENU.addWidget(newWorldButton);

        FrameButtonWidget configButton = new FrameButtonWidget(8, layoutBuilder.nextTo(newWorldButton), "Configuracion");
        configButton.setOnClickEvent(this::openConfig);
        this.GAME_MENU.addWidget(configButton);

        FrameButtonWidget leaveGameButton = new FrameButtonWidget(8, layoutBuilder.nextTo(configButton), "Guardar y salir");
        leaveGameButton.setOnClickEvent(() -> {
            File worldFile = new File(Main.installDirectory, "worlds/world.dat");
            worldFile.getParentFile().mkdirs();
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(worldFile))) {
                outputStream.writeObject(Main.world);
                outputStream.writeUTF("world.dat");
            } catch (IOException exception) {
                Logger.sendMessage("Error guardando el mundo: %s.\n", Logger.LogMessageType.FATAL, exception.getMessage());
            }
            GLFW.glfwSetWindowShouldClose(Window.window, true);
        });
        this.GAME_MENU.addWidget(leaveGameButton);
    }

    private void openNewWorld() {
        Main.world = new World(new Random().nextInt(100000000), 100);

        for (Terrain.TerrainType terrainType : Terrain.TerrainType.values()) {
            terrainType.updateMesh();
        }

        for (Feature.FeatureType featureType : Feature.FeatureType.values()) {
            featureType.updateMesh();
        }

        this.updateMoney(Main.world.getMoney());
        this.updateIncomes(Main.world.getIncomes());
        this.updatePopulation(Main.world.getPopulation());
        this.updateHappiness(Main.world.getHappiness());
        this.updateUnemployment(Main.world.getUnemployment());
        this.updateSecurity(Main.world.getSecurity());
        this.updateHealth(Main.world.getHealth());

        Window.currentScene.init();
    }

    private void createConfigMenu() {
        this.CONFIG_MENU.setOnBackClickEvent(this::openMenu);

        Frame.FrameLayoutBuilder layoutBuilder = new Frame.FrameLayoutBuilder(16);

        FrameButtonWidget videoConfigButton = new FrameButtonWidget(8, layoutBuilder.nextTo(), "Video");
        videoConfigButton.setOnClickEvent(this::openVideoConfig);
        this.CONFIG_MENU.addWidget(videoConfigButton);

        FrameButtonWidget audioConfigButton = new FrameButtonWidget(8, layoutBuilder.nextTo(videoConfigButton), "Audio");
        audioConfigButton.setOnClickEvent(this::openAudioConfig);
        this.CONFIG_MENU.addWidget(audioConfigButton);
    }

    private void createVideoMenu() {
        this.VIDEO_MENU.setOnBackClickEvent(this::openConfig);

        Frame.FrameLayoutBuilder layoutBuilder = new Frame.FrameLayoutBuilder(4);

        FrameButtonWidget fullscreenButton = new FrameButtonWidget(8, layoutBuilder.nextTo(), "Pantalla\ncompleta");
        fullscreenButton.setOnClickEvent(Window::toggleFullscreen);
        this.VIDEO_MENU.addWidget(fullscreenButton);

        FrameSeparatorWidget separatorWidget = new FrameSeparatorWidget(3, layoutBuilder.nextTo(fullscreenButton));
        this.VIDEO_MENU.addWidget(separatorWidget);

        OptionsFrameButtonWidgets<Boolean> diurnalCycleButton = new OptionsFrameButtonWidgets<>(8, layoutBuilder.nextTo(separatorWidget), "Ciclo día/noche", new LinkedHashMap<>() {{
            put(Boolean.TRUE, "Si");
            put(Boolean.FALSE, "No");
        }}, Setting.DIURNAL_CYCLE.getValue());
        diurnalCycleButton.setOnValueChangeEvent(Setting.DIURNAL_CYCLE::setValue);
        this.VIDEO_MENU.addWidget(diurnalCycleButton);

        OptionsFrameButtonWidgets<Float> cloudsButton = new OptionsFrameButtonWidgets<>(8, layoutBuilder.nextTo(diurnalCycleButton), "Nubes", new LinkedHashMap<>() {{
            put(.87f, "Muchas");
            put(.95f, "Moderado");
            put(.98f, "Pocas");
            put(1f, "No");
        }}, Setting.CLOUDS.getValue());
        cloudsButton.setOnValueChangeEvent(Setting.CLOUDS::setValue);
        this.VIDEO_MENU.addWidget(cloudsButton);
    }

    private void createAudioMenu() {
        this.AUDIO_MENU.setOnBackClickEvent(this::openConfig);

        Frame.FrameLayoutBuilder layoutBuilder = new Frame.FrameLayoutBuilder(4);

        SliderWidget volumeSlider = new SliderWidget(8, layoutBuilder.nextTo(), "Volumen general");
        volumeSlider.setValue(Setting.GENERAL_VOLUME.getValue());
        volumeSlider.setOnValueChangeEvent(value -> {
            Setting.GENERAL_VOLUME.setValue(value);
            SoundCategory.setGeneralVolume(value);

            SoundEffect.updateMusicVolume();
        });
        this.AUDIO_MENU.addWidget(volumeSlider);

        SliderWidget musicSlider = new SliderWidget(8, layoutBuilder.nextTo(volumeSlider), "Música");
        musicSlider.setValue(Setting.MUSIC_VOLUME.getValue());
        musicSlider.setOnValueChangeEvent(value -> {
            Setting.MUSIC_VOLUME.setValue(value);
            SoundCategory.MUSIC.setVolume(value);

            SoundEffect.updateMusicVolume();
        });
        this.AUDIO_MENU.addWidget(musicSlider);

        SliderWidget soundEffectsSlider = new SliderWidget(8, layoutBuilder.nextTo(musicSlider), "Efectos de sonido");
        soundEffectsSlider.setValue(Setting.SOUND_EFFECTS_VOLUME.getValue());
        soundEffectsSlider.setOnValueChangeEvent(value -> {
            Setting.SOUND_EFFECTS_VOLUME.setValue(value);
            SoundCategory.SOUND_EFFECTS.setVolume(value);
        });
        this.AUDIO_MENU.addWidget(soundEffectsSlider);
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

                if (item.getName() != null) {
                    TextTooltip tooltip = new TextTooltip()
                            .addLine(item.getName(), GameFont.PIXEL, Font.PLAIN, TextTooltip.TITLE_TEXT_SIZE);

                    if (item.getDescription() != null) {
                        tooltip.addLine(item.getDescription(), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
                    }

                    if (item.getPrice() != 0) {
                        tooltip.addLine("-" +item.getPrice() +"€", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE, Colors.RED);
                    }

                    slotWidget.setTooltip(tooltip);
                }

                slotWidget.setOnClickEvent(() -> {
                    this.INVENTORY.removeWidget(this.bulldozerIndicator);
                    this.isUsingBulldozer = false;

                    for (SlotWidget itemWidget : this.MENU_ITEMS_BY_SLOT.values()) {
                        itemWidget.setSelected(false);
                    }
                    slotWidget.setSelected(true);


                    //Cuando haces clic se abre un indicador en la pantalla, que cuando lo cierras se desselecciona el slot.
                    ScreenIndicatorWidget screenIndicatorWidget = new ScreenIndicatorWidget(20, 340, item.getTexture());
                    screenIndicatorWidget.setOnClickEvent(() -> {
                        this.selectedItem = null;
                        slotWidget.setSelected(false);
                    });
                    this.INVENTORY.addWidgetToAllTabs(screenIndicatorWidget);

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
        this.gameSpeedButton.onMouseClickEvent();
        if (Main.tickSpeed == 0) Main.tickSpeed = 1;
    }

    /**
     * Pausa/despausa el juego.
     */
    public void togglePauseGame() {
        if (Main.tickSpeed != 0) {
            this.previousGameSpeed = Main.tickSpeed;
            this.gameSpeedButton.onMouseClickEvent();
            Main.tickSpeed = 0;
        } else {
            this.gameSpeedButton.onMouseClickEvent();
            Main.tickSpeed = this.previousGameSpeed;
        }
    }

    /**
     * Activa/desactiva el bulldozer.
     */
    public void toggleBulldozer() {
        if (!this.isUsingBulldozer) {
            this.selectedItem = null;

            this.bulldozerIndicator = new ScreenIndicatorWidget(20, 340, Textures.BULLDOZER_ICON);
            this.bulldozerIndicator.setOnClickEvent(() -> {
                this.isUsingBulldozer = false;
            });
            this.INVENTORY.addWidgetToAllTabs(this.bulldozerIndicator);

            for (SlotWidget slotWidget : this.MENU_ITEMS_BY_SLOT.values()) {
                if (slotWidget.isSelected()) {
                    slotWidget.setSelected(false);
                }
            }
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
            return featureItem.getFeatureToPlace(MouseListener.getInGameLocation().truncate());
        }
        return null;
    }

    /**
     * Crea un terreno seleccionado en la ubicación especificada. Si la ubicación está fuera del mundo,
     * devuelve `null`. Si el ítem seleccionado no es nulo y es una instancia de `TerrainItem`, crea y
     * devuelve un nuevo terreno utilizando el tipo de terreno del ítem, junto con las características
     * del terreno actual en la ubicación especificada.
     *
     * @param location La ubicación donde se desea crear el terreno.
     * @return El terreno creado, o `null` si la ubicación está fuera del mundo o si no hay un ítem de terreno seleccionado.
     */
    public Terrain createSelectedTerrain(Location location) {
        if (location.isOutOfTheWorld()) return null;
        if (this.selectedItem != null && this.selectedItem instanceof TerrainItem terrainItem) {
            Terrain currentTerrain = location.getTerrain();
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
     * Actualiza el texto del cuadro de texto del dinero con el valor proporcionado y el color del texto.
     *
     * @param money El valor del dinero a mostrar.
     */
    public void updateMoney(int money) {
        this.MONEY_TEXT_BOX.setText(
                money + "€",
                Font.PLAIN,
                money > 0?Color.WHITE:Colors.RED
        );
    }

    /**
     * Actualiza el texto del cuadro de texto de ingresos con el valor proporcionado y el color del texto.
     *
     * @param incomes El valor de los ingresos a mostrar.
     */
    public void updateIncomes(int incomes) {
        this.INCOMES_TEXT_BOX.setText(
                (incomes > 0?"+":"") +incomes + "€/dia",
                Font.PLAIN,
                incomes > 0?Colors.GREEN:Colors.RED
        );
    }

    /**
     * Actualiza el texto del cuadro de texto de población con el valor proporcionado.
     *
     * @param population El número de población a mostrar.
     */
    public void updatePopulation(int population) {
        this.POPULATION_TEXT_BOX.setText(population +" patos", Font.PLAIN);
    }

    /**
     * Actualiza el texto del cuadro de texto de felicidad con el valor proporcionado y el color del texto.
     *
     * @param happiness El valor de la felicidad a mostrar (entre 0 y 1).
     */
    public void updateHappiness(float happiness) {
        Color color = Colors.RED;

        if (happiness > .5f) {
            color = Colors.ORANGE;

            if (happiness > .75f) {
                color = Colors.GREEN;
            }
        }

        this.HAPPINESS_TEXT_BOX.setText(
                (int) (happiness *100) + "%",
                Font.PLAIN,
                color
        );
    }

    /**
     * Actualiza el texto del cuadro de texto de desempleo con el valor proporcionado y el color del texto.
     *
     * @param unemployment El valor de desempleo a mostrar (entre 0 y 1).
     */
    public void updateUnemployment(float unemployment) {
        Color color = Colors.GREEN;

        if (unemployment > .5f) {
            color = Colors.ORANGE;

            if (unemployment > .75f) {
                color = Colors.RED;
            }
        }

        this.UNEMPLOYMENT_TEXT_BOX.setText(
                (int) (unemployment *100) + "%",
                Font.PLAIN,
                color
        );
    }

    /**
     * Actualiza el texto del cuadro de texto de seguridad con el valor proporcionado y el color del texto.
     *
     * @param security El valor de seguridad a mostrar (entre 0 y 1).
     */
    public void updateSecurity(float security) {
        Color color = Colors.RED;

        if (security > .5f) {
            color = Colors.ORANGE;

            if (security > .75f) {
                color = Colors.GREEN;
            }
        }

        this.SECURITY_TEXT_BOX.setText(
                (int) (security *100) + "%",
                Font.PLAIN,
                color
        );
    }

    /**
     * Actualiza el texto del cuadro de texto de salud con el valor proporcionado y el color del texto.
     *
     * @param health El valor de salud a mostrar (entre 0 y 1).
     */
    public void updateHealth(float health) {
        Color color = Colors.RED;

        if (health > .5f) {
            color = Colors.ORANGE;

            if (health > .75f) {
                color = Colors.GREEN;
            }
        }

        this.HEALTH_TEXT_BOX.setText(
                (int) (health *100) + "%",
                Font.PLAIN,
                color
        );
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
     * Cierra el contenedor anterior y abre el menú de configuración de video.
     */
    public void openVideoConfig() {
        this.container = this.VIDEO_MENU;
        this.isPaused = true;
        this.container.onResizeWindowEvent(Window.getWidth(), Window.getHeight());
    }

    /**
     * Cierra el contenedor anterior y abre el menú de configuración de audio.
     */
    public void openAudioConfig() {
        this.container = this.AUDIO_MENU;
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
    public boolean destroySelectedFeature() {
        Feature selectedFeature = MouseListener.getInGameLocation().getFeature();
        if (selectedFeature != null) {
            Main.world.removeFeature(selectedFeature);
            for (int particles = 0; particles < selectedFeature.getSize().x() * selectedFeature.getSize().y() * 5; particles++) {
                Main.world.addParticle(new BulldozerParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                        Main.RANDOM.nextFloat(0, selectedFeature.getBlockSize().x()),
                        Main.RANDOM.nextFloat(0, selectedFeature.getBlockSize().y())
                )));
            }

            return true;
        }

        return false;
    }

    /**
     * Intenta colocar el ítem seleccionado en el juego. Dependiendo del tipo de ítem seleccionado
     * (ya sea un ítem de característica o un ítem de terreno), realiza las acciones correspondientes.
     * Para los ítems de característica, intenta crear la característica seleccionada y la agrega al mundo
     * si cumple con las condiciones específicas y si el jugador tiene suficiente dinero. Para los ítems de
     * terreno, intenta cambiar el tipo de terreno en las ubicaciones seleccionadas, actualizando el mundo
     * en consecuencia.
     *
     * @return true si se pudo colocar el ítem seleccionado correctamente; de lo contrario, false.
     */
    public boolean placeSelectedItem() {
        if (this.selectedItem instanceof FeatureItem featureItem) {
            Feature selectedFeature = this.createSelectedFeature();
            if (selectedFeature != null) {
                if (selectedFeature.canBePlaced() && featureItem.getPrice() <= Main.world.getMoney()) {
                    Main.world.addFeature(selectedFeature);
                    Main.world.addMoney(-featureItem.getPrice());
                    for (int particles = 0; particles < selectedFeature.getSize().x() * selectedFeature.getSize().y() * 5; particles++) {
                        Main.world.addParticle(new PositiveParticle(MouseListener.getInGameLocation().truncate().add(-0.5f, -0.5f).add(
                                Main.RANDOM.nextFloat(0, selectedFeature.getBlockSize().x()),
                                Main.RANDOM.nextFloat(0, selectedFeature.getBlockSize().y())
                        )));
                    }

                    selectedFeature.getFeatureType().getSoundEffect().play(SoundCategory.SOUND_EFFECTS);
                } else {
                    Main.world.addParticle(new NegativeParticle(MouseListener.getInGameLocation().add(-.5f, .5f)));
                }
            }

            return true;
        }
        if (this.selectedItem instanceof TerrainItem && this.terrainSelection != null) {
            LinkedHashSet<Terrain.TerrainType> terrainTypesToUpdate = new LinkedHashSet<>();
            terrainTypesToUpdate.add(((TerrainItem) this.selectedItem).getTerrainToPlace());

            for (Location location : this.terrainSelection) {
                Terrain.TerrainType oldTerrainType = location.getTerrain().getType();
                terrainTypesToUpdate.add(oldTerrainType);

                Terrain selectedTerrain = this.createSelectedTerrain(location);

                if (selectedTerrain != null && !selectedTerrain.getType().equals(oldTerrainType)) {
                    Main.world.setTerrain(
                            (int) location.getX(),
                            (int) location.getY(),
                            selectedTerrain,
                            false
                    );

                    Feature feature = Main.world.getFeature(
                            (int) location.getX(),
                            (int) location.getY()
                    );
                    if (feature != null && !feature.checkSpecificConditions()) {
                        Main.world.removeFeature(feature);
                        for (int particles = 0; particles < feature.getSize().x() * feature.getSize().y() * 5; particles++) {
                            Main.world.addParticle(new BulldozerParticle(location.clone().add(-0.5f, -0.5f).add(
                                    Main.RANDOM.nextFloat(0, feature.getBlockSize().x()),
                                    Main.RANDOM.nextFloat(0, feature.getBlockSize().y())
                            )));
                        }
                    }

                    for (int particles = 0; particles < 5; particles++) {
                        Main.world.addParticle(new PositiveParticle(location.clone().add(-0.5f, -0.5f).add(
                                Main.RANDOM.nextFloat(0, 1),
                                Main.RANDOM.nextFloat(0, 1)
                        )));
                    }
                }
            }

            for (Terrain.TerrainType terrainType : terrainTypesToUpdate) {
                terrainType.updateMesh();
            }
            System.gc();

            SoundEffect.WORLD_EFFECT_PLACE_TERRAIN.play(SoundCategory.SOUND_EFFECTS);

            return true;
        }

        return false;
    }

    /**
     * Devuelve el tooltip asociado a este objeto.
     *
     * @return El tooltip asociado a este objeto.
     */
    public Tooltip getTooltip() {
        return tooltip;
    }

    /**
     * Establece el tooltip asociado a este objeto.
     *
     * @param tooltip El tooltip que se establecerá.
     */
    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Indica si se está seleccionando terreno actualmente.
     *
     * @return true si se está seleccionando terreno; false de lo contrario.
     */
    public boolean isSelectingTerrain() {
        return this.terrainSelection != null;
    }

    /**
     * Obtiene una copia de las ubicaciones seleccionadas de terreno.
     *
     * @return Una copia de las ubicaciones seleccionadas de terreno; null si no hay ninguna selección.
     */
    public LinkedHashSet<Location> getTerrainSelection() {
        if (!this.isSelectingTerrain()) return null;
        return new LinkedHashSet<>(this.terrainSelection);
    }

    /**
     * Agrega una ubicación a la selección de terreno, si el ítem seleccionado es un TerrainItem y la ubicación
     * seleccionada no coincide con el tipo de terreno del ítem. También reproduce un efecto de sonido al agregar
     * la ubicación.
     *
     * @param location La ubicación que se va a agregar a la selección de terreno.
     */
    public void addToTerrainSelection(Location location) {
        if (!((TerrainItem) this.selectedItem).getTerrainToPlace().equals(location.getTerrain().getType())) {
            if (!this.isSelectingTerrain()) this.terrainSelection = new LinkedHashSet<>();

            if (this.terrainSelection.add(location.clone().truncate())) {
                SoundEffect.WORLD_EFFECT_PLACE_TERRAIN.play(SoundCategory.SOUND_EFFECTS);
            }
        }
    }

    /**
     * Elimina la selección actual de terrenos, si la hay. Esto borra todas las ubicaciones
     * seleccionadas y establece la selección de terrenos a null.
     */
    public void removeTerrainSelection() {
        this.terrainSelection.clear();

        this.terrainSelection = null;
    }

    /**
     * Realiza acciones específicas en cada tick del juego. Si actualmente se está seleccionando terreno,
     * agrega partículas de cursor a cada ubicación seleccionada en el mundo para visualización.
     */
    public void onTick() {
        if (this.isSelectingTerrain()) {
            for (Location location : this.getTerrainSelection()) {
                Main.world.addParticle(new CursorParticle(location));
            }
        }
    }
}
