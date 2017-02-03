package org.eclipse.che.api.system.server;

import org.eclipse.che.api.system.server.SystemEventsWebsocketBroadcaster.MessageSender;
import org.eclipse.che.api.system.shared.dto.SystemEventDto;
import org.eclipse.che.api.system.shared.dto.SystemServiceEventDto;
import org.eclipse.che.api.system.shared.dto.SystemServiceItemStoppedEventDto;
import org.eclipse.che.api.system.shared.dto.SystemStatusChangedEventDto;
import org.eclipse.che.api.system.shared.event.EventType;
import org.eclipse.che.api.system.shared.event.SystemEvent;
import org.eclipse.che.api.system.shared.event.SystemStatusChangedEvent;
import org.eclipse.che.api.system.shared.event.service.StoppingSystemServiceEvent;
import org.eclipse.che.api.system.shared.event.service.SystemServiceItemStoppedEvent;
import org.eclipse.che.api.system.shared.event.service.SystemServiceStoppedEvent;
import org.eclipse.che.dto.server.DtoFactory;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumSet;

import static org.eclipse.che.api.system.server.SystemEventsWebsocketBroadcaster.SYSTEM_STATE_CHANNEL_NAME;
import static org.eclipse.che.api.system.shared.SystemStatus.PREPARING_TO_SHUTDOWN;
import static org.eclipse.che.api.system.shared.SystemStatus.RUNNING;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * Test {@link SystemEventsWebsocketBroadcaster}.
 *
 * @author Yevhenii Voevodin
 */
@Listeners(MockitoTestNGListener.class)
public class SystemEventsWebsocketBroadcasterTest {

    @Mock
    private MessageSender messageSender;

    private SystemEventsWebsocketBroadcaster broadcaster;

    @BeforeMethod
    public void setUp() {
        broadcaster = new SystemEventsWebsocketBroadcaster(messageSender);
    }

    @Test
    public void allEventsAreHandledByBroadcaster() {
        EnumSet<EventType> handled = EnumSet.of(EventType.STATUS_CHANGED,
                                                EventType.STOPPING_SERVICE,
                                                EventType.SERVICE_ITEM_STOPPED,
                                                EventType.SERVICE_STOPPED);
        assertEquals(handled, EnumSet.allOf(EventType.class));
    }

    @Test(dataProvider = "eventToDto")
    public void sendsMessage(SystemEvent event, SystemEventDto dto) throws Exception {
        broadcaster.onEvent(event);

        verify(messageSender).sendMessage(SYSTEM_STATE_CHANNEL_NAME, DtoFactory.getInstance().toJson(dto));
    }

    @Test
    public void sendExceptionsAreLoggedAndNotThrown() throws Exception {
        doThrow(new IOException("exception!")).when(messageSender).sendMessage(anyString(), anyString());

        broadcaster.onEvent(new SystemServiceStoppedEvent("service1"));
    }

    @DataProvider(name = "eventToDto")
    private static Object[][] eventToDto() {
        SystemStatusChangedEvent statusChanged = new SystemStatusChangedEvent(RUNNING, PREPARING_TO_SHUTDOWN);
        StoppingSystemServiceEvent stoppingService = new StoppingSystemServiceEvent("service1");
        SystemServiceStoppedEvent serviceStopped = new SystemServiceStoppedEvent("service1");
        SystemServiceItemStoppedEvent itemStopped = new SystemServiceItemStoppedEvent("service1", "item1", 5, 10);
        return new Object[][] {
                {statusChanged, SystemStatusChangedEventDto.fromEvent(statusChanged)},
                {stoppingService, SystemServiceEventDto.fromEvent(stoppingService)},
                {serviceStopped, SystemServiceEventDto.fromEvent(serviceStopped)},
                {itemStopped, SystemServiceItemStoppedEventDto.fromEvent(itemStopped)}
        };
    }
}
