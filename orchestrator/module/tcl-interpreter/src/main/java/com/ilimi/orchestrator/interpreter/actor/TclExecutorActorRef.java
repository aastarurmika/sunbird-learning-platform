package com.ilimi.orchestrator.interpreter.actor;

import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.SmallestMailboxPool;

import com.ilimi.common.router.RequestRouterPool;
import com.ilimi.graph.engine.router.ActorBootstrap;
import com.ilimi.orchestrator.dac.model.OrchestratorScript;

public class TclExecutorActorRef {

    public static final int poolSize = 8;

    private static ActorRef actorRef;

    public static void initExecutorActor(List<OrchestratorScript> commands) {
    	
        ActorSystem system = RequestRouterPool.getActorSystem();
        Map<String, Integer> actorMap = ActorBootstrap.getActorCountMap();
    	Integer count = actorMap.get("TclExecutor");
    	if (null == count)
    		count = poolSize;
    	System.out.println("Creating " + count + " TclExecutor actors");
        Props actorProps = Props.create(TclExecutorActor.class, commands);
        actorRef = system.actorOf(new SmallestMailboxPool(count).props(actorProps));
    }

    public static ActorRef getRef() {
        return TclExecutorActorRef.actorRef;
    }
}
