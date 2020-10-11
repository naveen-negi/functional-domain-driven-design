(ns library.placing-on-hold-policies
  (:require [cats.monad.either :as either]
            [library.specs :as specs]))

(defn regular-patron-maximum-number-of-holds [{:keys [book patron] :as book-hold}]
  (if (= :regular (::specs/patron-type book))
    (if (< (count (::specs/holds patron)) 5)
      (either/right book-hold)
      (either/left [:exceeded-number-of-allowed-hold])))
  (either/right book-hold))

(defn only-research-patron-can-hold-restricted-book [book-hold]
  (if (or (= (get-in book-hold [:patron ::specs/patron-type]) :research)
          (= (get-in book-hold [:book ::specs/type]) :circulating) )
    (either/right book-hold)
    (either/left [:regular-patron-not-allowed-to-hold-on-restricted-book])))

(defn overdue-checkouts-rejection [book-hold]
  (let [branch-id         (get-in book-hold [:book ::specs/branch-id])
        overdue-checkouts (->>  (get-in book-hold [:patron ::specs/overdue-checkouts])
                                (filter  #(= branch-id (::specs/branch-id %) ))
                                count)]
    (if (>= overdue-checkouts 2)
      (either/left [:books-overdue-at-requested-libary-branch])
      (either/right book-hold))))
