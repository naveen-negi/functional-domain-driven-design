(ns library.workflow
  (:require [clojure.spec.alpha :as s]))

(comment
  ;; Problem statement
  ;; User need to place a book on hold before checking it out
  ;; Before a hold is placed, we need to make sure that user and book exists)

(s/def :patron/patron-id string?)
(s/def :patron/number-of-holds (s/int-in 0 5))

(s/def ::patron (s/keys :req [:patron/patron-id :patron/number-of-holds]))

(s/def :book/book-id string?)
(s/def ::book (s/keys :req [:book/book-id]))

(defn find-book [book-id]
  (gen/generate (s/gen ::book)))

(defn find-patron [patron-id]
  (gen/generate (s/gen ::patron)))


(defn book-hold [book patron]
  {:book   book
   :patron patron})

(defn validate-book-hold [{:keys [book-id patron-id]}]
  (let [book   (find-book book-id)
        patron (find-patron patron-id)]
    (cond
      (and (nil? book) (nil? patron)) {:errors [:book-not-found :patron-not-found]}
      (nil? patron)                   {:errors [:patron-not-found]}
      (nil? book)                     {:errors [:book-not-found]}
      :else                           {:success (book-hold book patron)})))

















#_(defn place-book-on-hold [request]
    (let [validated-book-hold (validate-book-hold request)]
      (if (:success validated-book-hold)
        (if (< (get-in validated-book-hold [:success :patron :patron/number-of-holds]) 5)
          (update-in validated-book-hold [:success :patron :patron/number-of-holds] inc)
          {:errors [:exceeded-number-of-allowed-hold]})
        validated-book-hold)))
