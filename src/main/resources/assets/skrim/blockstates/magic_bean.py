import json

max_age = 100
curr_stage = 0
change_stage = [30, 60, 90, 1000]
stage_pointer = 0

data = {
    "variants": []
}

with open("magic_bean.json", "w") as wh:
    for age in range(0, max_age):
        data["variants"].append(
            {
                "age=%s" % (age,): {
                    "model": "minecraft:carrots_stage%s" % (curr_stage,)
                }
            }
        )
        if age >= change_stage[stage_pointer]:
            curr_stage += 1
            stage_pointer += 1
    json.dump(data, wh, sort_keys=True, indent=2)
