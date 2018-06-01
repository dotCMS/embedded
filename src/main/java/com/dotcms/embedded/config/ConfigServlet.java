package com.dotcms.embedded.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;

public class ConfigServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {


    String uri = req.getRequestURI();
    if (null == uri || uri.length() < 2) {
      uri = "/index.html";
    }
    if (uri.indexOf("..") > -1 || uri.indexOf("//") >= 1) {
      resp.sendError(404);
    }

    

    
    OutputStream out = resp.getOutputStream();
    try (InputStream fis = ConfigServlet.class.getResourceAsStream("/web" + uri)) {
      IOUtils.copy(fis,out);
    }



  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // TODO Auto-generated method stub
    super.doPost(req, resp);
  }

}
