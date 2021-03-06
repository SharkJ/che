/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.workspace.infrastructure.kubernetes.wsplugins.events;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.workspace.server.wsplugins.model.ChePlugin;
import org.eclipse.che.api.workspace.shared.dto.BrokerResultEvent;
import org.eclipse.che.commons.annotation.Nullable;
import org.slf4j.Logger;

/**
 * Configure JSON_RPC consumers of Che plugin broker events. Also converts {@link BrokerResultEvent}
 * to {@link BrokerEvent}.
 *
 * <p>This API is in <b>Beta</b> and is subject to changes or removal.
 *
 * @author Oleksander Garagatyi
 */
@Beta
@Singleton
public class BrokerService {

  private static final Logger LOG = getLogger(BrokerService.class);

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final EventService eventService;

  @Inject
  public BrokerService(EventService eventService) {
    this.eventService = eventService;
  }

  @Inject
  public void configureMethods(RequestHandlerConfigurator requestHandler) {
    requestHandler
        .newConfiguration()
        .methodName("broker/statusChanged")
        .paramsAsDto(BrokerResultEvent.class)
        .noResult()
        .withConsumer(this::handle);

    requestHandler
        .newConfiguration()
        .methodName("broker/result")
        .paramsAsDto(BrokerResultEvent.class)
        .noResult()
        .withConsumer(this::handle);
  }

  private void handle(BrokerResultEvent event) {
    // Tooling has fields that can't be parsed by DTO and JSON_RPC framework works with DTO only
    String encodedTooling = event.getTooling();
    if (event.getStatus() == null
        || event.getWorkspaceId() == null
        || (event.getError() == null && event.getTooling() == null)) {
      LOG.error("Broker event skipped due to illegal content: {}", event);
      return;
    }
    eventService.publish(new BrokerEvent(event, parseTooling(encodedTooling)));
  }

  @Nullable
  private List<ChePlugin> parseTooling(String toolingString) {
    if (!isNullOrEmpty(toolingString)) {
      try {
        return objectMapper.readValue(toolingString, new TypeReference<List<ChePlugin>>() {});
      } catch (IOException e) {
        LOG.error("Parsing Che plugin broker event failed. Error: " + e.getMessage(), e);
      }
    }
    return null;
  }
}
