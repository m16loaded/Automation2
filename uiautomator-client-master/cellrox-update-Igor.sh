#!/bin/bash -x
#
# cellrox-update.sh <local file> - push local file to device and install
#
# example for push to non default dir:
# DLOAD_DIR=/sdcard cellrox-update.sh ota.zip
#

UPDSRC="$1"

ADB=${ADB:-adb}
DLOAD_DIR=${DLOAD_DIR:-/data/media/ota}

function error()
{
	echo `basename $0` ": error: $1"
	exit 1
}

# test sadb/adb mixup
$ADB shell id | grep -v "not found" || error "connecting to device (ADB=$ADB)"
# test root privleges on device
$ADB shell id | grep "root" || ($ADB root && sleep 3)
$ADB shell id | grep "root" || error "insufficient privileges on device"

# mount /data and /cache just in case..
$ADB shell mount /cache
$ADB shell mount /data

# test dest dir on device
$ADB shell "mkdir -p $DLOAD_DIR"

function usage()
{
	echo "Usage: $0 <update file>"
	exit 1
}

[ -n "$UPDSRC" ] || usage

if [ -f "$UPDSRC" ]; then
	local_md5sum=`md5sum "$UPDSRC"|(read s f;echo $s)`
	# make room for new image
	$ADB shell "rm $DLOAD_DIR/*"
	UPDTRG=$DLOAD_DIR/`basename "$UPDSRC"`
	$ADB push "$UPDSRC" "$UPDTRG" || \
		error "pushing $UPDSRC failed"
	remote_md5sum=`$ADB shell "md5sum $UPDTRG"|(read s f;echo $s)`
	[ "$local_md5sum" = "$remote_md5sum" ] || \
		error "verifying $UPDSRC failed"
else
	error "file not found $UPDSRC"
fi

$ADB reboot recovery || error "cannot reboot to recovery"
# 'adb wait-for-device' doesn't work with recovery, so poll
while true; do
	$ADB shell id && break;
	sleep 5
done
$ADB shell "export DISABLE_SIGNATURE_CHECKING=1; recovery --update_package=$UPDTRG" || \
	error "error installing $UPDTRG"
