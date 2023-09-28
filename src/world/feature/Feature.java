package world.feature;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2i;
import utils.render.mesh.MaxSizedMesh;
import utils.render.mesh.Mesh;
import utils.render.scene.WorldScene;

import javax.swing.*;
import java.util.Arrays;

public abstract class Feature {
    private Vector2d location;
    private Vector2i sizeInBlocks;
    private FeatureType featureType;

    public Feature(Vector2d location, Vector2i sizeInBlocks, FeatureType featureType) {
        this.location = location;
        this.sizeInBlocks = sizeInBlocks;
        this.featureType = featureType;

        this.featureType.getMesh().addVertex((float) this.location.x(), (float) this.location.y(), this.sizeInBlocks.x(), this.sizeInBlocks.y());

        float screenPosX = WorldScene.SPRITE_SIZE * (float) location.x(), screenPosY = WorldScene.SPRITE_SIZE * (float) location.y();
        int sizeX = this.sizeInBlocks.x(), sizeY = this.sizeInBlocks.y();
    }

    public Vector2d getLocation() {
        return new Vector2d(this.location);
    }

    public FeatureType getFeatureType() {
        return this.featureType;
    }

    public enum FeatureType {
        TREE("assets/textures/feature/tree.png"),
        BUSH("assets/textures/feature/bush.png");

        private String texturePath;
        private MaxSizedMesh mesh = new MaxSizedMesh(Main.WORLD.getSize() * Main.WORLD.getSize());

        FeatureType(String texturePath) {
            this.texturePath = texturePath;
        }

        public String getTexturePath() {
            return this.texturePath;
        }

        public MaxSizedMesh getMesh() {
            return this.mesh;
        }
    }
}
