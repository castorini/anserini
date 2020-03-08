import json

def eventful_parser(fileName):
    with open(fileName, "r") as read_file:
        event_dict = json.load(read_file)
    list_of_events = event_dict.get("events").get("event")
    # event_dict.get("events").get("event")[0] gets you the list
    # of events
    tidy_list = []
    entry_num = 0
    for each in list_of_events:
        to_update = {}
        to_update.update({"index": "Result #" + str(entry_num + 1) + ":"})
        to_update.update({"title": each.get("title")})
        to_update.update({"start_time": each.get("start_time")})
        to_update.update({"stop_time": each.get("stop_time")})
        to_update.update({"venue_name": each.get("venue_name")})
        to_update.update({"url": each.get("url")})
        entry_num += 1
        tidy_list.append(to_update)
    return tidy_list