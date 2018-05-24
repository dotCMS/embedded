package com.dotcms.embedded.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.dotcms.embedded.SneakyThrow;

public class WarUnzipper {
  
  private final File warFile, catalinaHome;
  
  
  
  
  public WarUnzipper(final File warfile, final File catalinaHome) {
    super();
    this.warFile = warfile;
    this.catalinaHome = catalinaHome;
  }




  public File unzipWar()  {
    System.out.println("exploding " + this.warFile + " into " + catalinaHome);
    String warName =
        (warFile.getName().indexOf(".") > -1) ? warFile.getName().substring(0, warFile.getName().lastIndexOf(".")) : warFile.getName();


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
    }catch(Exception e ) {
      SneakyThrow.sneak(e);
    }
    return ROOT;
  }
}
