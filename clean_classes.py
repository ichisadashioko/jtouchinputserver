#!/usr/bin/env python3
# encoding=utf-8
import os

IGNORED_FILENAMES = [
    '.git',
]


def find_classes_files(infile: str, out_list: list):
    abs_path = os.path.abspath(infile)
    filename = os.path.basename(abs_path)

    if filename in IGNORED_FILENAMES:
        return

    if os.path.isfile(infile):
        ext = os.path.splitext(filename)[1]

        if ext == '.class':
            out_list.append(infile)
    elif os.path.isdir(infile):
        flist = os.listdir(infile)
        for cfn in flist:
            cfp = os.path.join(infile, cfn)
            find_classes_files(
                infile=cfp,
                out_list=out_list,
            )


if __name__ == "__main__":
    classes_flist = []
    find_classes_files(
        infile='.',
        out_list=classes_flist,
    )

    # print(classes_flist)
    for class_fpath in classes_flist:
        os.remove(class_fpath)
