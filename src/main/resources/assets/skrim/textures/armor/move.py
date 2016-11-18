import os
import shutil

for leaf in ["acacia", "birch", "dark_oak", "jungle", "oak", "spruce"]:
    for armor in ["boots", "chestplate", "helmet", "leggings"]:
        source = "%s_%s.png" % (leaf, armor)
        dest = "%s_leaf_%s.png" % (leaf, armor)
        if os.path.isfile(source):
            shutil.move(source, dest)
