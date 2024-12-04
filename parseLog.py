#############################
###   Input a log file    ###
###    Count how many     ###
###     total errors      ###
###    And which entry    ###
###  had the most errors  ###
### Output it in a nicely ###
### human-readable format ###
###       ~~~~~~{@        ###
#############################

# import our Lords and Saviors
import json # json, the All-Knowing
import re # Regex, the All-Powerful

# import and format the log file
log = open(input("Where is the file?\n"),"r") # prompts input to the json file, then loads it as a text file
data = json.loads(log.read()) # loads the data into the variable "data"
highest_error = {} # clears any instance of the "highest_error" dictionary
error_count = [item for item in data if "ERROR:" in item] # and finally, counts the amount of errors in the file

# Go forth, Lord Regex, the Reformater
# Note: now that we have the number of errors
# and have removed all entries that are not errors,
# it's okay to remove the "error" attribute.
error_dict = [re.sub(".*_",'container_',i) for i in error_count] # Gets rid of everything before "container_"
error_dict = [re.sub(" .*",'',i) for i in error_dict] # Gets rid of everything after the container name
highest_error = dict.fromkeys(error_dict,0) # create a dictionary, "highest_error" to count the occurrences of each container name

# Now count the occurrences of each container name
for each_occurrence in error_dict:
    highest_error[each_occurrence] += 1

# For the finale, put everything together in a nicely human-readable format
errors = {
    "total_errors":len(error_count),
    "container_with_most_errors":max(highest_error, key=highest_error.get),
    }
print(errors)