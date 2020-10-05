(ns library.workflow
  (:require [library.workflow-with-applicative :as sut]
            [mock-clj.core :as mock-clj]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is testing]]
            [clojure.spec.alpha :as s]
            [library.common.result :as result]
            [cats.monad.either :as either]))


(deftest place-book-on-hold-test
  (testing "patron should be able to put book on hold"
    (mock-clj/with-mock [sut/find-book (gen/generate (s/gen ::sut/book))
                         sut/find-patron (gen/generate (s/gen ::sut/patron))]
      (prn (sut/place-book-on-hold {:book-id "some-book-id" :patron-id "some-patron-id"}) )
      (is (either/right? (sut/place-book-on-hold {:book-id "some-book-id" :patron-id "some-patron-id"})))))

  (testing "should return error if patron doesn't exist"
    (mock-clj/with-mock [sut/find-book (gen/generate (s/gen ::sut/book))
                         sut/find-patron nil]
      (is (either/left?  (sut/place-book-on-hold {:book-id "some-book-id" :patron-id "some-patron-id"})))))

  (testing "should return error if patron and book don't exist"
    (mock-clj/with-mock [sut/find-book  nil
                         sut/find-patron nil]
      (is (either/left? (sut/place-book-on-hold {:book-id "some-book-id" :patron-id "some-patron-id"}))))))
