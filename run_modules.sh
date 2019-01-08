#!/usr/bin/env bash

# All modules must be compiled

echo "Running forming of stiffness matrix" && \
(cd ./Forming\ of\ stiffness\ matrix/ && exec ./plain_strain_task) && \
echo "Running Cholesky" && \
(cd ./Cholesky && java Cholesky_decomposition) && \
echo -e "\nDone"
