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

    private final Inventory INVENTORY = new Inventory();
    private final HashMap<MenuItem, SlotWidget> MENU_ITEMS_MAP = new HashMap<>();
    private final Camera CAMERA = new Camera(new Vector2f(0, 0)); //Iniciamos la cámara en 0,0.

    private MenuItem selectedFeatureToPlace;

    public Player() {
    }

    public void init() {
        this.INVENTORY.addWidget(new SeparatorWidget(20, 0));

        int widgetSep = 20, widgetTopPos = 4, widgetBottomPos = 23, widgetCounter = 0, widgetXPos = 40;;

        this.INVENTORY.addWidget(new TextWidget(40, 23, "kkkkkkkkk"));

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
                this.INVENTORY.addWidget(screenIndicatorWidget);

                this.selectedFeatureToPlace = item;
            });
            this.INVENTORY.addWidget(slotWidget);
        }


        this.INVENTORY.addWidget(new SlotWidget(440, 4, Textures.DUCK));
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
