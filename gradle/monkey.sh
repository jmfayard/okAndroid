#!/usr/bin/env bash
export PACKAGE=$1
export ITERATIONS=5
export EVENTS=100
export FABRIC="https://goo.gl/Hd9vDn"
echo "Starting $PACKAGE"

#test "$ITERATIONS" == "install" && {
#    echo "Building and installing $PACKAGE"
#    ./gradlew app:installFabricDebug
#    exit 0
#}

#export START_OK=$( adb shell am start -n ${PACKAGE}/com.mautinoa.client.MainActivity 2>&1 | grep -i error )
#test -z "$START_OK" || {
#    echo "Please install the latest fabric release of ${PACKAGE} from ${FABRIC}"
#    adb shell am start -a android.intent.action.VIEW -d "${FABRIC}"
#    exit 1
#}

echo "Will run ${ITERATIONS} iterations on ${PACKAGE} of the evil chaos monkey: "
echo "$ adb shell monkey -p ${PACKAGE} -v ${EVENTS}"

sleep 2
for i in $( seq 1 ${ITERATIONS} ) ; do
  adb shell monkey -p "${PACKAGE}" -v ${EVENTS}
done

