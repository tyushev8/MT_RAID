package agent;

import java.util.ArrayList;
//import java.util.Locale;

public class Main {
	
	public static int sum = 0;
	public static int agentCount = 6;
	public static int[] flags;
	private static int[][] graph = {
        {0, 1, 0, 0, 0, 0}, 
        {0, 0, 1, 0, 0, 0}, 
        {0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0} 
		                    };

	public static void main(String[] args) {
		//Locale.setDefault(Locale.GERMAN);
		flags = new int[Main.agentCount];
        ArrayList<String> jadeArgs = new ArrayList<String>();
        String str = new String();
        jadeArgs.add("-agents");
        str += " Ticker:agent.TickAgent();";
        for ( int i = 0; i < Main.agentCount; i++ )
        {
        	flags[i] = 0;
        	str += String.format(" Node_%d:agent.MyAgent(%d, %d); ", 
        						 i, i, (int)(Math.random()*16000+10));
        }
        jadeArgs.add(str);
        args = jadeArgs.toArray(args);
        jade.Boot.main(args);
	}

	public static boolean isEdge(int a, int b, int tick, int flag) {
		//return graph[a][b] == 1 || graph[b][a] == 1;
		if ( 
				( a < ( Main.agentCount - 1 ) && a == b - 1 ) ||
				( b < ( Main.agentCount - 1 ) && b == a - 1 ) ||
				( a > 0 && a == b + 1 ) ||
				( b > 0 && b == a + 1 ) ||
				( a == ( Main.agentCount - 1 ) && b == 0 ) ||
				( b == ( Main.agentCount - 1 ) && a == 0 )
		   )
		{
			if (flag)
			{
				return true
			}
			if ( tick % 2 == 0 && 
					( ( a % 2 == 0 && b > a ) || 
							( b % 2 == 0 && a > b ) ) )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}