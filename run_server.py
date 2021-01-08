#!/usr/bin/env python3
# encoding=utf-8
import os
import sys
import subprocess

subprocess.run(
    [
        'java',
        'io.github.ichisadashioko.jtouchinputserver.Main',
    ],
    cwd='src',
    stdout=sys.stdout,
    stderr=sys.stderr,
)
