package EXT.DOMAIN.cpe.rpc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import EXT.DOMAIN.cpe.auth.UserContext
import EXT.DOMAIN.cpe.hub.dao.IVistaAccountDao
import EXT.DOMAIN.cpe.jsonc.JsonCCollection
import EXT.DOMAIN.cpe.jsonc.JsonCResponse
import EXT.DOMAIN.cpe.vista.rpc.RpcEvent
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations
import EXT.DOMAIN.cpe.vista.rpc.conn.AccessVerifyConnectionSpec
import EXT.DOMAIN.cpe.vista.rpc.support.InMemoryRpcLog

import grails.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.stringModelAndView

@Controller
class RpcController {

    @Autowired
    RpcOperations rpcTemplate

    @Autowired
    IVistaAccountDao vistaAccountDao

    @Autowired
    UserContext userContext

    @Autowired
    InMemoryRpcLog rpcLog

    @RequestMapping(value = "/rpc/", method = RequestMethod.GET)
    ModelAndView index() {
        return new ModelAndView("/rpc/index", [user: userContext.getCurrentUser(), rpc: new RpcCommand(division: userContext.getCurrentUser().division), accounts: vistaAccountDao.list()])
    }

    @RequestMapping(value = "/rpc/execute")
    ModelAndView execute(RpcCommand rpc, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("RPC request is invalid", errors)
        }

        try {
            String result
            if (rpc.params) {
                result = rpcTemplate.executeForString(getRpcUrl(rpc), rpc.params)
            } else {
                result = rpcTemplate.executeForString(getRpcUrl(rpc))
            }

            if (rpc.format == 'xml') {
                def stringWriter = new StringWriter()
                def node = new XmlParser().parseText(result);
                new XmlNodePrinter(new PrintWriter(stringWriter)).print(node)
                return stringModelAndView('<?xml version="1.0" encoding="utf-8"?>\n' + stringWriter.toString(), "application/xml")
            } else if (rpc.format == 'json') {
                ObjectMapper jsonMapper = new ObjectMapper()
                JsonNode jsonNode = jsonMapper.readTree(result)
                result = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                return stringModelAndView(result, 'application/json')
            } else {
                return stringModelAndView(result, 'text/plain')
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return stringModelAndView(sw.toString(), "text/plain")
        }
    }

    private String getRpcUrl(RpcCommand rpc) {
        if (rpc.division && rpc.accessCode && rpc.verifyCode)
            return "vrpcb://${rpc.division}:${rpc.accessCode};${rpc.verifyCode}@${userContext.currentUser.vistaId}/${rpc.context}/${rpc.name}"
        else
            return "${rpc.context}/${rpc.name}"
    }

    @RequestMapping(value = "/rpc/log")
    ModelAndView log(Pageable pageable, HttpServletRequest request) {
        Page<RpcEvent> rpcPage = createPage(pageable)
        JsonCCollection<RpcEvent> jsonc = JsonCCollection.create(request, rpcPage)
        jsonc.put('enabledForAllUsers', rpcLog.allEnabled)
        jsonc.put('enabledForCurrentUser', isRpcLoggingEnabledForCurrentUser())
        return contentNegotiatingModelAndView(jsonc)
    }

    private Page<RpcEvent> createPage(Pageable pageable) {
        List<RpcEvent> rpcs = rpcLog.allEnabled ? rpcLog.getRpcEvents() : getRpcEventsForCurrentUser()
        if (rpcs.isEmpty()) return new PageImpl<RpcEvent>(rpcs)
        if (pageable.offset >= rpcs.size() - 1) throw new IllegalArgumentException("Requested start index '${pageable.offset}' for page is greater than the '${rpcs.size()}' total items")
        int fromIndex = pageable.offset
        int toIndex = Math.min(pageable.offset + pageable.pageSize, rpcs.size() - 1)
        Page<RpcEvent> rpcPage = new PageImpl(rpcs.subList(fromIndex, toIndex), pageable, rpcs.size())
        return rpcPage
    }

    @RequestMapping(value = "/rpc/log/toggle", method = RequestMethod.POST)
    ModelAndView toggle(@RequestParam Boolean enable, @RequestParam(required = false) Boolean all, HttpServletRequest request) {
        if (enable)
            enableRpcEventsForCurrentUser()
        else
            disableRpcEventsForCurrentUser()

        if (all)
            rpcLog.enableForAll()
        else
            rpcLog.disableForAll()

        JsonCResponse<Map> jsonc = JsonCResponse.create(request, [foo: "bar"])
        return contentNegotiatingModelAndView(jsonc)
    }

    @RequestMapping(value = "/rpc/log/clear", method = RequestMethod.POST)
    ModelAndView clear(HttpServletRequest request) {
        rpcLog.clear()
        JsonCResponse<Map> jsonc = JsonCResponse.create(request, [foo: "bar"])
        return contentNegotiatingModelAndView(jsonc)
    }

    private void enableRpcEventsForCurrentUser() {
        rpcLog.enableFor(userContext.currentUser.host, getCredentialsForCurrentUser())
    }

    private void disableRpcEventsForCurrentUser() {
        rpcLog.disableFor(userContext.currentUser.host, getCredentialsForCurrentUser())
    }

    private List<RpcEvent> getRpcEventsForCurrentUser() {
        return rpcLog.getRpcEvents(userContext.currentUser.host, getCredentialsForCurrentUser())
    }

    private boolean isRpcLoggingEnabledForCurrentUser() {
        return rpcLog.isEnabledFor(userContext.currentUser.host, getCredentialsForCurrentUser())
    }

    private String getCredentialsForCurrentUser() {
        AccessVerifyConnectionSpec av = new AccessVerifyConnectionSpec(userContext.currentUser.division, userContext.currentUser.accessCode, userContext.currentUser.verifyCode)
        return av.credentials
    }
}
