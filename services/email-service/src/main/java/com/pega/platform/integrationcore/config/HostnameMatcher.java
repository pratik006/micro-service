/*
 * $Id$
 *
 * Copyright (c) 2017  Pegasystems Inc.
 * All rights reserved.
 *
 * This  software  has  been  provided pursuant  to  a  License
 * Agreement  containing  restrictions on  its  use.   The  software
 * contains  valuable  trade secrets and proprietary information  of
 * Pegasystems Inc and is protected by  federal   copyright law.  It
 * may  not be copied,  modified,  translated or distributed in  any
 * form or medium,  disclosed to third parties or used in any manner
 * not provided for in  said  License Agreement except with  written
 * authorization from Pegasystems Inc.
*/

package com.pega.platform.integrationcore.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import com.pega.pegarules.priv.LogHelper;
import com.pega.pegarules.priv.ModuleVersion;

/**
 * HostnameMatcher
 * 
 * A caching replacement for the JVM-internal import sun.misc.RegexpPool
 * that was used for checking hostnames against the "http.nonProxyHosts" system parameter
 * <br/>
 * This is a generic matcher, but the {@link #HostnameMatcher(String)} expects a value like that of "http.nonProxyHosts"
 * 
 * @version		$Revision$ $Date$
 * @author Jeff Houle
 *
 */
public class HostnameMatcher {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//	public static final String VERSION = "$Id:$";
	public static final String VERSION = ModuleVersion.register("$Id:$");
	private static final LogHelper oLog = new LogHelper(HostnameMatcher.class);
	
	static final int MAX_CACHE_SIZE = 500;
	static final int STARTING_CACHE_SIZE = MAX_CACHE_SIZE / 10;
	
	private Set<String> matchCache = new LRUSet<String>();
	private Set<String> nonMatchCache = new LRUSet<String>();
	
	/** Pattern(s) used to check hostname values */
	private Set<Pattern> patternSet = new HashSet<Pattern>();
	
	private boolean mCaseSensitive = false;
	
	/**
	 * LRUSet
	 * A Set that applies a Least Recently Used policy. The maximum size is {@link HostnameMatcher#MAX_CACHE_SIZE}
	 * @version		$Revision$ $Date$
	 * @author Jeff Houle
	 *
	 * @param <V> - the type of the values in the Set
	 */
	private static class LRUSet<V> implements Set<V>
	{
		/**
		 * LRUCache
		 * The internal Map for the LRUSet
		 * Keys are the data.
		 * Values are negligible.
		 * 
		 * @version		$Revision$ $Date$
		 * @author Jeff Houle
		 *
		 * @param <V> the type of the values in the Map
		 */
		private static class LRUCache<V> extends LinkedHashMap<V, Boolean> {
		
			private static final long serialVersionUID = 1L;

			public LRUCache() {
				super(STARTING_CACHE_SIZE, (float) 0.75, true);
			}

			protected boolean removeEldestEntry(Map.Entry<V, Boolean> eldest) {
				return size() >= MAX_CACHE_SIZE;
			}
		}
		
		/** LRUCache that backs the LRU Set */
		private final LRUCache<V> internalCache = new LRUCache<V>();
		
		/**
		 * Default constructor
		 */
		public LRUSet()
		{
			// nothing to do
		}
		
		@Override
		public int size() {
			return internalCache.size();
		}
		
		@Override
		public boolean isEmpty() {
			return internalCache.isEmpty();
		}
		
		@Override
		public boolean contains(Object aO) {
			return internalCache.containsKey(aO);
		}
		
		@Override
		public Iterator<V> iterator() {
			return internalCache.keySet().iterator();
		}
		
		@Override
		public Object[] toArray() {
			return internalCache.keySet().toArray();
		}
		
		@Override
		public <T> T[] toArray(T[] aA) {
			return internalCache.keySet().toArray(aA);
		}
		
		@Override
		public boolean add(V aE) {
			
			if (null == aE)
			{
				return false;
			}
			
			Object old = internalCache.put(aE, Boolean.TRUE);
			return old == null;
		}
		
		@Override
		public boolean remove(Object aO) {
			if (null == aO)
			{
				return false;
			}
			
			Object old = internalCache.remove(aO);
			return old != null;
		}
		
		@Override
		public boolean containsAll(Collection<?> aC) {
			return internalCache.keySet().containsAll(aC);
		}
		
		@Override
		public boolean addAll(Collection<? extends V> aC) {
			
			boolean added = false;
			if (null == aC)
			{
				return false;
			}
			
			for (V v: aC)
			{
				added = this.add(v) || added;
			}
			
			return added;
		}
		
		@Override
		public boolean retainAll(Collection<?> aC) {
			
			boolean removed = false;
			if (aC == null || aC.isEmpty())
			{
				boolean empty = this.isEmpty();
				this.clear();
				
				return ! empty;
			}
			
			for (V key: internalCache.keySet())
			{
				if (! aC.contains(key))
				{
					removed = this.remove(key) || removed;
				}
			}
			
			return removed;
		}
		
		@Override
		public boolean removeAll(Collection<?> aC) 
		{
			boolean removed = false;
			if (null == aC)
			{
				return false;
			}
			
			for (Object v: aC)
			{
				removed = this.remove(v) || removed;
			}
			
			return removed;
		}
		
		@Override
		public void clear() 
		{
			internalCache.clear();
		}
	}
	
	/**
	 * Default constructor - matches all hosts
	 */
	public HostnameMatcher()
	{
		this(null, false);
	}
	
	/**
	 * Constructor - matches on supplied patterns and respects case sensitivity argument
	 * @param aPatterns - the patterns to check hostnames against
	 * @param aCaseSensitive - should the hostnames be checked for exact case?
	 */
	public HostnameMatcher(Collection<Pattern> aPatterns, boolean aCaseSensitive)
	{
		if (aPatterns != null)
		{
			patternSet.addAll(aPatterns);
		}
		
		mCaseSensitive = aCaseSensitive;
	}
	
	/**
	 * Constructor - accepts the format of the "http.nonProxyHosts" system property.
	 * @param aNonProxyHostsString - a String such as "*.foo.net|www.google.com"
	 */
	public HostnameMatcher(String aNonProxyHostsString)
	{
		// users of this always will expect it to not be case sensitive
		mCaseSensitive = false;
		if (aNonProxyHostsString == null)
		{
			return;
		}
		
		StringTokenizer st = new StringTokenizer(aNonProxyHostsString, "|", false);
         while ( st.hasMoreTokens() ) {
         	String patString = st.nextToken();
         	if (patString == null)
         	{
         		continue;
         	}
         	
         	// nonProxyHosts is forced to lowercase by default java HTTP client implementation
         	patString = patString.toLowerCase().trim();
         	if (patString.length() == 0)
         	{
         		continue;
         	}
         	
         	// configure the pattern by escaping dots then putting dots before stars
         	patString = patString.replace(".", "\\.").replace("*", ".*");
         	
         	try {
         		final Pattern p = Pattern.compile(patString);
    			patternSet.add(p);
         	} 
         	catch ( Exception e ) {
         		oLog.warn( "Error parsing part of Non-proxy hosts string: " + patString , e);
         	}
         }
	}

	/**
	 * Check if a hostname matches the logic of this matcher
	 * @param aHostname a hostname as a String
	 * @return true if the hostname is matched with logic in this matcher, false otherwise.
	 */
	public boolean match(String aHostname) 
	{
		if (aHostname == null || nonMatchCache.contains(aHostname))
		{
			return false;
		}
		
		if (matchCache.contains(aHostname))
		{
			return true;
		}

		for (Pattern p: patternSet)
		{
			try
			{
				String host = aHostname.trim();
				if (! mCaseSensitive)
				{
					host = host.toLowerCase();
				}

				if (p.matcher(host).matches())
				{
					matchCache.add(aHostname);
					return true;
				}
			}
			catch (Exception e)
			{
				oLog.warn("Could not use pattern for hostname matching: " + p.toString(), e);
			}
		}
		
		nonMatchCache.add(aHostname);
		return false;
	}
	
	/**
	 * Checks if this HostnameMatcher has any logic in it,
	 * So consumers can choose to discard it.
	 * <br/> This is a good test to call 
	 * after the {@link #HostnameMatcher(String)} constructor is used
	 * @return true if this Matcher does not have any filtering logic, false otherwise.
	 */
	public boolean matchesAll()
	{
		return null == patternSet || patternSet.isEmpty();
	}
}
