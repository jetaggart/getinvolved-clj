(ns getinvolved.routes.auth
  (:use compojure.core)
  (:require [getinvolved.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [getinvolved.models.db :as db]))

(defn valid? [id password password_confirmation]
  (vali/rule (vali/has-value? id)
             [:id "user ID is required"])
  (vali/rule (vali/min-length? password 5)
             [:password "password must be at least 5 characters"])
  (vali/rule (= password password_confirmation)
             [:password_confirmation "entered passwords do not match"])
  (not (vali/errors? :id :password :password_confirmation)))

(defn register [& [id]]
  (layout/render
    "registration.html"
    {:id id
     :id-error (vali/on-error :id first)
     :password-error (vali/on-error :password first)
     :password-confirmation-error (vali/on-error :password_confirmation first)}))

(defn handle-registration [id password password_confirmation]
  (if (valid? id password password_confirmation)
    (try
      (do
        (db/create-user {:id id :password (crypt/encrypt password)})
        (session/put! :user-id id)
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (register id)))

(defn profile []
  (layout/render
    "profile.html"
    {:user (db/get-user (session/get :user-id))}))

(defn update-profile [{:keys [first-name last-name email]}]
  (db/update-user (session/get :user-id) first-name last-name email)
  (profile))

(defn handle-login [id password]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare password (:password user)))
      (session/put! :user-id id)
      (session/flash-put! :notice "bad password"))
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [id password password_confirmation]
        (handle-registration id password password_confirmation))

  (GET "/profile" [] (profile))
  
  (POST "/update-profile" {params :params} (update-profile params))
  
  (POST "/login" [id password]
        (handle-login id password))

  (GET "/logout" []
        (logout)))
