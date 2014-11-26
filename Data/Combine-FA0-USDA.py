## This File will convert the FAO and USDA tables to a single table

import fiona
from collections import OrderedDict

#Schemas
###################################################################################################
###################################################################################################

# USDA Shapefile Schema
# {'geometry': 'Polygon', 'properties': OrderedDict([(u'ID', 'int:10'), (u'GRIDCODE', 'int:10'), 
#                                                   (u'SOIL_ORDER', 'str:50'), (u'SUBORDER', 'str:50')])}

# FAO Shapefile Schema
# {'geometry': 'Polygon', 'properties': OrderedDict([(u'SNUM', 'int:9'), (u'FAOSOIL', 'str:254'), (u'DOMSOI', 'str:254'), 
#                                                   (u'PHASE1', 'str:254'), (u'PHASE2', 'str:254'), (u'MISCLU1', 'str:254'), 
#                                                   (u'MISCLU2', 'str:254'), (u'PERMAFROST', 'str:254'), (u'CNTCODE', 'float:19'),
#                                                   (u'CNTNAME', 'str:254'), (u'SQKM', 'float:33.31'), (u'COUNTRY', 'str:254')])}


#Final Schema:
# UniqueID (based on iteration)    
# FAO/USDA
# SoilType
# Suborder
# Polygon
# Max Lat
# Min Lat
# Max Lon
# Max Lon

FAO_shp = "DSMW_RdY.shp"
USDA_shp = "global_suborders_2006.shp"

soildict  = OrderedDict({"UniqueID": 0, "FAO_USDA": "Unknown",
             "Soil": "Unknown", "Suborder": "Unknown",
             "Points": "",
             "maxlon": 0, "minlon": 0,
             "maxlat": 0, "minlat": 0})

FAOsoiltypes = {"Acrisols" :   ["A","Af","Ag","Ah","Ao","Ap"],
            "Cambisols":   ["B","Bc","Bd","Be","Bf", "Bg", "Bh", "Bk","Bv", "Bv", "Bx"],
            "Rendzinas":   ["E"],
            "Chernozems":  ["C","Cg","Ch","Ck","Cl"],
            "Podzoluvisols": ["D","Dd","De","Dg", "DS"],
            "Ferralsols":  ["Fa","Fh","Fo","Fp","Fr","Fx"],
            "Gleysols":    ["G","GL","Gc","Gd","Ge","Gh","Gm","Gp","Gx"],
            "Phaeozems":   ["H", "Hc", "Hg", "Hh", "Hl"],
            "Lithisols":   ["I"],
            "Fluvisols":   ["J", "Jc", "Jd","Je","Jt"],
            "Kastaznozems":["K","Kh","Kk","Kl"],
            "Luvisols":    ["L","La","Lc","Lf","Lg","Lk","Lo","Lp","Lv"],
            "Greyzems":    ["M","Mg","Mo"],
            "Nitrosols":   ["N", "ND", "Nd", "Ne", "Nh"],
            "Histosols":   ["O","Od","Oe","Ox"],
            "Podzols":     ["P","Pf","Pg","Ph","Pl","Po","Pp"],
            "Arenosols":   ["Q","Qa","Qc","Qf","Ql"],
            "Regosols":    ["R","RK","Rc","Rd","Re","Rx"],
            "Solonetz":    ["S", "ST","Sg","Sm","So"],
            "Andosols":    ["T","Th","Tm","To","Tv"],
            "Rankers":     ["U"],
            "Vertsols":    ["V","Vc","Vp"],
            "Planosols":   ["W","WR","Wd","We","Wh","Wm","Ws","Wx"],
            "Xerosols":    ["X","Xh","Xk","Xl","Xy"],
            "Yermosols":   ["Y","Yh","Yk","Yl","Yt","Yy"],
            "Zolonchaks":  ["Z","Zg","Zm","Zo","Zt"]}

inverted_soil_dict = { x:k for k,v in FAOsoiltypes.iteritems() for x in v}

def maxmin(x):
    """ Convert the String representation of GeoJSON polygons to a Python List
    Then get the lat/lon values and calcualte min/max
    
    """
    point_list = x[0]
    lons = [f for [f,s] in point_list]
    lats = [s for [f,s] in point_list]
    return [max(lats), min(lats), max(lons), min(lons)]

    
def process_USDA(x, num):
    """ 
    Convert the USDA format to a standard format
    """
    d = soildict
    maxlat,minlat, maxlon, minlon = maxmin(x['geometry']['coordinates'])
    d['UniqueID'] = num
    d['FAO_USDA'] = "USDA"
    d['Soil']     = x['properties']['SOIL_ORDER']
    d['Suborder'] = x['properties']['SUBORDER']
    d['Points']   = x['geometry']['coordinates']
    d['maxlat']   = maxlat
    d['maxlon']   = maxlon
    d['minlat']   = minlat
    d['minlon']   = minlon
    return d

def process_FAO(x, num):
    """ 
    Convert the FAO format to a standard format
    """
    d = soildict
    maxlat,minlat, maxlon, minlon = maxmin(x['geometry']['coordinates'])
    d['UniqueID'] = num
    d['FAO_USDA'] = "FAO"
    d['Soil']     = inverted_soil_dict[x['properties']['DOMSOI']]
    d['Suborder'] = x['properties']['DOMSOI']
    d['Points']   = x['geometry']['coordinates']
    d['maxlat']   = maxlat
    d['maxlon']   = maxlon
    d['minlat']   = minlat
    d['minlon']   = minlon
    return d

def write_record(rec):
    """ turn a record into a string for writing """
    l = [ rec['UniqueID'], rec['FAO_USDA'], rec['Soil'], rec['Suborder'],
          rec['maxlat'],   rec['maxlon'],   rec['minlat'], rec['minlon'], rec['Points'] ]
    return "\t".join(l) + "\n"
    
def combine():
    # Combine FAO and USDA Data as out.csv
    with open('combined.tsv','w') as w:
        # add header column
        header = ['UniqueID', 'FAO_USDA', 'Soil', 'Suborder',
                  'maxlat',   'maxlon',   'minlat', 'minlon', 'Points']
        w.write("\t".join(header)
        w.write("\n")
        count = 1
        # add FAO data
        with fiona.open(FAO_shp,'r') as source:
            #print source.schema
            for f in source:
                out = process_FAO(f, count)
                outvals = map(str, out.values())
                w.write("\t".join(outvals))
                w.write("\n")
                count += 1
            print "FAO Done"
        # add USDA data
        with fiona.open(USDA_shp,'r') as source:
            #print source.schema
            for f in source:
                out = process_USDA(f, count)
                outvals = map(str, out.values())
                w.write("\t".join(outvals))
                w.write("\n")
                count += 1
            print "USDA Done"

if __name__ == "__main__":
    combine()

