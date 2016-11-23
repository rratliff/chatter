(ns chatter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.page :as page]
            [hiccup.form :as form]
            [ring.middleware.params :refer [wrap-params]]))

(def chat-messages (atom '()))

(defn update-messages!
  "This will update a message list atom"
  [messages name new-message]
  (swap! messages conj {:name name :message new-message}))

(defn generate-message-view
  "This generates the HTML for displaying messages"
  [messages]
   (page/html5
     [:head
      [:title "bobby chatter"]
      (page/include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
      (page/include-js  "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
      (page/include-css "/chatter.css")]
     [:body
      [:h1 "Our Chat App"]
      (form/form-to
        [:post "/"]
        "Name: " (form/text-field "name")
        "Message: " (form/text-field "msg")
        (form/submit-button "Submit"))
      [:table#messages.table
      (map (fn [m] [:tr [:td (:name m)] [:td (:message m)]]) messages)]]))

(defroutes app-routes
  (GET "/" [] (generate-message-view @chat-messages))
  (POST "/" {params :params} 
        (let [name-param (get params "name")
              msg-param (get params "msg")
              new-messages (update-messages! chat-messages name-param msg-param)]
           (generate-message-view new-messages)
          ))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (wrap-params app-routes))
