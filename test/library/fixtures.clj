(ns library.fixtures
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [library.specs :as specs]))


(def book (s/gen ::specs/book))

(def as-circulating-book (partial gen/fmap #(assoc % ::specs/type :circulating)))

(def as-available (partial gen/fmap #(assoc % ::specs/book-state :available)))

(def as-restricted-book (partial gen/fmap #(assoc % ::specs/type :restricted)))

(def patron (s/gen ::specs/patron))

(def as-regular-patron (partial gen/fmap #(assoc % ::specs/patron-type :regular)))

(def as-research-patron (partial gen/fmap #(assoc % ::specs/patron-type :research)))


(def with-2-holds (partial gen/fmap #(assoc % ::specs/holds (gen/sample (s/gen ::specs/hold) 2))))

(def with-no-overdue-checkouts (partial gen/fmap #(assoc % ::specs/overdue-checkouts [])))
