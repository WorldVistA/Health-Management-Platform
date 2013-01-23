package org.osehra.cpe.hub

import org.osehra.cpe.vista.rpc.ConnectionCallback
import org.osehra.cpe.vista.rpc.RpcTemplate
import org.osehra.cpe.vista.rpc.conn.Connection

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView
import org.osehra.cpe.hub.dao.IVistaAccountDao

@RequestMapping("/vistaAccount/**")
@Controller
class VistaAccountController {

    @Autowired
    RpcTemplate rpcTemplate

    @Autowired
    IVistaAccountDao vistaAccountDao

    @RequestMapping(value = ["list", "index"])
    ModelAndView list(Pageable pageable, @RequestParam(required = false) String format) {
        Page<VistaAccount> vistaAccounts = vistaAccountDao.findAll(pageable);
        if (!format || "html" == format) {
            return new ModelAndView("/vistaAccount/list", [vistaAccountInstanceList: vistaAccounts.content, vistaAccountInstanceTotal: vistaAccounts.totalElements])
        } else {
            contentNegotiatingModelAndView([apiVersion: '1', data: [totalItems: vistaAccounts.totalElements, items: vistaAccounts.content]])
        }
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    ModelAndView create(GrailsParameterMap params) {
        VistaAccount vistaAccountInstance = new VistaAccount()
        vistaAccountInstance.properties = params
        return new ModelAndView("/vistaAccount/create", [vistaAccountInstance: vistaAccountInstance])
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    ModelAndView save(GrailsParameterMap params) {
        VistaAccount vistaAccountInstance = new VistaAccount(params)
        if (vistaAccountDao.save(vistaAccountInstance)) {
            // TODO: use flash in Spring 3.1 or ajaxify UI
            //            flash.message = "${message(code: 'default.created.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), vistaAccountInstance.id])}"
            return new ModelAndView("redirect:/vistaAccount/show/${vistaAccountInstance.id}")
        } else {
            return new ModelAndView("/vistaAccount/create", [vistaAccountInstance: vistaAccountInstance])
        }
    }

    @RequestMapping(value = "show/{id}", method = RequestMethod.GET)
    ModelAndView show(@PathVariable Long id) {
        def vistaAccountInstance = VistaAccount.get(params.id)
        if (!vistaAccountInstance) {
            // TODO: use flash in Spring 3.1 or ajaxify UI
            //            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
            return new ModelAndView("redirect:/vistaAccount/list");
        } else {
            return new ModelAndView("/vistaAccount/show", [vistaAccountInstance: vistaAccountInstance]);
        }
    }

    @RequestMapping(value = "test/{id}", method = RequestMethod.POST)
    ModelAndView test(@PathVariable Long id) {
        def vistaAccountInstance = VistaAccount.get(params.id)
        if (!vistaAccountInstance) {
            // TODO: use flash in Spring 3.1 or ajaxify UI
            //            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
            redirect(action: "list")
        } else {
            try {
                def vistaId = rpcTemplate.execute({ Connection connection -> connection.systemInfo.vistaId } as ConnectionCallback<String>, "vrpcb://${vistaAccountInstance.host}:${vistaAccountInstance.port}")
                if (vistaId) {
                    vistaAccountInstance.vistaId = vistaId
                    // TODO: this probably needs to be in a grails service
                    VistaAccount.withTransaction {
                        vistaAccountInstance.save(flush: true)
                    }
                }
                flash.message = "Connection successful."
            } catch (DataAccessException e) {
                flash.message = "Unable to connect."
            }

            [vistaAccountInstance: vistaAccountInstance]
        }
    }

    def edit = {
        def vistaAccountInstance = VistaAccount.get(params.id)
        if (!vistaAccountInstance) {
            // TODO: use flash in Spring 3.1 or ajaxify UI
            //            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [vistaAccountInstance: vistaAccountInstance]
        }
    }

    def update = {
        def vistaAccountInstance = VistaAccount.get(params.id.toLong())
        if (vistaAccountInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (vistaAccountInstance.version > version) {

                    vistaAccountInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'vistaAccount.label', default: 'VistaAccount')] as Object[], "Another user has updated this VistaAccount while you were editing")
                    render(view: "edit", model: [vistaAccountInstance: vistaAccountInstance])
                    return
                }
            }
            vistaAccountInstance.properties = params
            if (!vistaAccountInstance.hasErrors() && vistaAccountInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), vistaAccountInstance.id])}"
                redirect(action: "show", id: vistaAccountInstance.id)
            }
            else {
                render(view: "edit", model: [vistaAccountInstance: vistaAccountInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def vistaAccountInstance = VistaAccount.get(params.id.toLong())
        if (vistaAccountInstance) {
            try {
                vistaAccountInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'vistaAccount.label', default: 'VistaAccount'), params.id])}"
            redirect(action: "list")
        }
    }
}
