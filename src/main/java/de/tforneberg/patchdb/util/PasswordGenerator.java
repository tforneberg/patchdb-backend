package de.tforneberg.patchdb.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class PasswordGenerator {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		if (args != null && args.length > 0) {
			System.out.println(encoder.encode(args[0]));
		} else {
		    // create a scanner so we can read the command-line input
		    Scanner scanner = new Scanner(System.in);

		    //  prompt for the user's name
		    System.out.print("Enter password: ");

		    // get their input as a String
		    String password = scanner.next();

		    System.out.println(encoder.encode(password));
		    
		    scanner.close();
		}
	}
}  