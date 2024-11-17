;; copied from clojure/test/clojure/test_clojure/edn.clj

;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

; Author: Stuart Halloway


(ns better-clojure.edn-gen-test
  (:require
   [better-clojure.generators :as cgen]
   [better-clojure.edn :as edn]
   [clojure.test :refer [deftest is are testing]]
   [clojure.test.generative :refer [defspec]]
   [clojure.test.generative.runner :as runner]))

(defn roundtrip
  "Print an object and read it back as edn. Returns rather than throws
   any exceptions."
  [o]
  (binding [*print-length* nil
            *print-dup* nil
            *print-level* nil]
    (try
      (-> o pr-str edn/read-string)
      (catch Throwable t t))))

(defspec types-that-should-roundtrip
  roundtrip
  [^{:tag cgen/ednable} o]
  (when-not (= o %)
    (throw (ex-info "Value cannot roundtrip, see ex-data" {:printed o :read %}))))

(defspec types-that-should-not-roundtrip
  roundtrip
  [^{:tag cgen/non-ednable} o]
  (when-not (instance? Throwable %)
    (throw (ex-info "edn/read should have thrown, see ex-data" {:printed o :read %}))))

(def cpus
  8)

(def time-ms
  10000)

(deftest roundtrip-test
  (runner/run cpus time-ms #'better-clojure.edn-gen-test/types-that-should-roundtrip))

(deftest no-roundtrip-test
  (runner/run cpus time-ms #'better-clojure.edn-gen-test/types-that-should-not-roundtrip))

(comment
  (try
    (runner/run 10 10000 #'better-clojure.edn-gen-test/types-that-should-roundtrip)
    (catch clojure.lang.ExceptionInfo e
      (:input (ex-data e)))))
