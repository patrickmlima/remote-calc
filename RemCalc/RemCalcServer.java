import RemCalcApp.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

import java.util.Properties;

class RemCalcImpl extends RemCalcPOA {
	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	// implement calculate() method
	public String calculate(String expr) {
		String result = ProccessExpression.getInstance().eval(expr);
		return result;
	}

	// implement shutdown() method defined on the Hello interface
	public void shutdown() {
		orb.shutdown(false);
	}
}

public class RemCalcServer {
	public static void main(String args[]) {
		try {
			// create and initialize the ORB instance
			String orbConf[] = {"-ORBInitialPort","1050","-ORBInitialHost","localhost"};
			ORB orb = ORB.init(orbConf, null);

			// get reference to rootpoa and activate thePOAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			RemCalcImpl remCalcImpl = new RemCalcImpl();
			remCalcImpl.setORB(orb);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(remCalcImpl);
			RemCalc href = RemCalcHelper.narrow(ref);

			// get the root naming context
			// NameService invokes the name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			String name = "RemCalc";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path,href);

			System.out.println("RemCalc server is ready and waiting...");

			// wait for invocations from clients
			orb.run();
		}
		catch(Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	System.out.println("RemCalc server Exiting...");
	}
}