package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Observation;

public class ObservationImporter extends AbstractJsonImporter<Observation>{

	@Override
	protected Observation create() {
		return new Observation();
	}

}
