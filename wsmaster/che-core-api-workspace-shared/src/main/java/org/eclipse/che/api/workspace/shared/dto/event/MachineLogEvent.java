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
package org.eclipse.che.api.workspace.shared.dto.event;

import org.eclipse.che.api.workspace.shared.dto.RuntimeIdentityDto;
import org.eclipse.che.dto.shared.DTO;

/**
 * Defines event format for machine logs.
 *
 * @author Anton Korneta
 */
@DTO
public interface MachineLogEvent {

  /** Returns the contents of the log event. */
  String getText();

  void setText(String text);

  MachineLogEvent withText(String text);

  /** Returns the name of the machine that produces the logs. */
  String getMachineName();

  void setMachineName(String machineName);

  MachineLogEvent withMachineName(String machineName);

  /** Returns runtime identity. */
  RuntimeIdentityDto getRuntimeId();

  void setRuntimeId(RuntimeIdentityDto runtimeId);

  MachineLogEvent withRuntimeId(RuntimeIdentityDto runtimeId);

  /** Returns time in format '2017-06-27T17:11:09.306+03:00' */
  String getTime();

  void setTime(String time);

  MachineLogEvent withTime(String time);

  /** Returns standard streams, if present otherwise, null will be returned. */
  String getStream();

  void setStream(String stream);

  MachineLogEvent withStream(String stream);
}
