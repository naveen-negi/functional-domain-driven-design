(ns library.workflow-with-applicative
  (:require [library.common.result :as result]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [library.placing-on-hold-policies :as policies]
            [cats.core :as m]
            [library.specs :as specs]
            [cats.builtin :refer :all]
            [cats.monad.either :as either]))


;;specs
;;faking db calls
(defn find-book [book-id]
  (gen/generate (s/gen  ::specs/book)))

(defn find-patron [patron-id]
  (gen/generate (s/gen ::specs/patron)))

;;domain entity for book-hold
(defn book-hold [book patron]
  {:book   book
   :patron patron})

(defn patron [patron-id]
  (if-let [patron (find-patron patron-id)]
    (result/success patron)
    (result/failure [:patron-not-found])))

(defn book [book-id]
  (if-let [book (find-book book-id)]
    (result/success book)
    (result/failure [:book-not-found])))

(defn to-book-hold [{:keys [book-id patron-id]}]
  (result/either-of (m/ap book-hold (book book-id) (patron patron-id)) ))

(defn place-hold [book-hold]
  (let [holds (get-in book-hold [:patron :patron/holds])]
    (assoc book-hold [:patron :patron/holds] (conj holds (:book book-hold)))))

(defn place-book-on-hold [request]
  (m/->= (to-book-hold request)
         policies/regular-patron-maximum-number-of-hold
         policies/only-research-patron-can-hold-restricted-book
         policies/overdue-checkouts-rejection
         place-hold))

@(place-book-on-hold {:book-id "book-id" :patron-id "patron-id"} )
