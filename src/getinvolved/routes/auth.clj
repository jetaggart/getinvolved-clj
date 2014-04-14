(ns getinvolved.routes.auth
  (:use compojure.core)
  (:require [getinvolved.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [getinvolved.models.db :as db]))

(defn valid? [username password password_confirmation]
  (vali/rule (vali/has-value? username)
             [:username "username is required"])
  (vali/rule (vali/min-length? password 5)
             [:password "password must be at least 5 characters"])
  (vali/rule (= password password_confirmation)
             [:password_confirmation "entered passwords do not match"])
  (not (vali/errors? :username :password :password_confirmation)))

(defn register [& [username]]
  (layout/render
    "registration.html"
    {:username                    username
     :username-error              (vali/on-error :username first)
     :password-error              (vali/on-error :password first)
     :password-confirmation-error (vali/on-error :password_confirmation first)}))

(defn handle-registration [username password password_confirmation]
  (if (valid? username password password_confirmation)
    (try
      (do
        (db/create-user {:username username
                         :password (crypt/encrypt password)})
        (session/put! :username username)
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:username (.getMessage ex)])
        (register)))
    (register username)))

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

           (POST "/register" [username password password_confirmation]
                 (handle-registration username password password_confirmation))

           (GET "/profile" [] (profile))

           (POST "/update-profile" {params :params} (update-profile params))

           (POST "/login" [id password]
                 (handle-login id password))

           (GET "/logout" []
                (logout)))
