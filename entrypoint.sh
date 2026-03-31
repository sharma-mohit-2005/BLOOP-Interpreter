#!/bin/sh
# ══════════════════════════════════════════════════
#  BLOOP Interpreter — Smart Entrypoint
#  Supports both interactive and direct execution modes
# ══════════════════════════════════════════════════

if [ $# -eq 0 ]; then
    # No arguments → Interactive mode
    echo "╔══════════════════════════════════════════════════╗"
    echo "║     🟢  BLOOP Interpreter — Interactive Mode     ║"
    echo "╠══════════════════════════════════════════════════╣"
    echo "║                                                  ║"
    echo "║  Create a file:   vi test.bloop                  ║"
    echo "║  Run a program:   bloop test.bloop               ║"
    echo "║  Run samples:     bloop tests/program1.bloop     ║"
    echo "║  Exit:            exit                           ║"
    echo "║                                                  ║"
    echo "║  Sample BLOOP code:                              ║"
    echo "║    put 10 into x                                 ║"
    echo "║    put 3 into y                                  ║"
    echo "║    put x + y * 2 into result                     ║"
    echo "║    print result                                  ║"
    echo "║                                                  ║"
    echo "╚══════════════════════════════════════════════════╝"
    echo ""
    exec /bin/sh
else
    # Arguments given → Direct execution mode
    java -cp /app/out com.bloop.Main "$@"
fi
