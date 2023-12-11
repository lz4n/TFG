package utils;

import org.joml.Vector2f;
import ui.Inventory;
import ui.widget.*;
import utils.render.Camera;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.feature.Feature;

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

    private final static TabWidget GENERAL_TAB = new TabWidget(Textures.SELECTED_GENERAL_TAB_ICON, Textures.UNSELECTED_GENERAL_TAB_ICON),
        ZONING_TAB = new TabWidget(Textures.SELECTED_ZONING_TAB_ICON, Textures.UNSELECTED_ZONING_TAB_ICON),
        INDUSTRY_TAB = new TabWidget(Textures.SELECTED_INDUSTRY_TAB_ICON, Textures.UNSELECTED_INDUSTRY_TAB_ICON),
        SERVICES_TAB = new TabWidget(Textures.SELECTED_SERVICES_TAB_ICON, Textures.UNSELECTED_SERVICES_TAB_ICON),
        NATURE_TAB = new TabWidget(Textures.SELECTED_NATURE_TAB_ICON, Textures.UNSELECTED_NATURE_TAB_ICON),
        SPECIAL_TAB = new TabWidget(Textures.SELECTED_SPECIAL_TAB_ICON, Textures.UNSELECTED_SPECIAL_TAB_ICON);

    private final Inventory INVENTORY = new Inventory();
    private final HashMap<MenuItem, SlotWidget> MENU_ITEMS_MAP = new HashMap<>();
    private final Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    private TextWidget timeTextWidget;

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

        this.timeTextWidget = new TextWidget(4, 4, "");
        this.INVENTORY.addWidget(this.timeTextWidget, Player.GENERAL_TAB);
        this.INVENTORY.addWidget(new SeparatorWidget(44, 0), Player.GENERAL_TAB);
        this.INVENTORY.addWidget(new SlotWidget(4, 23, Textures.DUCK), Player.GENERAL_TAB);
        this.INVENTORY.addWidget(new SlotWidget(24, 23, Textures.DUCK), Player.GENERAL_TAB);

        int widgetSep = 20, widgetTopPos = 4, widgetBottomPos = 23, widgetCounter = 0, widgetXPos = 40;
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

    public void updateTime(String newTime) {
        this.timeTextWidget.setText(newTime);
    }

    public Inventory getInventory() {
        return this.INVENTORY;
    }

    public Camera getCamera() {
        return this.CAMERA;
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
