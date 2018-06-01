package com.dotcms.embedded.config;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import com.dotcms.embedded.DotCMSOptions;
import com.dotcms.embedded.DotStarter;
import com.dotcms.embedded.SneakyThrow;

public class ConfigDBStarter implements DotStarter {


  @Override
  public void run(final DotCMSOptions options) {



    Tomcat tomcat = new Tomcat();
    tomcat.setPort(Integer.valueOf(options.port));
    tomcat.setHostname(options.host);
    tomcat.enableNaming();
    tomcat.setBaseDir(options.DOTCMS_HOME.getAbsolutePath());

    

    // tomcat.getServer().addLifecycleListener(new VersionLoggerListener());
    // tomcat.getHost().addLifecycleListener(new HostConfig());

    StandardContext context=(StandardContext) tomcat.addContext("", "");
    Wrapper wrapper = context.createWrapper();
    wrapper.setServletClass(ConfigServlet.class.getName());
    wrapper.setServlet(new ConfigServlet());
    wrapper.setOverridable(false);
    wrapper.setName(ConfigServlet.class.getName());
 


    context.addChild(wrapper);

    
    context.addServletMapping("/*", ConfigServlet.class.getName());
    
    
    
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
