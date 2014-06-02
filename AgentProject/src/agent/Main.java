package agent;

import java.util.ArrayList;

public class Main {
	public static int agentCount = 5;
	private static int[][] graph = {
		                        {0, 0, 0, 1, 1}, 
		                        {1, 0, 0, 0, 0}, 
		                        {1, 0, 0, 0, 0},
		                        {0, 1, 1, 0, 0},
		                        {0, 0, 1, 0, 0} 
		                    };

	public static void main(String[] args) {		
        ArrayList<String> jadeArgs = new ArrayList<String>();
        jadeArgs.add("-agents");
        jadeArgs.add("Node_0:agent.MyAgent(0, 10); "   +
        		     "Node_1:agent.MyAgent(1, 1050); " +
        		     "Node_2:agent.MyAgent(2, 500); "  +
        		     "Node_3:agent.MyAgent(3, 150); "  + 
        		     "Node_4:agent.MyAgent(4, 2000); " + 
        		     "Ticker:agent.TickAgent();");        
        args = jadeArgs.toArray(args);
        jade.Boot.main(args);
	}

	public static boolean isEdge(int a, int b) {
		return graph[a][b] == 1;
	}
}