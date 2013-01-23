package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.VitalSign;

public class VitalSignImporter extends AbstractJsonImporter<VitalSign> {

	@Override
	protected VitalSign create() {
		return new VitalSign();
	}

}
