#!/bin/bash

echo "generating"
g++ initialiser_gen.cpp -o initialiser_gen && ./initialiser_gen < gen_config > "Initialiser.java"
