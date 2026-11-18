/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apiplayground.dao;

/**
 *
 * @author vcall
 */
public class ExceptionHandlingTest {


    public static void main(String[] args) {

        UserDAO dao = new UserDAO();

        System.out.println("================================");
        System.out.println("JDBC EXCEPTION HANDLING TEST");
        System.out.println("================================");

        System.out.println("\nAttempting duplicate username...");

        dao.addUser(
            "Charan",
            "duplicate@gmail.com",
            "test123",
            "USER"
        );

        System.out.println("\nTest Completed.");
    }
}

