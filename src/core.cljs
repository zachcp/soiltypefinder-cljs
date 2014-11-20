(ns soiltypefinder
  (:use-macros [dommy.macros :only [node sel sel1]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields init-field value-of]]
            [ajax.core :refer [GET POST] :as ajax]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; appstate and spreadsheet info
(def latlon (atom {:latitude 0 :longitude 0}))
(def soils (atom []))

(def spreadsheetkey "1uqfGchso5Vk6_VjZWKu-eJMFp0gtP9VjN15uco0ZjsE")
(def worksheetkey "onni5m2")

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

(defn geturl []
  "create the url using the latlon atom for locations"
  (let [url (baseurl spreadsheetkey worksheetkey (makequery (get @latlon :latitude) (get @latlon :longitude)))]
    (.log js/console (str url))
    url))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Get and Process the Data

(defn handler
  "handle data obtained from the ajax funtion and store the results in the appstate"
  [[ok response]]
  (if ok
    (let [entries (get-in response [:feed :entry])]
         ;(.log js/console (str (first entries)))
         ;(.log js/console (str (get-in (first entries) [:gsx$suborder :$t])))
         (reset! soils [] )
         (doseq [entry entries]
           (let [soil     (get-in entry [:gsx$suborder :$t])
                 suborder (get-in entry [:gsx$suborder :$t])
                 polygons (get-in entry [:gsx$suborder :$t])]
             (swap! soils conj {:soil soil :suborder suborder}))))
    (.error js/console (str response))))

(defn ajax []
  "make the url for lat lon and send the AJAX request"
  (let [url  (geturl)]
    (ajax/ajax-request
       {:uri url
        :method :get
        :handler handler
        :format (ajax/json-request-format)
        :response-format (ajax/json-response-format {:keywords? true})})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Layout

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(def form-template
  [:div
   (input "Latitude" :numeric :latitude)
   (input "Longitude" :numeric :longitude)])

(defn soilfinderapp
  "main app for the soil type finder"
  []
  [:div#container.container
    [:header#header]
    [bind-fields form-template latlon]
    (doall
     (for [soil @soils]
       [:div
        [:h2 (:soil soil)]
        [:h3 (:suborder soil)]]))

     [:button.btn.btn-default
         {:on-click #(ajax) } "Run"
        ; {:on-click #(.log js/console (str (geturl))) } "Run"

      ]])

;run
(defn ^:export run [] (reagent/render-component [soilfinderapp]  (.getElementById js/document "app")))


