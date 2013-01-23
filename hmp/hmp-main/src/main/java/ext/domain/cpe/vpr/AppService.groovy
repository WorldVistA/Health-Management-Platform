package org.osehra.cpe.vpr

import org.osehra.cpe.auth.HmpUserDetails
import org.osehra.cpe.auth.UserContext;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class AppService implements IAppService, ApplicationContextAware {
	@Autowired
	UserContext userContext

    // hard coded placeholder apps for now
	// TODO: This needs to be moved to spring xml definition?
	static defaultApps = [
		// dynamic list of appbar menuitems
		[type: 'org.osehra.cpe.appbar.mainmenu', code: 'cpe', extClass:'org.osehra.cpe.CPEApp', url: '/app/cpe', name: 'CPE', menu: 'Clinical Apps'],
        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'team', extClass:'org.osehra.hmp.team.TeamManagementApplication', url: '/app/team', name: 'Team Management', menu: 'Clinical Apps'],
		[type: 'org.osehra.cpe.appbar.mainmenu', code: 'admin', extClass:'org.osehra.hmp.admin.AdminApp', url: '/app/admin', name: 'System Config', menu: 'Admin Tools'],
		[type: 'org.osehra.cpe.appbar.mainmenu', code: 'adminapi', url: '/api', name: 'API Docs', menu: 'Admin Tools'],
		[type: 'org.osehra.cpe.appbar.mainmenu', code: 'config', url: '/config', name: 'Board Builder', menu: 'Admin Tools'],
        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'foo', extClass:'org.osehra.hmp.FooApp',url: '/app/foo', name: 'Foo', menu: 'Experimental', requireKey: 'XUPROG'],
        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'roles', extClass:'org.osehra.hmp.team.RoleApp',url: '/app/roles', name: 'Role-y Poley', menu: 'Experimental', requireAuthority: 'VISTA_KEY_XUPROG'],
        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'pageanalyzer', url: '/lib/extjs-4.1.3/examples/page-analyzer/page-analyzer.html', name: 'Page Analyzer', menu: 'Debug', requireKey: 'XUPROG'],
//        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'team2', extClass:'org.osehra.hmp.team.TeamApp2', url: '/app/team2', name: 'Team Management 2', menu: 'Experimental', requireKey: 'VPR EXPERIMENTAL'],
        //[type: 'org.osehra.cpe.appbar.mainmenu', code: 'cpedesigner', url: '/aviva/cpedesigner', name: 'CPE Designer', menu: 'Exploratory Apps', requireKey: 'VISTA_KEY_VPR_EXPERIMENTAL'],
        [type: 'org.osehra.cpe.appbar.mainmenu', code: 'cheklist', url: '/checklist', name: 'Check List', menu: 'Admin Tools'],


                        // dynamic list of panel views
		[type: 'org.osehra.cpe.panels', code: 'defaultchartpanel', name: 'Generic Patient Chart'],
		[type: 'org.osehra.cpe.panels', code: 'chfpanel', name: 'CHF Panel'],
        [type: 'org.osehra.cpe.panels', code: 'search', name: 'Search'],
        [type: 'org.osehra.cpe.panels', code: 'activity-stream', name: 'Activity Stream (Wall)'],
        [type: 'org.osehra.cpe.panels', code: 'brian-worksheet-exp', name: 'Brian Worksheet Exp', requireKey: 'VPR EXPERIMENTAL'],
		
		// dynamic list of tab types
		[type: 'org.osehra.cpe.tabtypes', code: 'viewdefgridpanel', name: 'Grid Detail'],
		[type: 'org.osehra.cpe.tabtypes', code: 'portalpanel', name: 'Dashboard'],
		[type: 'org.osehra.cpe.tabtypes', code: 'patientawarepanel', name: 'Web Page/App'],
		[type: 'org.osehra.cpe.tabtypes', code: 'wunderpanel', name: 'Dynamic Table'],
	];

    private ApplicationContext applicationContext;

    @Override
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private Set<HMPApp> getSpringApps() {
		// get a list of all the apps registered (via decorator interface)
		return new HashSet<HMPApp>(applicationContext.getBeansOfType(HMPApp.class).values())
	}

	public Map<String, Object> getApp(String code) {
		Map<String, Object> apps = getApps();
		return apps.get(code);
	}

	public Map<String, Object> getApps() {
		return getApps(null);
	}

	public Map<String, Object> getApps(String type) {
		def ret = [:];

		// start with the default apps (hard coded static list)
		List apps = defaultApps.clone();

		// add in the spring beans
		for (HMPApp app in getSpringApps()) {
			apps.add(app.getAppInfo());
		}

		for (a in apps) {
			// filter by type (if specified)
			if (type && a.getAt("type") != type) {
				continue;
			}

            // filter by required authorities (if declared by app)
            String requireAuthority = a.getAt('requireAuthority');
            if (requireAuthority && !userContext.currentUser.hasAuthority(requireAuthority)) {
                continue;
            }

			// filter by required security keys (if declared by app)
            String requireKey = a.getAt('requireKey');
            if (requireKey && !userContext.currentUser.hasVistaKey(requireKey)) {
 				continue;
            }

            def code = a.getAt("code");
            if (code) {
				ret.put(code, a);
			}
		}
		return ret;
	}
}
