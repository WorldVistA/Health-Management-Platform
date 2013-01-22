package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.VitalSign;

public class VitalSignImporter extends AbstractJsonImporter<VitalSign> {

	@Override
	protected VitalSign create() {
		return new VitalSign();
	}

}
