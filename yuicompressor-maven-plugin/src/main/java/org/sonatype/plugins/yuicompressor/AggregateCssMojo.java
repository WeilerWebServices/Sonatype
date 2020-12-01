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
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.yahoo.platform.yui.compressor.CssCompressor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_RESOURCES;

/**
 * Aggregate CSS sources.
 *
 * @since 1.0
 */
@Mojo(name="aggregate-css", defaultPhase = PROCESS_RESOURCES)
public class AggregateCssMojo
    extends AggregateMojoSupport
{
  public static final String[] DEFAULT_INCLUDES = { "**/*.css" };

  @Parameter(defaultValue = "${project.basedir}/src/main/css")
  private File sourceDirectory;

  @Parameter(defaultValue = "${project.build.outputDirectory}/${project.artifactId}-all.css")
  private File output;

  @Override
  protected void processSourceFile(File source, Reader in, Writer buf) throws IOException {
    CssCompressor compressor = new CssCompressor(in);
    compressor.compress(buf, linebreakpos);
  }

  @Override
  protected String[] getDefaultIncludes() {
    return DEFAULT_INCLUDES;
  }

  @Override
  protected File getOutput() {
    return output;
  }

  @Override
  protected File getSourceDirectory() {
    return sourceDirectory;
  }
}
