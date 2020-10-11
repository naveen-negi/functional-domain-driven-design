(ns library.workflow
  (:require [library.workflow-with-applicative :as sut]
            [library.specs :as specs]
            [library.fixtures :as f]
            [library.db :as db]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is testing]]
            [clojure.spec.alpha :as s]
            [cats.monad.either :as either]
            [cats.core :as m]
            [clojure.set :as set]))


(deftest place-book-on-hold-test
  (testing "patron should be able to put book on hold"
    (let [book              (gen/generate (-> f/book  f/as-circulating-book f/as-available) )
          patron            (gen/generate (-> f/patron f/as-research-patron f/with-2-holds f/with-no-overdue-checkouts))
          book-id           (db/add-new-book book)
          patron-id         (db/add-new-patron patron)
          book-hold-request {:book-id book-id :patron-id patron-id}]
      (let [{actual-patron :patron actual-book :book} (m/extract (sut/place-book-on-hold book-hold-request))]
        (is (< (count (::specs/holds patron))  (count (::specs/holds actual-patron) )))
        (is (set/subset? (::specs/holds book)  (::specs/holds actual-patron)))
        (is (= :on-hold  (::specs/book-state actual-book))))))

  (testing "should return error if regular patron tries to place hold on restricted book"
    (let [book              (db/add-new-book (gen/generate (-> f/book f/as-restricted-book f/as-available) ))
          patron            (db/add-new-patron (gen/generate (-> f/patron f/as-regular-patron)))
          book-hold-request {:book-id book :patron-id patron}]
      (is (= [:regular-patron-not-allowed-to-hold-on-restricted-book] (m/extract (sut/place-book-on-hold book-hold-request) )))))

  (testing "should return error if patron and book don't exist"
    (let [book-hold-request {:book-id (gen/generate (s/gen ::specs/book-id)) :patron-id (gen/generate (s/gen ::specs/patron-id))}]
      (is (= [:book-not-found] (m/extract (sut/place-book-on-hold book-hold-request) ))))))
