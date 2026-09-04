#!/usr/bin/env bash
set -e

# Navigate to project directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -d "$SCRIPT_DIR/allocationsystem" ]; then
    cd "$SCRIPT_DIR/allocationsystem"
else
    cd "$SCRIPT_DIR"
fi

# Load SDKMAN if present and select/install Java 17
if [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk use java 17.0.12-tem 2>/dev/null || sdk install java 17.0.12-tem || true
fi

# Locate Java 17 in standard locations if still not set
if ! javac -version 2>&1 | grep -q " 17"; then
    for jvm in /usr/lib/jvm/*17* $HOME/.sdkman/candidates/java/17*; do
        if [ -d "$jvm" ] && [ -x "$jvm/bin/javac" ]; then
            export JAVA_HOME="$jvm"
            export PATH="$JAVA_HOME/bin:$PATH"
            break
        fi
    done
fi

# Free up port 8080 if held by background process
fuser -k 8080/tcp 2>/dev/null || true

# Set Supabase database password
export DB_PASSWORD="${DB_PASSWORD:-collegeroomallocationsystem}"

echo "=================================================="
echo " Starting College Room Allocation System"
echo " Java Version : $(javac -version 2>&1)"
echo " Database     : Supabase PostgreSQL"
echo " Port         : 8080"
echo "=================================================="

# Run Spring Boot
./mvnw clean spring-boot:run
