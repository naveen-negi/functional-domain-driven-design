(ns library.common.result
  (:require [cats.protocols :as protocol]
            [cats.core :as m]
            [cats.builtin :as b]
            [cats.monad.either :as either]))


(defrecord Success [success])
(defrecord Failure [errors])

(defn failure [errors]
  (Failure. errors))

(defn success [success]
  (Success. success))

(success 1)
(failure [:error])


(+ 1 2)

(m/ap + (success 1) (success 2))

(m/ap + (failure [:not-a-number]) (success 2))

(m/ap + (failure [:not-a-number]) (failure [:some-other-error]))









(defn failure? [failure]
  (not (nil? (:errors failure))))

(defn success? [success]
  (not (nil? (:success success))))
























(defn either-of [result]
  (if (failure? result)
    (either/left (:errors result))
    (either/right (:success result))))

(def
  context
  (reify
    protocol/Context

    protocol/Monoid
    (-mempty [s] (Failure. []))

    protocol/Semigroup
    (-mappend
        [s sv sv']
      (Failure. (concat (:errors sv) (:errors sv'))))

    protocol/Functor
    (-fmap [_ f mv]
      (cond
        (failure? mv) mv
        :else         (Success. (f (:success mv)))))

    protocol/Applicative
    (-fapply [_ af av]
      (cond
        (and (failure? af)
             (failure? av)) (m/mappend af av)

        (and (failure? af)
             (success? av)) af

        (and (success? af)
             (failure? av)) av

        :else (m/fmap (:success af) av)))

    (-pure [_ v]
      (failure v))))

(extend-protocol protocol/Contextual
  Failure
  (-get-context [_] context)
  Success
  (-get-context [_] context))

(extend-protocol protocol/Extract
  Success
  (-extract [mv]
    (into {} mv))

  Failure
  (-extract [mv]
    (into {} mv)))
