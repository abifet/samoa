package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.TopologyWithDelay;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsTopology extends TopologyWithDelay {
	private static final Logger logger = LoggerFactory.getLogger(ThreadsTopology.class);
	
	public String topologyName;
    private ThreadsEntranceProcessingItem entrancePi;
    
    private void setupWorkers() {
    	for (IProcessingItem pi:this.processingItems) {
    		if (pi instanceof ThreadsProcessingItem) {
    			((ThreadsProcessingItem) pi).setupWorkers();
    		}
    	}
    }

    public void run() {
    	if (entrancePi == null) 
    		throw new IllegalStateException("You need to set entrance PI before run the topology.");
    		
    	while(entrancePi.injectNextEvent()) {
    		if (this.getSourceDelay() > 0) {
    			try {
    				Thread.sleep(this.getSourceDelay());
    			} catch (InterruptedException e) {
    				logger.error("Topology was interrupted while sleeping.");
    			}
    		}
    	}
    }
    
    public void start() {
        this.setupWorkers();
    	this.run();
    }

    public ThreadsTopology(String topoName) {
        this(topoName, 0);
    }
    
    public ThreadsTopology(String topoName, int delay) {
    	this.topologyName = topoName;
    	this.setSourceDelay(delay);;
    }

    public EntranceProcessingItem getEntranceProcessingItem() {
        return entrancePi;
    }

    @Override
    public void addEntrancePi(EntranceProcessingItem epi) {
        this.entrancePi = (ThreadsEntranceProcessingItem) epi;
        this.addProcessingItem(epi);
    }
}