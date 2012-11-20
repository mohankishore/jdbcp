/**
 * Copyright 2012 Apple, Inc
 * Apple Internal Use Only
 **/


package com.github.mohankishore.jdbcp.pool;

import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayBasedPool<T> {
    private PooledObject<T>[] array;
    private Object WAIT_MONITOR = new Object();
    
    public ArrayBasedPool(T[] actualObjects) {
        this.array = new PooledObject[actualObjects.length];
        for (int i=0; i < array.length; i++) {
            array[i] = new PooledObject<T>(actualObjects[i]);
        }
    }
    
    public T borrowObject() {
        int start = Thread.currentThread().hashCode() % array.length;
        for (int j=start; j < array.length; j++) {
            boolean lock = array[j].getCheckedOut().compareAndSet(false, true);
            if (lock) {
                return array[j].getActualObject();
            } else {
                if (j == start - 1) {
                    // one complete cycle complete
                    synchronized (WAIT_MONITOR) {
                        try {
                            WAIT_MONITOR.wait(WAIT_TIMEOUT);
                            for (int i=0; i < array.length; i++) {
                                lock = array[i].getCheckedOut().compareAndSet(false, true);
                                if (lock) {
                                    return array[i].getActualObject();
                                }
                            }
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    throw new RuntimeException("Could not find a free connection");
                } else if (j == array.length - 1) {
                    j = -1;
                }
            }
        }
        throw new RuntimeException("should never get here");
    }
    
    public void returnObject(T t) {
        int start = Thread.currentThread().hashCode() % array.length;
        for (int j=start; j < array.length; j++) {
            if (array[j].getActualObject() == t) {
                boolean lock = array[j].getCheckedOut().compareAndSet(true, false);
                if (lock) {
                    synchronized (WAIT_MONITOR) {
                        WAIT_MONITOR.notify();
                    }
                    return;
                } else {
                    throw new RuntimeException("Could not mark the object as returned");
                }
            } else {
                if (j == start - 1) {
                    // one complete cycle complete
                    throw new RuntimeException("The object you are trying to return does not belong to the pool");
                } else if (j == array.length - 1) {
                    j = -1;
                }
            }
        }
        throw new RuntimeException("should never get here");
    }
    
    private static class PooledObject<T> {
        private T actualObject;
        private AtomicBoolean checkedOut = new AtomicBoolean(false);
        
        public PooledObject(T t) {
            this.actualObject = t;
        }

        public T getActualObject() {
            return actualObject;
        }

        public AtomicBoolean getCheckedOut() {
            return checkedOut;
        }
        
    }
    
    private static class Worker extends Thread {
        private ArrayBasedPool<Integer> pool;
        private long[] stats = new long[6];
        /*
         * - errorOnBorrow
         * - sumBorrowTime
         * - countBorrowTime
         * - errorOnReturn
         * - sumReturnTime
         * - countReturnTime
         */
        
        public Worker(ArrayBasedPool<Integer> pool) {
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
        
        ArrayBasedPool<Integer> pool = new ArrayBasedPool<Integer>(actualObjects);
        
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
