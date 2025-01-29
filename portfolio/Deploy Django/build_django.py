######################
###  Build django  ###
### website before ###
###   deploying    ###
######################

import os
import sys
import time
from pip._internal.operations import freeze
import zipfile
os.chdir(sys.path[0])

def mk_archive(source, archive_name):
    if os.path.isdir(source):
        with zipfile.ZipFile(archive_name, 'a', zipfile.ZIP_DEFLATED) as zip:
            for root, dirs, files in os.walk(source):
                for file in files:
                    file_path = os.path.join(root, file)
                    zip.write(file_path, os.path.relpath(source + "\\" + file_path, source))
    if os.path.isfile(source):
        with zipfile.ZipFile(archive_name, 'a') as zip:
            zip.write(source)

if __name__ == "__main__":
    os.system('python ./manage.py collectstatic --noinput')
    sources_to_archive = [
        "portfolio",
        "productionfiles",
        "projects",
        "db.sqlite3",
        "staticfiles",
        "manage.py",
        "requirements.txt",
        "000-default.conf",
    ]
    output_archive = "portfolio.zip"
    if os.path.exists(output_archive):
        os.remove(output_archive)
    for each_src in sources_to_archive:
        mk_archive(each_src, output_archive)