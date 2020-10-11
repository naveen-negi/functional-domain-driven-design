(ns library.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;;Patron specs
(s/def ::patron-id uuid?)

(s/def ::number-of-holds (s/int-in 0 5))

(s/def ::book-id uuid?)
(s/def ::branch-id uuid?)

(s/def ::hold (s/keys :req [::branch-id ::book-id]))
(s/def ::holds (s/coll-of ::hold :distinct true :into []))

(s/def ::checkout (s/keys :req [::branch-id ::book-id]))
(s/def ::overdue-checkouts (s/coll-of ::checkout :distinct true :into []))

(s/def ::patron-type #{:regular :research})

(s/def ::patron (s/keys :req [::patron-id ::holds ::overdue-checkouts ::patron-type]))

;;Book specs
(s/def ::type #{:restricted :circulating})
(s/def ::book-state #{:available :on-hold :checked-out})
(s/def ::book (s/keys :req [::book-id ::type ::branch-id ::book-state]))
