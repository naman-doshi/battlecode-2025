import requests
import random

# The URL to send the POST requests to
url = "https://api.battlecode.org/api/compete/bc25java/request/"

# Your authorization token
auth_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzM4MjI0ODcyLCJpYXQiOjE3Mzc3OTI4NzIsImp0aSI6Ijg2OTJmMjc5MmU2NzQ2NWVhOTFkMjVjNjZkNjVmYWZkIiwidXNlcl9pZCI6MTM2NX0.UF775lV0Jtf0C6_B_CoYH_lc-O3WcO9OoEmtVLttbIQ"

# Configuration
excluded_team_id = 1288  # Your team ID
base_url = "https://api.battlecode.org/api"
all_maps = ["DefaultSmall", "DefaultMedium", "DefaultLarge", "DefaultHuge", "Fossil", "gardenworld", "Gears", "Justice", "Money", "Mirage", "MoneyTower", 
            "Racetrack", "Restart", "SaltyPepper", "SMILE", "Thirds", "UglySweater", "UnderTheSea", "catface", "memstore", "TargetPractice",
            "Barcode", "Bread", "BunnyGame", "Filter", "fix", "Flower", "galaxy", "giver", "gridworld", "leavemealone", "Piglets2", "quack", "sayhi",
            "sierpinski", "Snowglobe", "windmill"]

# set diff seeds for each team
# random sample 10 maps with seed 1509
# teamIds = [1509,1208,1070,1301,1186]
# bb = [1398,1182,1348,1278,1377]

def fetch_top_teams(auth_token, excluded_team_id, count=5):
    url = f"{base_url}/team/bc25java/t/?ordering=-rating%2Cname"
    headers = {
        "Authorization": f"Bearer {auth_token}",
        "Content-Type": "application/json",
        "Accept": "*/*",
        "Accept-Encoding": "gzip, deflate, br",
        "Accept-Language": "en-US,en;q=0.5",
        "Origin": "https://play.battlecode.org",
        "Referer": "https://play.battlecode.org/",
        "Sec-Fetch-Dest": "empty",
        "Sec-Fetch-Mode": "cors",
        "Sec-Fetch-Site": "same-site",
        "Sec-Gpc": "1"
    }

    ids = []
    while url and len(ids) < count:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()

        for team in data["results"]:
            #print(team["profile"])
            if team["id"] != excluded_team_id and team["profile"]["auto_accept_reject_unranked"] == 'A':
                ids.append(team["id"])
                if len(ids) == count:
                    break

        url = data.get("next")

    return ids

def generate_payload(team_id, ten_sets):
    return {
        "is_ranked": False,
        "requested_to": team_id,
        "player_order": "+",
        "map_names": ten_sets[team_id]
    }

def send_match_requests(auth_token, team_ids, ten_sets):
    url = f"{base_url}/compete/bc25java/request/"
    headers = {
        "Authorization": f"Bearer {auth_token}",
        "Content-Type": "application/json",
        "Accept": "*/*",
        "Accept-Encoding": "gzip, deflate, br",
        "Accept-Language": "en-US,en;q=0.5",
        "Origin": "https://play.battlecode.org",
        "Referer": "https://play.battlecode.org/",
        "Sec-Fetch-Dest": "empty",
        "Sec-Fetch-Mode": "cors",
        "Sec-Fetch-Site": "same-site",
        "Sec-Gpc": "1"
    }

    for team_id in team_ids:
        payload = {**generate_payload(int(team_id), ten_sets), "requested_to": team_id}
        response = requests.post(url, headers=headers, json=payload)

        if response.status_code == 201:
            print(f"Successfully sent request to team {team_id}: {response.json()}")
        else:
            print(f"Failed to send request to team {team_id}: {response.status_code} {response.text}")

if __name__ == "__main__":
    try:
        print("Fetching top teams...")
        team_ids = fetch_top_teams(auth_token, excluded_team_id)
        print(f"Top team IDs: {team_ids}")
        ten_sets = {}
        for team_id in team_ids:
            random.seed(team_id + 1)
            ten_sets[team_id] = random.sample(all_maps, 10)
        print("Sending match requests...")
        send_match_requests(auth_token, team_ids, ten_sets)

    except Exception as e:
        print(f"An error occurred: {e}")
