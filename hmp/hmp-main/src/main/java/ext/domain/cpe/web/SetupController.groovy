package org.osehra.cpe.web

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

import org.osehra.cpe.Bootstrap
import org.osehra.cpe.HmpProperties
import org.osehra.cpe.SetupCommand
import org.osehra.cpe.hub.VistaAccount
import org.osehra.cpe.hub.VistaAccountValidator
import org.osehra.cpe.hub.dao.IVistaAccountDao
import org.osehra.cpe.vista.rpc.ConnectionCallback
import org.osehra.cpe.vista.rpc.RpcOperations
import org.osehra.cpe.vista.rpc.conn.Connection
import org.osehra.cpe.vpr.sync.vista.SynchronizationRpcConstants
import org.osehra.cpe.vpr.termeng.TermEng
import org.osehra.cpe.vpr.web.BadRequestException
import org.osehra.cpe.vpr.web.IHealthCheck

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.methods.GetMethod
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.util.DefaultPropertiesPersister
import org.springframework.util.PropertiesPersister
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView


@Controller
class SetupController implements ApplicationContextAware, EnvironmentAware {

    private static Logger LOG = LoggerFactory.getLogger(SetupController.class);

    private Validator vistaAccountValidator
    private Validator setupValidator

    SetupController() {
        vistaAccountValidator = new VistaAccountValidator()
        setupValidator = new SetupCommandValidator(vistaAccountValidator)
    }

    ApplicationContext applicationContext

    Environment environment

    @Autowired
    RpcOperations synchronizationRpcTemplate

    @Autowired
    IVistaAccountDao vistaAccountDao

    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister()

	@RequestMapping("/checklist")
	ModelAndView checkList(){
	   SetupCommand setup = new SetupCommand()				   
	   setup.serverId = environment.getProperty(HmpProperties.SERVER_ID)
	   setup.serverHost = environment.getProperty(HmpProperties.SERVER_HOST)
	   setup.httpPort = Integer.parseInt(environment.getProperty(HmpProperties.SERVER_PORT_HTTP))
	   setup.httpsPort = Integer.parseInt(environment.getProperty(HmpProperties.SERVER_PORT_HTTPS))
	   
	   setup.datasourceName = environment.getProperty(HmpProperties.DATASOURCE_NAME)
	   setup.databaseDriverClassName = environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS)
	   setup.databaseUrl = environment.getProperty(HmpProperties.DATABASE_URL)
	   setup.databaseUsername = environment.getProperty(HmpProperties.DATABASE_USERNAME)
	   setup.databasePassword = environment.getProperty(HmpProperties.DATABASE_PASSWORD)
	      
	   return new ModelAndView("/setup/index", [setup: setup, vistaAccounts: vistaAccountDao.findAll(), status: getResourcesHealthStatus()])
					   
	}

	protected Map getResourcesHealthStatus() {
		def status = [:]
		
		// Check if services are alive
		status.put('jds', (environment.containsProperty(HmpProperties.JDS_URL))?isAlive(environment.getProperty(HmpProperties.JDS_URL)+"/ping"):Boolean.FALSE)
		status.put('openInfobutton', (environment.containsProperty(HmpProperties.INFO_BUTTON_URL))?isAlive(environment.getProperty(HmpProperties.INFO_BUTTON_URL)):Boolean.FALSE)
		status.put('solr', (environment.containsProperty(HmpProperties.SOLR_URL))?isAlive(environment.getProperty(HmpProperties.SOLR_URL)):Boolean.FALSE)

		TermEng termEng = (TermEng) applicationContext.getBean('termEng')
		if(termEng){
			for (def source : termEng.getDataSources()) {
				status.put('termDb', ((IHealthCheck) source).isAlive());
				if(!status.termDb){
					def msg = (!status.errTermDb)?"Error connecting to:<br/>" + source.jdbcURL:status.errTermDb<<"<br/>"<<source.jdbcURL
					status.put('errTermDb', msg)
				}
			}
		}else{
			status.put('termDb', false)
			status.put('errTermDb', "Error loading  term engine.")
		}
		
		boolean partialVistaConnection = false;
		List<VistaAccount> accounts = vistaAccountDao.findAll();
		for (VistaAccount account : accounts) {
			def url = "vrpcb://${account.vprUserCredentials};@${account.host}:${account.port}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${SynchronizationRpcConstants.VPR_DATA_VERSION}"
			try{
				synchronizationRpcTemplate.execute(url,[])
				if(status.vista == null) {status.put('vista',true)}//do not override previous error
				partialVistaConnection = true //app can run with at least one VistA connection alive
				
			}catch(Exception e){
				def msg = (!status.errVista)?"Error connecting to:<br/>${account.host}:${account.port}":status.errVista<<"<br/>${account.host}:${account.port}"
				status.put('errVista', msg);
				status.put('vista', false);
			}
		}
		status.put('complete', status.jds && status.openInfobutton && status.solr && status.termDb && partialVistaConnection)
		return status
	}

    @RequestMapping("/setup")
    ModelAndView index(SetupCommand setup,
                       Errors setupErrors,
                       @RequestParam(required = false) String access,
                       @RequestParam(required = false) String verify,
                       @RequestParam(required = false) Boolean done) {
        if (Bootstrap.isSetupComplete(environment)) {
            return new ModelAndView("redirect:/")
        } else if (Bootstrap.getHmpPropertiesResource(applicationContext).exists()) {
            return new ModelAndView("/setup/restart")
        } else {
            if (!setup.serverId) setup.serverId = environment.getProperty(HmpProperties.SERVER_ID)
            if (!setup.serverHost) setup.serverHost = environment.getProperty(HmpProperties.SERVER_HOST)
            if (!setup.httpPort) setup.httpPort = Integer.parseInt(environment.getProperty(HmpProperties.SERVER_PORT_HTTP))
            if (!setup.httpsPort) setup.httpsPort = Integer.parseInt(environment.getProperty(HmpProperties.SERVER_PORT_HTTPS))
            if (!setup.datasourceName) setup.datasourceName = environment.getProperty(HmpProperties.DATASOURCE_NAME)
            if (!setup.databaseDriverClassName) setup.databaseDriverClassName = environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS)
            if (!setup.databaseUrl) setup.databaseUrl = environment.getProperty(HmpProperties.DATABASE_URL)
            if (!setup.databaseUsername) setup.databaseUsername = environment.getProperty(HmpProperties.DATABASE_USERNAME)
            if (!setup.databasePassword) setup.databasePassword = environment.getProperty(HmpProperties.DATABASE_PASSWORD)

            if (setup.vista != null) {
                setup.vista = vistaAccountDao.findByDivisionHostAndPort(setup.vista.division, setup.vista.host, setup.vista.port) ?: setup.vista
            }
			
            ValidationUtils.invokeValidator(setupValidator, setup, setupErrors)
            if (!setupErrors.hasErrors()) {
                if (!access) throw new BadRequestException("missing required parameter 'access'");
                if (!verify) throw new BadRequestException("missing required parameter 'verify'");

                if (done) {
					setup.encrypt();
                    vistaAccountDao.save(setup.vista)
                    completeSetup(setup)
					setup.decrypt();
                    return new ModelAndView("redirect:/")
                }
            }

            return new ModelAndView("/setup/index", [setup: setup, vistaAccounts: vistaAccountDao.findAll(), status: getResourcesHealthStatus()])
        }
    }

    private void completeSetup(SetupCommand setup) {
        Properties props = new Properties();
		props.setProperty(HmpProperties.PROPERTIES_ENCRYPTED, "true")
        props.setProperty(HmpProperties.SERVER_ID, setup.getServerId())
        props.setProperty(HmpProperties.SERVER_HOST, setup.getServerHost())
        props.setProperty(HmpProperties.SERVER_PORT_HTTP, setup.getHttpPort().toString())
        props.setProperty(HmpProperties.SERVER_PORT_HTTPS, setup.getHttpsPort().toString())
        props.setProperty(HmpProperties.SETUP_COMPLETE, "true")

        Resource hmpPropertiesResource = Bootstrap.getHmpPropertiesResource(applicationContext)
        try {
            if (!hmpPropertiesResource.exists()) {
                hmpPropertiesResource.file.createNewFile();
            }
            propertiesPersister.store(props, new FileOutputStream(hmpPropertiesResource.file), "Health Management Platform Properties");
			for(String key: props.stringPropertyNames())
			{
				environment.getProperties().put(key, props.get("key"));
			}
        } catch (IOException ex) {
            LOG.error("unable to store ${hmpPropertiesResource.file.canonicalPath}", ex)
        }
    }

    @RequestMapping(value = "/setup/test", method = RequestMethod.POST)
    ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
        try {
			final SetupCommand setup = new SetupCommand();
			
			// I'm sure there's a better way but I don't know the guts of Spring well enough.
			// The SetupCommand class as a parameter was not working due to some sort of Spring-related validation magic not being parseable by Jackson.
			setup.vista = new VistaAccount();
			setup.vista.division = request.getParameter("vista.division");
			setup.vista.host = request.getParameter("vista.host");
			setup.vista.port = Integer.parseInt(request.getParameter("vista.port"));
			setup.vista.name = request.getParameter("vista.name");
			setup.vista.id = request.getParameter("vista.id")?Integer.parseInt(request.getParameter("vista.id")):null;
			setup.vista.vprUserCredentials = request.getParameter("vista.vprUserCredentials");
			setup.vista.production = Boolean.parseBoolean(request.getParameter("vista.production"));
			setup.vista.region = request.getParameter("vista.region");
			setup.vista.vistaId = request.getParameter("vista.vistaId");
			
			final String access = request.getParameter("access");
			final String verify = request.getParameter("verify");
		
//            SystemInfo systemInfo = synchronizationRpcTemplate.fetchSystemInfo(new RpcHost(setup.vista?.host, setup.vista?.port))
            synchronizationRpcTemplate.execute(new ConnectionCallback<String>() {
                String doInConnection(Connection c) {
                    setup.vista.vistaId = c.systemInfo.vistaId;
                    setup.vista.vprUserCredentials = access + ";" + verify;
                }
            }, "vrpcb://${setup.vista.division}:${access};${verify}@${setup.vista?.host}:${setup.vista?.port}/${SynchronizationRpcConstants.VPR_SYNCHRONIZATION_CONTEXT}/${SynchronizationRpcConstants.VPR_DATA_VERSION}");

            VistaAccount v = vistaAccountDao.findByDivisionHostAndPort(setup.vista.division, setup.vista.host, setup.vista.port);
            if (v) {
                v.name = setup.vista.name
                v.vistaId = setup.vista.vistaId
                v.vprUserCredentials = setup.vista.vprUserCredentials
                setup.vista = v
            }
            vistaAccountDao.save(setup.vista);

            Map r = [success: "true", data: [vistaId: setup.vista.vistaId]]
            return contentNegotiatingModelAndView(r)
        } catch (Throwable t) {
            response.setStatus(500) // TODO: move this to general Spring MVC exception handler
            Map r = [success: "false", error: [code: "500", message: t.cause?.message ?: t.message]]
            return contentNegotiatingModelAndView(r)
        }
    }
	
	boolean isAlive(String url){
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		try{
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				print("Method failed: " + method.getStatusLine());
				return false;
			}
		}catch(Exception ex){
				print("Exception : " + ex.message );
				return false;
		}
		return true;
	}
}

class SetupCommandValidator implements Validator {

    private Validator vistaAccountValidator

    SetupCommandValidator(Validator vistaAccountValidator) {
        if (vistaAccountValidator == null) {
            throw new IllegalArgumentException(
                    "The supplied [Validator] is required and must not be null.");
        }
        if (!vistaAccountValidator.supports(VistaAccount.class)) {
            throw new IllegalArgumentException(
                    "The supplied [Validator] must support the validation of [VistaAccount] instances.");
        }

        this.vistaAccountValidator = vistaAccountValidator;
    }

    @Override
    boolean supports(Class<?> clazz) {
        return SetupCommand.class.isAssignableFrom(clazz);
    }

    @Override
    void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverId", "default.blank.message", ["serverId", target.getClass()].toArray());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverHost", "default.blank.message", ["serverHost", target.getClass()].toArray());
        ValidationUtils.rejectIfEmpty(errors, "httpPort", "default.null.message", ["httpPort", target.getClass()].toArray());
        ValidationUtils.rejectIfEmpty(errors, "httpsPort", "default.null.message", ["httpsPort", target.getClass()].toArray());

        SetupCommand setup = (SetupCommand) target;
        try {
            errors.pushNestedPath("vista");
            ValidationUtils.invokeValidator(vistaAccountValidator, setup.vista, errors);
        } finally {
            errors.popNestedPath();
        }
    }

}
