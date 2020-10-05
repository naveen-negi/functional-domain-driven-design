(ns library.placing-on-hold-policies
  (:require [cats.monad.either :as either]
            [library.specs :as specs]))

(defn regular-patron-maximum-number-of-holds [book-hold]
  (if (and (= :regular (get-in book-hold [:patron ::patron-type]) )
           (< (count (get-in book-hold [:patron ::holds]) ) 5) )
    (either/right book-hold)
    (either/left [:exceeded-number-of-allowed-hold])))

(defn only-research-patron-can-hold-restricted-book [book-hold]
  (if (and (= (get-in book-hold [:patron ::patron-type]) :research)
           (= (get-in book-hold [:book ::book-type]) :restricted) )
    (either/right book-hold)
    (either/left [:regular-patron-not-allowed-to-hold-on-restricted-book])))

(defn overdue-checkouts-rejection [book-hold]
  (clojure.pprint/pprint book-hold)
  (let [branch-id         (get-in book-hold [:book ::specs/branch-id])
        overdue-checkouts (->>  (get-in book-hold [:patron ::specs/overdue-checkouts])
                                (filter  #(= branch-id (::specs/branch-id %) ))
                                count)]
    (if (>= overdue-checkouts 2)
      (either/left [:books-overdue-at-requested-libary-branch])
      (either/right book-hold))))
