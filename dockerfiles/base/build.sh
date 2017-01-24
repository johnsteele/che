#!/bin/sh
# Copyright (c) 2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html

IMAGE_NAME="eclipse/che-base"
base_dir=$(cd "$(dirname "$0")"; pwd)
. $base_dir/../build.include

init "$@"
build

if [ $(skip_tests "$@") = false ]; then
  sh $base_dir/test.sh $TAG
fi
