package com.attendance.reader;

import java.util.Scanner;

public class PasswordManager {
private static String password = "";
	
	public static boolean verifyPassword(String pass)
	{
		return password.equals(pass);
		
	}
	public static void updatePassword(Scanner sc)
	{
		sc.nextLine();
		System.out.println("Enter new password: ");
		password = sc.nextLine();
		System.out.println("Password updated successfully!");
	}
}
