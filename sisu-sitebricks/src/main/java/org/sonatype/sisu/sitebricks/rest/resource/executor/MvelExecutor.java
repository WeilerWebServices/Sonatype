package org.sonatype.sisu.sitebricks.rest.resource.executor;

import java.util.Map;

import org.mvel2.MVEL;

public class MvelExecutor implements Executor {
  private String expression;
  private Class<?> targetKey;
  private Object target;

  public MvelExecutor(String expression, Class<?> targetKey) {
    this.expression = expression;
    this.targetKey = targetKey;
  }

  public Class<?> getTargetKey() {
    return targetKey;
  }

  public void setTarget(Object target) {
    this.target = target;
  }

  public Object execute(Map<String, Object> variables) {
    return MVEL.eval(expression, target, variables);
  }

  public Object execute() {
    return MVEL.eval(expression, target);
  }
  
  public String getExpression() {
    return expression;
  }
}