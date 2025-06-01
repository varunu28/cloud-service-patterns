# Use the Traefik version you are deploying
FROM traefik:v2.11

# Define variables for paths to make it cleaner
ARG PLUGIN_MODULE_PATH=github.com/varunu28/traefik-plugin
ARG PLUGIN_SOURCE_DIR=./plugins/jwt-auth
ENV CONTAINER_PLUGIN_DIR /plugins-local/src/${PLUGIN_MODULE_PATH}

# Create the target directory in the container
RUN mkdir -p ${CONTAINER_PLUGIN_DIR}

# Copy the plugin manifest file
COPY ${PLUGIN_SOURCE_DIR}/.traefik.yml ${CONTAINER_PLUGIN_DIR}/.traefik.yml

# Copy go.mod and go.sum files
COPY ${PLUGIN_SOURCE_DIR}/go.mod ${CONTAINER_PLUGIN_DIR}/go.mod

# Copy your plugin's Go source files
COPY ${PLUGIN_SOURCE_DIR}/*.go ${CONTAINER_PLUGIN_DIR}/