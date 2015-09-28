import RemCalcApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Scanner;

public class RemCalcClient {
	static RemCalc remCalcImpl;

	public static void main(String args[]) {
		try {
			ORB orb = ORB.init(args,null);

			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. It's part
			// of the Interoperable naming Service.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// resolve the object reference in Naming
			String name = "RemCalc";
			remCalcImpl = RemCalcHelper.narrow(ncRef.resolve_str(name));

			System.out.println("Hello!!");
			System.out.println("This is the remote calculator service.");
			System.out.println("Please, insert a mathematical expression which you want to solve or insert q to exit:");

			Scanner sc = new Scanner(System.in);
			String expr;
			while((expr = sc.nextLine()) != "q") {
				System.out.println("Resultado: " + remCalcImpl.calculate(expr));
				System.out.println("\nInsert a mathematical expression which you wanto to solve or insert q to exit:");
			}
			System.out.println("\n\nThank you for using...");
			sc.nextLine();
			remCalcImpl.shutdown();
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	}
}