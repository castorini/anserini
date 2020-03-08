from django.urls import path
from .import views


urlpatterns = [
    path('', views.index, name ='index'),
    path('search/', views.search, name='search'),
    path('search/result/', views.search_and_display, name = 'result')
]