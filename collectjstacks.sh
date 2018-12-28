#!/bin/bash
#
# Collect JStacks for a given Java process until process dies or ^C is pressed.
# You can run this script even if the desired process is not running.
# It will start capturing stacks once the process is up (default max wait = 120 secs)
# @author rahulsh1

if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <procToken/pid> <interval to capture jstacks in seconds>" >&2
  echo "e.g. $0 myAppLauncher 1  <-- to capture jstack every 1 second for process myAppLauncher"
  echo "e.g. $0 77281 0.1        <-- to capture jstack every 100 millisecond for pid 77281"
  exit 1
fi

procToken=$1
interval=$2

# How often to check if process not up yet
checkInterval=1
# Max count before giving up. Total time = maxCount x checkInterval
maxCount=120
IS_APP_UP=no

# Font colors
GREEN='\033[01;32m'
RED='\033[01;31m'
GRAY='\033[01;37m'
YELLOW='\033[01;33m'
NONE='\033[00m'

SUFFIX=`date +"%m%d%Y_%H%M"`
jstackDir=jstack_${SUFFIX}
mkdir -p $jstackDir

function get_jstacks {
  check_process
  count=0
  printf "${YELLOW}Waiting for $maxCount seconds for process $procToken to be up\n${NONE}"
  while [ "$IS_APP_UP" == "no" ]
  do
    if [ "$count" -eq $maxCount ]; then
      printf "${RED}ERROR: Exceeded $maxCount, Aborting this run\n${NONE}"
      exit 1
    fi
    count=$(($count+1))
    sleep $checkInterval
    check_process
  done

  printf "${GREEN}Found process $procToken with PID: $PID\n${NONE}"
  echo "Collecting jstacks in dir: $jstackDir"
  while true
  do
    date=`date +%Y%m%d%H%M%S`
    jstack $PID > $jstackDir/$date.${PID}.jstack
    if [ $? -ne 0 ]; then
      # delete any partially captured file
      rm $jstackDir/$date.${PID}.jstack
      break
    fi
    echo "  $date.${PID}.jstack"
    sleep $interval
  done
  printf "${YELLOW}done capturing jstacks in dir: $jstackDir\n${NONE}"
}

function check_process {
  PRES=$(jps -l | grep $procToken)
  if [ $? -eq 0 ]; then
    PID=$(echo $PRES | awk ' { print $1 }' )
    IS_APP_UP="yes"
  else
    IS_APP_UP="no"
  fi
}

get_jstacks
