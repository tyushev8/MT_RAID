package agent;

import jade.core.AID;
import java.util.Locale;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MyAgent extends Agent {
	private volatile int id;
	private volatile double value;
	private volatile double min_value;
	private volatile int min_id;
	private volatile double sum_diff;
	private volatile int count = 0;
	private volatile int count_real;
	
	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			try {
				id = Integer.parseInt((String) args[0]);
				value = (double)Integer.parseInt((String)args[1]);
				Main.sum += (int)value;
				System.out.println(String.format(Locale.ENGLISH, 
								   "id = %d, name = %s, value = %f", id, 
								   getLocalName(), value));
			} catch (Exception any) {
				any.printStackTrace();
			}
		}
		
		for (int i = 0; i < Main.agentCount; i++) {
			if (Main.isEdge(id, i, -1)) {
				count++;
			}
		}		
	    
	    addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROXY));
				
				if (msg != null) {
					double tmp = value;
					int tick;
					while (TickAgent.lock);
					sum_diff = 0;
					count_real = 0;
					tick = Integer.parseInt(msg.getContent());
					System.out.println("Next tick: Agent " + 
				                        myAgent.getLocalName() + " value = " + 
				                        (Main.agentCount * value) + " sum = " + Main.sum + 
				                        " tick = " + tick);
					for (int i = 0; i < Main.agentCount; i++) {
						if (Main.isEdge(i, id, tick)) {
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.setContent(String.format(Locale.ENGLISH, "%d %f", id, tmp));
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
					double val =  Double.parseDouble(tokens[1]);
					sum_diff += (val - value);
					if ( ++count_real == count ) {
						value += 0.1 * sum_diff;
						ACLMessage msg2 = new ACLMessage(ACLMessage.PROPOSE);
						msg2.addReceiver(new AID("Ticker", AID.ISLOCALNAME));
						send(msg2);
						if ( Math.abs(value * Main.agentCount - Main.sum) < 0.5 )
						{
							Main.flags[id] = 1;
						}
					}
				} else {
					block();
				}
			}
	    });    
	}	
}
