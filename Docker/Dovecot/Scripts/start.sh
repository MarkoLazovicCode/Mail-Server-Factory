#!/bin/sh

echo "Starting Dovecot on `hostname`"

chmod -R +r /run/dovecot
chmod -R +w /run/dovecot

dovecot