package org.osehra.cpe.team;

import com.fasterxml.jackson.databind.JsonNode;
import org.osehra.cpe.auth.UserContext;
import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.jsonc.JsonCResponse;
import org.osehra.cpe.vpr.NotFoundException;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.sync.vista.IVistaVprObjectDao;
import org.osehra.cpe.vpr.web.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.contentNegotiatingModelAndView;
import static org.osehra.cpe.vpr.web.servlet.view.ModelAndViewFactory.stringModelAndView;

@Controller
public class TeamManagementController {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    IGenericPOMObjectDAO jdsDao;

    @Autowired
    IVistaVprObjectDao vprObjectDao;

    @Autowired
    UserContext userContext;

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/person/list", method = RequestMethod.GET)
    public ModelAndView persons(@PathVariable String apiVersion) throws IOException {
        String personsJson = getResourceAsString("classpath:org.osehra/cpe/team/persons.json");
        return stringModelAndView(personsJson, "application/json");
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/team/list", method = RequestMethod.GET)
    public ModelAndView teams(@PathVariable String apiVersion, HttpServletRequest request) throws IOException {
        List<Team> teams = jdsDao.findAll(Team.class);
        return contentNegotiatingModelAndView(JsonCCollection.create(request, teams));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/team/new", method = RequestMethod.POST)
    public ModelAndView createTeam(@PathVariable String apiVersion, @RequestBody String requestJson, HttpServletRequest request) throws IOException {
        Team team = POMUtils.newInstance(Team.class, requestJson);
        team.setData("ownerUid", userContext.getCurrentUser().getUid());
        team.setData("ownerName", userContext.getCurrentUser().getDisplayName());
        team = vprObjectDao.save(team);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, team));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/team/{uid}", method = RequestMethod.GET)
    public ModelAndView readTeam(@PathVariable String apiVersion, @PathVariable String uid, HttpServletRequest request) throws IOException {
        Team team = jdsDao.findByUID(Team.class, uid);
        if (team == null) throw new NotFoundException("Team '" + uid + "' not found.");
        return contentNegotiatingModelAndView(JsonCResponse.create(request, team));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/team/{uid}", method = RequestMethod.POST)
    public ModelAndView updateTeam(@PathVariable String apiVersion, @PathVariable String uid, @RequestBody String requestJson, HttpServletRequest request) throws IOException {
        Team team = POMUtils.newInstance(Team.class, requestJson);
        if (!uid.equalsIgnoreCase(team.getUid())) throw new BadRequestException("Team UID mismatch");
        // TODO: check for ownership and/or other privs
//        if (userContext.getCurrentUser().getUid().equals(team.getOwnerUid())) throw new AccessDeniedException("Current user is not the owner of this team");
        team = vprObjectDao.save(team);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, team));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/team/{uid}", method = RequestMethod.DELETE)
    public ModelAndView deleteTeam(@PathVariable String apiVersion, @PathVariable String uid, HttpServletRequest request) throws IOException {
        // TODO: check for ownership and/or other privs
        jdsDao.deleteByUID(Team.class, uid);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, Collections.emptyMap()));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/position/list", method = RequestMethod.GET)
    public ModelAndView positions(@PathVariable String apiVersion, HttpServletRequest request) throws IOException {
        // TODO: I think we need to pull this from VPR OBJECT file?  Or slurp it all into JDS at startup or something?
        List<TeamPosition> positions = jdsDao.findAll(TeamPosition.class);
        if (positions.isEmpty()) {
            positions = initializePositionList();
        }
        return contentNegotiatingModelAndView(JsonCCollection.create(request, positions));
    }

    private  List<TeamPosition> initializePositionList() throws IOException {
        List<TeamPosition> positions = new ArrayList<TeamPosition>();
        String positionsJson = getResourceAsString("classpath:org.osehra/cpe/team/team-positions.json");
        JsonNode json = POMUtils.parseJSONtoNode(positionsJson);
        JsonNode items = json.path("data").path("items");
        for (JsonNode item : items) {
            TeamPosition position = POMUtils.newInstance(TeamPosition.class, item);
            position = vprObjectDao.save(position); // assigns a UID
            positions.add(position);
        }
        return positions;
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/position/new", method = RequestMethod.POST)
    public ModelAndView createPosition(@PathVariable String apiVersion, @RequestBody String requestJson, HttpServletRequest request) throws IOException {
        TeamPosition position = POMUtils.newInstance(TeamPosition.class, requestJson);
        position = vprObjectDao.save(position);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, position));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/position/{uid}", method = RequestMethod.GET)
    public ModelAndView readPosition(@PathVariable String apiVersion, @PathVariable String uid, HttpServletRequest request) throws IOException {
        TeamPosition position = jdsDao.findByUID(TeamPosition.class, uid);
        if (position == null) throw new NotFoundException("Team Position '" + uid + "' not found.");
        return contentNegotiatingModelAndView(JsonCResponse.create(request, position));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/position/{uid}", method = RequestMethod.POST)
    public ModelAndView updatePosition(@PathVariable String apiVersion, @PathVariable String uid, @RequestBody String requestJson, HttpServletRequest request) throws IOException {
        TeamPosition position = POMUtils.newInstance(TeamPosition.class, requestJson);
        if (!uid.equalsIgnoreCase(position.getUid())) throw new BadRequestException("Team Position UID mismatch");
        position = vprObjectDao.save(position);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, position));
    }

    @RequestMapping(value = "/teamMgmt/v{apiVersion}/position/{uid}", method = RequestMethod.DELETE)
    public ModelAndView deletePosition(@PathVariable String apiVersion, @PathVariable String uid, HttpServletRequest request) throws IOException {
        jdsDao.deleteByUID(TeamPosition.class, uid);
        return contentNegotiatingModelAndView(JsonCResponse.create(request, Collections.emptyMap()));
    }

    private String getResourceAsString(String location) throws IOException {
        Resource personsResource = applicationContext.getResource(location);
        return FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(personsResource.getInputStream())));
    }
}
