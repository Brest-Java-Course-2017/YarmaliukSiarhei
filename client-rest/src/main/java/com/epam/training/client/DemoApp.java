package com.epam.training.client;


import com.epam.training.client.exception.ServerDataAccessException;
import com.epam.training.client.rest.api.UsersConsumer;
import com.epam.training.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;


@Component
public class DemoApp {

    @Autowired
    private UsersConsumer mUsersConsumer;

    private Scanner mScanner = new Scanner(System.in);

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");

        DemoApp demoApp = context.getBean(DemoApp.class);
        demoApp.menu();
    }

    private void menu() {

       printMenu();

        int selectedAction = 0;

        while (selectedAction != 8) {

            System.out.print("Select action: ");

            if (mScanner.hasNextInt()) {

                selectedAction = mScanner.nextInt();
                caseSelected(selectedAction);

            } else {
                System.out.println("Invalid selection");
            }
        }
    }

    private void printMenu(){

        System.out.println("=============================");
        System.out.println("|            Menu           |");
        System.out.println("=============================");
        System.out.println("|  Select action:           |");
        System.out.println("|   1. Get all users        |");
        System.out.println("|   2. Get user by login    |");
        System.out.println("|   3. Get user by id       |");
        System.out.println("|   4. Add new user         |");
        System.out.println("|   5. Delete user by id    |");
        System.out.println("|   6. Delete user by login |");
        System.out.println("|   7. Show menu            |");
        System.out.println("|   8. Exit                 |");
        System.out.println("=============================");
    }

    private void caseSelected(int choice) {

        try {

            switch (choice) {

                case 1:
                    getAllUsers();
                    break;

                case 2:
                    getUserByLogin();
                    break;

                case 3:
                    getUserById();
                    break;

                case 4:
                    addNewUser();
                    break;

                case 5:
                    deleteUserById();
                    break;

                case 6:
                    deleteUserByLogin();
                    break;

                case 7:
                    printMenu();
                    break;

                case 8:
                    System.out.println("Exist.");
                    break;

                default:
                    System.out.println("Invalid selection.");
            }

        } catch (ServerDataAccessException ex) {
            System.out.println("    ERROR: " + ex.getMessage());
        }

    }

    private User getInputUser() {

        String userLogin = "";
        System.out.print("    Enter user login: ");
        if (mScanner.hasNextLine()) userLogin = mScanner.next();

        String userPassword = "";
        System.out.print("    Enter user password: ");
        if (mScanner.hasNextLine()) userPassword = mScanner.next();

        String description = null;
        System.out.print("    Enter user description: ");
        if (mScanner.hasNextLine()) description = mScanner.next();

        return new User(userLogin, userPassword, !description.equals("") ? description : null);
    }

    private <T> T getInputValue(Class<T> classValue) {

        try {

            String typeName = classValue.getName();

            if (typeName.equals(String.class.getName())) {

                String userLogin = "";
                System.out.print("    Enter user login: ");

                if (mScanner.hasNextLine()) userLogin = mScanner.next();
                return classValue.cast(userLogin);

            } else if (typeName.equals(Integer.class.getName())) {

                Integer userId = -1;
                System.out.print("    Enter user id: ");

                if (mScanner.hasNextInt()) userId = mScanner.nextInt();
                return classValue.cast(userId);

            } else {
                throw new IllegalArgumentException("Not supported incoming class type. Incoming type should be String or Integer.");
            }

        } catch (IllegalArgumentException ex) {
            throw new ServerDataAccessException(ex.getMessage(), ex.getCause());
        }
    }


    private void getAllUsers() throws ServerDataAccessException {

        List<User> allUsers = mUsersConsumer.getAllUsers();
        System.out.println(allUsers.toString());
    }

    private void getUserById() throws ServerDataAccessException {

        Integer userId = getInputValue(Integer.class);

        User user = mUsersConsumer.getUserById(userId);
        System.out.println("    User: " + user);
    }

    private void getUserByLogin() throws ServerDataAccessException {

        String userLogin = getInputValue(String.class);

        User user = mUsersConsumer.getUserByLogin(userLogin);
        System.out.println("    User: " + user);
    }

    private void addNewUser() throws ServerDataAccessException {

        User user = getInputUser();

        user.setUserId(mUsersConsumer.addUser(user));
        System.out.println("    User: " + user);
    }

    private void deleteUserById() throws ServerDataAccessException {

        Integer userId = getInputValue(Integer.class);

        mUsersConsumer.deleteUserById(userId);
        System.out.println("    User with id: " + userId + " was deleted.");
    }

    private void deleteUserByLogin() throws ServerDataAccessException {

        String userLogin = getInputValue(String.class);

        mUsersConsumer.deleteUserByLogin(userLogin);
        System.out.println("    User with login: " + userLogin + " was deleted.");
    }

}
