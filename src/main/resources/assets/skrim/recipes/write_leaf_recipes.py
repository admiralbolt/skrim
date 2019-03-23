import json

template = {
  "_comment": [
    "Can only be crafted with level 75 woodcutting."
  ],
  "group": "",
  "type": "minecraft:crafting_shaped",
  "pattern": [],
  "key": {
    "L": {
      "item": "minecraft:leaves",
      "data": ""
    }
  },
  "result": {
    "item": ""
  }
}

leafies = [
  (0, "oak", "minecraft:leaves"),
  (1, "spruce", "minecraft:leaves"),
  (2, "birch", "minecraft:leaves"),
  (3, "jungle", "minecraft:leaves"),
  (0, "acacia", "minecraft:leaves2"),
  (1, "dark_oak", "minecraft:leaves2")
]

helmet_pattern = [
  "LLL",
  "L L"
]

chest_pattern = [
  "L L",
  "LLL",
  "LLL"
]

leg_pattern = [
  "LLL",
  "L L",
  "L L"
]

boot_pattern = [
  "L L",
  "L L"
]

armors = [
  ("helmet", helmet_pattern),
  ("armor", chest_pattern),
  ("pants", leg_pattern),
  ("boots", boot_pattern)
]


for data_value, leaf_name, leaf_type in leafies:
  for armor_name, pattern in armors:
    file_name = f"{leaf_name}_leaf_{armor_name}.json"
    data = template.copy()
    data["pattern"] = pattern
    data["key"]["L"] = {
      "item": leaf_type,
      "data": data_value
    }
    data["group"] = f"skrim:{leaf_name}_leaf_armor"
    data["result"]["item"] = f"skrim:{leaf_name}_leaf_{armor_name}"
    with open(file_name, "w") as wh:
      json.dump(data, wh, indent="  ")
