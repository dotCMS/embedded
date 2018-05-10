package com.dotcms.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        final String DOTCMS_HOME = options.home;
        final File fileOrFolder = new File(options.fileOrFolder);
        System.out.println("Trying:" + fileOrFolder.getAbsolutePath());
        System.setProperty("catalina.home", DOTCMS_HOME);
        final File dotCMSHome = new File(DOTCMS_HOME);
        dotCMSHome.mkdirs();
        
        if (!fileOrFolder.exists()) {
            System.err.println(fileOrFolder.getAbsolutePath() + " does not exist");
            printUsage(parser);
            return;
        }

        final File WEB_ROOT = (!fileOrFolder.isDirectory()) ? unzipWar(fileOrFolder, dotCMSHome) : fileOrFolder;
        final File ASSET_REAL_PATH = new File(options.getString("ASSET_REAL_PATH", dotCMSHome.getAbsolutePath() + File.separator + "assets"));
        final File DYNAMIC_CONTENT_PATH = new File(options.getString("DYNAMIC_CONTENT_PATH", dotCMSHome.getAbsolutePath() + File.separator + "dotsecure"));
        final File CONFIG_PATH = new File(options.getString("CONFIG_PATH", dotCMSHome.getAbsolutePath() + File.separator + "config"));
        
        ASSET_REAL_PATH.mkdirs();
        /*
        final File assets = new File( WEB_ROOT.getAbsolutePath() + File.separator + "assets");
        if(assets.exists() && !Files.isSymbolicLink(assets.toPath())) {
            assets.renameTo(ASSET_REAL_PATH);
            Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator + "assets").toPath(), ASSET_REAL_PATH.toPath());
        }
        else if(!assets.exists()) {
            Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator + "assets").toPath(), ASSET_REAL_PATH.toPath());
        }
        
        
        if(!DYNAMIC_CONTENT_PATH.exists()) {
            final File dotsecure = new File( WEB_ROOT.getAbsolutePath() + File.separator + "dotsecure");
            if(dotsecure.exists() && !Files.isSymbolicLink(dotsecure.toPath())) {
                dotsecure.renameTo(DYNAMIC_CONTENT_PATH);
                Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator + "dotsecure").toPath(), DYNAMIC_CONTENT_PATH.toPath());
            }
            else if(!dotsecure.exists()) {
                DYNAMIC_CONTENT_PATH.mkdirs();
                Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator + "dotsecure").toPath(), DYNAMIC_CONTENT_PATH.toPath());
            }
        }
        */

        

        CONFIG_PATH.mkdirs();
        
        
        
        
        System.out.println("DOTCMS_HOME          :" + dotCMSHome.getAbsolutePath());
        System.out.println("WEB_ROOT             :" + WEB_ROOT.getAbsolutePath());
        System.out.println("ASSET_REAL_PATH      :" + ASSET_REAL_PATH.getAbsolutePath());
        System.out.println("CONFIG_PATH          :" + CONFIG_PATH.getAbsolutePath());
        System.out.println("DYNAMIC_CONTENT_PATH :" + DYNAMIC_CONTENT_PATH.getAbsolutePath());

        
        System.exit(0);
        
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.valueOf(options.port));
        tomcat.setHostname(options.host);
        tomcat.enableNaming();
        tomcat.setBaseDir(dotCMSHome.getAbsolutePath());

        
        // tomcat.getServer().addLifecycleListener(new VersionLoggerListener());
        // tomcat.getHost().addLifecycleListener(new HostConfig());
        StandardContext context = (StandardContext) tomcat.addWebapp("", WEB_ROOT.getAbsolutePath());

        
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
        resource.setProperty("maxTotal", options.getString("db_maxTotal", "100"));
        resource.setProperty("url", options.getString("db_url", "jdbc:mysql://localhost/dotcms5?characterEncoding=UTF-8"));
        resource.setProperty("username", options.getString("db_username", "dotcms"));
        resource.setProperty("password", options.getString("db_password", "dotcms"));

        context.getNamingResources().addResource(resource);





        tomcat.start();
        tomcat.getServer().await();

    }

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar dotcms.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
    }

    private static File unzipWar(final File warFile, final File catalinaHome) throws IOException {
        System.out.println("exploding " + warFile + " into " + catalinaHome);
        String warName =
                (warFile.getName().indexOf(".") > -1) ? warFile.getName().substring(0, warFile.getName().lastIndexOf("."))
                        : warFile.getName();


        final File ROOT = new File(catalinaHome, warName);

        // if already exploded
        if (ROOT.exists() && new File(ROOT, "WEB-INF").exists()) {
            return ROOT;
        }
        byte[] buffer = new byte[4096];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(warFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(ROOT.getAbsolutePath() + File.separator + fileName);
                if (fileName.endsWith(File.separator)) {
                    newFile.mkdirs();
                } else {
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
    
    private static String logObject(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
        
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
