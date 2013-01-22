package EXT.DOMAIN.cpe.vpr

import EXT.DOMAIN.cpe.HmpProperties
import EXT.DOMAIN.cpe.auth.UserContext
import EXT.DOMAIN.cpe.param.ParamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import EXT.DOMAIN.cpe.jsonc.JsonCResponse
import EXT.DOMAIN.cpe.vpr.pom.POMUtils
import javax.servlet.http.HttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Controller
public class AppController {

    @Autowired
    UserContext userContext

    @Autowired
    ParamService paramService

    @Autowired
    IAppService appService

    @Autowired
    ApplicationContext ctx;

    /**
     * Returns a bunch of app, system and environment variables.
     * Useful for debugging sometimes and capturing system context.
     */
    @RequestMapping(value = "/app/info")
    ModelAndView info(HttpServletRequest request) {
        if (!userContext.isLoggedIn()) {
            return contentNegotiatingModelAndView(JsonCResponse.create(request,
                    [userInfo: [displayName: 'Guest'],
                     props: ["${HmpProperties.VERSION}": ctx.getEnvironment().getProperty(HmpProperties.VERSION)]]
            ));
        } else {
            return contentNegotiatingModelAndView(JsonCResponse.create(request,
                    [userInfo: userContext.currentUser,
                    userPrefs: paramService.getUserParamMap("VPR USER PREF", null),
                    env: System.getenv(),
                    system: System.getProperties(),
                    props: HmpProperties.getProperties(ctx.getEnvironment()),
                    menus: appService.getApps('EXT.DOMAIN.cpe.appbar.mainmenu').values(),
                    panels: getPanels(),
                    contexts:[
                            pid: request.getSession().getAttribute('pid'),
                            rosterId: request.getSession().getAttribute('rosterID'),
                            panelId: request.getSession().getAttribute('panelId')
                    ]]
            ));
        }
    }

    // TODO: fold this into AppService?
    private List<Map> getPanels() {
        List<Map> panels = [];

        // first add all the app registrations (could be considered global r/o defaults)
        Map apps = appService.getApps('EXT.DOMAIN.cpe.panels');
        for (String key : apps.keySet()) {
            Map m = apps.get(key);
            panels.add([code: key, name: m.get('name'), url: "/js/config/" << key << ".js"]);
        }

        // then add any user-defined page configs
        List<String> params = paramService.getUserParamInstanceIDs("CPE PAGE CONFIG");
        for (String s in params) {
            panels.add([code: s, name: s, url: "/param/get/CPE PAGE CONFIG?instance=" + s]);
        }

        return panels
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String renderDefault() {
        String defaultApp = paramService.getUserParamVal('VPR USER PREF', 'aviva.default.app') ?: 'cpe';
        return "redirect:/app/${defaultApp}"
    }

    @RequestMapping(value = "/app/{app}", method = RequestMethod.GET)
    ModelAndView render(@PathVariable String app, HttpServletRequest request, ModelMap model) {
        // get the default app if none is specified
        app = app ? app : paramService.getUserParamVal('VPR USER PREF', 'aviva.default.app') ?: 'cpe';
        model.put("appService", appService);
        model.put("paramService", paramService);

        Map<String, Object> appConfig = appService.getApp(app)
        if (appConfig.containsKey('extClass')) {
            model.put('extClass', appConfig.get('extClass'))
            model.put('title', "${ ctx.getMessage('platform.name', null, request.getLocale())} &raquo; ${appConfig.get('name')}")
            return new ModelAndView('/app/extClass', model)
        }

        // otherwise, get the list of all apps and find the apps logical view name or URL to redirect to.
        String url = appConfig?.get('url');
        if (url && url.startsWith("http")) {
            return new ModelAndView("redirect:${url}", model)
        } else if (url) {
            return new ModelAndView(url, model)  // url is logical view name
        } else {
            return new ModelAndView("/app/${app}", model) // should cause spring dispatcher servlet to complain about unknown view
        }
    }

    @RequestMapping(value = "/app/list")
    public ModelAndView list(@RequestParam(required = false) String type) {
        def vals = appService.getApps(type).values();
		def svals = vals.sort([compare:{a,b-> a.name.compareTo(b.name) } ] as Comparator)
        return contentNegotiatingModelAndView([items: svals]);
    }

    // TODO: generalize this to handle patient/roster/team/location context(s)? meh.
    @RequestMapping(value = "/app/context", method = RequestMethod.POST)
    public ModelAndView setContext(HttpServletRequest request) {
        final List attrNames = ['pid', 'rosterID', 'panelId']

        Map attrs = [:]
        HttpSession session = request.getSession(false);
        if (session) {
            GrailsParameterMap params = new GrailsParameterMap(request);
            for (String attrName : attrNames) {
                if (params.containsKey(attrName)) session.setAttribute(attrName, params.get(attrName));
                attrs[attrName] = session.getAttribute(attrName)
            }
        }
        return contentNegotiatingModelAndView(JsonCResponse.create(request, attrs));
    }

}
