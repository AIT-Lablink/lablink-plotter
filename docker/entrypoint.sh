#!/bin/sh
set -eu

# Derive URLs at container start so -e LLCONFIG=... takes effect.
LOGGER_ID=${LOGGER_ID:-ait.all.log4j2}
LOGGER_CONFIG="-Dlog4j.configurationFile=${LLCONFIG}${LOGGER_ID}"
CONFIG_FILE_URI="${LLCONFIG}${PLOT_CONFIG}"

# Optional output directory, matching -DdataDir in the example scripts.
DATA_DIR_FLAG=
if [ -n "${DATA_DIR:-}" ]; then
  DATA_DIR_FLAG="-DdataDir=${DATA_DIR}"
fi

echo LLCONFIG: $LLCONFIG
echo LOGGER_CONFIG: $LOGGER_CONFIG
echo DATA_DIR_FLAG: $DATA_DIR_FLAG
echo MEMORY_FLAG: $MEMORY_FLAG
echo PLOT_JAR_FILE: $PLOT_JAR_FILE
echo PLOT_CLASS: $PLOT_CLASS
echo CONFIG_FILE_URI: $CONFIG_FILE_URI

# Desktop mode keeps pointer coordinates in sync with the HTML5 canvas.
# resize-display=yes matches the browser size; matchbox maximises the plotter into it.
# Xvfb can only shrink, so its initial size is the maximum browser size. xpra's
# default of 8192x4096 makes Swing allocate 134 MB screen-sized buffers on every
# resize (-> OutOfMemoryError with -Xmx512m); 4K is plenty and 4x smaller.
exec xpra start-desktop :100 \
  --bind-tcp=0.0.0.0:8080 \
  --html=on \
  --xvfb='Xvfb -screen 0 3840x2160x24 -dpi 96 +extension GLX +extension RANDR +extension RENDER +extension Composite -extension DOUBLE-BUFFER -nolisten tcp -noreset -auth $XAUTHORITY' \
  --resize-display=yes \
  --desktop-scaling=off \
  --clipboard=no \
  --audio=no --speaker=disabled --microphone=disabled --pulseaudio=no \
  --webcam=no --notifications=no --bell=no --mdns=no \
  --printing=no --ssh-upgrade=no --start-new-commands=no \
  --video-encoders=vpx,openh264 \
  --start="matchbox-window-manager -use_titlebar no" \
  --start-child="java $LOGGER_CONFIG $DATA_DIR_FLAG $MEMORY_FLAG -cp $PLOT_JAR_FILE $PLOT_CLASS -c $CONFIG_FILE_URI" \
  --exit-with-children --daemon=no
