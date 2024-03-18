package utils.render.texture;

import org.lwjgl.opengl.GL20;

/**
 * Lista de todas las texturas que se usan en el juego. Cuando se inicia se cargan en memoria, y cuando se cierra el juego
 * se eliminan para liberar memoria.
 */
public final class Textures {
    public final static Texture
        SELECTOR = new StaticTexture("assets/textures/ui/selector.png"),
        WORLD_BORDER = new AnimatedTexture("assets/textures/terrain/border", 5, 5, GL20.GL_REPEAT),

        /** TERRAIN **/
        WATER = new AnimatedTexture("assets/textures/terrain/water", 5, 8),
        GRASS = new StaticTexture("assets/textures/terrain/grass.png"),
        SAND = new StaticTexture("assets/textures/terrain/sand.png"),
        STONE = new StaticTexture("assets/textures/terrain/stone.png"),
        SNOW = new StaticTexture("assets/textures/terrain/snow.png"),
        GRAVEL = new StaticTexture("assets/textures/terrain/gravel.png"),
        PATH = new StaticTexture("assets/textures/terrain/path.png"),
        STONE_TILE = new StaticTexture("assets/textures/terrain/stone_tile.png"),

        /** FEATURES **/
        ROCK = new StaticTexture("assets/textures/feature/rock/rock.png"),
        TULIP = new StaticTexture("assets/textures/feature/flower/tulip.png"),
        TULIP_2 = new StaticTexture("assets/textures/feature/flower/tulip2.png"),
        BLUE_ORCHID = new StaticTexture("assets/textures/feature/flower/blue_orchid.png"),
        DANDELION = new StaticTexture("assets/textures/feature/flower/dandelion.png"),
        RED_LILY = new StaticTexture("assets/textures/feature/flower/red_lily.png"),
        BUSH = new StaticTexture("assets/textures/feature/bush.png"),
        TREE1 = new StaticTexture("assets/textures/feature/tree.png"),
        TREE2 = new StaticTexture("assets/textures/feature/tree2.png"),
        HOUSE_LVL1 = new StaticTexture("assets/textures/feature/building/house/house_lvl1.png"),
        HOUSE_LVL2 = new StaticTexture("assets/textures/feature/building/house/house_lvl2.png"),
        HOUSE_LVL3 = new StaticTexture("assets/textures/feature/building/house/house_lvl3.png"),
        HOUSE_LVL4 = new StaticTexture("assets/textures/feature/building/house/house_lvl4.png"),
        HOUSE_LVL5 = new StaticTexture("assets/textures/feature/building/house/house_lvl5.png"),
        HOUSE_LVL6 = new StaticTexture("assets/textures/feature/building/house/house_lvl6.png"),
        HOUSE_LVL7 = new AnimatedTexture("assets/textures/feature/building/house/house_lvl7", 4, 28),

        /** ENTITIES **/
        DUCK = new StaticTexture("assets/textures/entity/duck.png"),

        /** PARTICLES **/
        NEGATIVE_PARTICLE = new StaticTexture("assets/textures/particle/negative.png"),
        BULLDOZER_PARTICLE = new StaticTexture("assets/textures/particle/bulldozer.png"),
        POSITIVE_PARTICLE = new StaticTexture("assets/textures/particle/positive.png"),
        CLOUD_PARTICLE = new StaticTexture("assets/textures/particle/cloud/cloud_1.png"),
        CLOUD_SHADOW_PARTICLE = new StaticTexture("assets/textures/particle/cloud/cloud_shadow_1.png"),
        SMOKE_PARTICLE = new StaticTexture("assets/textures/particle/smoke.png"),

        /** USER INTERFACE **/
        CONTAINER = new StaticTexture("assets/textures/ui/inventory/container.png"),
        SELECTED_RIGHT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/selected_right_arrow.png"),
        UNSELECTED_RIGHT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/unselected_right_arrow.png"),
        HOVERED_RIGHT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/hovered_right_arrow.png"),
        SELECTED_LEFT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/selected_left_arrow.png"),
        UNSELECTED_LEFT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/unselected_left_arrow.png"),
        HOVERED_LEFT_ARROW = new StaticTexture("assets/textures/ui/inventory/arrow/hovered_left_arrow.png"),
        SEPARATOR_WIDGET = new StaticTexture("assets/textures/ui/inventory/separator.png"),
        SELECTED_SLOT_WIDGET = new StaticTexture("assets/textures/ui/inventory/slot/selected_slot.png"),
        UNSELECTED_SLOT_WIDGET = new StaticTexture("assets/textures/ui/inventory/slot/unselected_slot.png"),
        HOVERED_SLOT_WIDGET = new StaticTexture("assets/textures/ui/inventory/slot/hovered_slot.png"),
        SCREEN_INDICATOR = new StaticTexture("assets/textures/ui/inventory/screen_indicator/screen_indicator.png"),
        HOVERED_SCREEN_INDICATOR = new StaticTexture("assets/textures/ui/inventory/screen_indicator/hovered_screen_indicator.png"),
        UNSELECTED_TAB = new StaticTexture("assets/textures/ui/inventory/tab/unselected_tab.png"),
        SELECTED_TAB = new StaticTexture("assets/textures/ui/inventory/tab/selected_tab.png"),
        UNSELECTED_GENERAL_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/general_tab/unselected_general_tab_icon.png"),
        SELECTED_GENERAL_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/general_tab/selected_general_tab_icon.png"),
        UNSELECTED_ZONING_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/zoning_tab/unselected_zoning_tab_icon.png"),
        SELECTED_ZONING_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/zoning_tab/selected_zoning_tab_icon.png"),
        UNSELECTED_INDUSTRY_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/industry_tab/unselected_industry_tab_icon.png"),
        SELECTED_INDUSTRY_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/industry_tab/selected_industry_tab_icon.png"),
        UNSELECTED_SERVICES_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/services_tab/unselected_services_tab_icon.png"),
        SELECTED_SERVICES_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/services_tab/selected_services_tab_icon.png"),
        UNSELECTED_NATURE_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/nature_tab/unselected_nature_tab_icon.png"),
        SELECTED_NATURE_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/nature_tab/selected_nature_tab_icon.png"),
        UNSELECTED_SPECIAL_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/special_tab/unselected_special_tab_icon.png"),
        SELECTED_SPECIAL_TAB_ICON = new StaticTexture("assets/textures/ui/inventory/tab/special_tab/selected_special_tab_icon.png"),
        TEXT_BOX = new StaticTexture("assets/textures/ui/inventory/text_box/text_box.png"),
        SMALL_TEXT_BOX = new StaticTexture("assets/textures/ui/inventory/text_box/text_box_small.png"),
        HORIZONTAL_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/horizontal/horizontal_button.png"),
        CLICKED_HORIZONTAL_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/horizontal/clicked_horizontal_button.png"),
        VERTICAL_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/vertical/vertical_button.png"),
        CLICKED_VERTICAL_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/vertical/clicked_vertical_button.png"),
        FRAME_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/frame_button/frame_button.png"),
        HOVERED_FRAME_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/frame_button/hovered_frame_button.png"),
        CLICKED_FRAME_BUTTON = new StaticTexture("assets/textures/ui/inventory/button/frame_button/clicked_frame_button.png"),
        PAUSED_GAME_ICON = new StaticTexture("assets/textures/ui/inventory/button/game_speed_button/paused_game_icon.png"),
        X1_GAME_ICON = new StaticTexture("assets/textures/ui/inventory/button/game_speed_button/x1_game_icon.png"),
        X2_GAME_ICON = new StaticTexture("assets/textures/ui/inventory/button/game_speed_button/x2_game_icon.png"),
        X3_GAME_ICON = new StaticTexture("assets/textures/ui/inventory/button/game_speed_button/x3_game_icon.png"),
        X5_GAME_ICON = new StaticTexture("assets/textures/ui/inventory/button/game_speed_button/x5_game_icon.png"),
        BULLDOZER_BUTTON_ICON = new StaticTexture("assets/textures/ui/inventory/button/bulldozer_button/bulldozer_icon.png"),
        BULLDOZER_ICON = new StaticTexture("assets/textures/ui/icon/bulldozer_icon.png"),
        FRAME_CONTAINER = new StaticTexture("assets/textures/ui/inventory/frame/frame.png"),
        FRAME_SCROLL_BAR_RAIL = new StaticTexture("assets/textures/ui/inventory/frame/frame_scroll_bar_rail.png"),
        FRAME_SCROLL_BAR_SLIDER = new StaticTexture("assets/textures/ui/inventory/frame/frame_scroll_bar_slider.png");

}
