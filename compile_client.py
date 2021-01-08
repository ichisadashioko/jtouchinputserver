#!/usr/bin/env python3
# encoding=utf-8
import os
import sys
import subprocess

subprocess.run(
    [
        'javac',
        'io/github/ichisadashioko/jtouchtestclient/Main.java',
    ],
    cwd='src',
    stdout=sys.stdout,
    stderr=sys.stderr,
)
