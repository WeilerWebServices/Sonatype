package org.sonatype.sisu.sitebricks.rest.resource;


import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.sonatype.sisu.sitebricks.rest.resource.executor.Executor;

import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.routing.ServiceAction;

public class UriHandler extends ServiceAction {
  
  public enum Operation { CREATE, READ, READ_COLLECTION, UPDATE, DELETE };
  
  private Operation operation;
  private Class<? extends Annotation> httpMethod;
  private Class<?> entityType;
  private String path;
  private Executor executor;  

  UriHandler(Operation operation, Class<? extends Annotation> httpMethod, Class<?> entityType, String path, final Executor executor) {
    this.operation = operation;
    this.httpMethod = httpMethod;
    this.entityType = entityType;
    this.path = path;
    this.executor = executor;
  }
  
  //
  //TODO: This needs to accept JSON and XML
  //TODO: method specific handlers would probably make this faster
  //
  
  protected Reply<?> call(Request request, Map<String, String> pathFragments) {
    
    Map<String, Object> executorParameters = new HashMap<String, Object>();

    String method = request.method();
    if (method.equals("POST") || method.equals("PUT")) {
      Object entity = request.read(entityType).as(Json.class);
      executorParameters.put("entity", entity);
    }
    
    for (String key : pathFragments.keySet()) {
      executorParameters.put(key, pathFragments.get(key));
    }

    //
    // When you have /path/:id and use /path/1
    //
    // You will have a Map with the following entry:
    //
    // {id=1}
    //
    return Reply.with(executor.execute(executorParameters)).type("application/json").as(Json.class);
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public Class<? extends Annotation> getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(Class<? extends Annotation> httpMethod) {
    this.httpMethod = httpMethod;
  }

  public Class<?> getEntityType() {
    return entityType;
  }

  public void setEntityType(Class<?> entityType) {
    this.entityType = entityType;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Executor getExecutor() {
    return executor;
  }

  public void setExecutor(Executor executor) {
    this.executor = executor;
  }
}