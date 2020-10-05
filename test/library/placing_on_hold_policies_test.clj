(ns library.placing-on-hold-policies-test
  (:require [library.placing-on-hold-policies :as sut]
            [clojure.test :refer [deftest testing is]]
            [mock-clj.core :as mock-clj]
            [library.specs :as specs]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [cats.monad.either :as either]))

(def book (gen/generate (s/gen ::specs/book)))

;; (def branch-ids (gen/sample (s/gen ::specs/branch-id) 3))
;; (def overdue-checkouts (gen/sample (gen/elements branch-ids) 5) )

(def regular-patron (gen/generate (s/gen ::specs/patron)))

(def patron-with-overdue-checkouts (gen/fmap #(-> %
                                                  (assoc ::spec/overdue-checkouts (repeat 2 (s/conform ::spec/checkout book))))
                                             (s/gen ::spec/patron)))

(deftest overdue-checkouts-rejection-test
  (testing "should reject if checkouts are overdue in a given branch"
    (let [book-hold {:book   book
                     :patron (gen/generate patron-with-overdue-checkouts)}]
      (is (either/left? (sut/overdue-checkouts-rejection book-hold))))))

(deftest regular-patron-maximum-number-of-holds-test
  (testing "should reject if checkouts are overdue in a given branch"
    (let [book-hold {:book   book
                     :patron (merge regular-patron {::specs/holds (gen/sample (s/gen ::specs/hold) 5)})}]
      (is (either/left? (sut/regular-patron-maximum-number-of-holds book-hold))))))

(deftest only-research-patron-can-hold-restricted-book-test
  (testing "should reject if regular patron tries to hold restricted book"
    (let [book-hold {:book   (merge book {::specs/type :restricted})
                     :patron regular-patron}]
      (is (either/left? (sut/only-research-patron-can-hold-restricted-book book-hold))))))
