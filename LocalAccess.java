import java.sql.*;
import java.util.ArrayList;

public class LocalAccess {
    private final String dbpath;
    private Connection connectdb;
    private Statement stmt;

    // Path of the database file as parameter
    public LocalAccess(String dbpath){
        this.dbpath = dbpath;
        createPlayerTable();
    }

    public String getDbpath() {
        return dbpath;
    }

    public Statement getStmt(){
        return stmt;
    }

    // Connect to database in constructor
    public void connect(){
        try{
            Class.forName("org.sqlite.JDBC");
            connectdb = DriverManager.getConnection("jdbc:sqlite:"+getDbpath());
            stmt = connectdb.createStatement();
            System.out.println("Connecting to database "+getDbpath()+"...");
        }catch(ClassNotFoundException notFoundException ){
            notFoundException.printStackTrace();
            System.out.println("Driver not found");
        }catch(SQLException E){
            E.printStackTrace();
            System.out.println("Database not found");
        }
    }

    // Close connection with database
    public void close(){
        try {
            connectdb.close();
            stmt.close();
            System.out.println("Closing connection from database "+getDbpath());
        }catch (SQLException E){
            E.printStackTrace();
            System.out.println("Can't close");
        }
    }

    //Send query to database
    public ResultSet query(String request){
        ResultSet query = null;
        try {
            query = stmt.executeQuery(request);
        }catch (SQLException E){
            E.printStackTrace();
            System.out.println("Request error, check syntax.");
        }
        return query;
    }

    // Execute instant queries such as INSERT
    public void execQuery(String request){
        try{
            stmt.execute(request);
        }catch (SQLException E){
            E.printStackTrace();
        }
    }

    // Gather datas following the query and the column we want in a collection
    public ArrayList<String> gatherDataInCollec(ResultSet resultSet,String columnName){
        ArrayList<String> tempo = new ArrayList<>();
        try {
            while(resultSet.next()){
                tempo.add(resultSet.getString(columnName));
            }
        }catch (SQLException E){
            E.printStackTrace();
        }
        return tempo;
    }

    public void deletePlayerFromDB(String id){
        if(stmt == null){
            connect();
            execQuery("DELETE FROM Player WHERE player_id = "+id);
            close();
        }else {
            execQuery("DELETE FROM Player WHERE player_id = "+id);
        }

    }

    // Get the last ID of any table
    public int getLastID(String tableName){
        int id;
        String columnName = tableName.toLowerCase() +"_id";
        ResultSet resultSet = query("Select "+columnName+" FROM "+tableName);
        ArrayList<String> tempo = gatherDataInCollec(resultSet, columnName);
        if(tempo.size() == 0){
            id = 0;
        } else {
            id = Integer.parseInt(tempo.get(tempo.size()-1));
        }
        return id;
    }

    // Create table if not exists
    public void createPlayerTable(){
        String sql = "CREATE TABLE IF NOT EXISTS Player(" +
                "player_id INTEGER NOT NULL PRIMARY KEY," +
                "player_name TEXT NOT NULL," +
                "player_password TEXT NOT NULL" +
                ");";
        if(stmt == null){
            connect();
            execQuery(sql);
            close();
        }else {
            execQuery(sql);
        }
    }
}
