package org.sonatype.sisu.sitebricks.rest.resource.executor;

import java.util.Map;

public interface Executor {
  Class<?> getTargetKey();
  void setTarget(Object target);
  Object execute();
  Object execute(Map<String,Object> variables);
  String getExpression();
}