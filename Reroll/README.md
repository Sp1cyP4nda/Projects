# Conversions
Hello, and welcome to the [Reroll](https://app.reroll.co) conversations section. Uploading data to Reroll is a great way to expand the options available in the app. Unfortunately, in order to do so, you have to manually type in what you want uploaded. This becomes an slog when you want to upload many options at the same time. Introducing: my conversion scripts. Visit [5e.tools](https://5e.tools/makecards.html) card maker to download options that you currently own the rights to (i.e. the books you own), then run those options through the appropriate converter to convert the .json files into the proper format that Reroll reads.

## Legal note:
I do not endorse downloading files that you do not own the rights to. Do this at your own risk.

## Usage
These scripts are written in Bash, meaning you must use a Linux terminal to run them. After downloading a file, use `chmod +x <filename>.sh` to give the script exectute rights then run as follows:
`./path/to/<script>.sh INPUT.json` where `INPUT.json` is the name of the file you downloaded from 5e.tools and wish to convert.
After the script finishes, check the integrity of the outputed .json file with a [.json checker](https://jsonchecker.com/).
