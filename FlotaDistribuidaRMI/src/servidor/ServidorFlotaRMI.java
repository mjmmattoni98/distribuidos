package servidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import servidor.ImpServidorJuegoRMI;

public class ServidorFlotaRMI {
	public static void main(String args[]) {
        try {
            System.setProperty("java.security.policy", "src/servidor/java.policy");
            
            System.setSecurityManager(new SecurityManager());
        	
        	int RMIPortNum = 1099;
            startRegistry(RMIPortNum);

            ImpServidorJuegoRMI exportedObj = new ImpServidorJuegoRMI();
            String registryURL = "rmi://localhost:" + RMIPortNum + "/FlotaRMI";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Server registered. Registry contains:");
            // list names currently in the registry
            listRegistry(registryURL);
            System.out.println("ServidorFlotaRMI ready.");
        }// end try
        catch (Exception re) {
            System.out.println("Exception in ServidorFlotaRMI.main: " + re);
            re.printStackTrace();
        } // end catch
    } // end main

    // This method starts a RMI registry on the local host, if
    // it doesn't already exists at the specified port number.
    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();  // This call will throw an
            //exception if the registry does not already exist
        } catch (RemoteException e) {
            // No valid registry at that port.
            System.out.println("RMI registry cannot be located at port " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        }
    } // end startRegistry

    //This method lists the names registered with a Registry
    private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
        System.out.println("Registry " + registryURL + " contains: ");
        String[] names = Naming.list(registryURL);
        for (int i = 0; i < names.length; i++)
            System.out.println(names[i]);
    } //end listRegistry
}
