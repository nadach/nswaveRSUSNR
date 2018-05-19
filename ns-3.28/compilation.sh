#!/bin/bash

./waf clean
./waf configure --build-profile=optimized --enable-examples --enable-tests
./waf