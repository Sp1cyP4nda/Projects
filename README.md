# MassFileMove
Move a large quantity of files from many directories in a source directory to a different directory.
Okay, first GitHub doodad. Woot woot!
Wicked simple program designed to automate unzipping and pulling files out of a ridiculous amount of sub-directories into one single target directory.

# Warning:
Do NOT put the target directory inside the source directory.
It WILL be deleted and you will lose all your data

# Second Warning:
Back up your data.
If you don't have a drive that's big enough, [wasabi ](https://wasabi.com/cloud-storage-pricing/) has a great rate for a lot of data.

## Okay, now onto the walk-through

### 'Is everything unzipped'
Defaults to "yes, don't unzip anything." If you want the script to search and unzip all zip files, type an `n` or `no` then hit enter.

### 'What is the source directory?'
Enter the directory from where you want to pull all of the files. Include the `/`

### 'Where do you want the files?'
Enter the location to where you want the script to put the files.
#### Important Note:
The script changes directories (cd's) into the source directory you enter when it runs, therefore you need to tell the script how to get to the target location from the source location. Entering `../<target>/` tells the script "the target location is up one directory."
Example: if the target and source locations share a parent directory, enter `../<target_dir>/` and if the target directory is in the grandparent directory of the source directory, enter `../../<target_dir>/`
If you have questions, send me a message.

### 'Do you want to remove all zip files'
###### Okay, back to zip files for a sec. `unzip` needed to be directed, which is why I needed to put it after the two questions asking where the files are coming from and where they're going.
Defaults to "no, keep all zip files." This option only appears if you use the script to unzip all files. Type a `y` or `yes` if you want the script to remove all zip files in the source directory. If you decide here to keep the zip files, you get the option later to remove them if you change your mind.
#### Note:
If you look closely, this command may give the error: `caution: not extracting; -d ignored`. This is due to me trying to be verbose so I can keep a log file in case something went wrong (Back up your shit!!) I can figure out what happened. For some reason running `unzip` verbosely refuses to actually extract the files that I tested. To fix this, I just added a second `unzip` command without verbosity. Both output everything to a log file.

### 'File types in source directory'
This scans the source directory for all of the file types that can be moved then asks which one to do first. If you opted earlier to not delete the zip files, they will appear here and you can decide whether to keep them or not.
#### Important Note: Only enter one file type at a time. This script wasn't made to do more than one at a time. I could probably figure out how to do that, but I am okay not spending more time on this and getting to other projects. 

### 'Final Question before running: Run the mv command with which mode?'
This last question asks how to move files over. Once you choose an option, it will run to completion unless you give the `kill` command: `ctrl+c`.
`-i` is the interactive mode. It will ask you before each instance of overwriting what you want to do.
`-f` is force mode. It will automatically overwrite files without prompting you what to do.
`-n` is no overwrite mode. Instead of overwriting files, it will just not move the file over and delete it instead.
#### Note:
The `mv` command (move) works by copying a file to a new location, then deleting the old file. Using `-n` will not move a file over if it would overwrite one, so instead, it just moves all the files it can, then deletes the ones it can't.

### "Shall I restart?"
Exactly what it says on the tin. Because you can only enter one file type at a time, the program has to run multiple times. Instead of exiting the program then going back into it, the program "remembers" the source and target locations. And since you already unzipped everything, you won't have to worry about doing that either. This will run the script from the "File types in source directory" question.

### And finally:
After you've moved out all the files you wish to move, type `n` or `no` to "Shall I restart?" and the script will prompt you asking if it should remove the source directory in it's entirety. 
#### Very Important: If you enter `y` or `yes` (it defaults to "no, keep the source directory intact") the script will recursively delete the entire directory and EVERYTHING in it.

### Odd bugs that I don't care to fix at this time:
- If you end up using the defaults at the time of prompting instead of typing in an option, the script will return errors that look like the following:
`<script name>: Line X: [: ==: unary operator expected` These are okay to see and will not mess anything up.

If you have any questions, feel free to send me a message. If it's an error code, please include a screenshot of the terminal.
