package org.sonatype.sisu.sitebricks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;
import org.sonatype.sisu.sitebricks.rest.resource.UriHandlerMap;
import org.sonatype.sisu.sitebricks.rest.resource.UriHandlerMapGeneratorUsingMethodNames;

import com.google.inject.Injector;
import com.google.inject.Module;

//
// A convenient way to create modules for testing and application use in the same way
//
public class SisuSitebricksModulesBuilder {

  private File webappDirectory;
  private String appName;
  private List<UriHandlerMap> resources = new ArrayList<UriHandlerMap>();
  private List<Module> modules = new ArrayList<Module>();
  private SisuSitebricksModule sitebricksModule;

  public SisuSitebricksModulesBuilder(File webappDirectory, String appName) {
    this.webappDirectory = webappDirectory;
    this.appName = appName;
  }

  public void build() {
    modules.add(new SpaceModule(new URLClassSpace(getClass().getClassLoader()), BeanScanning.CACHE));
    modules.add(new SisuConfigurationModule(webappDirectory, appName));
    modules.add(sitebricksModule);
  }

  protected void scanForResources(Class<?> classToScanForResources, String path, SisuSitebricksModule sitebricksModule) {
    UriHandlerMapGeneratorUsingMethodNames resourceGenerator = new UriHandlerMapGeneratorUsingMethodNames(path, classToScanForResources, sitebricksModule);
    resources.add(resourceGenerator.generate());
  }

  public void onInjectorConstruction(Injector injector) {
    for (UriHandlerMap resource : resources) {
      resource.activateUriHandler(injector);
    }
  }

  public void setSitebricksModule(SisuSitebricksModule sitebricksModule) {
    this.sitebricksModule = sitebricksModule;
  }
  
  public List<Module> getModules() {
    return modules;
  }
}
