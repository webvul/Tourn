package ml.davvs.tourn.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.davvs.tourn.model.PersistanceManager;
import ml.davvs.tourn.model.persisted.Tournament;
import ml.davvs.tourn.webserver.HTTPStatusCodeException;
import ml.davvs.tourn.webserver.WebServer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
 
public class TournamentServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ServletContextTemplateResolver templateResolver;
	private TemplateEngine templateEngine;
	private TournamentController tournamentController;
	private SeasonController seasonController;
	private PersistanceManager persistanceManager;

	public TournamentServlet () {
    	templateResolver = new ServletContextTemplateResolver();
        // XHTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix("/WEB-INF/");
        templateResolver.setSuffix(".html");
        //templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCacheTTLMs(0L);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        
        persistanceManager = new PersistanceManager();
        tournamentController = new TournamentController(persistanceManager);
        seasonController = new SeasonController(persistanceManager);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String path = req.getPathInfo();
			
			String[] pathParts;
		
			if (path != null) {
				pathParts = path.split("/");
				if (pathParts.length > 0 && pathParts[1].equals("tournament")){
					String tournamentName = req.getParameter("tournamentName");
					if (tournamentName == null || !tournamentName.matches("[A-Za-z0-9 ]+")){
						WebServer.SendResponse("Unhandeled URI", HttpServletResponse.SC_NOT_FOUND, resp);
						return;
					}
					tournamentController.newTournament(req, resp, tournamentName);
				} else if (pathParts.length > 0 && pathParts[1].equals("season")) {
					String tournamentId = req.getParameter("tournamentId");
					if (pathParts[2].equals("setupTournamentConfig")) {
						seasonController.setupTournamentConfig(req, resp);
					} else if (pathParts[2].equals("insertTeams")) {
						String s = req.getParameter("teamsField");
						seasonController.insertTeams(req, resp);
					} else {
						tournamentController.createSeason(req, resp, UUID.fromString(tournamentId));					
					}
				} else {
					WebServer.SendResponse("Unhandeled URI", HttpServletResponse.SC_NOT_FOUND, resp);
					return;
				}
			}
		} catch (HTTPStatusCodeException e){
			String stackTraceMessage = getStackTraceAsHTMLString(e);
			WebServer.SendResponse("Internal server error\n\n" + e.getMessage() + "\n\nPOST: " + req.getRequestURI() +  "\n\n" + stackTraceMessage
					, e.getStatusCode(), resp);
		}
	}

	private String getStackTraceAsHTMLString(Throwable t){
		String stackTrace = "";
		stackTrace += "\n" + t.getMessage() + "\n";

		for (StackTraceElement s : t.getStackTrace()){
			stackTrace += s.getClassName() + " " + s.getFileName() + ":" + s.getLineNumber() + "\n";
		}

		if (t.getCause() != null) {
			stackTrace += getStackTraceAsHTMLString(t.getCause());
		}
		return stackTrace;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getPathInfo();
        WebContext ctx = new WebContext(req, resp, getServletConfig().getServletContext(), req.getLocale());
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

		String[] pathParts;
		if (path != null && !path.equals("/")) {
			pathParts = path.split("/");
			if (pathParts.length > 0 && pathParts[1].equals("tournament")){
				UUID tournamentUUID = UUID.fromString(pathParts[2]);
				String templateFile = tournamentController.getTournament(req, resp, ctx, tournamentUUID);
		        templateEngine.process(templateFile, ctx, resp.getWriter());
				return;
			} else if (pathParts.length > 0 && pathParts[1].equals("season")){
					UUID seasonUUID = UUID.fromString(pathParts[2]);
					String templateFile = seasonController.getSeason(req, resp, ctx, seasonUUID);
			        templateEngine.process(templateFile, ctx, resp.getWriter());
					return;
			} else {
				WebServer.SendResponse("Unhandeled URI", HttpServletResponse.SC_NOT_FOUND, resp);
				return;
			}
		}

        ctx.setVariable("tournaments", persistanceManager.getTournaments().values());
        
        // This will be prefixed with /WEB-INF/ and suffixed with .html
        templateEngine.process("landing", ctx, resp.getWriter());
	}

//	
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//    {
//		//GetTournaments
//		//GetSeasons
//		//GetPlayers
//		//GetCups
//		//GetQualifiers
//		
//        response.setContentType("text/html");
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().println("<h1>Hello Servlet</h1>");
//        response.getWriter().println("session=" + request.getSession(true).getId());
//        
//    }
}

