package EXT.DOMAIN.cpe.vpr

import javax.jms.Destination
import javax.jms.JMSException;
import javax.jms.Message
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import org.atmosphere.cpr.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.SessionCallback;
import org.springframework.security.core.context.SecurityContext
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.beans.factory.annotation.Autowired
import static EXT.DOMAIN.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView

@Controller
@RequestMapping("/chat/**")
class ChatController {
	
	@RequestMapping(value="/chat/users", method=RequestMethod.GET)
	public ModelAndView users(HttpServletRequest req) {
		ArrayList<Map<String, Object>> usrs = new ArrayList<String>();
		for(Object princip: EventController.pendingResponsesByPrincipal.keySet()) {
			Map<String, Object> usr = new HashMap<String, Object>();
			usr.put('displayName', princip?.displayName?.toString());
			usr.put('uid', princip?.uid?.toString());
			usrs.add(usr);
		}
	
		return contentNegotiatingModelAndView(['data':usrs]);
	}
	
	@RequestMapping(value="/chat/sendMessage", method=RequestMethod.POST)
	public ModelAndView sendMessage(@RequestParam("uid") String uid, @RequestParam("message") String message, HttpServletRequest req) {
		Object fromPrincipal = currentPrincipal(req);
		def rslt = [:]
		for(Object princip: EventController.pendingResponsesByPrincipal.keySet()) {
			String suid = princip?.uid?.toString();
			if(suid!=null && suid.equals(uid)) {
				ArrayList<HttpServletResponse> notifications = new ArrayList<HttpServletResponse>();
				for(HttpServletResponse resp: EventController.pendingResponsesByPrincipal.get(princip)) {
					def msg = ['from':['displayName':fromPrincipal.displayName,'uid':fromPrincipal.uid],'message':message]
					EventController.pendingResponses.get(resp).put('chatMessage',msg);
					notifications.add(resp);
					rslt['message']=msg;
				}
				// Concurrent modification exception happens when I do this above.
				for(HttpServletResponse resp: notifications) {synchronized(resp) {resp.notify();}}
			}
		}
		return contentNegotiatingModelAndView(rslt);
	}
	
	private Object currentPrincipal(HttpServletRequest req) {
		HttpSession sess = req.getSession();
		SecurityContext securityContext = (SecurityContext)sess.getAttribute(org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if(securityContext!=null && securityContext.getAuthentication().isAuthenticated()) {
			return securityContext.getAuthentication().getPrincipal();
		}
		return null;
	}
}
