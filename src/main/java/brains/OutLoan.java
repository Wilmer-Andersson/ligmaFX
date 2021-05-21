package brains;

import Objects.Artikel;
import Objects.Loan;
import Objects.User;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OutLoan {
    static String dbUrl = "jdbc:mysql://localhost:3306/bobblan";
    static String dbUser = "root";
    static String dbPass = "Lösenord";

    public static void loanBack(int LoanID,int ArticleID, int Amount){
        String loanID = String.valueOf(LoanID);
        String articleID = String.valueOf(ArticleID);
        String amount = String.valueOf(Amount+1);
        try{
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            String statement1 = "update loan set active = 0 where loanID = " + loanID;
            String statement2 = "update articles set amount = " + amount + " where articleID = " + articleID;

            PreparedStatement ps1 = con.prepareStatement(statement1);
            PreparedStatement ps2 = con.prepareStatement(statement2);

            ps1.executeUpdate();
            ps2.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static List searchLoans(User user){

        List<Loan> loanList = new ArrayList<>();

        try{
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            String statement = "select * from loan where userName = '" + user.getUserName() + "' and active = 1";

            PreparedStatement ps = con.prepareStatement(statement);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                loanList.add(new Loan(rs.getInt("loanID"),rs.getInt("articleID"),rs.getString("userName"),rs.getInt("loanDuration"),Date.valueOf(rs.getString("startDate")),rs.getInt("active")));
            }

            for (Loan l : loanList){
                System.out.println("Låneid: " + l.getLoanID() + " articleID: " + l.getArticle() + " användare: " + l.getUserName());
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return loanList;
    }

    public static List searchLoansArticles(User user) throws SQLException{
        List<Artikel> testLista = new ArrayList<>();
        try{
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            PreparedStatement ps = con.prepareStatement("select * from loan inner join articles a on loan.articleID = a.articleID where userName = ? and active = true");
            ps.setString(1,user.getUserName());
            System.out.println(user.getUserName());

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                System.out.println(rs.getString("articleID"));
                testLista.add(new Artikel(rs.getInt("a.articleID"),rs.getString("articleName"),rs.getInt("loanTime")));
            }

            for(Artikel a : testLista){
                System.out.println(a.getArtikelNamn()+", "+a.getArtikelNr()+", "+a.getLaneTid());
            }

            return testLista;

        } catch (SQLException throwables){
            return null;
        }
    }

    public static void CreateLoan(User user, String artikelID,int loanTime) throws SQLException {

        try{
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPass);

            /*PreparedStatement checkAmount = con.prepareStatement("select antal from artiklar where artikelID = ?");
            checkAmount.setString(1,artikelID);
            ResultSet rs = checkAmount.executeQuery();

            //TODO fixa antal grejen
            if(rs.getInt("antal") <= 1){

            }*/

            PreparedStatement ps = con.prepareStatement("insert into loan(articleID,userName,loanDuration,startDate,active) values(?,?,?,?,?)");
            ps.setString(1,artikelID);
            ps.setString(2,user.getUserName());
            ps.setInt(3,loanTime);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setBoolean(5,true);

            ps.executeUpdate();

            System.out.println("Loan created!");

            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
