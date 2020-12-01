package org.sonatype.sisu.sitebricks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.sonatype.guice.bean.binders.ParameterKeys;

import com.google.inject.AbstractModule;

/**
 * Attempt to load a configuration for this application from a properties file. First we attempt to read
 * the properties from the file system, and if that fails we attempt to read the properties from
 * the classpath.
 * 
 * @author jvanzyl
 */

public class SisuConfigurationModule extends AbstractModule {
  
  private File configurationDirectory;
  private String applicationId;

  public SisuConfigurationModule(File configurationDirectory, String applicationId) {
    this.configurationDirectory = configurationDirectory;
    this.applicationId = applicationId;
  }

  @Override
  protected void configure() {
    String propertiesFileName = applicationId + ".properties";
    Properties properties = new Properties();
    File propertiesFile = new File(configurationDirectory, propertiesFileName);

    if (propertiesFile.exists()) {
      try {
        properties.load(new FileInputStream(propertiesFile));     
      } catch (FileNotFoundException e) {
      } catch (IOException e) {
      }
    } else {
      try {
        InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
        if (is != null) {
          properties.load(is);
        }
      } catch (IOException e) {
      }
    }
    
    // This should be in a proviso module
    properties.setProperty("configDir", configurationDirectory.getAbsolutePath());
    properties.setProperty("runtimeBaseDirectory", System.getProperty("runtime.home"));
    properties.setProperty("workDirectory", System.getProperty("workDirectory"));
    
    //
    // Add configuration directory as a property
    //
    Map<String,String> m = (Map) properties;
    for(String key: m.keySet()) {
      System.out.println(key + " ==> " + m.get(key));
    }

    if (!properties.isEmpty()) {
      bind(ParameterKeys.PROPERTIES).toInstance((Map) properties);
    }
  }
}