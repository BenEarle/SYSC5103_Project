// Environment code for project SYSC5103_Project.mas2j


import java.io.*;
import java.net.*;
import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;


public class KrisletEnvironment extends Environment {



    private Logger logger = Logger.getLogger("SYSC5103_Project.mas2j."+KrisletEnvironment.class.getName());
	private Krislet player;


    /** Called before the MAS execution with the args informed in .mas2j */

    @Override

    public void init(String[] args) {
		String	hostName = "localhost";
		int			port = 6000;
		String	team = new String("Krislet3");
		
        super.init(args);
		
		try	{		
			//System.out.println("Creating Krislet player");
			player = new Krislet(InetAddress.getByName(hostName), port, team);
			//System.out.println("Calling main init");
			player.mainInit();
			//System.out.println("Calling the update function");
			player.mainUpdate();
		} catch (Exception e) {	}
		//System.out.println("Updating player");
		updatePlayer(player);
		//System.out.println("End init");
    }


	public static final Term    turn40 = Literal.parseLiteral("turn(40)");
    @Override

    public boolean executeAction(String agName, Structure action) {
	/*	String temp = action.toString();
		String[] arrOfStr = temp.split("(");
		String comm = arrOfStr[0];
		String param = arrOfStr[1];
		param = param.replaceAll(")","");
		switch(comm){
			case "turn":
			player.turn(Double.parseDouble(param));
			break;
		}*/
		try{player.mainUpdate();}catch(Exception e){}
		if(action.equals(turn40)){
			player.turn(40);
		} else {
			logger.info("executing: "+action+", but not implemented!");
        }
		if (true) { // you may improve this condition
			updatePlayer(player);
            informAgsEnvironmentChanged();

        }
		

        return true; // the action was executed with success

    }

	

    /** Called before the end of MAS execution */

    @Override

    public void stop() {
		
        super.stop();

    }
	
	/** Private helper functions*/
	private void updatePlayer(Krislet p){
		try {
			// Specify agent name !!!
			clearPercepts();
			//addPercept(ASSyntax.parseLiteral("percept(demo)"));
			
			ObjectInfo object = p.m_memory.getObject("ball");
			if( object == null ){
				// If you don't know where is the ball
				logger.info("Ball was not seen");
			} else {
				addPercept(ASSyntax.parseLiteral("ball"));   
				logger.info("Ball was seen");
				/*if( object.m_distance > 1.0 ) {
					// The ball is out of range
					if( object.m_direction != 0 ){
						// Not facing the ball
					} else{
						// Facing the ball
					}
				}*/
			}
			//System.out.println("Got Ball info");
			// Look for the goal
/*			if( p.m_side == 'l' )
				object = p.m_memory.getObject("goal r");
			else
				object = p.m_memory.getObject("goal l");
	
			if( object == null ) {
				// Cannot see the goal
			} else {
				// Can see the goal	
			}		
	*/	} catch (Exception e) {}
	}

}


