import math

def generate_sorted_offsets(max_distance):
    """
    Generates all (dx, dy) offsets within the given max_distance
    from the origin, sorted by distance.
    """
    max_d = math.ceil(max_distance)  # Maximum absolute value for dx and dy
    offsets = []
    for dx in range(-max_d, max_d + 1):
        for dy in range(-max_d, max_d + 1):
            if dx**2 + dy**2 <= max_distance**2:
                offsets.append((dx, dy))
    # Sort the offsets by their Euclidean distance from the origin
    offsets.sort(key=lambda offset: math.sqrt(offset[0]**2 + offset[1]**2))
    return offsets

# Generate and display the offsets for sqrt(20) range
max_distance = math.sqrt(20)
sorted_offsets = generate_sorted_offsets(max_distance)

dirs = [
    ("EAST", 1, 0),
    ("NORTHEAST", 1, 1),
    ("NORTH", 0, 1),
    ("NORTHWEST", -1, 1),
    ("WEST", -1, 0),
    ("SOUTHWEST", -1, -1),
    ("SOUTH", 0, -1),
    ("SOUTHEAST", 1, -1)
]

print("int x = rc.getLocation().x;")
print("int y = rc.getLocation().y;")
print("switch (dir) {")
for dir, dx, dy in dirs:
    print(f"\tcase {dir}:")
    new_offsets = []
    for x, y in sorted_offsets:
        new_offsets.append((x + dx, y + dy))
    for x, y in new_offsets:
        if sorted_offsets.count((x, y)) > 0:
            continue
        x -= dx
        y -= dy
        checks = []
        if x > 0:
            checks.append(f"x + {x} < maxX")
        elif x < 0:
            checks.append(f"x >= {-x}")

        if y > 0:
            checks.append(f"y + {y} < maxY")
        elif y < 0:
            checks.append(f"y >= {-y}")

        bruh1 = "x"
        bruh2 = "y"
        if x > 0:
            bruh1 = f"x + {x}"
        elif x < 0:
            bruh1 = f"x - {-x}"
        if y > 0:
            bruh2 = f"y + {y}"
        elif y < 0:
            bruh2 = f"y - {-y}"

        # checks.append(f"pred.test(rc.senseMapInfo(new MapLocation({bruh1}, {bruh2})))")
        # checks.append(f"pred.test(new MapLocation({bruh1}, {bruh2}))")
        # if len(checks) > 0:
        # print(f"\t\tif ({" && ".join(checks)}) {'{'}")
        print(f"\t\tif ({" && ".join(checks)})")
        # bruh3 = "nx" if x != 0 else "x"
        # bruh4 = "ny" if y != 0 else "y"
        bruh3 = "x" if x == -4 else f"x + {x + 4}"
        bruh4 = "y" if y == -4 else f"y + {y + 4}"
        # if x != 0:
            # print(f"\t\t\tint nx = {bruh1};")
        # if y != 0:
            # print(f"\t\t\tint ny = {bruh2};")
        print(f"\t\t\tmapInfos[{bruh3}][{bruh4}] = rc.senseMapInfo(new MapLocation({bruh1}, {bruh2}));")
        # print(f"\t\t\tlastUpdateTime[{bruh3}][{bruh4}] = time;")
        # print(f"\t\t{'}'}")
    print("\t\tbreak;")
print("}")
