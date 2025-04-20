#!/usr/bin/env bash

# collect_java.sh
# Usage: ./collect_java.sh [source_dir] [output_file]
# Defaults: source_dir="."   output_file="all_java_code.txt"

set -euo pipefail

# Parameters
SRC_DIR="${1:-.}"
OUT_FILE="${2:-all_java_code.txt}"

# Start with an empty output file
: > "$OUT_FILE"

# Find all .java files and concatenate them
find "$SRC_DIR" -type f -name '*.java' -print0 \
| while IFS= read -r -d '' file; do
    # Write a separator/header
    printf '===== %s =====\n' "$file" >> "$OUT_FILE"
    # Append the file’s contents
    cat "$file" >> "$OUT_FILE"
    # Blank line between files
    printf '\n\n' >> "$OUT_FILE"
done

echo "All Java files under '$SRC_DIR' have been written to '$OUT_FILE'."
