package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.HealthFactor;

public class HealthFactorImporter extends AbstractJsonImporter<HealthFactor> {

	@Override
	protected HealthFactor create() {
		return new HealthFactor();
	}

}
