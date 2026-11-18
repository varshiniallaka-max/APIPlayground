/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apiplayground.dao;


public class UserCRUDTest {


    public static void main(String[] args) {

        UserDAO dao = new UserDAO();

        System.out.println("===== CREATE =====");
        dao.addUser(
            "testuser",
            "test@gmail.com",
            "test123",
            "USER"
        );

        System.out.println("\n===== READ =====");
        dao.getUsers();

        System.out.println("\n===== UPDATE =====");
        dao.updateUser(
            3,
            "updated@gmail.com"
        );

        System.out.println("\n===== DELETE =====");
        dao.deleteUser(3);
    }
}

