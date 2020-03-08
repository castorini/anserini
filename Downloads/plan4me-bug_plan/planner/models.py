from django.db import models
import uuid

# Create your models here.

# ========================================================


# location refers to the city and the country
# right now we'll just work with Canada
class Location(models.Model):
    #id = models.AutoField(primary_key=True)
    city = models.CharField(max_length=80)
    country = models.CharField(max_length=80)
    # --------------------
    def __str__(self):
        return f"{self.city}, {self.country}"


    

# PoiType right now can only be three types:
# 0) others
# 1) restaurant
# 2) hotel
class PoiType(models.Model):
    #poitype_id = models.AutoField(primary_key=True)
    
    pType = models.CharField(
        max_length=80,
        default='unknown',
    )
    # --------------------
    def __str__(self):
        return self.pType




# Poi stands for place of interest
class Poi(models.Model):
    #poi_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=50)
    address = models.CharField(max_length=100)
    
    '''
    PRICE_CHOICES = [
        (0, 'unknown price level'),
        (1, '$'),
        (2, '$$'),
        (3, '$$$'),
        (4, '$$$$'),
    ]
    '''
    priceLV = models.IntegerField(
        #choices=PRICE_CHOICES,
        default=0,
        verbose_name="price level",
    )
    rating = models.DecimalField(
        max_digits=2, 
        decimal_places=1,
        default=-1
    )
    ratingCount = models.IntegerField(
        default=-1
    )
    placeType = models.ForeignKey(
        PoiType,
        on_delete=models.CASCADE
    )
    location = models.ForeignKey(
        Location,
        on_delete=models.CASCADE
    )
    
    class Meta:
        ordering = ['rating', 'ratingCount']
    # --------------------
    def __str__(self):
        return self.name

# interesting local events that a person can check out
class Event(models.Model):
    #event_id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=150)
    location = models.ForeignKey(
        'Location',
        on_delete=models.CASCADE
    )
    starting_time = models.DateTimeField()
    description = models.TextField()
    price = models.DecimalField(
        max_digits=10,
        decimal_places=2,
        null=True,
    )
    venue_name = models.CharField(max_length=80)
    url_link = models.TextField()
    venue_url_link = models.TextField()
    # --------------------
    def __str__(self):
        return self.title

