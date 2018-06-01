package com.dotcms.embedded;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

import com.dotcms.embedded.config.ConfigDBStarter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.devtools.common.options.OptionsParser;

public class DotCMS {



  public static void main(String[] args) throws ServletException, LifecycleException, IOException {


    OptionsParser parser = OptionsParser.newOptionsParser(DotCMSOptions.class);
    parser.parseAndExitUponError(args);
    final DotCMSOptions options = parser.getOptions(DotCMSOptions.class).resolveOptions();
    if (options.fileOrFolder.isEmpty() || options.port < 0) {
      printUsage(parser);
      return;
    }



    /*
     * final File assets = new File( WEB_ROOT.getAbsolutePath() + File.separator + "assets");
     * if(assets.exists() && !Files.isSymbolicLink(assets.toPath())) { assets.renameTo(ASSET_REAL_PATH);
     * Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator +
     * "assets").toPath(), ASSET_REAL_PATH.toPath()); } else if(!assets.exists()) {
     * Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator +
     * "assets").toPath(), ASSET_REAL_PATH.toPath()); }
     * 
     * 
     * if(!DYNAMIC_CONTENT_PATH.exists()) { final File dotsecure = new File( WEB_ROOT.getAbsolutePath()
     * + File.separator + "dotsecure"); if(dotsecure.exists() &&
     * !Files.isSymbolicLink(dotsecure.toPath())) { dotsecure.renameTo(DYNAMIC_CONTENT_PATH);
     * Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() + File.separator +
     * "dotsecure").toPath(), DYNAMIC_CONTENT_PATH.toPath()); } else if(!dotsecure.exists()) {
     * DYNAMIC_CONTENT_PATH.mkdirs(); Files.createSymbolicLink(new File( WEB_ROOT.getAbsolutePath() +
     * File.separator + "dotsecure").toPath(), DYNAMIC_CONTENT_PATH.toPath()); } }
     */



    if ("config".equals(options.type) || true) {
      new ConfigDBStarter().run(options);
    }

    else if ("tomcat".equals(options.type)) {
      new TomcatStarter().run(options);
    } else {
      new JettyStarter().run(options);
    }

  }

  private static void printUsage(OptionsParser parser) {
    System.out.println("Usage: java -jar dotcms.jar OPTIONS");
    System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
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
