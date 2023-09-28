package world.feature;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2i;
import utils.render.mesh.MaxSizedMesh;
import utils.render.mesh.Mesh;
import utils.render.scene.WorldScene;
import world.World;

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
        //this.featureType.getMesh().addVertex((float) this.location.x(), (float) this.location.y(), this.sizeInBlocks.x(), this.sizeInBlocks.y());
        if (this.canBePlaced()) {
            this.featureType.getMesh().addVertex((float) this.location.x(), (float) this.location.y(), this.sizeInBlocks.x(), this.sizeInBlocks.y());
            for (int x = 0; x <= this.sizeInBlocks.x(); x++) for (int y = 0; y <= this.sizeInBlocks.y(); y++) {
                Main.WORLD.setFeature((int) this.location.x() + x, (int) this.location.y() + y, this);
            }
        }
    }

    private boolean canBePlaced() {
        Feature feature;
        for (int x = 0; x <= this.sizeInBlocks.x(); x++) for (int y = 0; y <= this.sizeInBlocks.y(); y++) {
            feature = Main.WORLD.getFeature(this.getLocation().add(x, y));
            if (feature != null && !feature.getFeatureType().equals(this.featureType)) return false;

        }
        return true;
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
