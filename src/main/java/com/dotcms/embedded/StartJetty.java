package com.dotcms.embedded;
/*
import java.io.File;
import java.lang.management.ManagementFactory;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

*/
public class StartJetty implements DotStarter{

  @Override
  public void run(DotCMSOptions options) {
    // TODO Auto-generated method stub
    
  }
/*
  @Override
  public void run(final DotCMSOptions options) {
      // Create a basic jetty server object that will listen on port 8080.
      // Note that if you set this to port 0 then
      // a randomly available port will be assigned that you can either look
      // in the logs for the port,
      // or programmatically obtain it for use in test cases.
      Server server = new Server( 8080 );

      // Setup JMX
      MBeanContainer mbContainer = new MBeanContainer(
              ManagementFactory.getPlatformMBeanServer() );
      server.addBean( mbContainer );

      // The WebAppContext is the entity that controls the environment in
      // which a web application lives and
      // breathes. In this example the context path is being set to "/" so it
      // is suitable for serving root context
      // requests and then we see it setting the location of the war. A whole
      // host of other configurations are
      // available, ranging from configuring to support annotation scanning in
      // the webapp (through
      // PlusConfiguration) to choosing where the webapp will unpack itself.
      WebAppContext webapp = new WebAppContext();
      webapp.setContextPath( "/" );
      File warFile = options.WEB_ROOT;
      if (!warFile.exists())
      {
          throw new RuntimeException( "Unable to find WAR File: "
                  + warFile.getAbsolutePath() );
      }
      webapp.setWar( warFile.getAbsolutePath() );

      // This webapp will use jsps and jstl. We need to enable the
      // AnnotationConfiguration in order to correctly
      // set up the jsp container
      Configuration.ClassList classlist = Configuration.ClassList
              .setServerDefault( server );
      classlist.addBefore(
              "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
              "org.eclipse.jetty.annotations.AnnotationConfiguration" );

      // Set the ContainerIncludeJarPattern so that jetty examines these
      // container-path jars for tlds, web-fragments etc.
      // If you omit the jar that contains the jstl .tlds, the jsp engine will
      // scan for them instead.
       * 
       */
  
     // webapp.setAttribute(  "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );

      // A WebAppContext is a ContextHandler as well so it needs to be set to
      // the server so it is aware of where to
      // send the appropriate requests.
  /*
      server.setHandler( webapp );



      // Start things up!
      try {
        server.start();
        server.join();
      } catch (Exception e) {
        SneakyThrow.sneak(e);
      }


     
  }
  */

}
