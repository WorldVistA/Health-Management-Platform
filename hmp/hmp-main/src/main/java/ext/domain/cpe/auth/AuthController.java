package org.osehra.cpe.auth;

import org.osehra.cpe.Bootstrap;
import org.osehra.cpe.HmpProperties;
import org.osehra.cpe.hub.VistaAccount;
import org.osehra.cpe.hub.dao.IVistaAccountDao;
import org.osehra.cpe.vista.rpc.RpcHost;
import org.osehra.cpe.vista.rpc.RpcOperations;
import org.osehra.cpe.vista.rpc.conn.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView;

@Controller
public class AuthController implements EnvironmentAware {

    private static Logger log = LoggerFactory.getLogger(AuthController.class);

    private UserContext userContext;

    private RpcOperations authenticationRpcTemplate;

    private IVistaAccountDao vistaAccountDao;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Autowired
    public void setAuthenticationRpcTemplate(RpcOperations authenticationRpcTemplate) {
        this.authenticationRpcTemplate = authenticationRpcTemplate;
    }

    @Autowired
    public void setVistaAccountDao(IVistaAccountDao vistaAccountDao) {
        this.vistaAccountDao = vistaAccountDao;
    }

    @RequestMapping(value = "/auth")
    public String index() {
        return "redirect:/auth/login";
    }

    @RequestMapping(value = "/auth/keepalive")
    @ResponseBody
    public String keepalive() {
        return "keepalive";
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        if (!Bootstrap.isSetupComplete(environment)) {
            return new ModelAndView("redirect:/");
        }

        if (userContext.isLoggedIn()) {
            return new ModelAndView("redirect:/");
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("hmpVersion", environment.getProperty(HmpProperties.VERSION));
        return new ModelAndView("/auth/login", map);
    }

    @RequestMapping(value = "/auth/logout")
    public String logout() {
        // TODO put any pre-logout code here

        return "redirect:/j_spring_security_logout";
    }

    @RequestMapping(value = "/auth/accounts", method = RequestMethod.GET)
    public ModelAndView accounts() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        List<VistaAccount> accounts = vistaAccountDao
                .findAllByVistaIdIsNotNull();
        Map<String, List<VistaAccount>> map = new HashMap<String, List<VistaAccount>>();
        map.put("items", accounts);

        Map<String, Map<String, List<VistaAccount>>> dataMap = new HashMap<String, Map<String, List<VistaAccount>>>();
        dataMap.put("data", map);
        /*
         * This is a stop-gap until we come up with a permanent solution to protect synchronization credentials.
         */
        for(String s: map.keySet())
        {
        	for(VistaAccount vac: map.get(s))
        	{
        		vac.setVprUserCredentials(null);
        	}
        }
        return contentNegotiatingModelAndView(dataMap);
    }

    @RequestMapping(value = "/auth/welcome", method = RequestMethod.POST)
    @ResponseBody
    public String welcome(@RequestParam(required = true) String host,
                          @RequestParam(required = true) String port,
                          HttpServletResponse response) {
        response.setContentType("text/plain");
        String r = "VistA Welcome Message Here (TBD)";
        try {
            SystemInfo systemInfo = authenticationRpcTemplate
                    .fetchSystemInfo(new RpcHost(host, new Integer(port)));
            r = systemInfo.getIntroText();
        } catch (DataAccessException e) {
            log.error("unable to fetch VistA welcome message", e);
            r = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        }
        return r;
    }
}
