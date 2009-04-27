/**
 *   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion 
 *   Centre InterActive)
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

/**
 * 
 */
package org.esco.ws4Internet2Grouper.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.aspectwerkz.joinpoint.CodeRtti;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;


/**  
 * @author GIP RECIA - A. Deman
 * 17 avr. 2009
 * 
 */
public class ProfilingAspect  {

    /**
     * A profiling entry for a given method.
     * @author GIP RECIA - A. Deman
     * 17 avr. 2009
     */
    class ProfilingEntry {

        /** The number of calls. */
        private int nbCalls;

        /** The max of ellapsed time. */
        private long maxElapsed;

        /** The minimum of ellapsed time. */ 
        private long minElapsed;


        /** The total of ellapsed time. */
        private long totalElapsed;

        /**
         * Builds an instance of ProfilingEntry.
         * @param elapsed The elapsed time during the first call.
         */
        public ProfilingEntry(final long elapsed) {
            nbCalls = 1;
            totalElapsed = elapsed;
            minElapsed = elapsed;
            maxElapsed = elapsed;
        }

        /**
         * Handles a call.
         * @param elapsed The time used by the call.
         */
        public void handleCall(final long elapsed) {
            if (elapsed > maxElapsed) {
                maxElapsed = elapsed;
            } else if (elapsed < minElapsed) {
                minElapsed = elapsed;
            }
            totalElapsed += elapsed;
            nbCalls++;
        }

        /**
         * Getter for nbCalls.
         * @return nbCalls.
         */
        public int getNbCalls() {
            return nbCalls;
        }

        /**
         * Getter for maxElapsed.
         * @return maxElapsed.
         */
        public long getMaxElapsed() {
            return maxElapsed;
        }

        /**
         * Getter for minElapsed.
         * @return minElapsed.
         */
        public long getMinElapsed() {
            return minElapsed;
        }

        /**
         * Getter for totalElapsed.
         * @return totalElapsed.
         */
        public long getTotalElapsed() {
            return totalElapsed;
        }

        /**
         * Gives the average of the elapsed time for the calls.
         * @return The average time.
         */
        public long computeAverageElapsed() {
            return totalElapsed / nbCalls;
        }

        /**
         * Gives the string representation of the entry.
         * @return The string with the values of the entry.
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Calls: " + nbCalls + " total: " + totalElapsed 
            + " min: "  + minElapsed + " max: " + maxElapsed
            + " average: " + computeAverageElapsed();
        }

    }

    /** The max interval to consider before dumping the results in ms. */
    private static final long DUMP_INTERVAL = 10000;
    
    /** The logger to use. */
    private final Logger logger = Logger.getLogger(getClass());

    /** The profiling entries. */
    private Map<String, ProfilingEntry> profilingEntries;


    /** Flag to determine if the profiling is started. */
    private boolean started;
    
    /** The lastTop time the profilers was used. */
    private Long lastTop;
    
    

    /**
     * Construit une instance de ProfilingAspect.
     */
    public ProfilingAspect() {
        super();
    }

    /**
     * Test advice.
     * @param jp The join point.
     */
    public void checkWeaving(final JoinPoint jp) {
        System.out.println("Dans l'aspect callee: " + jp.getCallee().getClass().getSimpleName()); 
        logger.info("Dans l'aspect callee: " + jp.getCallee().getClass().getSimpleName());
        logger.info("Dans l'aspect signature: " + jp.getSignature());
        logger.info("Dans l'aspect signature.getName: " + jp.getSignature().getName());
        logger.info("Dans l'aspect rtti.getName: " + jp.getRtti().getName());
        logger.info("Dans l'aspect target: " + jp.getRtti().getTarget());
    }

    /**
     * Starts to profile if the profiling has not already been started.
     */
    public void startProfilingIfNeeded() {
        if (!started) {
            startProfiling();
        }
    }
    
    /**
     * Starts the profiling.
     */
    public void startProfiling() {
        profilingEntries = new HashMap<String, ProfilingEntry>();
        started = true;
        logger.info(">>>==== Profiling Started ====<<<");
    }
    
    /**
     * Builds the key for a given join point.
     * @param jp The join point.
     * @return The key.
     */
    private String buildKey(final JoinPoint jp) {
        final CodeRtti rtti = (CodeRtti) jp.getRtti();
        String key = rtti.getDeclaringType().getSimpleName() + "." + rtti.getName() + "(";
        boolean addComma = false;
        for (Class< ? > c : rtti.getParameterTypes()) {
            if (addComma) {
                key += ", ";
            } else {
                addComma = true;
            }
            
            key += c.getSimpleName();
        }
        key += ")";
        return key;
        
    }

    /**
     * Profile the call of a method.
     * @param jp The join point.
     * @return The result of the execution of the point.
     * @throws Throwable
     */
    public Object profile(final JoinPoint jp) throws Throwable {
        
        lastTop = System.currentTimeMillis();
        if (started) {
            final long top = System.currentTimeMillis();
            final Object result = jp.proceed();
            final long elapsed = System.currentTimeMillis() - top;

            final String key = buildKey(jp);
            ProfilingEntry entry = profilingEntries.get(key);
            if (entry == null) {
                entry = new ProfilingEntry(elapsed);
                profilingEntries.put(key, entry);
            } else {
                entry.handleCall(elapsed);
            }

            return result;
        }
        return jp.proceed();
    }
    
    /**
     * Display the results and profile.
     * The results are displayed if the interval between two calls of profile is grater thant 
     * DUMP_INTERVAL.
     * @param jp The join point.
     * @return The result of the call of the join point.
     * @throws Throwable
     */
    public Object displayResultsIfNeededBeforeProfiling(final JoinPoint jp) throws Throwable {
        final long newTop = System.currentTimeMillis();
        if (lastTop != null) {
            final long elapsed = newTop - lastTop;
            if (elapsed > DUMP_INTERVAL) {
             displayResult();   
             startProfiling();
            }
        }
        lastTop = newTop;
        return profile(jp);
        
    }

    /**
     * Displays the result of the profiling.
     */
    public void displayResult() {
        logger.info("-------------------------------------------------");
        logger.info(" Profiling");
        logger.info("-------------------------------------------------");
        for (String key : profilingEntries.keySet()) {
            final String result = key + ": " + profilingEntries.get(key);
            System.out.println(result);
            logger.info(result);
        }
        logger.info("-------------------------------------------------");
    }
}