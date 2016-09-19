package cn.td.geotags.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.talkingdata.monitor.client.MonitorServlet;

public class CustomMonitorServlet extends MonitorServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String sortStr = req.getParameter("sort");
        String prettyStr = req.getParameter("pretty");
        if ((sortStr == null || "".equals(sortStr)) && (prettyStr == null || "".equals(prettyStr))) {
            out.println(CustomMonitorClient.toJson());
        } else {
            out.println(CustomMonitorClient.toJson(Boolean.parseBoolean(sortStr), Boolean.parseBoolean(prettyStr)));
        }
    }
}
