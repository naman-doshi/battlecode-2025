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

print("int x = rc.getLocation().x;")
print("int y = rc.getLocation().y;")
print("int maxX = rc.getMapWidth();")
print("int maxY = rc.getMapHeight();")
sorted_offsets.reverse()
for x, y in sorted_offsets:

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
    if len(checks) > 0:
        print(f"if ({" && ".join(checks)})")
    # print(f"\tnearby[--nxt] = mapInfos[{bruh1}][{bruh2}];")
    print(f"\tnearby[nearbyCnt++] = rc.senseMapInfo(new MapLocation({bruh1}, {bruh2}));")

print("return null;")