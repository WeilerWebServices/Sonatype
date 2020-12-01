package org.sonatype.sisu.sitebricks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;
import org.sonatype.sisu.sitebricks.rest.resource.UriHandlerMap;
import org.sonatype.sisu.sitebricks.rest.resource.UriHandlerMapGeneratorUsingMethodNames;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

public abstract class SisuSitebricksConfig extends GuiceServletContextListener {

  public static final String INJECTOR_KEY = "@INJECTOR";
  
  private Logger logger;
  private Injector injector;
  private List<Module> modules;
  
  private File webappDirectory;
  //
  // This needs to be an extension point, where we are scanning for resources that instruct the system how to map a URI to an
  // action to perform.
  //
  private List<UriHandlerMap> resources = new ArrayList<UriHandlerMap>();
  private ServletContext servletContext;

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {

    servletContext = servletContextEvent.getServletContext();
    webappDirectory = new File(servletContextEvent.getServletContext().getRealPath("/"));

    //
    // We need to set the injector here first because super.contextInitialized( servletContext ) will call getInjector() so if we have not
    // retrieved our injector created elsewhere, say from a testing environment, a new one will be created and cause inconsistencies.
    //
    injector = (Injector) servletContextEvent.getServletContext().getAttribute(INJECTOR_KEY);

    if (injector != null) {
      logger = injector.getInstance(Logger.class);
    }

    super.contextInitialized(servletContextEvent);
  }

  protected Injector getInjector() {
    //
    // If an injector has been created and added to the servlet context then the client has decided they have what they need already.
    //
    if (injector != null) {
      return injector;
    }

    addModules();    
    
    if (logger != null) {
      for (Module m : modules) {
        logger.info("Installing module from " + getClass().getName() + ": " + m);
      }
    } else {
      for (Module m : modules) {
        System.out.println("Installing module from " + getClass().getName() + ": " + m);
      }
    }
    
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new WireModule(modules));
    //
    // The injector has now been created so if there are any operations that require the injector before
    // the system starts up completely here is where they have access to the injector
    //
    onInjectorConstruction(injector);

    return injector;
  }

  protected abstract void addModules();
  
  protected abstract String getApplicationId();

  //
  // At this point the Injector has been created so we can use it for anything
  // we need before the application starts up.
  //
  protected void onInjectorConstruction(Injector injector) {
    for (UriHandlerMap resource : resources) {
      resource.activateUriHandler(injector);
    }
  }

  protected void scanForResources(Class<?> classToScanForResources, String path, SisuSitebricksModule sitebricksModule) {
    UriHandlerMapGeneratorUsingMethodNames resourceGenerator = new UriHandlerMapGeneratorUsingMethodNames(path, classToScanForResources, sitebricksModule);
    resources.add(resourceGenerator.generate());
  }

  protected void addModule(Module module) {
    //
    // If an injector has been added to the servlet context then the client has decided they have what they need already.
    //
    if (injector != null) {
      return;
    }

    if (modules == null) {
      modules = new ArrayList<Module>();
    }
    modules.add(module);
  }

  protected File getWebappDirectory() {
    return webappDirectory;
  }

  protected File getWebInfDirectory() {
    return new File(getWebappDirectory(), "WEB-INF");
  }

  protected ServletContext getServletContext() {
    return servletContext;
  }
  
  //
  // Standard Modules
  //
  protected Module spaceModule() {
    return new SpaceModule(new URLClassSpace(getClass().getClassLoader()), BeanScanning.CACHE);
  }
  
  protected Module configurationModule() {
    return new SisuConfigurationModule(getWebInfDirectory(), getApplicationId());
  }
}
