#####################
### Input Pokémon ###
### Output Stats  ###
###    ~~~~~{@    ###
#####################

# Errors:
# > Crashes when input is not a pokemon (can still use console to input)
# >> Same for pokemon that aren't G1
# > Nidoran

# import json
import requests
import re
import os
import kivy
import g1Pokedex
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.app import App
from kivy.uix.label import Label
from kivy.uix.image import AsyncImage
from kivy.uix.textinput import TextInput

class PokedexGUI(App):
    def build(self):
        self.window = GridLayout()
        self.window.cols = 1
        self.window.size_hint = (0.6,0.8)
        self.window.pos_hint = {"center_x": 0.5, "center_y": 0.5}

        # image widget
        self.image = AsyncImage(source="pokemon\\pikachu.png",size_hint=(1,0.85))
        self.window.add_widget(self.image) # This will be where the Pokemon images go

        # label widget
        self.pokemon = Label(text="Which Pokémon do you want to know about?",font_size = 18, color='F6CF57')
        self.window.add_widget(self.pokemon)

        # text input widget
        self.user = TextInput(multiline=False,hint_text="Enter Pokémon name", padding_y = (10,10), size_hint = (1,0.5))
        self.Pkmn = ""
        self.pkmn = ""
        # self.user.bind(on_text_validate=self.callback)
        # self.user.bind(on_text_validate=self.create_pkmn_pic)
        # self.user.bind(on_text_validate=self.get_pkmn_stats)
        self.window.add_widget(self.user)

        # button widget
        self.button = Button(text="Search Database",color = '000000', size_hint = (0.25,0.35),bold = True, background_color = 'F6CF57', background_normal = "")
        self.button.bind(on_press=self.callback)

        # self.button.bind(on_press=self.create_pkmn_pic)
        # self.button.bind(on_press=self.get_pkmn_stats)
        self.window.add_widget(self.button)

        # output widget
        self.output = Label(font_size = 17, color='F6CF57', valign = 'middle', pos_hint = (1,1))
        self.restart = Button(valign = 'bottom',text="Next Pokémon Search",size_hint = (0.05,0.05), color = 'F6CF57',bold = True, background_color = '000000', background_normal = "")
        self.restart.bind(on_press=self.refresh)

        return self.window

    def callback(self, instance):
        self.Pkmn = (self.user.text).title()
        self.pkmn = (self.user.text).casefold()
        self.window.remove_widget(self.user)
        self.window.remove_widget(self.pokemon)
        self.window.remove_widget(self.button)
        self.set_stats = create_pokemon(self.pkmn)
        self.image.source = set_pkmn_pic(int(self.set_stats.get_id()))
        self.Pkmn = self.set_stats.get_name()
        # name = self.pkmn
        # if name == 'nidoran-f' or name == "nidoran-m" or name == 'nidoran':
        #     self.window2 = FloatLayout
        #     self.window2.size = {300,300}
        #     self.m_or_f = TextInput(hint_text="Male or Female")
        #     self.window2.add_widget(self.m_or_f)
        pkmn_id = "ID: " + str(self.set_stats.get_id())
        pkmn_types = self.set_stats.get_type()
        if len(pkmn_types) == 1:
            pkmn_types = "Type: " + pkmn_types[0]
        if len(pkmn_types) == 2:
            pkmn_types = "Types: " + pkmn_types[0] + ", " + pkmn_types[1]
        pkmn_ability = self.set_stats.get_ability()
        if len(pkmn_ability) == 1:
            pkmn_ability = "Ability: " + pkmn_ability[0]
        if len(pkmn_ability) == 2:
            pkmn_ability = "Abilities: " + pkmn_ability[0] + ", " + pkmn_ability[1]
        pkmn_hp = "HP: " + str(self.set_stats.get_baseStats('hp'))
        pkmn_atk = "Attack: " + str(self.set_stats.get_baseStats('atk'))
        pkmn_def = "Defense: " + str(self.set_stats.get_baseStats('def'))
        pkmn_spatk = "Special Attack: " + str(self.set_stats.get_baseStats('spatk'))
        pkmn_spdef = "Special Defense: " + str(self.set_stats.get_baseStats('spdef'))
        pkmn_spd = "Speed: " + str(self.set_stats.get_baseStats('spd'))
        pkmn_evol =  "Evolutions: " + self.set_stats.get_evolution()
        self.output.text = "Here's some information about " + self.Pkmn + ":\n" + pkmn_id + "\n" + pkmn_types + "\n" + pkmn_ability + "\nBase Stats:\n    " + pkmn_hp + "\n    " + pkmn_atk + "\n    " + pkmn_def + "\n    " + pkmn_spatk + "\n    " + pkmn_spdef + "\n    " + pkmn_spd + "\n" + pkmn_evol
        self.window.add_widget(self.output)
        self.window.add_widget(self.restart)
    
    def refresh(self, instance):
        self.window.clear_widgets()
        self.image = AsyncImage(source="pokemon\\pikachu.png")
        self.window.add_widget(self.image)
        self.pokemon = Label(text="Which Pokémon do you want to know about?",font_size = 18, color='F6CF57')
        self.window.add_widget(self.pokemon)
        self.user = TextInput(multiline=False,hint_text="Enter Pokémon name", padding_y = (20,20), size_hint = (1,0.5))
        self.user.bind(on_text_validate=self.callback)
        self.window.add_widget(self.user)
        self.button = Button(text="Search Database",color = '000000', size_hint = (1,0.5),bold = True, background_color = 'F6CF57', background_normal = "")
        self.window.add_widget(self.button)

def create_pokemon(pkmn):
    pkmn = pkmn.lower()
    set_stats = g1Pokedex.set_stats(pkmn)
    return set_stats

def set_pkmn_pic(pkmn_id):
    pkmn_id = int(pkmn_id)
    if len(str(pkmn_id)) == 1:
        pkmn_id = "00" + str(pkmn_id)
    if len(str(pkmn_id)) == 2:
        pkmn_id = "0" + str(pkmn_id)
    pkmn_image = "https://www.serebii.net/pokemon/art/" + str(pkmn_id) + ".png"
    return pkmn_image
    
if __name__ == "__main__":
    PokedexGUI().run()
