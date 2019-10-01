import cv2
import numpy as np
import os

alpha_skills = ["blacksmithing", "botany", "cooking", "defense", "demolition", "digging", "farming", "fishing", "melee", "mining", "ranged", "woodcutting"]

def invert(im):
  # Split the alpha channel, invert, then reapply alpha.
  b, g, r, a = cv2.split(im)
  inverted = cv2.bitwise_not(cv2.merge((b, g, r)))
  b, g, r = cv2.split(inverted)
  return cv2.merge((b, g, r, a))

for skill in alpha_skills:
  im = cv2.imread(f"{skill}.png", cv2.IMREAD_UNCHANGED)
  # Want to create a color inverted version of the icon.
  for j in range(4):
    start_y = 32 + j * 16
    color_icon = im[start_y:start_y + 16, 0:16]
    im[start_y:start_y + 16, 32:48] = invert(color_icon)
  cv2.imwrite(f"{skill}.png", im)

