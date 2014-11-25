#this requires covnersion to CSV

# Get FAO DATA
# base url http://worldmap.harvard.edu/data/geonode:DSMW_RdY
DSMW_RdY.csv:
	curl -o  DSMW_RdY.csv  -p "http://worldmap.harvard.edu/download/wfs/821/csv?outputFormat=csv&service=WFS&request=GetFeature&format_options=charset%3AUTF-8&typename=geonode%3ADSMW_RdY&version=1.0.0"
	touch DSMW_RdY.csv


# Get USDA DATA
FAO.zip:
	curl -o FAO.zip "http://worldmap.harvard.edu/download/wfs/821/zip?outputFormat=SHAPE-ZIP&service=WFS&request=GetFeature&format_options=charset%3AUTF-8&typename=geonode%3ADSMW_RdY&version=1.0.0"
	
DSMW_RdY.shp:
	unzip FAO.zip
	touch DSMW_RdY.shp 
	
	
# Get USDA DATA
USDA.zip:
	curl -o USDA.zip ftp://ftp-fc.sc.egov.usda.gov/NHQ/pub/outgoing/soils/global_soil_suborders_data.zip

global_suborders_2006.shp: USDA.zip
	unzip USDA.zip
	touch global_suborders_2006.shp

USDA.csv: global_suborders_2006.shp
	ogr2ogr -f csv USDA.csv global_suborders_2006.shp
	
USDA/globalsoilsJSON.txt: USDA/globalsoils.txt
	python make_GeoJSON.py
	touch $@
	  
# all: worldsoils.topojson worldsoilssample50.topojson worldsoilssample25.topojson \#
#	worldsoilsample1.topojson worldsoilsample2.topojson

all: FAO.shp, global_suborders_2006.shp

counties.zip:
	curl -o counties.zip 'http://www2.census.gov/geo/tiger/GENZ2010/gz_2010_us_050_00_20m.zip'

gz_2010_us_050_00_20m.shp: counties.zip
	unzip counties.zip
	touch gz_2010_us_050_00_20m.shp

counties.json: gz_2010_us_050_00_20m.shp
	topojson -o counties.json -- counties=gz_2010_us_050_00_20m.shp
