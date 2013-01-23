package org.osehra.cpe.vpr;

import java.util.Map;

public interface IAppService {
    public Map<String, Object> getApps();
    Map<String, Object> getApp(String code);
}
