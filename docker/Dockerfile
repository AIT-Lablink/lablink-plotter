# ---------- Stage 1: resolve deps + build (Maven + JDK 11) ----------
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build

# Copy ONLY the POM first — this layer caches all Maven Central downloads
COPY pom.xml .
RUN mvn -B -e dependency:go-offline

# Sources come after, so editing code doesn't re-download dependencies
RUN mvn -B package -DskipTests \
    && mvn -B dependency:copy-dependencies -DoutputDirectory=target/lib

# ---------- Stage 2: runtime (JDK 11 + xpra) ----------
FROM eclipse-temurin:11-jdk-jammy

# Ubuntu jammy ships xpra 3.1 with an old bundled HTML5 client that needs source
# patches to behave. Use the current xpra server from the xpra.org repository and
# a pinned xpra-html5 release, which is configurable via default-settings.txt.
ARG XPRA_HTML5_VERSION=20

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
         ca-certificates wget \
    && wget -qO /usr/share/keyrings/xpra.asc https://xpra.org/xpra.asc \
    && wget -qO /etc/apt/sources.list.d/xpra.sources \
         https://raw.githubusercontent.com/Xpra-org/xpra/master/packaging/repos/jammy/xpra.sources \
    && apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
         xpra \
         xpra-x11 \
         matchbox-window-manager \
         dbus-x11 \
         fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# xpra-html5: installs into /usr/share/xpra/www, config in /etc/xpra/html5-client.
RUN wget -qO- https://github.com/Xpra-org/xpra-html5/archive/refs/tags/v${XPRA_HTML5_VERSION}.tar.gz \
      | tar -xz -C /tmp \
    && cd /tmp/xpra-html5-${XPRA_HTML5_VERSION} \
    && python3 setup.py install \
    && rm -rf /tmp/xpra-html5-${XPRA_HTML5_VERSION}

# HTML5 client defaults (clipboard off, no toolbar, ...).
COPY default-settings.txt /etc/xpra/html5-client/default-settings.txt
# Replace the connect form (shown after a disconnect) with an auto-reconnect page.
# xpra serves a pre-gzipped sibling if present, so drop the stale one.
COPY connect.html /usr/share/xpra/www/connect.html
RUN rm -f /usr/share/xpra/www/connect.html.gz

# xpra complains loudly without a runtime dir
ENV XDG_RUNTIME_DIR=/tmp/runtime
RUN mkdir -p /tmp/runtime && chmod 700 /tmp/runtime

WORKDIR /app
COPY --from=build /build/target/lib /app/lib

# Connection string for configuration server. Override at runtime with -e LLCONFIG=...
# LOGGER_CONFIG and CONFIG_FILE_URI are derived from this in entrypoint.sh.
ENV LLCONFIG=http://lablink-config:10101/get?id=

# Plotter configuration id (appended to LLCONFIG).
ENV PLOT_CONFIG=lablink-plotter.config

# Path to class implementing the main routine.
ENV PLOT_CLASS=at.ac.ait.lablink.clients.plotter.PlotterAsync

# Path to Java JAR file of data plotter.
ENV PLOT_JAR_FILE=/app/lib/plotter-0.0.4-jar-with-dependencies.jar

# Specify the maximum size (in bytes) of the memory allocation pool.
ENV MEMORY_FLAG=-Xmx512m

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
