package bitcoin.auction;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
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
		String item_title;
		String item_desc;
		double price;
		int category_id = 0;
		String status = "available";	//obviously
		String start_time;
		int num_days;
		String end_time;
		
		System.out.println("Enter listing title:");
		item_title = in.nextLine();
		System.out.println("Describe your listing in one line:");
		item_desc = in.nextLine();
		// Choose a category
		do {
			category_id = getCategory();
		} while (category_id == 0);
		
		System.out.println("Enter item price in BTC:");
		price = in.nextDouble();
		
		System.out.println("Enter the number of days you want to list this item for:");
		num_days = in.nextInt();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		start_time = timestamp.toString();
		
		cal.setTime(timestamp);
		cal.add(Calendar.DAY_OF_WEEK, num_days);
		timestamp.setTime(cal.getTime().getTime());
		end_time = timestamp.toString();
		
		command = "INSERT INTO Item "
				+ "(seller_id, cat_id, title, description, price, status, start_time, end_time) "
				+ "VALUES ("
				+ userId + ", "
				+ category_id + ", "
				+ item_title + ", "
				+ item_desc + ", "
				+ price + ", "
				+ status + ", "
				+ start_time + ", "
				+ end_time + ") RETURNING item_id;";
		results = statement.executeQuery(command);
		
		int item_id;
		ArrayList<Integer> tag_id;
		// Tags
		if(results.next() && results.getInt(1) > 0) {
			item_id = results.getInt(1);
			do {
				tag_id = getTag();
			} while (tag_id == null);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("INSERT INTO ItemTags VALUES ");
			for (Integer id : tag_id) {
				buffer.append("(" + item_id + ", " + id.intValue() + "),");
			}
			buffer.setCharAt(buffer.lastIndexOf(","), ';');
			command = buffer.toString();
			results = statement.executeQuery(command);
		}
		else System.out.println("There was a problem adding the item");
	}
	
	/**
	 * Asks the user to select a category
	 * @return the selected category or 0
	 * @throws SQLException
	 */
	private static int getCategory() throws SQLException {
		int max_category = 0;
		command = "SELECT * FROM Category;";
		results = statement.executeQuery(command);
		System.out.println("Select an item category under which to list your item:");
		while(results.next()) {
			System.out.println(results.getInt("cat_id") + ") " + results.getString("cat_name"));
		}
		command = "SELECT max(cat_id) FROM Category;";
		if(results.next()) {
			max_category = results.getInt(1);
		}
		int entered_category = in.nextInt();
		return entered_category <= max_category && entered_category > 0 ? entered_category : 0;
	}
	
	/**
	 * Asks the user to select a bunch of tags
	 * @return an ArrayList of tags
	 * @throws SQLException
	 */
	private static ArrayList<Integer> getTag() throws SQLException {
		int max_tag = 0;
		command = "SELECT max(tag_id) FROM Tag;";
		if(results.next()) {
			max_tag = results.getInt(1);
		}
		
		boolean more_tag = false;
		ArrayList<Integer> entered_tags = new ArrayList<Integer>();
		command = "SELECT * FROM Tag;";
		results = statement.executeQuery(command);
		do {
			System.out.println("Select a tag to associate with your listing");
			while(results.next()) {
				System.out.println(results.getInt("tag_id") + ") " + results.getString("tag_name"));
			}
			results.beforeFirst(); // reset cursor
			Integer entered_tag = new Integer(in.nextInt());
			if(entered_tag <= max_tag && entered_tag > 0 && !entered_tags.contains(entered_tag)) {
				entered_tags.add(entered_tag);
			}
			System.out.println("Add another tag? (y/n)");
			more_tag = in.nextLine().equals("y");
		} while (more_tag);
		return entered_tags;
	}
	
	/**
	 * Lists the seller's current listings and allow them to upgrade it
	 * @throws SQLException
	 */
	private static void updateListing() throws SQLException {
		// get still active listings
		command = "SELECT * FROM Item WHERE seller_id = " + userId + " && end_time < NOW();";
		results = statement.executeQuery(command);
		System.out.println("Here are your current listings:");
		while(results.next()) {
			System.out.println(results.getInt("item_id") + ") " + results.getString("title") + ": " + results.getString("description"));
		}
		System.out.println("Select a listing to edit:");
		int selected_listing = in.nextInt();
		// no error checking yolo
		System.out.println("Type in the new listing description in one line:");
		String new_desc = in.nextLine();
		
		// update description
		results.beforeFirst();
		while(results.next() && results.getInt("item_id") != selected_listing);
		results.updateString("description", new_desc);
		results.next();	//apparently can't call updateRow when the cursor is currently there so move the cursor
		results.updateRow();
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
