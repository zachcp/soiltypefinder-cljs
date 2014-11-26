# Get FAO DATA
# base url http://worldmap.harvard.edu/data/geonode:DSMW_RdY
FAO.zip:
	curl -o FAO.zip "http://worldmap.harvard.edu/download/wfs/821/zip?outputFormat=SHAPE-ZIP&service=WFS&request=GetFeature&format_options=charset%3AUTF-8&typename=geonode%3ADSMW_RdY&version=1.0.0"
DSMW_RdY.shp: FAO.zip
	unzip FAO.zip
	touch DSMW_RdY.shp 
	
# Get USDA DATA
USDA.zip:
	curl -o USDA.zip ftp://ftp-fc.sc.egov.usda.gov/NHQ/pub/outgoing/soils/global_soil_suborders_data.zi
global_suborders_2006.shp: USDA.zip
	unzip USDA.zip
	touch global_suborders_2006.shp

#combine the data
combined.tsv: global_suborders_2006.shp DSMW_RdY.shp
	python Combine-FA0-USDA.py
	touch combined.tsv

all: combined.tsv
	
clean:
	rm *.shp
	rm *.dbf
	rm *.shx
	rm *.cst
	rm *.prj
	rm wfsrequest.txt 	