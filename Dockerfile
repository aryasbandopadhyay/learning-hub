# syntax=docker/dockerfile:1
# =============================================================================================
# learning-hub — container image
# =============================================================================================
# The app is a Spring Boot server, but its DSA "online judge" shells out to a standalone Python
# program (learning-hub/judge/runner.py). So the runtime image needs BOTH a JRE and Python 3.
#
# Runtime directory layout (mirrors local dev, where the app runs from learning-hub/):
#   /app                     <- content root (hub.root="" => parent of the working dir)
#   /app/dsa, /app/internals, /app/spring-theory, /app/spring-crud-demo, /app/<lld dirs>, ...
#   /app/learning-hub        <- WORKDIR (so user.dir here; judge finds judge/ + manifests)
#   /app/learning-hub/app.jar
#   /app/learning-hub/judge/runner.py + genlib.py + manifests/
# =============================================================================================

# ---- Stage 1: build the Spring Boot fat jar with Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# Copy the POM first so dependency resolution is cached when only source changes.
COPY learning-hub/pom.xml pom.xml
RUN mvn -q -B dependency:go-offline
# Now the source, then package (skip tests for a fast, reproducible image build).
COPY learning-hub/src src
RUN mvn -q -B -DskipTests package

# ---- Stage 2: slim runtime with JRE + Python 3 for the judge ----
FROM eclipse-temurin:17-jre AS runtime

# Python 3 is required by the online judge (runner.py is invoked as a subprocess).
RUN apt-get update \
 && apt-get install -y --no-install-recommends python3 \
 && rm -rf /var/lib/apt/lists/*

# ----- Learning content (markdown + Python), one level above the app (= content root) -----
WORKDIR /app
COPY dsa/                  /app/dsa/
COPY internals/           /app/internals/
COPY spring-theory/       /app/spring-theory/
COPY system-design-hld/   /app/system-design-hld/
COPY spring-crud-demo/    /app/spring-crud-demo/
COPY parking-lot/         /app/parking-lot/
COPY amazon-locker/       /app/amazon-locker/
COPY bookmyshow/          /app/bookmyshow/
COPY movie-ticket-booking/ /app/movie-ticket-booking/
COPY splitwise/           /app/splitwise/
COPY vending-machine/     /app/vending-machine/
COPY elevator-system/     /app/elevator-system/
COPY atm-machine/         /app/atm-machine/
COPY car-rental/          /app/car-rental/
COPY hotel-management/    /app/hotel-management/
COPY airline-reservation/ /app/airline-reservation/
COPY library-management/  /app/library-management/
COPY chess/               /app/chess/
COPY snake-and-ladder/    /app/snake-and-ladder/
COPY tic-tac-toe/         /app/tic-tac-toe/
COPY logging-framework/   /app/logging-framework/
COPY notification-service/ /app/notification-service/
COPY cache/               /app/cache/
COPY rate-limiter/        /app/rate-limiter/
COPY job-scheduler/       /app/job-scheduler/
COPY find-command/        /app/find-command/

# ----- The judge engine + generated manifests (live under learning-hub/judge) -----
COPY learning-hub/judge/  /app/learning-hub/judge/

# ----- The built application jar -----
COPY --from=build /build/target/*.jar /app/learning-hub/app.jar

# Run from the app directory so:
#   - user.dir = /app/learning-hub  -> judge resolves judge/ + judge/manifests
#   - content root (blank => parent of user.dir) = /app  -> all content folders visible
WORKDIR /app/learning-hub

# Point the judge at the Linux Python executable (relaxed-binding of hub.judge.python-exe).
ENV HUB_JUDGE_PYTHON_EXE=python3

# Run as a non-root user for defense-in-depth: both the JVM and the Python judge subprocess
# it spawns execute unprivileged, so a sandbox escape cannot act as root inside the container.
RUN useradd --system --uid 10001 --create-home --home-dir /home/appuser appuser \
 && chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
