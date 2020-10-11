(ns library.workflow-with-applicative
  (:require [library.common.result :as result]
            [library.placing-on-hold-policies :as policies]
            [cats.core :as m]
            [library.db :as db]
            [library.book :as book]
            [library.patron :as patron]
            [cats.monad.either :as either]))

(defn book-hold [book patron]
  {:book   book
   :patron patron})

(defn to-book-hold [find-book find-patron {:keys [book-id patron-id]}]
  (m/ap book-hold (find-book book-id) (find-patron patron-id)))

(defn workflow-result [book patron]
  {:book   book
   :patron patron})

(defn handle-success [update-book update-patron {:keys [book patron]}]
  (m/ap workflow-result (update-book book) (update-patron patron)))

(defn place-book-on-hold [request]
  (m/->>= (to-book-hold db/find-book db/find-patron request)
          patron/place-hold
          book/book-on-hold
          (handle-success db/update-book db/update-patron)))
