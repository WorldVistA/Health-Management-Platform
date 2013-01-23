package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Problem;

public class ProblemImporter extends AbstractJsonImporter<Problem> {
	@Override
	protected Problem create() {
		return new Problem();
	}

}
