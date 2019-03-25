with open("en_US.lang", "r") as rh:
  lines = rh.readlines()

with open("en_US.lang", "w") as wh:
  for line in sorted(lines):
    if line.strip():
      wh.write(line)
