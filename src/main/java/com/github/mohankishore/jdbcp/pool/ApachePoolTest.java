/**
 * Copyright 2012 Apple, Inc
 * Apple Internal Use Only
 **/


package com.github.mohankishore.jdbcp.pool;

import org.apache.commons.pool.impl.GenericObjectPool;

public class ApachePoolTest<T> {
    private static class Worker extends Thread {
        private GenericObjectPool<Integer> pool;
        private long[] stats = new long[6];
        /*
         * - errorOnBorrow
         * - sumBorrowTime
         * - countBorrowTime
         * - errorOnReturn
         * - sumReturnTime
         * - countReturnTime
         */
        
        public Worker(GenericObjectPool<Integer> pool) {
            this.pool = pool;
        }
        
        @Override
        public void run() {
            long start = 0L;
            Integer obj = null;
            for (int i=0; i < ITERS; i++) {
                try {
                    start = System.nanoTime();
                    obj = pool.borrowObject();
                } catch(Exception e) {
                    stats[0] += 1;
                    continue;
                } finally {
                    stats[1] += (System.nanoTime() - start);
                    stats[2] += 1;
                }
                try {
                    Thread.sleep(SLEEP_WITH_OBJ);
                } catch(Exception e) {
                    // ignore
                }
                try {
                    start = System.nanoTime();
                    pool.returnObject(obj);
                } catch(Exception e) {
                    stats[3] += 1;
                } finally {
                    stats[4] += (System.nanoTime() - start);
                    stats[5] += 1;
                }
                try {
                    Thread.sleep(SLEEP_WITHOUT_OBJ);
                } catch(Exception e) {
                    // ignore
                }
            }
        }
        
        public long[] getStats() {
            return stats;
        }
    }
    
    private static final int OBJS = 25;
    private static final int THREADS = 200;
    private static final int ITERS = 1000;
    private static final int SLEEP_WITH_OBJ = 10;
    private static final int SLEEP_WITHOUT_OBJ = 100;
    private static final int WAIT_TIMEOUT = 1;

    public static void main(String[] args) {
        Integer[] actualObjects = new Integer[OBJS];
        for (int i=0; i < actualObjects.length; i++) {
            actualObjects[i] = new Integer(i);
        }
        
        GenericObjectPool<Integer> pool = new GenericObjectPool<Integer>(new org.apache.commons.pool.PoolableObjectFactory<Integer>() {
            public void activateObject(Integer obj) throws Exception {
            }
            public void destroyObject(Integer obj) throws Exception {
            }
            public Integer makeObject() throws Exception {
                return new Integer(1);
            }
            public void passivateObject(Integer obj) throws Exception {
            }
            public boolean validateObject(Integer obj) {
                return true;
            }
        });
        pool.setMaxActive(OBJS);
        pool.setMaxIdle(-1);
        pool.setMaxWait(WAIT_TIMEOUT);
        pool.setMinEvictableIdleTimeMillis(-1);
        pool.setTimeBetweenEvictionRunsMillis(-1);
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        
        Worker[] workers = new Worker[THREADS];
        for (int i=0; i < workers.length; i++) {
            workers[i] = new Worker(pool);
            workers[i].start();
        }
        
        long[] stats = new long[6];
        for (int i=0; i < workers.length; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                // ignore
            }
            for (int j=0; j < 6; j++) {
                stats[j] += workers[i].getStats()[j];
            }
        }
        System.out.println("Borrow stats: ");
        System.out.println("- error (%): " + (stats[0] * 100) / (THREADS * ITERS));
        System.out.println("- time (ns): " + stats[1] / stats[2]);
        System.out.println("Return stats: ");
        System.out.println("- error (%): " + (stats[3] * 100) / (THREADS * ITERS));
        System.out.println("- time (ns): " + stats[4] / stats[5]);
    }
}
