import json
def location_parser(fileName):
    with open(fileName, "r") as read_file:
        location_dict = json.load(read_file)
    tidy_list = []
    entry_num = 0
    for entry in location_dict["results"]:
        tidy_dict = {}
        tidy_dict.update({"index": "Result #" + str((entry_num+1)) + ":"})
        tidy_dict.update({"address": entry["formatted_address"]})
        tidy_dict.update({"name": entry["name"]})
        #tidy_dict.update({"photo": entry["photos"][0]["html_attributions"]})
        tidy_dict.update({"rating": entry["rating"]})
        tidy_dict.update({"how_many_ratings": entry["user_ratings_total"]})
        if "price_level" in entry:
            tidy_dict.update({"price_level": entry["price_level"]})
        tidy_list.append(tidy_dict)
        entry_num += 1
    return tidy_list

