package agent;

import java.util.Locale;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TickAgent extends Agent {
	public static volatile boolean lock;
	public int count = 0;
	//public int agent_count = 5;
	public int cur_count = 0;
	
	protected void setup() {
		Object[] args = getArguments();
		if (args != null) {
			try {
				//System.out.println("Ticker has started\n");
			} catch (Exception any) {
				any.printStackTrace();
			}
		}
	    
	    addBehaviour(new WakerBehaviour(this, 500) {
		      protected void handleElapsedTimeout() {
			        System.out.println("Next tick " + (count++));
			        TickAgent.lock = true;
			        for (int i = 0; i < Main.agentCount; i++) {
						ACLMessage msg2 = new ACLMessage(ACLMessage.PROXY);
						msg2.setContent(String.format(Locale.ENGLISH, "%d", count));
						msg2.addReceiver(new AID(String.format("Node_%d", i), AID.ISLOCALNAME));
						send(msg2);
					}
			        TickAgent.lock = false;	
		      }
	    });	    
	    
	    addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action() {
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				
				if (msg != null) {
					//System.out.println("cur_count =  " + cur_count);
					if (++cur_count == Main.agentCount)
					{
						try {
							java.lang.Thread.sleep(20);
						} catch(InterruptedException ex) {
						    Thread.currentThread().interrupt();
						}						
						System.out.println("Next tick " + (count++));
				        TickAgent.lock = true;
				        for (int i = 0; i < Main.agentCount; i++) {
							ACLMessage msg2 = new ACLMessage(ACLMessage.PROXY);
							msg2.setContent(String.format(Locale.ENGLISH, "%d", count));
							msg2.addReceiver(new AID(String.format("Node_%d", i), AID.ISLOCALNAME));
							send(msg2);
						}
				        cur_count = 0;
				        TickAgent.lock = false;
				        
				        boolean flag = true;
				        for ( int i = 0; i < Main.agentCount; i++ )
				        {
				        	if ( Main.flags[i] == 0 )
				        	{
				        		flag = false;
				        	}
				        }				  
				        
				        for ( int i = 0; i < Main.agentCount; i++ )
				        {
				        	Main.flags[i] = 0;
				        }
				        
				        if (flag)
				        {
				        	myAgent.doDelete();
				        }				        
					}
				} else {
					block();
				}
			}
	    });		    
	}
}
