# Elastic Search on Rivers

This guide describes how to run ElasticSearch on the [Hydro Rivers dataset](http://www.hydrosheds.org/page/hydrorivers). 

## Prepare Data

1. Download the data from the [Hydro Rivers dataset](https://www.hydrosheds.org/page/hydrorivers). Select a region under the Shapefiles section. 
2. `cd` into the directory that you’ve extracted from the zipped file, and create a conda environment.
3. Convert the shapefile to .geojson format using `gdal`:
    
    ```bash
    conda install -c conda-forge gdal
    ogr2ogr -f GeoJSON output.geojson HydroRIVERS_v10_gr.shp
    ```
    
    In this guide, we use the Greenland dataset. Change `HydroRIVERS_v10_gr.shp` based on the dataset you’ve downloaded.
    

## Install Elastic Search

This section mostly follows the [Elastic Search Quickstart](https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html)

1. Install [Docker Desktop](https://www.docker.com/products/docker-desktop)
2. Run Elastic Search
    
    ```bash
    docker network create elastic
    
    docker pull docker.elastic.co/elasticsearch/elasticsearch:7.16.3
    
    docker run --name es01-test --net elastic -p 127.0.0.1:9200:9200 \
    -p 127.0.0.1:9300:9300 -e "discovery.type=single-node" \
    docker.elastic.co/elasticsearch/elasticsearch:7.16.3
    ```
    
3. Start up Kibana
    
    ```bash
    # Start up Kibana
    docker pull docker.elastic.co/kibana/kibana:7.16.3
    
    docker run --name kib01-test --net elastic -p 127.0.0.1:5601:5601 \
    -e "ELASTICSEARCH_HOSTS=http://es01-test:9200" \
    docker.elastic.co/kibana/kibana:7.16.3
    ```
    

## Index Data

1. Add the index schema named `rivers-index`. The entire point of this is to tell Elastic search that our `geometry` field is a `geo_shape`. 

After your Kibana image started up, go to http://localhost:5601, then Tools > Console. Then paste the code below into your console.
    
    ```bash
    PUT rivers-index
    {
      "mappings": {
        "properties": {
    			"geometry": {
    				"type": "geo_shape"
    			}
        }
      }
    }
    ```
    
    Can be replaced with curl command.
    
2. Paste the following file into a Python file, and run the following to index the rivers.
    
    `python index_data.py --path 'path-to-geojson' --index 'rivers-index'`
    where `‘path-to-geojson’` is the location of your `output.geojson` file indicated in Step 3 of the “Preparing Data” section.
    
    ```python
    # index_data.py
    
    import geojson
    import argparse
    from elasticsearch import Elasticsearch, helpers
    
    def convert_format(data, index):
      '''
      Converts geojson to data format that can be fed into Elasticsearch
      by adding a few extra fields
      '''
      for feature in data['features']:
        # remove redundant type argument
        del feature['type'] 
    
        # add index
        yield {
          '_index': index,
          '_source': feature
        }
    
    if __name__ == "__main__":
      parser = argparse.ArgumentParser()
      parser.add_argument('--path', required=True)
      parser.add_argument('--index', required=True)
      args = parser.parse_args()
    
      with open(args.path) as f:
        data = geojson.load(f)
        es = Elasticsearch(hosts=[{'host': 'localhost', 'port': 9200}])
        helpers.bulk(es, convert_format(data, args.index))
    ```
    

## Query Data

There are two ways to query your Geo data in Kibana.

1. Using Dev Tools → Console. This specific example includes a bounding box. Feel free to try out other types of queries in the [documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html).
    
    ```bash
    # The following should be put into Dev Tools > Console in Kibana 
    # http://localhost:5601
    GET rivers-index/_search
    {
      "query": {
        "bool": {
          "must": {
            "match_all": {}
          },
          "filter": {
            "geo_bounding_box": {
              "geometry": {
                "top_left": {
                  "lon": -30,
                  "lat": 75
                },
                "bottom_right": {
                  "lon": -28,
                  "lat": 70
                }          
              }
            }
          }
        }
      }
    }
    ```
    
    Sample output for Greenland dataset:
    
    ```bash
    #! Elasticsearch built-in security features are not enabled. Without authentication, your cluster could be accessible to anyone. See https://www.elastic.co/guide/en/elasticsearch/reference/7.16/security-minimal-setup.html to enable security.
    {
      "took" : 6,
      "timed_out" : false,
      "_shards" : {
        "total" : 1,
        "successful" : 1,
        "skipped" : 0,
        "failed" : 0
      },
      "hits" : {
        "total" : {
          "value" : 2545,
          "relation" : "eq"
        },
        "max_score" : 1.0,
        "hits" : [
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "Z0shn34BHsvIanDBVp3t",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.23125,
                    75.0125
                  ],
                  [
                    -29.23125,
                    75.00625
                  ],
                  [
                    -29.222917,
                    74.997917
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045635,
                "NEXT_DOWN" : 90047620,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 1.65,
                "DIST_DN_KM" : 400.3,
                "DIST_UP_KM" : 23.4,
                "CATCH_SKM" : 12.03,
                "UPLAND_SKM" : 12.0,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 4,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120069090
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "aEshn34BHsvIanDBVp3t",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.239583,
                    75.15625
                  ],
                  [
                    -29.239583,
                    75.089583
                  ],
                  [
                    -29.222917,
                    75.072917
                  ],
                  [
                    -29.222917,
                    74.997917
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045636,
                "NEXT_DOWN" : 90047620,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 17.67,
                "DIST_DN_KM" : 400.2,
                "DIST_UP_KM" : 70.6,
                "CATCH_SKM" : 7.17,
                "UPLAND_SKM" : 87.3,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 2,
                "ORD_CLAS" : 3,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120069090
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "mkshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.620833,
                    75.054167
                  ],
                  [
                    -29.614583,
                    75.047917
                  ],
                  [
                    -29.614583,
                    74.997917
                  ],
                  [
                    -29.60625,
                    74.989583
                  ],
                  [
                    -29.589583,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045686,
                "NEXT_DOWN" : 90045688,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 7.71,
                "DIST_DN_KM" : 415.0,
                "DIST_UP_KM" : 28.8,
                "CATCH_SKM" : 18.87,
                "UPLAND_SKM" : 18.9,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 4,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "m0shn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.6125,
                    75.145833
                  ],
                  [
                    -29.60625,
                    75.139583
                  ],
                  [
                    -29.60625,
                    75.047917
                  ],
                  [
                    -29.597917,
                    75.039583
                  ],
                  [
                    -29.597917,
                    74.997917
                  ],
                  [
                    -29.589583,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045687,
                "NEXT_DOWN" : 90045688,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 17.46,
                "DIST_DN_KM" : 415.2,
                "DIST_UP_KM" : 36.2,
                "CATCH_SKM" : 17.4,
                "UPLAND_SKM" : 17.4,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 3,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "nEshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.589583,
                    74.989583
                  ],
                  [
                    -29.572917,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045688,
                "NEXT_DOWN" : 90045690,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 0.48,
                "DIST_DN_KM" : 414.5,
                "DIST_UP_KM" : 36.9,
                "CATCH_SKM" : 0.56,
                "UPLAND_SKM" : 36.8,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 2,
                "ORD_CLAS" : 3,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "nUshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.58125,
                    75.145833
                  ],
                  [
                    -29.58125,
                    74.997917
                  ],
                  [
                    -29.572917,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045689,
                "NEXT_DOWN" : 90045690,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 17.41,
                "DIST_DN_KM" : 414.7,
                "DIST_UP_KM" : 35.1,
                "CATCH_SKM" : 21.72,
                "UPLAND_SKM" : 21.7,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 4,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "nkshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.572917,
                    74.989583
                  ],
                  [
                    -29.53125,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045690,
                "NEXT_DOWN" : 90045706,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 1.2,
                "DIST_DN_KM" : 413.3,
                "DIST_UP_KM" : 38.1,
                "CATCH_SKM" : 8.47,
                "UPLAND_SKM" : 67.0,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 2,
                "ORD_CLAS" : 3,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "n0shn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.579167,
                    75.179167
                  ],
                  [
                    -29.572917,
                    75.172917
                  ],
                  [
                    -29.572917,
                    75.147917
                  ],
                  [
                    -29.564583,
                    75.139583
                  ],
                  [
                    -29.564583,
                    75.13125
                  ],
                  [
                    -29.55625,
                    75.122917
                  ],
                  [
                    -29.55625,
                    75.014583
                  ],
                  [
                    -29.53125,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045691,
                "NEXT_DOWN" : 90045706,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 21.25,
                "DIST_DN_KM" : 413.5,
                "DIST_UP_KM" : 35.7,
                "CATCH_SKM" : 22.38,
                "UPLAND_SKM" : 22.4,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 4,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120149770
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "oEshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.55,
                    75.158333
                  ],
                  [
                    -29.539583,
                    75.147917
                  ],
                  [
                    -29.539583,
                    75.139583
                  ],
                  [
                    -29.53125,
                    75.13125
                  ],
                  [
                    -29.53125,
                    75.08125
                  ],
                  [
                    -29.522917,
                    75.072917
                  ],
                  [
                    -29.522917,
                    75.03125
                  ],
                  [
                    -29.502083,
                    75.010417
                  ],
                  [
                    -29.502083,
                    75.002083
                  ],
                  [
                    -29.489583,
                    74.989583
                  ],
                  [
                    -29.472917,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045692,
                "NEXT_DOWN" : 90045707,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 19.46,
                "DIST_DN_KM" : 411.2,
                "DIST_UP_KM" : 36.9,
                "CATCH_SKM" : 29.42,
                "UPLAND_SKM" : 29.4,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 3,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120069220
              }
            }
          },
          {
            "_index" : "rivers-index",
            "_type" : "_doc",
            "_id" : "oUshn34BHsvIanDBVp3u",
            "_score" : 1.0,
            "_source" : {
              "geometry" : {
                "type" : "LineString",
                "coordinates" : [
                  [
                    -29.50625,
                    75.158333
                  ],
                  [
                    -29.50625,
                    75.03125
                  ],
                  [
                    -29.497917,
                    75.022917
                  ],
                  [
                    -29.497917,
                    75.014583
                  ],
                  [
                    -29.472917,
                    74.989583
                  ]
                ]
              },
              "properties" : {
                "HYRIV_ID" : 90045693,
                "NEXT_DOWN" : 90045707,
                "MAIN_RIV" : 90034871,
                "LENGTH_KM" : 18.89,
                "DIST_DN_KM" : 411.4,
                "DIST_UP_KM" : 36.2,
                "CATCH_SKM" : 18.95,
                "UPLAND_SKM" : 19.0,
                "ENDORHEIC" : 0,
                "DIS_AV_CMS" : 0.0,
                "ORD_STRA" : 1,
                "ORD_CLAS" : 4,
                "ORD_FLOW" : 10,
                "HYBAS_L12" : 9120069220
              }
            }
          }
        ]
      }
    }
    ```
    
2. We can also query using the Maps tool. This option will be described in more detail in the Grand River example below.

## Query Example: Grand River

To test Elastic Search, we can try to query for the segment of Grand River in Waterloo. Note that this requires you to have downloaded the North America or Global dataset.

1. Using Dev Tools → Console
    
    ```bash
    GET rivers-index/_search
    {
      "query": {
        "bool": {
          "must": {
            "match_all": {}
          },
          "filter": {
            "geo_distance": {
              "distance": "7km",
              "geometry": {
                "lat": 43.4716099,
                "lon": -80.5498658
              }
            }
          }
        }
      }
    }
    ```
    
    The following should return all river segments within 7km radius of the University of Waterloo coordinate. The 4th result should be the Grand River segment in Waterloo:
    
    ```bash
    {
      "_index" : "rivers-index-global",
      "_type" : "_doc",
      "_id" : "hTzaoX4ButjpHc749g6z",
      "_score" : 1.0,
      "_source" : {
        "geometry" : {
          "type" : "LineString",
          "coordinates" : [
            [
              -80.49375,
              43.539583
            ],
            [
              -80.485417,
              43.539583
            ],
            [
              -80.472917,
              43.527083
            ],
            [
              -80.472917,
              43.522917
            ],
            [
              -80.477083,
              43.51875
            ],
            [
              -80.48125,
              43.51875
            ],
            [
              -80.489583,
              43.510417
            ],
            [
              -80.489583,
              43.50625
            ],
            [
              -80.485417,
              43.502083
            ],
            [
              -80.472917,
              43.502083
            ],
            [
              -80.46875,
              43.497917
            ],
            [
              -80.46875,
              43.489583
            ],
            [
              -80.477083,
              43.48125
            ]
          ]
        },
        "properties" : {
          "HYRIV_ID" : 70474122,
          "NEXT_DOWN" : 70474340,
          "MAIN_RIV" : 70367865,
          "LENGTH_KM" : 9.59,
          "DIST_DN_KM" : 1169.6,
          "DIST_UP_KM" : 114.3,
          "CATCH_SKM" : 33.93,
          "UPLAND_SKM" : 2239.6,
          "ENDORHEIC" : 0,
          "DIS_AV_CMS" : 15.308,
          "ORD_STRA" : 4,
          "ORD_CLAS" : 2,
          "ORD_FLOW" : 5,
          "HYBAS_L12" : 7121033560
        }
      }
    }
    ```
    
2. We can also query using the Maps tool. To do so, first create an index pattern with the same name as your index (i.e. `rivers-index`) [here](http://localhost:5601/app/management/kibana/indexPatterns).

After you’re done creating it, go to Analytics → Maps. Select Add Layer → Documents → Select the index pattern that you’ve just created. Choose `Use vector tiles` for scaling because our dataset is too large. If you zoom in near Toronto, you should see something similar to this:
    
![Untitled](https://user-images.githubusercontent.com/42086645/151693301-ddf44696-b7d9-43f5-8a49-d3fd86ebae09.png)
    
Zoom in to the Waterloo region:
    
![Untitled 1](https://user-images.githubusercontent.com/42086645/151693309-3353c735-0b79-4ac5-a0a0-9369edf7db6f.png)
    
Click on the wrench on the left, and you can choose a way to filter your data:

![Screen_Shot_2022-01-30_at_3 47 19_AM](https://user-images.githubusercontent.com/42086645/151693319-76bcb080-6db0-40f4-b49f-cd8153197151.png)
    
A quick way to do so is to choose the first option, then Spatial relation: `within` inside that option, and draw something like this:

![Untitled 2](https://user-images.githubusercontent.com/42086645/151693314-79b373af-4e5d-4bfc-8682-ee718eb7b456.png)
    
The green line shows the Grand River segment queried.
