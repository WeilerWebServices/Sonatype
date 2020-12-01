package org.sonatype.sisu.sitebricks.rest.resource;


import java.util.LinkedHashMap;
import java.util.Map;

import org.sonatype.sisu.sitebricks.rest.resource.UriHandler.Operation;
import org.sonatype.sisu.sitebricks.rest.resource.executor.Executor;

import com.google.inject.Injector;
import com.google.sitebricks.PageBinder;
import com.google.sitebricks.SitebricksModule;

// I would just like to adapt this into the PageBook

public class UriHandlerMap {
  private String path;
  private SitebricksModule sitebricksModule;
  private Map<PathOperation, UriHandler> uriHandlers;

  UriHandlerMap(String path, SitebricksModule sitebricksModule) {
    this.path = path;
    this.sitebricksModule = sitebricksModule;
    this.uriHandlers = new LinkedHashMap<PathOperation, UriHandler>();
  }

  public void addHandler(UriHandler handler) {
    uriHandlers.put(new PathOperation(handler.getPath(),handler.getOperation()), handler);
  }

  @SuppressWarnings("unchecked")
  public void activateUriHandler(Injector injector) {
    
    Map<String,PageBinder.ShowBinder> binders = new LinkedHashMap<String,PageBinder.ShowBinder>();
    
    for (UriHandler uriHandler : uriHandlers.values()) {
      PageBinder.ShowBinder binder = binders.get(uriHandler.getPath());
      
      if (binder == null) {
        binder = sitebricksModule.at(uriHandler.getPath());
        binders.put(uriHandler.getPath(), binder);
      }
      
      Executor executor = uriHandler.getExecutor();
      executor.setTarget(injector.getInstance(executor.getTargetKey()));
      
      binder
        .perform(uriHandler)
        .on(uriHandler.getHttpMethod());
    }    
  }
  
  class PathOperation {
    String path;
    Operation operation;
        
    public PathOperation(String path, Operation operation) {
      this.path = path;
      this.operation = operation;
    }
    
    public String getPath() {
      return path;
    }
    public void setPath(String path) {
      this.path = path;
    }
    public Operation getOperation() {
      return operation;
    }
    public void setOperation(Operation operation) {
      this.operation = operation;
    }
  }
}