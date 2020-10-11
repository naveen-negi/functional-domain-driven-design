(ns library.db
  (:require [library.specs :as specs]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [library.common.result :as result]
            [cats.monad.either :as either]
            [cats.core :as m]))

(def books (atom {}))
(def patrons (atom {}))

(defn find-patron [patron-id]
  (if-let [patron (get @patrons patron-id)]
    (either/right patron)
    (either/left [:patron-not-found])))

(defn find-book [book-id]
  (if-let [book (get @books book-id)]
    (either/right book)
    (either/left [:book-not-found])))

(defn save-book [book]
  (swap! books assoc (str (::specs/book-id book) ) book))

(defn- update-if-available? [book-to-update original-book]
  (prn (::specs/book-state original-book))
  (if (= :available (::specs/book-state original-book))
    (do
      (swap! books assoc (str (::specs/book-id book-to-update) ) book-to-update)
      (either/right book-to-update))
    (either/left [:duplicate-hold-found-for-book])))

(defn update-book [book]
  (m/->>= (find-book (str (::specs/book-id book)))
          (update-if-available? book )))

(defn save-patron [patron]
  (swap! patrons assoc (str (::specs/patron-id  patron) ) patron))

(defn update-patron [patron]
  (swap! patrons assoc (str (::specs/patron-id  patron) ) patron)
  (either/right patron))

(defn add-new-book
  ([]
   (let [book (gen/generate (s/gen ::specs/book))]
     (save-book book)
     (str (::specs/book-id book))) )
  ([book]
   (save-book book)
   (str (::specs/book-id book))))

(defn add-new-patron
  ([]
   (let [patron (gen/generate (s/gen ::specs/patron))]
     (add-new-patron patron)
     (str (::specs/patron-id patron))) )
  ([patron]
   (save-patron patron)
   (str (::specs/patron-id patron))))
