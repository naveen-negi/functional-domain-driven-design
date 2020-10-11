(ns library.patron
  (:require [cats.monad.either :as either]
            [library.specs :as specs]
            [cats.core :as m]
            [library.placing-on-hold-policies :as policies]))

(defn- update-book-hold [{:keys [patron] :as book-hold}]
  (either/right (assoc-in book-hold [:patron ::specs/holds] (conj (::specs/holds patron) (:book book-hold))) ))

(defn- available? [{:keys [book] :as book-hold}]
  (if (= :available (::specs/book-state book))
    (either/right book-hold)
    (either/left [:book-not-available-for-hold])))

(defn place-hold [book-hold]
  (m/->= (available? book-hold)
         policies/regular-patron-maximum-number-of-holds
         policies/only-research-patron-can-hold-restricted-book
         policies/overdue-checkouts-rejection
         update-book-hold))
