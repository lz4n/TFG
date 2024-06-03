package world.feature.building.jobSite;

import org.joml.Vector2i;
import world.entity.Duck;
import world.feature.building.Building;
import world.location.Location;

public abstract class JobSite extends Building {
    protected Duck[] workers;

    public JobSite(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    public boolean addWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == null) {
                this.workers[index] = duck;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onRemove() {
        super.onRemove();

        for (Duck duck: this.workers) {
            if (duck != null) {
                duck.removeJobSite();
            }
        }
    }

    public void removeWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == duck) {
                this.workers[index] = null;
                return;
            }
        }
    }
}
