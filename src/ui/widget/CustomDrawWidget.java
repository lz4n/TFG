package ui.widget;

import utils.render.mesh.Mesh;

public interface CustomDrawWidget {
    void draw(Mesh mesh, float pixelSizeInScreen, float posX, float posY, float width, float height);
}
