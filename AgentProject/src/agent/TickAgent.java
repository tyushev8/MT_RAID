package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TickAgent extends Agent {
	public static volatile boolean lock;
	
	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			try {
				System.out.println(String.format("Ticker has started\n"));
			} catch (Exception any) {
				any.printStackTrace();
			}
		}

	    addBehaviour(new TickerBehaviour(this, 1000) {
	      protected void onTick() {
	        System.out.println("Next tick");
	        TickAgent.lock = true;
	        for (int i = 0; i < Main.agentCount; i++) {
				ACLMessage msg = new ACLMessage(ACLMessage.PROXY);
				msg.addReceiver(new AID(String.format("Node_%d", i), AID.ISLOCALNAME));
				send(msg);
			}
	        TickAgent.lock = false;
	      }
	    });
	    
	    addBehaviour(new WakerBehaviour(this, 60000) {
		      protected void handleElapsedTimeout() {
		        myAgent.doDelete();
		      }
	    });	    
	}
}
