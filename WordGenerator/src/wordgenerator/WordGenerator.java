/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordgenerator;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author john-vostro
 */
public class WordGenerator implements Runnable {

    private static Connection connection = null;
    private static PreparedStatement stmtInsert = null;
    private static PreparedStatement stmtSelect = null;
    private int startAt;
    private int endAt;
    private int goAt;

    public WordGenerator(int startAt, int endAt, int goAt) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.goAt = goAt;
    }

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException {

        WordGenerator word1 = new WordGenerator(1, 10, 256);
//        WordGenerator word2 = new WordGenerator(11, 20, 256);
//        WordGenerator word3 = new WordGenerator(21, 30, 256);
//        WordGenerator word4 = new WordGenerator(31, 40, 256);
//        WordGenerator word5 = new WordGenerator(41, 50, 256);
//        WordGenerator word6 = new WordGenerator(51, 60, 256);
//        WordGenerator word7 = new WordGenerator(61, 70, 256);
//        WordGenerator word8 = new WordGenerator(71, 80, 256);
        

        Thread t1 = new Thread(word1);
//        Thread t2 = new Thread(word2);
//        Thread t3 = new Thread(word3);
//        Thread t4 = new Thread(word4);
//        Thread t5 = new Thread(word5);
//        Thread t6 = new Thread(word6);
//        Thread t7 = new Thread(word7);
//        Thread t8 = new Thread(word8);

        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
//        t6.start();
//        t7.start();
//        t8.start();

    }

    public static PreparedStatement getStatementInsert() throws SQLException {

        if (stmtInsert == null) {

            String sql = "INSERT INTO Words (word, md5, sha1) values (?, md5(?), sha1(?))";

            stmtInsert = getConnection().prepareStatement(sql);
        }
        return stmtInsert;

    }

    public static PreparedStatement getStatementSelect() throws SQLException {

        if (stmtSelect == null) {

            String sql = "select count(*) from  Words where word = ?";

            stmtSelect = getConnection().prepareStatement(sql);
        }
        return stmtSelect;

    }

    public void generateWords() throws SQLException {
        for (int i = this.startAt; i <= this.endAt; i++) {
            String currentString = this.generateCharByASCIIIndex(i);

            insert(currentString);

            this.generateWords(currentString);
        }
    }

    public void generateWords(String s) throws SQLException {
        for (int i = 1; i <= 255; i++) {
            String currentString = s + this.generateCharByASCIIIndex(i);
            insert(currentString);
            if (currentString.length() <= this.goAt) {
                this.generateWords(currentString);
            }
        }

    }

    public String generateCharByASCIIIndex(int index) {
        char currentChar = (char) index;
        String currentString = Character.toString(currentChar);
        return currentString;
    }

    private boolean insert(String word) throws SQLException {

        try {
            PreparedStatement stmt1 = getStatementSelect();
            stmt1.setString(1, word);

            stmt1.execute();
            ResultSet result = stmt1.getResultSet();
            if (!result.next()) {

                PreparedStatement stmt2 = getStatementInsert();
                stmt2.setString(1, word);
                stmt2.setString(2, word);
                stmt2.setString(3, word);
                return stmt2.execute();
            }
            return false;
        } catch (MySQLIntegrityConstraintViolationException ex) {
//            Logger.getLogger(WordGenerator.class.getName()).log(Level.SEVERE, null, ex);

            return true;
        } catch (Exception ex) {
            Logger.getLogger(WordGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    private static Connection getConnection() {

        try {
            if (connection == null) {
                // Carregando o JDBC Driver padrão  
                String driverName = "com.mysql.jdbc.Driver";
                Class.forName(driverName);
                // Configurando a nossa conexão com um banco de dados//  
                String serverName = "127.0.0.1:3306";
                //caminho do servidor do BD  
                String mydatabase = "WordBruteForce";
                //nome do seu banco de dados  
                String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
                String username = "root";        //nome de um usuário de seu BD        
                String password = "";      //sua senha de acesso  
                connection = DriverManager.getConnection(url, username, password);
            }
            return connection;

        } catch (ClassNotFoundException e) {  //Driver não encontrado  
            System.out.println("O driver expecificado nao foi encontrado.");
            return null;
        } catch (SQLException e) {
            //Não conseguindo se conectar ao banco  
            System.out.println("Nao foi possivel conectar ao Banco de Dados:" + e.getMessage() + "code: " + e.getErrorCode());
            return null;
        }
    }

    @Override
    public void run() {
        try {
            generateWords();
        } catch (SQLException ex) {
            Logger.getLogger(WordGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
