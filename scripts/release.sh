#!/usr/bin/env bash
set -euo pipefail

# Build for all supported MC versions and bundle release artifacts
# Usage: ./scripts/release.sh [version]

VERSION="${1:-$(date +%Y%m%d)}"
OUTDIR="release-$VERSION"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "==> Cleaning old builds"
./gradlew clean 2>/dev/null || true

echo "==> Building for 1.20.1"
./gradlew build -PmcVersion=1.20.1
mkdir -p "$OUTDIR/1.20.1"
cp build/libs/dogecoin-*+mc1.20.1.jar "$OUTDIR/1.20.1/"
cp build/libs/dogecoin-*+mc1.20.1-sources.jar "$OUTDIR/1.20.1/" 2>/dev/null || true

echo "==> Building for 1.21.1"
./gradlew build -PmcVersion=1.21.1
mkdir -p "$OUTDIR/1.21.1"
cp build/libs/dogecoin-*+mc1.21.1.jar "$OUTDIR/1.21.1/"
cp build/libs/dogecoin-*+mc1.21.1-sources.jar "$OUTDIR/1.21.1/" 2>/dev/null || true

echo "==> Building for 1.21.4"
./gradlew build -PmcVersion=1.21.4
mkdir -p "$OUTDIR/1.21.4"
cp build/libs/dogecoin-*+mc1.21.4.jar "$OUTDIR/1.21.4/"
cp build/libs/dogecoin-*+mc1.21.4-sources.jar "$OUTDIR/1.21.4/" 2>/dev/null || true

echo "==> Release artifacts in $OUTDIR/"
ls -lh "$OUTDIR"/*/
