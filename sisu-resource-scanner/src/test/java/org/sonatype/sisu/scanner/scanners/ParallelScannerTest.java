/*
 * Copyright (c) 2010-2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 */
package org.sonatype.sisu.scanner.scanners;

import org.sonatype.sisu.resource.scanner.Scanner;
import org.sonatype.sisu.resource.scanner.scanners.ParallelScanner;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ParallelScannerTest
    extends AbstractScannerTest
{

  protected Scanner createScanner() {
    return new ParallelScanner(2);
  }

  @Test
  public void parallelisationStrategyNever() {
    assertThat(ParallelScanner.NEVER.shouldScanInParallel(util.getBaseDir()), is(false));
  }

  @Test
  public void parallelisationStrategyEveryDirectory() {
    assertThat(ParallelScanner.EVERY_DIRECTORY.shouldScanInParallel(util.getBaseDir()), is(true));
  }

}
