orth_directions = [
    (1, 0, "EAST"),
    (0, 1, "NORTH"),
    (-1, 0, "WEST"),
    (0, -1, "SOUTH")
]

directions = [
    (0, 1, "NORTH"),
    (1, 1, "NORTHEAST"),
    (1, 0, "EAST"),
    (1, -1, "SOUTHEAST"),
    (0, -1, "SOUTH"),
    (-1, -1, "SOUTHWEST"),
    (-1, 0, "WEST"),
    (-1, 1, "NORTHWEST")
]

def bruh(cap):
    print("int score = 0;")
    print("switch (topDir.ordinal()) {")
    diri = -1
    for dx, dy, dir_str in directions:
        diri += 1
        print(f"case {diri}:")
        x = 4 + dx
        y = 4 + dy
        for di in range(-1, cap):
            possi = (diri + di + 8) % 8
            dx2, dy2, dir_str2 = directions[possi]
            print(f"if (canMove(Direction.{dir_str2})) {{")
            print("score = 0;")
            for dx3, dy3, dir_str3 in directions:
                print(f"if (RobotTracker.bot{x + dx3}{y + dy3} != null && RobotTracker.bot{x + dx3}{y + dy3}.team == team) score++;")
            print(f"MapInfo info = rc.senseMapInfo(Game.pos.add(Direction.{dir_str2}));")
            print("if (info.getPaint() == PaintType.EMPTY) score++;")
            print("if (info.getPaint().isEnemy()) score *= 2;")
            print("if (cellPenalty != null) score += cellPenalty.apply(info);")
            print("score *= 1000000;")
            print(f"score += Game.pos.add(Direction.{dir_str2}).distanceSquaredTo(target) * 10;")
            print("score += trng.nextInt(10);")
            print("if (score < bestScore) {")
            print("bestScore = score;")
            print(f"best = Direction.{dir_str2};")
            print("}")
            print("}")
        print("break;")
    print("}")

print("if (alwaysLeftTurn) {")
bruh(1);
print("} else {")
bruh(2);
print("}")
