/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.terminal;

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;

import com.google.gwt.core.client.ScriptInjector;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.lib.terminal.client.TerminalResources;

/** Terminal entry point. */
@Singleton
public class TerminalInitializer {

  @Inject
  public TerminalInitializer(final TerminalResources terminalResources) {
    terminalResources.getTerminalStyle().ensureInjected();
    ScriptInjector.fromString(terminalResources.xtermScript().getText())
        .setWindow(TOP_WINDOW)
        .inject();
    ScriptInjector.fromString(terminalResources.fitScript().getText())
        .setWindow(TOP_WINDOW)
        .inject();
  }
}
