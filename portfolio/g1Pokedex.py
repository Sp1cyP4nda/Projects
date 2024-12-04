#####################
### Input Pokémon ###
### Output Stats  ###
###    ~~~~~{@    ###
#####################

# import json
import requests
import re

# errors: 
# > only one ability (gastly/haunter)

class Pokemon:
    def __init__(self,name):
        self.name = name
        self.id = 0
        self.type = []
        self.ability = []
        self.baseStats = []
        self.evolution = ""
    
    def set_name(self, rawStats):
        name = rawStats["name"].title()
        self.name = name

    def set_id(self, rawStats: int) -> None:
        id = rawStats["id"]
        self.id = id

    def set_type(self,rawStats):
        # According to Uncle Google, there are no Gen1 Pokemon with more than two types
        # count = [*range(rawStats["types"][0]["slot"],rawStats["types"][-1]["slot"])]
        count = [1,2]
        for each_type in count:
            result = rawStats['types'][each_type-2]['type']['name']
            if result not in self.type: # only add unique entries
                self.type.append(result)
        return self.type
    
    def set_ability(self,rawStats):
        # According to Uncle Google, there are no Gen1 Pokemon with more than two abilities
        # count = [*range(rawStats["abilities"][0]["slot"],rawStats["abilities"][-1]["slot"])]
        count = [1,2]
        for each_ability in count:
            result = rawStats['abilities'][each_ability-2]['ability']['name']
            if result not in self.type: # only add unique entries
                self.ability.append(result)
        return self.ability
    
    def set_baseStats(self,rawStats):
        count = [*range(0,6)]
        for each_stat in count:
            result = rawStats['stats'][each_stat]['base_stat']
            self.baseStats.append(result)
        self.hp = self.baseStats[0]
        self.atk = self.baseStats[1]
        self.defense = self.baseStats[2]
        self.sp_atk = self.baseStats[3]
        self.sp_defense = self.baseStats[4]
        self.speed = self.baseStats[5]
        return self.baseStats

    def set_evolution(self,rawStats):
        chain_count = [*range(1,21)] # there are 20 evolution chains
        chain_start = rawStats['name'] # this gets the pokemon name
        if isEevee(chain_start):
            self.evolution = "Vaporeon (water stone), Jolteon (thunder stone), and Flareon (fire stone)"
            return self.evolution
        for each_evolution in chain_count: # for each evolution chain, do the following
            chain_number = str(each_evolution) # only strings can be concatenated
            evolution_chain = requests.get('https://pokeapi.co/api/v2/evolution-chain/' + chain_number) # checking each chain
            raw_evolution = evolution_chain.json() # have to parse it as a json
            if re.search(chain_start,str(raw_evolution)): # re.search(pattern_to_find,txt_to_search) txt_to_search can only be a string
                second_evolution = (raw_evolution['chain']['evolves_to'][0]['species']['name']).title()
                self.evolution = second_evolution
                try:
                    third_evolution = (raw_evolution['chain']['evolves_to'][0]['evolves_to'][0]['species']['name']).title()
                except:
                    third_evolution = ''
                if chain_start == second_evolution.casefold():
                    self.evolution = third_evolution
                    return self.evolution
                if chain_start == third_evolution.casefold():
                    self.evolution = "This Pokémon is the final evolution."
                    return self.evolution
                return self.evolution
        self.evolution = "Does not evolve"

    def get_name(self) -> str:
        return self.name

    def get_id(self):
        return self.id
    
    def get_type(self):
        return self.type
    
    def get_ability(self):
        return self.ability
    
    def get_baseStats(self,selection):
        selection = selection.casefold()
        isSp_Atk = ("^(?=.*sp|.*special)(?=.*atk|.*attack)")
        isSp_Def = ("^(?=.*sp|.*special)(?=.*def|.*defense)")
        if selection == "hp":
            self.hp = self.baseStats[0]
            return self.hp
        if selection == 'atk' or selection == 'attack':
            self.atk = self.baseStats[1]
            return self.atk
        if selection == 'def' or selection == 'defense':
            self.defense = self.baseStats[2]
            return self.defense
        if re.search(isSp_Atk,selection):
            self.sp_atk = self.baseStats[3]
            return self.sp_atk
        if re.search(isSp_Def,selection):
            self.sp_defense = self.baseStats[4]
            return self.sp_defense
        if selection == 'spd' or selection == 'speed':
            self.speed = self.baseStats[5]
            return self.speed
        return self.baseStats
    
    def get_evolution(self):
        return self.evolution

def isNidoran(rawStats):
    if isinstance(rawStats,dict):
        name = (rawStats['name']).casefold()
        id = rawStats['id']
        if name == 'nidoran-f' or name == "nidoran-m" or name == 'nidoran' or id == 29 or id == 32:
            return True
    if isinstance(rawStats,int):
        id = rawStats
        if id == 29 or id == 32:
            return True
    if isinstance(rawStats,str):
        name = rawStats
        if name == 'nidoran-f' or name == "nidoran-m" or name == 'nidoran':
            return True
    return False

def isEevee(rawStats):
    if isinstance(rawStats,dict):
        name = (rawStats['name']).casefold()
        id = rawStats['id']
        if name == "eevee" or id == 133:
            return True
    if isinstance(rawStats,int):
        id = rawStats
        if id == 133:
            return True
    if isinstance(rawStats,str):
        name = rawStats
        if name == "eevee":
            return True
    return False

def genCheck(rawStats):
    id = rawStats["id"]
    if id > 151:
        print("Invalid Pokémon. ")
        return False
    if id == 29 or id == 32:
        return True
    return True

def isError(pokeName):
    pokeName = pokeName.casefold()
    pokemon = requests.get('https://pokeapi.co/api/v2/pokemon/' + pokeName)
    return pokemon.status_code

def connect(pokeName):
    pokeName = pokeName.casefold()
    pokemon = requests.get('https://pokeapi.co/api/v2/pokemon/' + pokeName)
    rawStats = pokemon.json()
    return rawStats

def set_stats(pkmnName):
    # print()
    pkmn = Pokemon(pkmnName)
    raw_pkmn = connect(pkmn.get_name())
    # if isNidoran(raw_pkmn):
    #     name = raw_pkmn['name']
    # while genCheck(raw_pkmn) is False:
    #     pkmn = input("Please stick to first Gen Pokémon: ")
    #     pkmn = Pokemon(pkmn)
    #     raw_pkmn = connect(pkmn.get_name())
    pkmn.set_name(raw_pkmn)
    pkmn.set_id(raw_pkmn)
    pkmn.set_type(raw_pkmn)
    pkmn.set_ability(raw_pkmn)
    pkmn.set_baseStats(raw_pkmn)
    pkmn.set_evolution(raw_pkmn)
    return pkmn

def display_stats(pkmn):
    print()
    print("Name:",pkmn.get_name())
    print("ID:",pkmn.get_id())
    types = pkmn.get_type()
    if len(types) == 1:
        print("Type:",types[0])
    if len(types) == 2:
        print("Types:",types[0] + ",",types[1])
    ability = pkmn.get_ability()
    if len(ability) == 1:
        print("Ability:",ability[0])
    if len(ability) == 2:
        print("Abilities:",ability[0] + ",",ability[1])
    print("Base Stats:")
    print("    HP:",pkmn.get_baseStats('hp'))
    print("    Attak:",pkmn.get_baseStats('atk'))
    print("    Defense:",pkmn.get_baseStats('def'))
    print("    Special Attack:",pkmn.get_baseStats('spatk'))
    print("    Special Defense:",pkmn.get_baseStats('spdef'))
    print("    Speed:",pkmn.get_baseStats('spd'))
    print("Evolutions:",pkmn.get_evolution())
    print()
