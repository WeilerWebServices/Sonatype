/*
 * Copyright (c) 2007-2014 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.plugins.yuicompressor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonatype.plexus.build.incremental.BuildContext;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.Scanner;

/**
 * Support for source processing mojos.
 *
 * @since 1.0
 */
abstract class ProcessSourcesMojoSupport
    extends AbstractMojo
{
  /**
   * List of sources includes patterns.
   */
  @Parameter
  protected String[] includes;

  /**
   * List of sources excludes patterns.
   */
  @Parameter
  protected String[] excludes;

  /**
   * Whether sources are required (the default) or optional. The build will fail if sources are not present and value
   * of this parameter is <code>true</code>. Setting to <code>false</code> can be useful when mojo is configured in
   * parent pom.xml and applies to multiple modules.
   */
  @Parameter(defaultValue = "true")
  protected boolean required;

  @Component
  protected BuildContext buildContext;

  @Override
  public void execute() throws MojoExecutionException {
    List<File> sources = new ArrayList<File>();

    File sourceDirectory = getSourceDirectory();
    if (sourceDirectory.exists()) {
      if (includes == null || includes.length <= 0) {
        Scanner scanner = buildContext.newScanner(sourceDirectory, true);
        scanner.setIncludes(getDefaultIncludes());
        scanner.setExcludes(excludes);
        scanTo(sources, scanner);
      }
      else {
        // honour <includes> order
        // TODO maybe do something about same file matching multiple includes
        for (String include : includes) {
          Scanner scanner = buildContext.newScanner(sourceDirectory, true);
          scanner.setIncludes(new String[]{include});
          scanner.setExcludes(excludes);
          scanTo(sources, scanner);
        }
      }
    }
    else {
      getLog().warn("Source directory " + sourceDirectory + " does not exist.");
    }

    if (sources.isEmpty() && required) {
      throw new MojoExecutionException("No sources to process");
    }

    if (!sources.isEmpty()) {
      processSources(sources);
    }
  }

  private void scanTo(List<File> sources, Scanner scanner) {
    scanner.addDefaultExcludes();
    scanner.scan();
    for (String relPath : scanner.getIncludedFiles()) {
      sources.add(new File(scanner.getBasedir(), relPath));
    }
  }

  protected abstract void processSources(List<File> sources) throws MojoExecutionException;

  protected abstract String[] getDefaultIncludes();

  protected abstract File getSourceDirectory();
}
