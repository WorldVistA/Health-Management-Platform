package org.osehra.cpe.hub.dao;

import org.osehra.cpe.dao.HmpRepository;
import org.osehra.cpe.hub.VistaAccount;

import java.util.List;
import java.util.Map;

public interface IVistaAccountDao extends HmpRepository<VistaAccount, Integer> {
    VistaAccount findByDivisionHostAndPort(String division, String host, int port);
    List<VistaAccount> findAllByVistaId(String vistaId);
    List<VistaAccount> findAllByVistaIdIsNotNull();
    List<VistaAccount> findAllByHostAndPort(String host, int port);
}
