(ns library.workflow-with-applicative
  (:require [library.common.result :as result]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [cats.core :as m]
            [cats.builtin :refer :all]
            [cats.monad.either :as either]))


;;specs
(s/def :patron/patron-id string?)
(s/def :patron/number-of-holds (s/int-in 0 5))

(s/def ::patron (s/keys :req [:patron/patron-id :patron/number-of-holds]))

(s/def :book/book-id string?)
(s/def ::book (s/keys :req [:book/book-id]))

;;faking db calls
(defn find-book [book-id]
  nil)

(defn find-patron [patron-id]
  nil)

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

(defn validate-book-hold [{:keys [book-id patron-id]}]
  (m/ap book-hold (book book-id) (patron patron-id)))

(validate-book-hold {:patron-id "patron-id" :book-id "book-id"})


























(validate-book-hold {:book-id "" :patron-id ""})


















#_(defn validate-number-of-book-holds [{:keys [patron] :as book-hold}]
    (if (< (:patron/number-of-holds patron) 5)
      (either/right (update-in book-hold [:patron :patron/number-of-holds] inc))
      (either/left [:exceeded-number-of-allowed-hold])))

#_(defn place-book-on-hold [request]
    (m/extract (m/->= (validate-book-hold request)
                      validate-number-of-book-holds) ))




#_(place-book-on-hold {:book-id "" :patron-id ""})
