import json

flower_names = [
  "poppy",
  "dandelion",
  "blue_orchid",
  "allium",
  "azure_bluet",
  "red_tulip",
  "orange_tulip",
  "white_tulip",
  "pink_tulip",
  "oxeye_daisy",
  "sunflower",
  "lilac",
  "rose_bush",
  "peony"
]

# Only exceptions
model_map = {
  "oxeye_daisy": "daisy",
  "blue_orchid": "orchid",
  "azure_bluet": "houstonia"
}

mapping = {
  "poppy": {
    "item": "minecraft:red_flower",
    "data": 0
  },
  "dandelion": {
    "item": "minecraft:yellow_flower",
    "data": 0
  },
  "blue_orchid": {
    "item": "minecraft:red_flower",
    "data": 1
  },
  "allium": {
    "item": "minecraft:red_flower",
    "data": 2
  },
  "azure_bluet": {
    "item": "minecraft:red_flower",
    "data": 3
  },
  "red_tulip": {
    "item": "minecraft:red_flower",
    "data": 4
  },
  "orange_tulip": {
    "item": "minecraft:red_flower",
    "data": 5
  },
  "white_tulip": {
    "item": "minecraft:red_flower",
    "data": 6
  },
  "pink_tulip": {
    "item": "minecraft:red_flower",
    "data": 7
  },
  "oxeye_daisy": {
    "item": "minecraft:red_flower",
    "data": 8
  },
  "sunflower": {
    "item": "minecraft:double_plant",
    "data": 0
  },
  "lilac": {
    "item": "minecraft:double_plant",
    "data": 1
  },
  "rose_bush": {
    "item": "minecraft:double_plant",
    "data": 4
  },
  "peony": {
    "item": "minecraft:double_plant",
    "data": 5
  }
}

blockstate_template = {
  "forge_marker": 1,
  "variants": {
    "normal": {
      "model": "minecraft:poppy"
    },
    "inventory": {
      "model": "minecraft:poppy"
    }
  }
}

glow_flower_recipe = {
  "type": "minecraft:crafting_shapeless",
  "group": "skrim:glow_flower",
  "ingredients": [
    {
      "item": "minecraft:red_flower",
      "data": 0
    },
    {
      "item": "minecraft:glowstone_dust"
    }
  ],
  "result": {
    "item": "skrim:glow_flower_poppy"
  }
}

enchanted_flower_recipe = {
  "type": "minecraft:crafting_shaped",
  "group": "skrim:enchanted_flower",
  "pattern": [
    "ABA",
    "CDC",
    "ABA"
  ],
  "key": {
    "A": {
      "item": "minecraft:diamond"
    },
    "B": {
      "item": "minecraft:obsidian"
    },
    "C": {
      "item": "minecraft:glass"
    },
    "D": {
      "item": "minecraft:red_flower",
      "data": 0
    }
  },
  "result": {
    "item": "skrim:enchanted_flower_poppy"
  }
}


for flower in flower_names:
  blockstate_template["variants"]["inventory"]["model"] = f"minecraft:{model_map.get(flower, flower)}"
  blockstate_template["variants"]["normal"]["model"] = f"minecraft:{model_map.get(flower, flower)}"
  with open(f"blockstates/glow_flower_{flower}.json", "w") as wh:
    json.dump(blockstate_template, wh, indent="  ")
  with open(f"blockstates/enchanted_flower_{flower}.json", "w") as wh:
    json.dump(blockstate_template, wh, indent="  ")

  glow_flower_recipe["ingredients"][0]["item"] = mapping[flower]["item"]
  glow_flower_recipe["ingredients"][0]["data"] = mapping[flower]["data"]
  glow_flower_recipe["result"]["item"] = f"skrim:glow_flower_{flower}"
  with open(f"recipes/glow_flower_{flower}.json", "w") as wh:
    json.dump(glow_flower_recipe, wh, indent="  ")

  enchanted_flower_recipe["key"]["D"]["item"] = mapping[flower]["item"]
  enchanted_flower_recipe["key"]["D"]["data"] = mapping[flower]["data"]
  enchanted_flower_recipe["result"]["item"] = f"skrim:enchanted_flower_{flower}"
  with open(f"recipes/enchanted_flower_{flower}.json", "w") as wh:
    json.dump(enchanted_flower_recipe, wh, indent="  ")

  enchanted_flower_recipe["key"]["D"]["item"] = f"skrim:glow_flower_{flower}"
  del enchanted_flower_recipe["key"]["D"]["data"]
  with open(f"recipes/enchanted_flower_{flower}_from_glow.json", "w") as wh:
    json.dump(enchanted_flower_recipe, wh, indent="  ")


glow_flowers = [f"glow_flower_{flower}" for flower in flower_names]
enchanted_flowers = [f"enchanted_flower_{flower}" for flower in flower_names]

for flower in glow_flowers:
  print(f"public static GlowFlower {flower.upper()} = new GlowFlower(\"{flower}\");")

for flower in enchanted_flowers:
  print(f"public static EnchantedFlower {flower.upper()} = new EnchantedFlower(\"{flower}\");")

print("""
public static final Block[] GLOW_FLOWERS = {
  %s
};
""" % (",\n".join(glow_flowers).upper()))

print("""
public static final Block[] ENCHANTED_FLOWERS = {
  %s
};
""" % (",\n".join(enchanted_flowers).upper()))

for flower in glow_flowers + enchanted_flowers:
  s = flower.split("_")
  name = s[0].title() + " " + " ".join([q.title() for q in s[2:]])
  print(f"tile.{flower}.name={name}")
