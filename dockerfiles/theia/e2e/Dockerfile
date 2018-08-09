# Copyright (c) 2018 Red Hat, Inc.
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0

# Use upstream image
FROM ${BUILD_ORGANIZATION}/${BUILD_PREFIX}-theia:${BUILD_TAG} as che-theia

# Use of cypress
FROM cypress/base:8
# Make tsserver available
RUN npm install -g typescript@2.8.4

# Add cypress scripts and grab dependencies
ADD package.json /home/cypress/
RUN cd /home/cypress && yarn

# Copy theia folder to this image
COPY --from=che-theia /home/theia /home/theia

COPY / /home/cypress/
RUN cd /home/cypress && yarn

# Command to trigger the tests
CMD /home/cypress/docker-run.sh
