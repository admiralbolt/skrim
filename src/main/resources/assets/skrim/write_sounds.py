import json

"""
sounds.json ->
{
  "dogsong": {
    "category": "record",
    "sounds": [{
      "name": "skrim:records/dogsong",
      "stream": true
    }]
  }
}

model.json ->
{
  "parent": "item/generated",
  "textures": {
    "layer0": "skrim:items/dogsong"
  }
}
"""

sounds = [
    "aruarian_dance",
    "bubberducky",
    "cassandra",
    "color",
    "dogsong",
    "gdawg",
    "heya",
    "money",
    "north",
    "number10",
    "samurai",
    "truck"
]

song_info = {
    "aruarian_dance": {
        "artist": "Nujabes",
        "name": "Aruarian Dance"
    },
    "bubberducky": {
        "artist": "Dunkey",
        "name": "Bubberducky 3"
    },
    "cassandra": {
        "artist": "Dave Brubeck",
        "name": "Cassandra"
    },
    "color": {
        "artist": "Death Cab for Cutie",
        "name": "A Lack of Color"
    },
    "dogsong": {
        "artist": "Toby Fox",
        "name": "Dogsong"
    },
    "gdawg": {
        "artist": "Avi Knecht",
        "name": "G-Dawg is Hawt"
    },
    "heya": {
        "artist": "He-Man",
        "name": "HEYYEYAAEYAAAEYAEYAA"
    },
    "money": {
        "artist": "Toby Fox / Hail Mary Mallon",
        "name": "Temmie Get Money for College"
    },
    "north": {
        "artist": "Phoenix",
        "name": "North"
    },
    "number10": {
        "artist": "Moon Hooch",
        "name": "Number 10"
    },
    "samurai": {
        "artist": "Nujabes",
        "name": "1st. Samurai"
    },
    "truck": {
        "artist": "The Octopus Project",
        "name": "Truck"
    }
}

sound_json_dict = {}
en_us = []
java = []
java_init = []

for sound in sounds:
    sound_json_dict[sound] = {
        "category": "record",
        "sounds": [{
          "name": "skrim:records/%s" % (sound,),
          "stream": True
        }]
    }
    model_dict = {
        "parent": "item/generated",
        "textures": {
            "layer0": "skrim:items/%s" % (sound,)
        }
    }
    model_file_name = "item.%s.json" % (sound,)
    with open("models/item/item.%s.json" % (sound,), "w") as wh:
        json.dump(model_dict, wh, sort_keys=True, indent=2, separators=(",", ":"))

    en_us.append("item.%s.name=%s" % (sound, song_info[sound]["name"]))
    en_us.append("item.record.%s.desc=%s - %s" % (sound, song_info[sound]["artist"], song_info[sound]["name"]))
    java_init.append("public static CustomRecord %s;" % (sound.upper(),))
    java.append("songs.put(\"%s\", %s);" % (sound, sound.upper()))

with open("sounds.json", "w") as wh:
    json.dump(sound_json_dict, wh, sort_keys=True, indent=2, separators=(",", ":"))

print "\n".join(en_us)
print "\n".join(java_init)
print "\n".join(java)
