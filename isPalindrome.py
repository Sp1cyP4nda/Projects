##############################
### Input a word or string ###
###    Find out if it's    ###
###     a palindrome       ###
###        ~~~~~{@         ###
##############################

def isPalindrome():
    word = input("\n\nGive me a word, and I'll tell you\nif it's a palindrome: ") 
    word = word.casefold() # get's rid of cases 
    word = word.replace(" ","") # removes spaces 
    word_list = list((word)) # defines the word as a list
    letter_count = len(word_list) # finds the amount of letters

    # Reverses the input word
    each_letter = letter_count-1
    check = []
    while each_letter >= 0:
        check.append(word_list[each_letter])
        each_letter -= 1
    
    # Checks to see if they are equal
    if word_list==check:
        print(word,"is a palindrome")
    else:
        print(word,"is not a palindrome")
    print("\n")
isPalindrome()