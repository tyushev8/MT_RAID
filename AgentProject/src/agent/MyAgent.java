package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MyAgent extends Agent {
	private volatile int id;
	private volatile int value;
	private volatile int min_value;
	private volatile int min_id;
	private volatile int sum;
	private volatile int count;
	private volatile int count_real;
	
	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			try {
				id = Integer.parseInt((String) args[0]);
				value = Integer.parseInt((String)args[1]);				
				System.out.println(String.format("id = %d, name = %s\n", id, getLocalName()));
			} catch (Exception any) {
				any.printStackTrace();
			}
		}
		
		for (int i = 0; i < Main.agentCount; i++) {
			if (Main.isEdge(id, i)) {
				count++;
			}
		}		
	    
	    addBehaviour(new WakerBehaviour(this, 60000) {
		      protected void handleElapsedTimeout() {
		        myAgent.doDelete();
		      }
	    });
	    
	    addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROXY));
				
				if (msg != null) {
					while (TickAgent.lock);
					sum = 0;
					min_value = 1000000;
					count_real = 0;						
					System.out.println("Next tick: Agent " + 
				                        myAgent.getLocalName() + " value=" + value + "");										
					for (int i = 0; i < Main.agentCount; i++) {
						if (Main.isEdge(id, i)) {
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.setContent(String.format("%d %d", id, value));
							msg2.addReceiver(new AID(String.format("Node_%d", i), AID.ISLOCALNAME));
							send(msg2);
						}
					}
				} else {
					block();
				}
			}
	    });
	    
	    addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				
				if (msg != null) {
					String[] tokens = msg.getContent().split(" ");
					int sender_id = Integer.parseInt(tokens[0]);
					int val = Integer.parseInt(tokens[1]);
					sum += val;
					if ( val < min_value ) {
						min_id = sender_id;
						min_value = val;
					}
					if ( ++count_real == count ) {
						int avarage = (sum + value) / (count + 1);
						int diff = value - avarage;
						if ( diff > 0 )
						{
							ACLMessage msg2 = new ACLMessage(ACLMessage.PROPOSE);
							msg2.setContent(String.format("%d", diff));
							msg2.addReceiver(new AID(String.format("Node_%d", min_id), AID.ISLOCALNAME));
							send(msg2);
							value -= diff;
						}
					}
				} else {
					block();
				}
			}
	    });
	    
	    addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				
				if (msg != null) {
					int val = Integer.parseInt(msg.getContent());
					value += val;				
				} else {
					block();
				}
			}
	    });	    
	}	
}
