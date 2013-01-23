package org.osehra.cpe.vpr.pom;

import org.osehra.cpe.dao.HmpRepository;

public interface IPOMObjectDAO<T extends IPOMObject> extends HmpRepository<T, String> {

}
