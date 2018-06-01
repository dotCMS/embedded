package com.dotcms.embedded;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class TomcatStarter implements DotStarter {


  @Override
  public void run(final DotCMSOptions options) {



    Tomcat tomcat = new Tomcat();
    tomcat.setPort(Integer.valueOf(options.port));
    tomcat.setHostname(options.host);
    tomcat.enableNaming();
    tomcat.setBaseDir(options.DOTCMS_HOME.getAbsolutePath());


    // tomcat.getServer().addLifecycleListener(new VersionLoggerListener());
    // tomcat.getHost().addLifecycleListener(new HostConfig());
    StandardContext context=null;
    try {
      context = (StandardContext) tomcat.addWebapp("", options.WEB_ROOT.getAbsolutePath());
    } catch (ServletException e1) {
      SneakyThrow.sneak(e1);
    }


    context.setTldValidation(false);
    context.setDefaultContextXml("default-web.xml");
    context.setJarScanner(jarScanner());
    context.setFireRequestListenersOnForwards(Boolean.TRUE);
    context.setReloadable(Boolean.FALSE);

    Connector c = tomcat.getConnector();

    c.setProperty("compression", options.getString("connector_compression", "on"));
    c.setProperty("compressionMinSize", "1024");
    c.setProperty("noCompressionUserAgents", "gozilla, traviata");
    c.setProperty("compressableMimeType", "text/html,text/xml, text/css, application/json, " + "application/javascript");


    tomcat.setConnector(c);


    ContextResource resource = new ContextResource();
    resource.setName("jdbc/dotCMSPool");
    resource.setAuth("Container");
    resource.setType("javax.sql.DataSource");
    
    resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");

    resource.setProperty("driverClassName", options.getString("db_driver", "com.mysql.jdbc.Driver"));
    resource.setProperty("removeAbandonedTimeout", options.getString("db_removeAbandonedTimeout", "60"));
    resource.setProperty("initialSize", options.getString("db_initialSize", "100"));
    resource.setProperty("minIdle", options.getString("db_minIdle", "100"));
    resource.setProperty("maxActive", options.getString("db_maxTotal", "100"));
    resource.setProperty("characterEncoding", options.getString("db_characterEncoding", "UTF-8"));
    resource.setProperty("url", options.getString("db_url", "jdbc:mysql://localhost/dotcms5?characterEncoding=UTF-8"));
    resource.setProperty("username", options.getString("db_username", "dotcms"));
    resource.setProperty("password", options.getString("db_password", "dotcms"));

    context.getNamingResources().addResource(resource);



    try {
      tomcat.start();
    } catch (LifecycleException e) {
      SneakyThrow.sneak(e);
    }
    tomcat.getServer().await();

  }




  private static final JarScanner jarScanner() {
    StandardJarScanner scanner = new StandardJarScanner();
    scanner.setScanBootstrapClassPath(false);
    StandardJarScanFilter jarScanFilter = (StandardJarScanFilter) scanner.getJarScanFilter();
    jarScanFilter.setTldSkip("*");
    jarScanFilter.setPluggabilitySkip("*");
    scanner.setJarScanFilter(jarScanFilter);
    return scanner;

  }
}
