# ══════════════════════════════════════════════════
#  BLOOP Interpreter — Docker Image
# ══════════════════════════════════════════════════
#  Interactive mode:  docker run -it bloop-interpreter
#  Direct mode:       docker run --rm bloop-interpreter tests/program1.bloop
# ══════════════════════════════════════════════════

# Stage 1: Build — Compile the Java source code
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy source files
COPY src/ src/

# Compile all Java files into the 'out' directory
RUN javac -d out -sourcepath src src/com/bloop/Main.java

# Stage 2: Runtime — Lightweight image with JRE + shell tools
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install nano editor (Alpine mein by default nahi hota)
RUN apk add --no-cache nano

# Copy compiled classes from builder stage
COPY --from=builder /app/out/ out/

# Copy test programs (built into the image for easy demo)
COPY tests/ tests/

# Copy the smart entrypoint script
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Create a 'bloop' shortcut command so inside the container
# you can just type: bloop test.bloop
RUN echo '#!/bin/sh' > /usr/local/bin/bloop && \
    echo 'java -cp /app/out com.bloop.Main "$@"' >> /usr/local/bin/bloop && \
    chmod +x /usr/local/bin/bloop

# Smart entrypoint: interactive shell (no args) or direct run (with args)
ENTRYPOINT ["/app/entrypoint.sh"]
