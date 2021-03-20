import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginManager {
    private LocalAccess localAccess;
    private User user;

    public LoginManager(){
        localAccess = new LocalAccess("databaseFile");

    }

    public void launcher(){
        String answer;
        while(user == null){
            boolean ans = false;
            System.out.println("Do you want to login or create your account(login/create)");
            answer = inputString();
            while(!ans){
                if(answer.equalsIgnoreCase("login")){
                    user = login();
                    ans = true;
                }else if(answer.equalsIgnoreCase("create")){
                    user = createNewAccount();
                    ans = true;
                }else {
                    System.out.println("Wrong answer, retry");
                }
            }
            if(user == null){
                System.out.println("You didn't succeed on logging in");
            }
        }
        displayPlayerTable();
    }

    public User login(){
        User aUser = null;
        ArrayList<String> tempo = new ArrayList<>();
        boolean retry = true;
        boolean verif = false;
        String username =null, password = null, answer;
        while(retry && !verif){
            System.out.println("Login system \n" +
                    "Enter your username:");
            username = inputString().toLowerCase();

            localAccess.connect();
            ResultSet searchUsername = localAccess.query("SELECT player_name " +
                    "FROM Player " +
                    "WHERE player_name = '"+username+"';");
            tempo = localAccess.gatherDataInCollec(searchUsername,"player_name");
            if(tempo.size() == 1){
                verif = true;
            }else {
                System.out.println("Account doesn't exist");
                answer = answer();
                if(answer.equalsIgnoreCase("no")){
                    retry = false;
                }
            }
        }
        if(verif){
            verif = false;
            retry = true;

            while(!verif && retry){
                System.out.println("Enter your password");
                password = inputString();
                ResultSet resultSet = localAccess.query("SELECT player_password FROM Player WHERE player_name = '"+username+"';");
                tempo = localAccess.gatherDataInCollec(resultSet,"player_password");

                if(!password.equalsIgnoreCase(tempo.get(0))){
                    System.out.println("Wrong password, do you want to retry?");
                    answer = answer();
                    if(answer.equalsIgnoreCase("no")){
                        retry = false;
                    }
                }else {
                    System.out.println("Logging in...");
                    verif = true;
                }
            }
            aUser = new User(username,password);
        }
        return aUser;
    }

    public User createNewAccount(){
        boolean verif = false, retry = true;
        String username = null;
        String answer, password = "1", confPassword = "2";
        User aUser = null;
        ArrayList<String> tempo = new ArrayList<>();
        while(!verif && retry){
            System.out.println("Create your account\n" +
                    "Enter your Username:");
            username = inputString().toLowerCase();
            localAccess.connect();

            ResultSet searchUsername = localAccess.query("SELECT * FROM Player WHERE player_name = '"+username+"'");

            try {
                while(searchUsername.next()){
                    tempo.add(searchUsername.getString("player_name"));
                }
            }catch (SQLException E){
                E.printStackTrace();
            }
            if(tempo.size() == 0){
                verif = true;
            }else {
                System.out.println("Name already taken");
                answer = answer();
                if(answer.equalsIgnoreCase("no")){
                    retry = false;
                }
                tempo.clear();
            }
        }
        if(verif){
            while(!password.equalsIgnoreCase(confPassword)){
                System.out.print("Enter your password:");
                password = inputString();
                System.out.print("Confirm your password:");
                confPassword = inputString();
                if(!password.equalsIgnoreCase(confPassword)){
                    System.out.println("Passwords do not match, retry");
                }
            }

            int nextID = localAccess.getLastID("Player")+1;

            localAccess.execQuery("INSERT INTO Player(player_id, player_name, player_password) VALUES("+nextID+",'"+username+"','"+password+"');");
            aUser = new User(username,password);
        }

        localAccess.close();

        return aUser;
    }

    public void displayPlayerTable(){
        localAccess.connect();
        ResultSet resultSet = localAccess.query("SELECT player_id, player_name FROM Player");
        try {
            while(resultSet.next()){
                System.out.println(resultSet.getInt("player_id"));
                System.out.println(resultSet.getString("player_name"));
            }
        }catch (SQLException E){
            E.printStackTrace();
        }
        localAccess.close();
    }

    public static String inputString(){
        Scanner input = new Scanner(System.in);
        return input.next();
    }

    public String answer(){
        boolean ans = false;
        String answer = null;
        while(!ans){
            System.out.println("Do you want to retry?(yes/no)");
            answer = inputString();
            if(answer.equalsIgnoreCase("yes")){
                ans = true;
            }else if(answer.equalsIgnoreCase("no")){
                ans = true;
            }else {
                System.out.println("Wrong answer, retry");
            }
        }
        return answer;
    }
}
