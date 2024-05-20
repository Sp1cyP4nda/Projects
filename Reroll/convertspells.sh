#!/bin/bash

infile=$1 # name of input file to be converted

# full line removals
sed -i '/"rule",/d' $infile
sed -i '/"count":/d' $infile
sed -i '/"color":/d' $infile
sed -i '/tags/,/]/d' $infile
sed -i '/"icon":/d' $infile
sed -i '/"icon_back/d' $infile
sed -i '/"contents":/d' $infile
sed -i '/]/d' $infile 																			# delete trailing ']'

# replacements
sed -i 's/"title"/"name"/g' $infile
sed -i 's/\t"property | Casting Time | /"casting_time": "/g' $infile
sed -i 's/\t"property | Range | /"range": "/g' $infile
sed -i 's/\t"property | Components | /"components": "/g' $infile
sed -i 's/\t"property | Duration | /"duration": "/g' $infile
sed -i 's/<[^>]*>//g' $infile																	# removes html <>'s

# Edit spell type, level, school
# Two patterns to change:
#   ["subtitle | <School> cantrip"]
#     -> ["type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "<school>"
sed -i 's/\t"subtitle | \([^"]*\) cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "\L\1",/' $infile
#   ["subtitle | 1st/2nd/3rd/xth-level <school>",] 
#     -> ["type": "spell",\n\t\t"level_number": x,\n\t\t"school": "<school>"
sed -i 's/\t"subtitle | \([0-9]*\)\([a-z]*\)-level \([^"]*\)",/"type": "spell",\n\t\t"level_number": \1,\n\t\t"school": "\u\3",/' $infile

# Don't use the following, it's just for reference.
#sed -i 's/\t"subtitle | Cantrip /"level_number": 0,/g' $infile
#sed -i 's/\t"subtitle | 1st-level /"type": "spell",\n\t\t"level_number": 1,\n/g' $infile
#sed -i 's/\t"subtitle | 2nd-level /"type": "spell",\n\t\t"level_number": 2,\n/g' $infile
#sed -i 's/\t"subtitle | 3rd-level /"type": "spell",\n\t\t"level_number": 3,\n/g' $infile
#sed -i 's/\t"subtitle | 4th-level /"type": "spell",\n\t\t"level_number": 4,\n/g' $infile
#sed -i 's/\t"subtitle | 5th-level /"type": "spell",\n\t\t"level_number": 5,\n/g' $infile
#sed -i 's/\t"subtitle | 6th-level /"type": "spell",\n\t\t"level_number": 6,\n/g' $infile
#sed -i 's/\t"subtitle | 7th-level /"type": "spell",\n\t\t"level_number": 7,\n/g' $infile
#sed -i 's/\t"subtitle | 8th-level /"type": "spell",\n\t\t"level_number": 8,\n/g' $infile
#sed -i 's/\t"subtitle | 9th-level /"type": "spell",\n\t\t"level_number": 9,\n/g' $infile
#sed -i 's/\t"subtitle | Abjuration cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Abjuration",/g' $infile
#sed -i 's/\t"subtitle | Conjuration cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Conjuration",/g' $infile
#sed -i 's/\t"subtitle | Divination cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Divination",/g' $infile
#sed -i 's/\t"subtitle | Enchantment cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Enchantment",/g' $infile
#sed -i 's/\t"subtitle | Evocation cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Evocation",/g' $infile
#sed -i 's/\t"subtitle | Illusion cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Illusion",/g' $infile
#sed -i 's/\t"subtitle | Necromancy cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Necromancy",/g' $infile
#sed -i 's/\t"subtitle | Transmutation cantrip",/"type": "cantrip",\n\t\t"level_number": 0,\n\t\t"school": "Transmutation",/g' $infile
#sed -i 's/abjuration/\t\t"school": "Abjuration/g' $infile
#sed -i 's/conjuration/\t\t"school": "Conjuration/g' $infile
#sed -i 's/divination/\t\t"school": "Divination/g' $infile
#sed -i 's/enchantment/\t\t"school": "Enchantment/g' $infile
#sed -i 's/evocation/\t\t"school": "Evocation/g' $infile
#sed -i 's/illusion/\t\t"school": "Illusion/g' $infile
#sed -i 's/necromancy/\t\t"school": "Necromancy/g' $infile
#sed -i 's/transmutation/\t\t"school": "Transmutation/g' $infile

# Final touches
sed -i 's/\t"section | At higher levels",/"higher_level": "/g' $infile							# changes 'section | At Higher Levels' to '"higher_level":
sed -zi 's/\"\,\n\t\t\t\"bullet \|//g' $infile													# reformats bullets
sed -zi 's/\.\"\,\n\t\t\t\"description |/\./g' $infile											# reformats 'description |'
sed -i 's/\t\t\t"text | /"desc": "/g' $infile													# changes 'text |' to '"desc":'
sed -i 's/\t\t\t"section | /"desc": "/g' $infile												# changes 'text |' to '"desc":'
sed -i '/"duration": "[^"]*",/a\\t\t"placeholder": "' $infile									# appends 'placeholder' after 'duration'
sed -zi 's/\": \"\n\"/\": \"/g' $infile															# changes '": "\n' to '": "'
sed -i 's/\"higher_level\": \"desc\": /\"higher_level\": /g' $infile							# changes 'higher_level\t\tdesc' to 'higher_level'
sed -zi 's/\.\"\,\n\"desc\": \"/\. /g' $infile													# adds necessary .", syntax
sed -i 's/\"desc/\t\t\"desc/g' $infile															# adds two tabs before 'desc'
sed -zi 's/\"\,\n\t\t\"desc\"\: \"/. /g' $infile												# deletes excess '"desc"'

# Clean up
sed -i '/"desc": "[^"]*"/a\\t\t"higher_level": ""' $infile										# appends blank '"higher_level": ""'
sed -zi 's/\"higher_level\"\: \"\"\n\t\t\"higher_level\"\: \"/\"higher_level\"\: \"/g' $infile	# deletes excess '"higher_level": ""'
sed -i 's/placeholder": \t\t"desc/desc/g' $infile												# replaces '"placeholder"'
sed -i 's/"school": "\([^"]*\) \([^"]*\)",/"school": "\1",/g' $infile							# deletes parenthesis in '"school":'

# Delete hanging spaces
sed -i 's/", /",/g' $infile
sed -i 's/"" /""/g' $infile
sed -i 's/." /."/g' $infile
sed -zi 's/\.\"\n\t\t\"/\.\"\,\n\t\t\"/g' $infile												# adds comma to end of lines
echo "]" >> $infile																				# adds the necessary ']' at EoF
