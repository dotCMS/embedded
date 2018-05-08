package com.dotcms.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.HostConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import com.google.devtools.common.options.OptionsParser;

public class StartDotCMS {



    public static void main(String[] args) throws ServletException, LifecycleException, IOException {


        OptionsParser parser = OptionsParser.newOptionsParser(DotCMSOptions.class);
        parser.parseAndExitUponError(args);
        DotCMSOptions options = parser.getOptions(DotCMSOptions.class);
        if (options.fileOrFolder.isEmpty() || options.port < 0) {
            printUsage(parser);
            return;
        }

        final String CATALINA_HOME = options.home;
        final File fileOrFolder = new File(options.fileOrFolder);
        System.out.println("Trying:" + fileOrFolder.getAbsolutePath());
        if (!fileOrFolder.exists()) {
            System.out.println(fileOrFolder.getAbsolutePath() + " does not exist");
            printUsage(parser);
            return;
        }



        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.valueOf(options.port));
        tomcat.setHostname(options.host);
        System.setProperty("catalina.home", CATALINA_HOME);
        tomcat.enableNaming();
        File catalinaHome = new File(CATALINA_HOME);
        catalinaHome.mkdirs();

        final File WEB_ROOT=(!fileOrFolder.isDirectory()) ? unzipWar(fileOrFolder, catalinaHome) : fileOrFolder;


        System.out.println("CATALINA_HOME:" + catalinaHome.getAbsolutePath());
        System.out.println("WEB_ROOT:" + WEB_ROOT.getAbsolutePath());



        tomcat.setBaseDir(catalinaHome.getAbsolutePath());
        tomcat.getServer().addLifecycleListener(new VersionLoggerListener());
        tomcat.getHost().addLifecycleListener(new HostConfig());

        StandardContext context = (StandardContext) tomcat.addWebapp("",WEB_ROOT.getAbsolutePath());

        context.setTldValidation(false);
        context.setDefaultContextXml("default-web.xml");

        StandardJarScanner scanner = new StandardJarScanner();
        scanner.setScanBootstrapClassPath(true);
        context.setJarScanner(scanner);


        StandardJarScanFilter jarScanFilter = (StandardJarScanFilter) context.getJarScanner().getJarScanFilter();
        jarScanFilter.setTldSkip("*");



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
        
        resource.setProperty("driverClassName",         options.getString("db_driver", "com.mysql.jdbc.Driver"));
        resource.setProperty("removeAbandonedTimeout",  options.getString("db_removeAbandonedTimeout", "60"));
        resource.setProperty("initialSize",             options.getString("db_initialSize", "100"));
        resource.setProperty("minIdle",                 options.getString("db_minIdle", "100"));
        resource.setProperty("maxTotal",                options.getString("db_maxTotal", "100"));
        resource.setProperty("url",                     options.getString("db_url", "jdbc:mysql://localhost/dotcms5?characterEncoding=UTF-8"));
        resource.setProperty("username",                options.getString("db_username", "dotcms"));
        resource.setProperty("password",                options.getString("db_password", "dotcms"));

        context.getNamingResources().addResource(resource);


        tomcat.start();
        tomcat.getServer().await();

    }

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar dotcms.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
    }

    private static File unzipWar(final File warFile, final File catalinaHome) throws IOException {
        System.out.println("exploding " +warFile + " into " + catalinaHome);
        String warName = (warFile.getName().indexOf(".")>-1) ? warFile.getName().substring(0, warFile.getName().lastIndexOf(".")) : warFile.getName();
        
        
        final File ROOT = new File(catalinaHome, warName);
        byte[] buffer = new byte[4096];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(warFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(ROOT.getAbsolutePath() + File.separator + fileName);
                if(fileName.endsWith(File.separator)) {
                    newFile.mkdirs();
                }
                else {
                    new File(newFile.getParent()).mkdirs();
                
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
        }
        return ROOT;
    }
}
