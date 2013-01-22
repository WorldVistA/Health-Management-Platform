package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Observation;

public class ObservationImporter extends AbstractJsonImporter<Observation>{

	@Override
	protected Observation create() {
		return new Observation();
	}

}
