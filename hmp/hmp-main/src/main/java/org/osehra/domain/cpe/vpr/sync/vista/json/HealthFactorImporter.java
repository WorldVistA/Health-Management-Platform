package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.HealthFactor;

public class HealthFactorImporter extends AbstractJsonImporter<HealthFactor> {

	@Override
	protected HealthFactor create() {
		return new HealthFactor();
	}

}
