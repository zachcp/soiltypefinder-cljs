(ns soiltypefinder.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:use-macros [dommy.macros :only [node sel sel1]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [dommy.core :as dommy]
            [cljs.core.async :refer [<! put! chan]]
            [ajax.core :refer [GET POST] :as ajax]
            [cognitect.transit :as t]
            )
  (:import [goog.net Jsonp]
           [goog Uri])))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Spreadsheet URL Generation

(defn baseurl
  "define baseurl for searching Google Spreadsheets"
  [sheetkey worksheetId query]
  (let [spreadsheet "https://spreadsheets.google.com/feeds"
        list-or-key "list"
        sheetkey    sheetkey
        worksheetId worksheetId
        json       "&alt=json"
        jq (str  "full" query json)]
    (apply str (interpose "/" [spreadsheet list-or-key sheetkey worksheetId "public" jq]))))

(defn makequery
    "make REST query for lat/lon"
    [lat lon]
    (let [maxlat "maxlat>"
          minlat "minlat<"
          maxlon "maxlon>"
          minlon "minlon<"]
    (apply str ["?sq="maxlat lat "%20and%20" minlat lat "%20and%20" maxlon lon "%20and%20" minlon lon])))

(def spreadsheetkey "1uqfGchso5Vk6_VjZWKu-eJMFp0gtP9VjN15uco0ZjsE")
(def worksheetkey "onni5m2")
(def query1 (baseurl spreadsheetkey worksheetkey (makequery 10 0)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Get and Process the Data


(defn getentryinfo [entry]
  ""
  (let [soil     (get-in entry [:gsx$soil :$t])
        suborder (get-in entry [:gsx$suborder :$t])
        polygons (get-in entry [:gsx$points :$t])]
        (.log js.console (str soil suborder))))
  
(defn handler [[ok response]]
  (if ok
    (let [entries (get-in response [:feed :entry])
         ie   (map getentryinfo entries)]
       ; (.log js/console (count entries))
;        (.log js/console (str (first entries)))
;        (.log js/console (str (second entries)))
;        (.log js/console (str (nth entries 2)))
;        (.log js/console (str (get-in (first entries) [:gsx$suborder :$t])))
;        (map getentryinfo entries)
       (for [entry entries]
         (let [soil     (get-in entry [:gsx$suborder :$t])
               suborder (get-in entry [:gsx$suborder :$t])
               polygons (get-in entry [:gsx$suborder :$t])]
           (.log js/console soil)
           ie )))
    (.error js/console (str response))))

(defn ajax []
  (ajax/ajax-request
     {:uri query1
      :method :get
      :handler handler
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})}))

; Add a listener to the search button
(dommy/listen! (sel1 :#search) :click ajax)

