#########################
### Who wants to play ###
###    tic tac toe    ###
###     with me??     ###
#########################
#
import re
import random

def playerSetup():
    players = input("\nWho's playing? ")
    player = re.sub(r" and |and|,|, | ",",",players)
    player = player.split(",")
    first = random.randint(0,1)
    if first == 0:
        player1 = player[0].capitalize()
        player2 = player[1].capitalize()
    else:
        player1 = player[1].capitalize()
        player2 = player[0].capitalize()
    p1piece=player1[0]
    p2piece=player2[0]
    print()
    print(player1,"is first")
    players = dict()
    players[player1] = p1piece
    players[player2] = p2piece
    return players

def startingBoard():
    gameMatrix=[1,2,3,4,5,6,7,8,9]
    print("\033[4m",gameMatrix[0],"|",gameMatrix[1],"|",gameMatrix[2],"\033[0m")
    print("\033[4m",gameMatrix[3],"|",gameMatrix[4],"|",gameMatrix[5],"\033[0m")
    print("\033[0m",gameMatrix[6],"|",gameMatrix[7],"|",gameMatrix[8],"\033[0m")
    return gameMatrix

# 123 456 789 147 258 369 159 357
#  6  120 504  28  80 162  45 105
# def winCheck(playerCheck,gameMatrix):
    # Looks like these multiplicative numbers are not unique to the wincons T..T


def tictactoe(gameMatrix):
    move = input("Choose a position: ")
    while True:
        try:
            int(move)
            if 0 > int(move) or int(move) > 9:
                print("Position must be between 1 and 9. Try again.")
                raise TypeError
            if str(gameMatrix[int(move) - 1]).isalpha():
                print("Position taken. Try again")
                raise TypeError
        except ValueError:
            print("Entry must be a number. Try again.")
            move = input("Choose a position: ")
        except TypeError:
            move = input("Choose a position: ")
        else:
            break
    return move
    
def boardState(move,players,gameMatrix):
    gameMatrix=[1,2,3,4,5,6,7,8,9]
    move = tictactoe(gameMatrix)
    player1 = list(players.keys())[0]
    p1piece = list(players.values())[0]
    player2 = list(players.keys())[1]
    p2piece = list(players.values())[1]
    pXpiece = p1piece
    turnCounter = 1
    while turnCounter <= 9:
        print("Turn number: ",turnCounter)
        gameMatrix[int(move)-1]=pXpiece
        print("\033[4m",gameMatrix[0],"|",gameMatrix[1],"|",gameMatrix[2],"\033[0m")
        print("\033[4m",gameMatrix[3],"|",gameMatrix[4],"|",gameMatrix[5],"\033[0m")
        print("\033[0m",gameMatrix[6],"|",gameMatrix[7],"|",gameMatrix[8],"\033[0m")
        # winCheck(playerCheck,gameMatrix)
        if turnCounter == 9:
            exit()
        if turnCounter % 2 != 0:
            print(player2,"\'s turn")
            move = tictactoe(gameMatrix)
            pXpiece = p2piece
            turnCounter += 1
        else:
            print(player1,"\'s turn")
            move = tictactoe(gameMatrix)
            pXpiece = p1piece
            turnCounter += 1

# Playing the game
players = playerSetup()
gameMatrix = startingBoard()
# move = tictactoe(gameMatrix)
move = 0
boardState(move,players,gameMatrix)