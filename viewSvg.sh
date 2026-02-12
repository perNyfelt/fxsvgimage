#!/usr/bin/env bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
groovy -cp "$SCRIPT_DIR/target/classes" "$SCRIPT_DIR/viewSvg.groovy" "$@"