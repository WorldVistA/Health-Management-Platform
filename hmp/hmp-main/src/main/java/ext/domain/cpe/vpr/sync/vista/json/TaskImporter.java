package org.osehra.cpe.vpr.sync.vista.json;


import org.osehra.cpe.vpr.Task;

public class TaskImporter extends AbstractJsonImporter<Task> {

    @Override
    protected Task create() {
        return new Task();
    }
}
