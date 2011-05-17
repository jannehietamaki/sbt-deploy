#!/bin/sh

exec /usr/bin/java $@ 1>$LOG_DIR/$NAME-stdout.log 2>$LOG_DIR/$NAME-stderr.log
