/*
 *
 */
package com.garry.springlifecycle.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * the locator for configure file
 *
 */
public class FileLocator {

 /**
  * com.jdon.sample.xxx.xml ==>
  * com/jdon/sample/xxx.xml
  * @param filePathName
  * @return filename's string
  */
  public String getConfPathXmlFile(String filePathName){
     int i = filePathName.lastIndexOf(".xml");
     String name = filePathName.substring(0, i);
     name = name.replace('.', '/');
     name += ".xml";
     return getConfFile(name);
  }

  /**
   * same as getConfPathXmlFile
   * @param filePathName
   * @return the InputStream intance
   */
  public InputStream getConfPathXmlStream(String filePathName){
     int i = filePathName.lastIndexOf(".xml");
     String name = filePathName.substring(0, i);
     name = name.replace('.', '/');
     name += ".xml";
     return getConfStream(name);
  }


  public String getConfFile(String fileName) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }
    URL confURL = classLoader.getResource(fileName);
    if (confURL == null)
      confURL = classLoader.getResource("META-INF/" + fileName);
    if (confURL == null) {
      return null;
    } else {
      File file1 = new File(confURL.getFile());
      if (file1.isFile()) {
        System.out.println(" locate file: " + confURL.getFile());
        return confURL.getFile();
      } else {
        System.err.println(" it is not event file: " + confURL.getFile());
        return null;
      }
    }
  }

  public InputStream getConfStream(String fileName) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = this.getClass().getClassLoader();
    }
    InputStream stream = classLoader.getResourceAsStream(fileName);
    if (stream == null)
      stream = classLoader.getResourceAsStream("META-INF/" + fileName);
   
    return stream;
  }

}
