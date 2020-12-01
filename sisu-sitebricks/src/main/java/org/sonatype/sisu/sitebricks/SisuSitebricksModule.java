package org.sonatype.sisu.sitebricks;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.sitebricks.SitebricksModule;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.routing.ServiceAction;

public class SisuSitebricksModule extends SitebricksModule {

  //
  // TODO: This needs to be radically simplified
  //
  @SuppressWarnings("unchecked")
  protected void serveAt(String path, Class<? extends Annotation> httpMethod, final String response) {
    at(path).perform(new ServiceAction() {
      public Reply<?> call(Request request, Map<String, String> pathFragments) {
        return Reply.with(response);
      }
    }).on(httpMethod);
  }
}
