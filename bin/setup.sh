#!/bin/sh

mkdir -p $1/chanels/A00/inflight $1/chanels/A01/inflight $1/chanels/A10/inflight
mkdir -p $1/chanels/A11/inflight $1/chanels/L00I/inflight $1/chanels/L10/inflight
mkdir -p $1/chanels/L10U01/inflight $1/chanels/U00I/inflight $1/chanels/U01/inflight
mkdir -p $1/chanels/calculateL10/inflight $1/chanels/calculateU01/inflight $1/chanels/calculateA11/inflight
mkdir -p $1/chanels/A11calculated/inflight $1/chanels/terminate/inflight $1/chanels/generate/inflight
mkdir -p $1/chanels/aggregatedU01/inflight $1/chanels/aggregatedL10/inflight $1/chanels/L10U01A11/inflight

mkdir -p $1/matrix/LI $1/matrix/UI $1/matrix/U $1/matrix/L $1/matrix/A
