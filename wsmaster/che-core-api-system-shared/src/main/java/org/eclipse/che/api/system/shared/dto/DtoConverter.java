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
package org.eclipse.che.api.system.shared.dto;

import org.eclipse.che.api.system.shared.event.SystemStatusChangedEvent;
import org.eclipse.che.api.system.shared.event.service.SystemServiceEvent;
import org.eclipse.che.api.system.shared.event.service.SystemServiceItemStoppedEvent;

/**
 * Copies data from events to DTOs.
 *
 * @author Yevhenii Voevodin
 */
final class DtoConverter {

    public static SystemStatusChangedEventDto copy(SystemStatusChangedEventDto dto, SystemStatusChangedEvent event) {
        dto.setType(event.getType());
        dto.setStatus(event.getStatus());
        dto.setPrevStatus(event.getPrevStatus());
        return dto;
    }

    public static SystemServiceEventDto copy(SystemServiceEventDto dto, SystemServiceEvent event) {
        dto.setService(event.getServiceName());
        dto.setType(event.getType());
        return dto;
    }

    public static SystemServiceItemStoppedEventDto copy(SystemServiceItemStoppedEventDto dto, SystemServiceItemStoppedEvent event) {
        dto.setService(event.getServiceName());
        dto.setType(event.getType());
        dto.setCurrent(event.getCurrent());
        dto.setTotal(event.getTotal());
        dto.setItem(event.getItem());
        return dto;
    }

    private DtoConverter() {}
}
