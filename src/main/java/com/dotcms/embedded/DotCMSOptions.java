package com.dotcms.embedded;

import java.io.File;

import com.dotcms.embedded.util.WarUnzipper;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class DotCMSOptions extends OptionsBase {

  public File DOTCMS_HOME,ASSET_REAL_PATH,DYNAMIC_CONTENT_PATH,CONFIG_PATH,WEB_ROOT;

  @Option(name = "host", abbrev = 'o', help = "The server host.", category = "startup", defaultValue = "localhost")
  public String host;

  @Option(name = "port", abbrev = 'p', help = "The server port to listen on.", category = "startup", defaultValue = "8080")
  public int port;

  @Option(name = "fileOrFolder", abbrev = 'f', help = ".war file or path to the exploded war folder.", category = "startup",
      defaultValue = "ROOT.war")
  public String fileOrFolder;

  @Option(name = "home", abbrev = 'h', help = "Sets the DOTCMS_HOME", category = "startup", defaultValue = "dotcms")
  public String home;

  @Option(name = "type", abbrev = 't', help = "tomcat || jetty", category = "startup", defaultValue = "tomcat")
  public String type;

  public String getString(final String var, final String defaultVal) {
    return (System.getProperty(var) != null) ? System.getProperty(var) : (System.getenv(var) != null) ? System.getenv(var) : defaultVal;
  }

  public boolean getBoolean(final String var, final boolean defaultVal) {
    String val = (System.getProperty(var) != null) ? System.getProperty(var)
        : (System.getenv(var) != null) ? System.getenv(var) : String.valueOf(defaultVal);
    return Boolean.valueOf(val);
  }

  public DotCMSOptions resolveOptions() {
    
    final String dotHome = this.home;
    final File fileOrFolder = new File(this.fileOrFolder);
    
    if (!fileOrFolder.exists()) {
      System.err.println(fileOrFolder.getAbsolutePath() + " does not exist");
      throw new RuntimeException(fileOrFolder.getAbsolutePath() + " does not exist");
    }
    System.out.println("Trying:" + fileOrFolder.getAbsolutePath());
    System.setProperty("catalina.home", dotHome);
    this.DOTCMS_HOME = new File(dotHome);
    DOTCMS_HOME.mkdirs();
    
    
    this.WEB_ROOT = (!fileOrFolder.isDirectory()) ? new WarUnzipper(fileOrFolder, DOTCMS_HOME).unzipWar() : fileOrFolder;
    this.ASSET_REAL_PATH = new File(this.getString("ASSET_REAL_PATH", DOTCMS_HOME.getAbsolutePath() + File.separator + "assets"));
    this.DYNAMIC_CONTENT_PATH = new File(this.getString("DYNAMIC_CONTENT_PATH", DOTCMS_HOME.getAbsolutePath() + File.separator + "dotsecure"));
    this.CONFIG_PATH = new File(this.getString("CONFIG_PATH", DOTCMS_HOME.getAbsolutePath() + File.separator + "config"));
    CONFIG_PATH.mkdirs();
    ASSET_REAL_PATH.mkdirs();
    
    


    System.out.println("--------------------------------------------------------------------------------------");
    System.out.println("type                 :" + this.type);
    System.out.println("port                 :" + this.port);
    System.out.println("fileOrFolder         :" + this.fileOrFolder);
    System.out.println("home                 :" + this.home);
    System.out.println("DOTCMS_HOME          :" + this.DOTCMS_HOME.getAbsolutePath());
    System.out.println("WEB_ROOT             :" + this.WEB_ROOT.getAbsolutePath());
    System.out.println("ASSET_REAL_PATH      :" + this.ASSET_REAL_PATH.getAbsolutePath());
    System.out.println("CONFIG_PATH          :" + this.CONFIG_PATH.getAbsolutePath());
    System.out.println("DYNAMIC_CONTENT_PATH :" + this.DYNAMIC_CONTENT_PATH.getAbsolutePath());
    System.out.println("--------------------------------------------------------------------------------------");
    
    return this;
  }
  
}
