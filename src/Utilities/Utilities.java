package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utilities {
    /**
     * currently only checks for VMware virtualization since that is what the test server runs
     * this would be expanded to check for other VM environments if it went into actual production
     *
     * @throws IOException
     */
    public void VMCheck() throws IOException {//change to return int or boolean for VM requirement
        try {
            String command = "systeminfo";

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("System Manufacturer"))
                    if (line.contains("VMware"))
                        System.out.println("Using a VM");//replace with boolean return
                    else
                        System.out.println("Not Using a VM");//replace with boolean return
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Error -> " + e.getMessage());
        }
        try {
            String command = "systemd-detect-virt";

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("vmware"))
                    System.out.println("Using a VM");//replace with boolean return
                else
                    System.out.println("Not Using a VM");//replace with boolean return
            }
        } catch (Exception e) {
            System.out.println("Error -> " + e.getMessage());
        }
    }
}
