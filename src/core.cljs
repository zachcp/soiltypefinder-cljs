(ns soiltypefinder
  (:use-macros [dommy.macros :only [node sel sel1]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields init-field value-of]]
            [ajax.core :refer [GET POST] :as ajax]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; App State
(def soils (atom []))

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

(defn handler
  "handle data obtained from the ajax funtion and store the results in the appstate"
  [[ok response]]
  (if ok
    (let [entries (get-in response [:feed :entry])]
         ; these will all work showing that entries is seqable
         ; and that I can access the sub-data of an individual entry
         ;(.log js/console (count entries))
         ;(.log js/console (str (first entries)))
         ;(.log js/console (str (second entries)))
         ;(.log js/console (str (nth entries 2)))
         (.log js/console (str (get-in (first entries) [:gsx$suborder :$t])))

       ;for/let does not work
          (reset! soils [] )
          (doseq [entry entries]
            (let [soil     (get-in entry [:gsx$suborder :$t])
                  suborder (get-in entry [:gsx$suborder :$t])
                  polygons (get-in entry [:gsx$suborder :$t])]
                 (swap! soils conj {:soil soil :suborder suborder} )
             (.log js/console (str @soils)))))
    (.error js/console (str response))))

(defn ajax []
  (ajax/ajax-request
     {:uri query1
      :method :get
      :handler handler
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Layout

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))


(defn soilfinderapp
  "main app for the soil type finder"
  []

   [:div
    [:header#header]
     (input "Latitude" :number :latitude)
     (input "Longitude" :number :longitude)
     [:h1 "todos"]
      (doall
       (for [soil @soils]
         [:div
          [:h2 (:soil soil)]
          [:h3 (:suborder soil)]]))
     [:button.btn.btn-default
         {:on-click ajax} "Run"]])

;run
;(defn ^:export run [] (reagent/render-component [soilfinderapp] (.-body js/document)))
(defn ^:export run [] (reagent/render-component [soilfinderapp]  (.getElementById js/document "app")))


