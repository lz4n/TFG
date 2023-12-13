package utils;

import main.Main;
import org.joml.Vector2f;
import ui.Inventory;
import ui.widget.*;
import ui.widget.buttonWidget.AbstractButtonWidget;
import ui.widget.textWidgets.SmallTextBox;
import ui.Tab;
import ui.widget.textWidgets.TextBox;
import utils.render.Camera;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.feature.Feature;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Player {
    private final static List<MenuItem> ITEMS = Arrays.asList(
            new MenuItem(Textures.TULIP_2, Feature.FeatureType.FLOWER),
            new MenuItem(Textures.ROCK, Feature.FeatureType.ROCK),
            new MenuItem(Textures.BUSH, Feature.FeatureType.BUSH),
            new MenuItem(Textures.TREE2, Feature.FeatureType.TREE)
    );

    private final static Tab GENERAL_TAB = new Tab(Textures.SELECTED_GENERAL_TAB_ICON, Textures.UNSELECTED_GENERAL_TAB_ICON),
        ZONING_TAB = new Tab(Textures.SELECTED_ZONING_TAB_ICON, Textures.UNSELECTED_ZONING_TAB_ICON),
        INDUSTRY_TAB = new Tab(Textures.SELECTED_INDUSTRY_TAB_ICON, Textures.UNSELECTED_INDUSTRY_TAB_ICON),
        SERVICES_TAB = new Tab(Textures.SELECTED_SERVICES_TAB_ICON, Textures.UNSELECTED_SERVICES_TAB_ICON),
        NATURE_TAB = new Tab(Textures.SELECTED_NATURE_TAB_ICON, Textures.UNSELECTED_NATURE_TAB_ICON),
        SPECIAL_TAB = new Tab(Textures.SELECTED_SPECIAL_TAB_ICON, Textures.UNSELECTED_SPECIAL_TAB_ICON);

    private final Inventory INVENTORY = new Inventory();
    private final HashMap<MenuItem, SlotWidget> MENU_ITEMS_MAP = new HashMap<>();
    private final Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    private TextBox timeTextBox;
    private AbstractButtonWidget gameSpeedButton;

    private boolean isHidingUI = false;
    private int previousGameSpeed = Main.tickSpeed;
    private MenuItem selectedFeatureToPlace;

    public Player() {
    }

    public void init() {
        this.INVENTORY.addTab(Player.GENERAL_TAB);
        this.INVENTORY.addTab(Player.ZONING_TAB);
        this.INVENTORY.addTab(Player.INDUSTRY_TAB);
        this.INVENTORY.addTab(Player.NATURE_TAB);
        this.INVENTORY.addTab(Player.SERVICES_TAB);
        this.INVENTORY.addTab(Player.SPECIAL_TAB);

        this.timeTextBox = new TextBox(4, 4, "", Font.PLAIN);
        this.INVENTORY.addWidgetToAllTabs(this.timeTextBox);

        this.gameSpeedButton = new AbstractButtonWidget(4, 24, Textures.GAME_SPEED_BUTTON, Textures.CLICKED_GAME_SPEED_BUTTON, new BoundingBox(36, 16)) {
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
        this.INVENTORY.addWidgetToAllTabs(this.gameSpeedButton);

        this.INVENTORY.addWidgetToAllTabs(new SeparatorWidget(44, 0));

        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 4, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 14, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 24, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(50, 34, "", Font.PLAIN));

        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 4, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 14, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 24, "", Font.PLAIN));
        this.INVENTORY.addWidgetToAllTabs(new SmallTextBox(90, 34, "", Font.PLAIN));

        this.INVENTORY.addWidgetToAllTabs(new SeparatorWidget(130, 0));

        this.INVENTORY.addWidget(new SlotWidget(800, 24, Textures.DUCK), Player.SPECIAL_TAB);

        int widgetSep = 20, widgetTopPos = 4, widgetBottomPos = 24, widgetCounter = 0, widgetXPos = 40;
        for (MenuItem item: Player.ITEMS) {
            SlotWidget slotWidget;

            if (widgetCounter++ == 0) {
                slotWidget = new SlotWidget(widgetXPos, widgetTopPos, item.TEXTURE);
            } else {
                slotWidget = new SlotWidget(widgetXPos, widgetBottomPos, item.TEXTURE);
                widgetCounter = 0;
                widgetXPos += widgetSep;
            }

            this.MENU_ITEMS_MAP.put(item, slotWidget);
            slotWidget.setOnClickEvent(() -> {
                this.MENU_ITEMS_MAP.values().forEach(itemWidget -> itemWidget.setSelected(false));
                slotWidget.setSelected(true);


                ScreenIndicatorWidget screenIndicatorWidget = new ScreenIndicatorWidget(20, 340, item.TEXTURE);
                screenIndicatorWidget.setOnClickEvent(() -> {
                    this.selectedFeatureToPlace = null;
                    slotWidget.setSelected(false);
                });
                this.INVENTORY.addWidget(screenIndicatorWidget, Player.NATURE_TAB);

                this.selectedFeatureToPlace = item;
            });
            this.INVENTORY.addWidget(slotWidget, Player.NATURE_TAB);
        }

        this.INVENTORY.setCurrentTab(Player.GENERAL_TAB);
    }

    public void toggleIsHidingUi() {
        this.isHidingUI = !this.isHidingUI;
    }

    public void  toggleGameSpeed() {
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

    public void updateTime(String newTime) {
        this.timeTextBox.setText(newTime, Font.PLAIN);
    }

    public Inventory getInventory() {
        return this.INVENTORY;
    }

    public Camera getCamera() {
        return this.CAMERA;
    }

    public boolean isHidingUi() {
        return this.isHidingUI;
    }

    public static class MenuItem {
        private final Texture TEXTURE;
        private final Feature.FeatureType FEATURE_TO_PLACE;

        public MenuItem(Texture texture, Feature.FeatureType featureToPlace) {
            TEXTURE = texture;
            FEATURE_TO_PLACE = featureToPlace;
        }
    }
}
