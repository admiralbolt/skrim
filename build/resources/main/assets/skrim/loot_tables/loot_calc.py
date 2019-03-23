import json
from pprint import pprint
import operator

loot_table_path = "gameplay/random_treasure.json"
luck = 20


with open(loot_table_path, "r") as rh:
    json_data = json.load(rh)

pool = json_data["pools"][0]
total_weight = 0
item_chance = {}

for item in pool["entries"]:
    item_weight = item["weight"] + luck * item["quality"]
    item_name = item["name"] + ("_" + item["functions"][0]["function"] if "functions" in item else "")
    item_chance[item_name] = item_weight
    total_weight += item_weight

for item_name, item_weight in item_chance.items():
    item_chance[item_name] = float(item_weight) / total_weight

pprint(sorted(item_chance.items(), key=operator.itemgetter(1), reverse=True))
