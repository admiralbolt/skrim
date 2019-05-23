import cv2
import numpy as np
import os

alpha_skills = ["blacksmithing", "botany", "cooking", "defense", "demolition", "digging", "farming", "fishing", "melee", "mining", "ranged", "woodcutting"]

skills = cv2.imread("skills.png", cv2.IMREAD_UNCHANGED)
abilities = cv2.imread("skill_abilities.png", cv2.IMREAD_UNCHANGED)
canvas = cv2.imread("canvas.png", cv2.IMREAD_UNCHANGED)

def grayscale(im):
  # Split the alpha channel, convert to grayscale, then reapply the alpha channel.
  rgb = im[:,:,3]
  gray_color = cv2.cvtColor(cv2.cvtColor(im, cv2.COLOR_RGB2GRAY), cv2.COLOR_GRAY2RGB)
  return np.dstack([gray_color, im[:,:,3]])
  

j = 0
for i, skill in enumerate(alpha_skills):
  output = canvas.copy()
  y = (i // 8) * 32
  x = (i % 8) * 32
  if not os.path.isdir(skill):
    os.makedirs(skill)
  # Skill Icon + Grayscale version
  output[0:32, 0:32] = skills[y:y+32, x:x+32]
  output[0:32, 32:64] = grayscale(skills[y:y+32, x:x+32])

  # Now the abilities.
  for q in range(4):
    aby = (j // 16) * 16
    abx = (j % 16) * 16
    targety = 32 + q * 16
    output[targety:targety+16, 0:16] = abilities[aby:aby+16, abx:abx+16]
    output[targety:targety+16, 16:32] = grayscale(abilities[aby:aby+16, abx:abx+16])
    j += 1

  cv2.imwrite(f"{skill}.png", output)



