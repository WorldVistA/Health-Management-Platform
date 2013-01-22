package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Problem;

public class ProblemImporter extends AbstractJsonImporter<Problem> {
	@Override
	protected Problem create() {
		return new Problem();
	}

}
