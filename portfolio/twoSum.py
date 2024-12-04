##############################
### Give this script any   ###
### array of numbers and   ###
### any target number      ###
### It will tell you if    ###
### any combination of the ###
### numbers in the array   ###
### add up to the target   ###
###       ~~~~~{@          ###
##############################

# Get rid of 
# > the automatic split()
# > data structures
def twoSum():
    delimiter = input("\n\nEnter number delimiter: ")
    num_array = input("Give me an array of numbers: ").split(delimiter)
    target = input("Now give me a target that any two of those numbers add up to: ")
    print("You entered the array:",num_array)
    print("And the target:",target)
    for each_num in num_array:
        counter = 0
        while counter < len(num_array):
            test = int(each_num) + int(num_array[counter])
            if test == int(target):
                print(int(each_num),"+",int(num_array[counter]),"=",target,"\n")
                exit()
            else:
                counter += 1
    print("No combinations of the numbers given match the named target\n")
twoSum()
