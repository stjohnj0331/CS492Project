package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utilities {

    public void VMCheck() throws IOException {
        //try to check for windows or linux OS

        try {
            String command = "systeminfo";

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                if(line.contains("System Manufacturer"))
                    if(line.contains("VMware"))
                        System.out.println("Using a VM");
                    else
                        System.out.println("Not Using a VM");

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            String command = "systemd-detect-virt";

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                    if(line.contains("vmware"))
                        System.out.println("Using a VM");
                    else
                        System.out.println("Not Using a VM");

            }
        }catch(Exception e){

        }
    }
}
