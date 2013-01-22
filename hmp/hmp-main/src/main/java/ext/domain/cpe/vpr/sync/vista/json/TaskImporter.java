package EXT.DOMAIN.cpe.vpr.sync.vista.json;


import EXT.DOMAIN.cpe.vpr.Task;

public class TaskImporter extends AbstractJsonImporter<Task> {

    @Override
    protected Task create() {
        return new Task();
    }
}
