(ns inplus.core-test
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cemerick.cljs.test :refer-macros (is deftest testing)])
            [inplus.core :as in+]))


(def ^:private tstobj {:a {:b [{:id :gg} {:id :ff :c 1}]}})

(deftest get-in+-test
  (testing "null path"
    (is (= (get-in tstobj [])           (in+/get-in+ tstobj []))))
  (testing "normal paths"
    (is (= (get-in tstobj [:a])         (in+/get-in+ tstobj [:a])))
    (is (= (get-in tstobj [:a :b])      (in+/get-in+ tstobj [:a :b]))))
  (testing "with a matching path"
    (is (= (get-in tstobj [:a :b 0])    (in+/get-in+ tstobj [:a :b {:id :gg}]) {:id :gg}))
    (is (= (get-in tstobj [:a :b 1 :c]) (in+/get-in+ tstobj [:a :b {:id :ff} :c]) 1))))

(deftest update-in+-test
  (testing "null path"
    (is (= (assoc tstobj :x :y)
           (in+/update-in+ tstobj [] assoc :x :y))))
  (testing "simple paths"
    (is (= (update-in tstobj [:a] assoc :x :y)
           (in+/update-in+ tstobj [:a] assoc :x :y)))
    (is (= (update-in tstobj [:a :b] assoc 2 :y)
           (in+/update-in+ tstobj [:a :b] assoc 2 :y)))
    (is (= (update-in tstobj [:a :z :zzz] assoc :x :y)
           (in+/update-in+ tstobj [:a :z :zzz] assoc :x :y))))
  (testing "matching paths"
    (is (= (update-in tstobj [:a :b 0] assoc :x :y)
           (in+/update-in+ tstobj [:a :b {:id :gg}] assoc :x :y)))
    (is (= (update-in tstobj [:a :b 1 :c] inc)
           (in+/update-in+ tstobj [:a :b {:id :ff} :c] inc)))
    (is (= (update-in tstobj [:a :b] conj {:id :mm :c 1})
           (in+/update-in+ tstobj [:a :b {:id :mm}] assoc :c 1)))
    (is (= {:a [{:x :y :z :z}]}
           (in+/update-in+ {} [:a {:x :y}] assoc :z :z)))))

(deftest assoc-in+-test
  (testing "null path"
    (is (= (assoc-in tstobj [] :y)
           (in+/assoc-in+ tstobj [] :y))))
  (testing "simple paths"
    (is (= (assoc-in tstobj [:a] :y)
           (in+/assoc-in+ tstobj [:a] :y)))
    (is (= (assoc-in tstobj [:a :z] :y)
           (in+/assoc-in+ tstobj [:a :z] :y)))
    (is (= (assoc-in tstobj [:a :z :zz :zzz] :y)
           (in+/assoc-in+ tstobj [:a :z :zz :zzz] :y)))
    (is (= (assoc-in tstobj [:a :b] :y)
           (in+/assoc-in+ tstobj [:a :b] :y))))
  (testing "matching paths"
    (is (= (assoc-in tstobj [:a :b 0] :y)
           (in+/assoc-in+ tstobj [:a :b {:id :gg}] :y)))
    (is (= (assoc-in tstobj [:a :b 1 :c] inc)
           (in+/assoc-in+ tstobj [:a :b {:id :ff} :c] inc)))
    (is (= (update-in tstobj [:a :b] conj {:id :mm :x :y})
           (in+/assoc-in+ tstobj [:a :b {:id :mm} :x] :y)))))

;; TODO add dissoc-in+-test
(deftest contains?+-test
  (testing "null path"
    (is (true? (in+/contains?+ tstobj []))))
  (testing "normal paths"
    (is (true? (in+/contains?+ tstobj [:a])))
    (is (true? (in+/contains?+ tstobj [:a :b]))))
  (testing "with a matching path"
    (is (true? (in+/contains?+ tstobj [:a :b {:id :gg}])))
    (is (true? (in+/contains?+ tstobj [:a :b {:id :ff} :c]))))
  (testing "not contains"
    (is (false? (in+/contains?+ tstobj [:c])))
    (is (false? (in+/contains?+ tstobj [:a :b {:id :xx}])))))
