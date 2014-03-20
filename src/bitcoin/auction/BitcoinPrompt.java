package bitcoin.auction;
import java.sql.*;
import java.util.Scanner;

public class BitcoinPrompt {
	private static Scanner in = new Scanner(System.in);
	private static int userId = 0;
	private static boolean isSeller;
	private static boolean isBuyer;
	private static Connection connection = null;
	private static Statement statement = null;
	private static ResultSet results = null;
	private static String command;
	public static void main(String[] args) {

		try	{
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/CS421", "cs421g04", "[$2014g04");
			statement = connection.createStatement();
		}
		catch(Exception e){
			System.out.println("Something went wrong connecting to the database");
		}
		try {
			int option;
			do{
				printMenu();
				option = in.nextInt();
				
				String email, password;
				switch(option){
					case 1:
						System.out.println("Enter an email address for your account.");
						email = in.nextLine();
						System.out.println("Enter your password.");
						password = in.nextLine();
						login(email, password);
						break;
					case 2:
						System.out.println("Enter an email address for your account.");
						email = in.nextLine();
						System.out.println("Enter your password.");
						password = in.nextLine();
						createAccount(email, password);
						break;
					case 3:
						upgradeAccount();
						break;
					case 4:
						createListing();
						break;
					case 5:
						updateListing();
						break;
				}
		} while(option != 0);
		} catch(SQLException e){
			System.out.println("SQL error\n" + e.getMessage());
		}
	}
	private static void printMenu() {
		System.out.println("MENU\nChoose an option:");
		System.out.println("1)\tLogin");
		System.out.println("2)\tCreate Account");
		// Show more stuff if the user is logged in
		if(userId > 0) {
			System.out.println("3)\tUpgrade Account");
			if(isSeller) {
				System.out.println("4)\tCreate selling listing");
				System.out.println("5)\tUpdate listing");
			}
		}
		System.out.println("0)\tExit");
	}
	
	/**
	 * Login the user
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	private static void login(String email, String password) throws SQLException {
		command = "SELECT user_id FROM AuctionUser WHERE email = " + email + " AND password = " + password +";";
		results = statement.executeQuery(command);
		// After login, check the user account type. If the user is not a seller, they are a buyer.
		// So we only need to check if they exist in the seller table.
		if(results.next()) {
			userId = results.getInt(1);
			
			command = "SELECT * FROM Seller WHERE seller_id = " + userId + ";";
			results = statement.executeQuery(command);
			isSeller = results != null;
			
			command = "SELECT * FROM Buyer WHERE buyer_id = " + userId + ";";
			results = statement.executeQuery(command);
			isBuyer = results != null;
		} else System.out.println("The login information provided are invalid.\nThis incident will be reported");
	}
	
	/**
	 * Create an account and prompt the user to select an initial account type (buyer/seller)
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	private static void createAccount(String email, String password) throws SQLException {
		// insert new user and get user id
		if (addAccount(email, password)) {			
			// Choose account type
			int accountType = 0;
			while(accountType == 0) {
				System.out.println("Choose one of the available account types:");
				System.out.println("1) Seller");
				System.out.println("2) Buyer");
				accountType = in.nextInt();
				switch(accountType){
					case 1:
						while(!addSeller()) {
							System.out.println("Unable to create your seller account. Please try again.");
						}
						break;
					case 2:
						while(!addBuyer()) {
							System.out.println("Unable to create your buyer account. Please try again.");
						}
						break;
				}
			}
		} else System.out.println("There was an error creating your account.");
		
	}
	
	/**
	 * Tries to upgrade the user account.
	 * @throws SQLException
	 */
	private static void upgradeAccount() throws SQLException {
		if(!isSeller) {
			while(!addSeller()) {
				System.out.println("Unable to update your account. Please try again.");
			}
		} else if(!isBuyer) {
			while(!addBuyer()) {
				System.out.println("Unable to create your buyer account. Please try again.");
			}
		}
	}
	
	/**
	 * Allows a seller to create a listing
	 * @throws SQLException
	 */
	private static void createListing() throws SQLException {
		// TODO
	}
	
	/**
	 * Lists the seller's current listings and allow them to upgrade it
	 * @throws SQLException
	 */
	private static void updateListing() throws SQLException {
		// TODO
	}
	
	/**
	 * Tries to insert an account in the DB
	 * @param email
	 * @param password
	 * @return true if the operation was successful; false otherwise
	 * @throws SQLException
	 */
	private static boolean addAccount(String email, String password) throws SQLException {
		command = "INSERT INTO AuctionUser (email, password) VALUES (" + email +", " + password + ") RETURNING user_id;";
		results = statement.executeQuery(command);
		if(results.next() && results.getInt(1) > 0) {
			userId = results.getInt(1);
			return true;
		} else return false;
	}
	
	/**
	 * Tries to insert a seller in the DB
	 * @return true if the operation was successful; false otherwise
	 * @throws SQLException
	 */
	private static boolean addSeller() throws SQLException {
		System.out.println("Enter your phone number to complete registration.");
		String phone_number = in.nextLine();
		command = "INSERT INTO Seller (seller_id, phone_number) VALUES (" + userId + ", " + phone_number + ");";
		results = statement.executeQuery(command);
		if(results == null) return false;
		return true;
	}
	
	/**
	 * Tries to insert a buyer in the DB
	 * @return true if the operation was successful; false otherwise
	 * @throws SQLException
	 */
	private static boolean addBuyer() throws SQLException {
		System.out.println("Enter your home address to complete registration.");
		String address = in.nextLine();
		command = "INSERT INTO Buyer (buyer_id, home_address) VALUES (" + userId + ", " + address + ");";
		if(results == null) return false;
		return true;
	}
}
