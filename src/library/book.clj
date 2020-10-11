(ns library.book
  (:require [library.specs :as specs]
            [cats.monad.either :as either]))

(defn book-on-hold [book-hold]
  (either/right (assoc-in book-hold [:book ::specs/book-state] :on-hold)))
