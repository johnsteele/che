/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.system.server;

import com.google.common.annotations.VisibleForTesting;

import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.api.system.shared.dto.SystemServiceItemStoppedEventDto;
import org.eclipse.che.api.system.shared.dto.SystemEventDto;
import org.eclipse.che.api.system.shared.dto.SystemServiceEventDto;
import org.eclipse.che.api.system.shared.dto.SystemStatusChangedEventDto;
import org.eclipse.che.api.system.shared.event.service.SystemServiceItemStoppedEvent;
import org.eclipse.che.api.system.shared.event.SystemEvent;
import org.eclipse.che.api.system.shared.event.service.SystemServiceEvent;
import org.eclipse.che.api.system.shared.event.SystemStatusChangedEvent;
import org.eclipse.che.dto.server.DtoFactory;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Broadcasts system status events to the websocket channel.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class SystemEventsWebsocketBroadcaster implements EventSubscriber<SystemEvent> {

    public static final String SYSTEM_STATE_CHANNEL_NAME = "system:state";

    private final MessageSender messageSender;

    public SystemEventsWebsocketBroadcaster() {
        this(new WebsocketMessageSender());
    }

    public SystemEventsWebsocketBroadcaster(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Inject
    public void subscribe(EventService eventService) {
        eventService.subscribe(this);
    }

    @Override
    public void onEvent(SystemEvent event) {
        try {
            messageSender.sendMessage(SYSTEM_STATE_CHANNEL_NAME, DtoFactory.getInstance().toJson(asDto(event)));
        } catch (Exception x) {
            LoggerFactory.getLogger(getClass()).error(x.getMessage(), x);
        }
    }

    private static SystemEventDto asDto(SystemEvent event) {
        switch (event.getType()) {
            case STATUS_CHANGED:
                return DtoFactory.newDto(SystemStatusChangedEventDto.class).copy((SystemStatusChangedEvent)event);
            case SERVICE_ITEM_STOPPED:
                return DtoFactory.newDto(SystemServiceItemStoppedEventDto.class).copy((SystemServiceItemStoppedEvent)event);
            case SERVICE_STOPPED:
            case STOPPING_SERVICE:
                return DtoFactory.newDto(SystemServiceEventDto.class).copy((SystemServiceEvent)event);
            default:
                throw new IllegalStateException("Can't convert event to dto, event type '" + event.getType() + "' is unknown");
        }
    }

    /** An abstraction that allows to switch message sender backend. */
    @VisibleForTesting
    interface MessageSender {

        /**
         * Sends a given text message to a given channel.
         *
         * @param channel
         *         channel to send message to
         * @param message
         *         message to send
         * @throws Exception
         *         when any error occurs
         */
        void sendMessage(String channel, String message) throws Exception;
    }

    private static class WebsocketMessageSender implements MessageSender {
        @Override
        public void sendMessage(String channel, String message) throws Exception {
            ChannelBroadcastMessage cbm = new ChannelBroadcastMessage();
            cbm.setBody(message);
            cbm.setChannel(channel);
            WSConnectionContext.sendMessage(cbm);
        }
    }
}
