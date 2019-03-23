import json

max_age = 15
curr_stage = 0
change_stage = [5, 10, 14]
stage_pointer = 0

data = {
    "forge_marker": 1,
    "variants": {
        "normal": {
            "model": "skrim:magic_bean_stage0"
        }
    }
}

with open("magic_bean.json", "w") as wh:
    for age in range(0, max_age + 1):
        data["variants"]["age=%s" % (age,)] = {"model": "skrim:magic_bean_stage%s" % (curr_stage,)}
        if stage_pointer < len(change_stage):
            if age >= change_stage[stage_pointer]:
                curr_stage += 1
                stage_pointer += 1
    json.dump(data, wh, sort_keys=True, indent=2)
