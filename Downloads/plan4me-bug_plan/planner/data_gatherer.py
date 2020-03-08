"""Gathers data for travelplan about events"""
# Credit goes to Calder Johnson
import requests
from .models import Poi, PoiType, Event, Location

class DataGatherer:
    """container for data gathering methods"""

    #dates go in format: YYYYMMDDHHMMSS
    @staticmethod
    def get_event_data(city, date_start, date_end, keywords):
        """updates event data, using the eventful api and the ticketmaster api"""

        #eventful apis url and parameters
        eventful_url = "http://api.eventful.com/json/events/search"
        eventful_params = {
            "app_key":"KcCB2wQs2czjXvDN",
            "date": date_start[:8] + "00" + "-" + date_end[:8] + "00",
            "location": city,
            "page_size": 20,
            "keywords": keywords,
            "include": "price"
        }

        #ticketmaster apis url and parameters
        ticketmaster_url = "https://app.ticketmaster.com/discovery/v2/events.json"
        ticketmaster_params = {
            "apikey": "aNvaAwI2oLvcaPRVGyh9QXxYiReaoXRk",
            "countryCode": "CA",
            "city": city,
            "startDateTime": date_start[:4] + "-" + date_start[4:6] + "-" + date_start[6:8] + "T"
                             + date_start[8:10] + ":" + date_start[10:12] + ":"
                             + date_start[12:14] + "Z",
            "endDateTime": date_end[:4] + "-" + date_end[4:6] + "-" + date_end[6:8] + "T"
                           + date_end[8:10] + ":" + date_end[10:12] + ":"
                           + date_end[12:14] + "Z",
            "keyword": keywords,
            "size": "20"
        }

        #make requests, save to variables
        eventful_data = requests.get(url=eventful_url, params=eventful_params)
        ticktemaster_data = requests.get(url=ticketmaster_url, params=ticketmaster_params)

        #use Django ORM to save data into dbms, sqlite3, for event_data
        #for entry in eventful_data.json()['events']['event']:


        #save eventful data to a file
        with open("eventful_data.json", "w", encoding="utf-8") as eventful_datafile:
            eventful_datafile.write(eventful_data.text)

        #save ticketmaster data to a file
        #with open("ticketmaster_data.json", "w", encoding="utf-8") as ticketmaster_datafile:
        #    ticketmaster_datafile.write(ticktemaster_data.text)

    @staticmethod
    def get_location_data(city: str, budget_min: int, budget_max: int, keywords: str):
        """updates location data, using the google places api"""
        
        #apis url and parameters
        url = "https://maps.googleapis.com/maps/api/place/textsearch/json"

        if keywords in ['lodging', 'hotel']:
            params = {
                "key": "AIzaSyATjPLsCokyLgsrIrYrvVxOxVVIMH_qkRQ",
                "region": "ca",
                "opennow": "true",
                #"minprice": budget_min,
                #"maxprice": budget_max,
                "query": city + " " + keywords
                # if min/maxprice were not disabled, the search would return 0 results
                # hotels' price_level seems to be just None
                # we can leave that as a "flaw" in our project's pitch
            }
        elif keywords in ['restaurant', 'bar']:
            params = {
                "key": "AIzaSyATjPLsCokyLgsrIrYrvVxOxVVIMH_qkRQ",
                "region": "ca",
                "opennow": "true",
                "minprice": budget_min,
                "maxprice": budget_max,
                "query": city + " " + keywords
            }

        #make request, save to a variable
        places_data = requests.get(url=url, params=params)

        #use Django ORM to save data to dbms, sqlite3, for location_data
        for entry in places_data.json()['results']:
            data = Poi()
            if "price_level" in entry:
                data.priceLV = entry["price_level"]
                
                data.placeType = PoiType(pType='restaurant')
            else:
                
                data.placeType = PoiType(pType="hotel")
            data.address = entry["formatted_address"]
            data.name = entry["name"]
            data.rating = entry["rating"]
            data.ratingCount = entry["user_ratings_total"]
            data.location = Location(city=city, country='canada')
            data.placeType.save()
            data.location.save()
            data.save()
            
            
            
            #data.save()
        
        #print(Poi.objects.all())
        #save data to file
        with open(f"{keywords}_data.json", "w", encoding="utf-8") as datafile:
            datafile.write(places_data.text)
        

# test runs
#DataGatherer.get_location_data("Vancouver", 1, 4, "restaurant")
#DataGatherer.get_event_data("Toronto", "20200101083000", "20200831093000", "music")
