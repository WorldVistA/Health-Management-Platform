package EXT.DOMAIN.cpe.vpr

import EXT.DOMAIN.cpe.vpr.pom.POMObjectMapper

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.concurrent.CountDownLatch

import javax.jms.Destination
import javax.jms.JMSException;
import javax.jms.Message
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.swing.JFrame
import javax.swing.JTextField

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
@RequestMapping("/event/**")
class EventController {
	
	@Autowired
	JmsTemplate jmstemplate
	
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

	private POMObjectMapper objectMapper = new POMObjectMapper();

	/**
	 * Submission hook
	 * @param atmosphereResource
	 */
	@RequestMapping(value="/event/comet2", method=RequestMethod.POST)
	@ResponseBody
	public void comet2post(HttpServletRequest req) throws Exception {
		String message = req.getReader().readLine();
		broadcastMessage(message);
	}
	
	private static void broadcastMessage(String message) {
		Map<String, Object> newMsg = new HashMap<String, Object>();
		newMsg.put("message", message);
		broadcastMessage(newMsg);
	}
	
	public static void broadcastMessage(Map message) {
		for(HttpServletResponse rsp: pendingResponses.keySet()) {
			pendingResponses.put(rsp, message);
			synchronized(rsp) {
				rsp.notify();
			}
		}
	}
	
	static HashMap<HttpServletResponse, Map<String, Object>> pendingResponses = new HashMap<HttpServletResponse, Map<String, Object>>();
	static HashMap<Object, ArrayList<HttpServletResponse>> pendingResponsesByPrincipal = new HashMap<Object, ArrayList<HttpServletResponse>>();
	
	private ModelAndView waitFor(HttpServletRequest req, HttpServletResponse resp) {
		pendingResponses.put(resp, new HashMap<String, Object>());
		Object princip = currentPrincipal(req);
		if(princip != null ) {
			ArrayList<HttpServletResponse> lst = pendingResponsesByPrincipal.get(princip);
			if(lst==null) {
				lst = new ArrayList<HttpServletResponse>();
				pendingResponsesByPrincipal.put(princip, lst);
			}
			lst.add(resp);
		}
		synchronized(resp) {
			if(!stopListening) {
				resp.wait();
				Map<String, Object> msg = pendingResponses.get(resp);
				pendingResponses.remove(resp);
				if(princip!=null) {
					pendingResponsesByPrincipal.get(princip).remove(resp);
				}
				return contentNegotiatingModelAndView(msg);
			}
			return null;
		}
	}
	
	def stopListening = false;
	
	public void destroy() {
		stopListening = true;
		for(HttpServletResponse resp: pendingResponses.keySet()) {
			resp.notify();
		}
	}
	
	private Object currentPrincipal(HttpServletRequest req) {
		HttpSession sess = req.getSession();
		SecurityContext securityContext = (SecurityContext)sess.getAttribute(org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if(securityContext!=null && securityContext.getAuthentication().isAuthenticated()) {
			return securityContext.getAuthentication().getPrincipal();
		}
		return null;
	}

	private void reportUser(HttpServletRequest req, String prefix) {
		Object principal = currentPrincipal(req);
		if (principal != null) {
			System.out.println(prefix+"principal is: " + principal.toString());
		} else {
			System.out.println(prefix+"isAuthenticated NO");
		}
	}

	/**
	 * Subscription hook
	 * @param req
	 * @param rsp
	 */
	@RequestMapping(value="/event/comet2", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView comet2get(HttpServletRequest req, HttpServletResponse resp){
		startListener();
		return waitFor(req, resp);
	}

	private static JFrame frm = null;
	
	/**
	 * showWnd is a debug tool to show a server-side window to simulate messages to be broadcasted to all connections simultaneously.
	 * @param req
	 */
	private static void showWnd(final HttpServletRequest req) {
		if(!frm) {
			frm = new JFrame();
			frm.setLayout(new BorderLayout());
			JTextField brack = new JTextField();
			brack.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
				String val = ((JTextField)e.getSource()).getText();
				broadcastMessage(val);
			}});
			frm.add(brack);
			frm.setPreferredSize(new Dimension(200,200));
			frm.pack();
			frm.setVisible(true);
			frm.setLocationRelativeTo(null);
		}
	}
	
	Thread listenerThread = null;
	Destination dest = null;
	
	private void startListener() {
		if (listenerThread == null ||  dest == null) {
			jmstemplate.execute(new SessionCallback<Object>() {
				@Override
				public Object doInJms(Session session) throws JMSException {
					dest = jmstemplate.getDestinationResolver().resolveDestinationName(session, "ui.notify", true)
				}
			});
			listenerThread = new Thread(){public void run() {
					while(true) {
						Message msg = jmstemplate.receiveSelected(dest, null);
						broadcastMessage(msg.getProperties());
					}
				}
			};
			listenerThread.start();
		}
	}
}
