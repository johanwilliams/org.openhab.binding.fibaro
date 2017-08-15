/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal;

import java.util.ArrayList;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

/**
 *
 * @author Crunchify.com
 *
 */
public class InMemoryCache<K, T> {

    private long timeToLive;
    private LRUMap crunchifyCacheMap;

    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public T value;

        protected CacheObject(T value) {
            this.value = value;
        }
    }

    public InMemoryCache(long crunchifyTimeToLive, final long crunchifyTimerInterval, int maxItems) {
        this.timeToLive = crunchifyTimeToLive * 1000;

        crunchifyCacheMap = new LRUMap(maxItems);

        if (timeToLive > 0 && crunchifyTimerInterval > 0) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(crunchifyTimerInterval * 1000);
                        } catch (InterruptedException ex) {
                        }
                        cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    public void put(K key, T value) {
        synchronized (crunchifyCacheMap) {
            crunchifyCacheMap.put(key, new CacheObject(value));
        }
    }

    @SuppressWarnings({ "unchecked", "null" })
    public T get(K key) {
        synchronized (crunchifyCacheMap) {
            CacheObject c = (CacheObject) crunchifyCacheMap.get(key);

            if (c == null) {
                return null;
            } else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (crunchifyCacheMap) {
            crunchifyCacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (crunchifyCacheMap) {
            return crunchifyCacheMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (crunchifyCacheMap) {
            MapIterator itr = crunchifyCacheMap.mapIterator();

            deleteKey = new ArrayList<K>((crunchifyCacheMap.size() / 2) + 1);
            K key;
            CacheObject c;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheObject) itr.getValue();

                if (c != null && (now > (timeToLive + c.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (crunchifyCacheMap) {
                crunchifyCacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}
