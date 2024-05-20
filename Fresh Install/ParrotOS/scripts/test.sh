#!/bin/bash
while IFS= read -r repo; do
  git -C ~/Tools/ clone $repo.git
done < "$(dirname "$0")"/github.tools
