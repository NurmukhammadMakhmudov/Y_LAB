package com.example.y_lab.services;

import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitRepo;
import com.example.y_lab.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final ConnectionService connectionService  = new ConnectionService();
    private final UserRepo userRepo = new UserRepo(connectionService);
    private final HabitRepo habitRepo = new HabitRepo(connectionService);




    public void registerUser(String email, String password, String name) {
        User user1 = userRepo.findByEmail(email);
        if (user1 == null) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            userRepo.save(user);
        }
        else
            throw new IllegalArgumentException("Email is already taken.");
    }

    public User login(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user.getPassword().equals(password) )
            if (!user.isBlocked())
                return user;
            else
                throw new IllegalArgumentException("User Blocked");
        throw new IllegalArgumentException("Invalid email or password.");
    }


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }


    public void editUserProfile(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        updateUser(user, newName, newEmail, newPassword);
        System.out.println("Profile updated successfully.");
    }

    public void updateUser(User user, String name, String email, String password)
    {
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.update(user);

    }


    public void deleteUser(User user) {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userRepo.delete(user.getId());
            System.out.println("Account deleted.");
            System.exit(0);
        } else {
            System.out.println("Account deletion canceled.");
        }
    }

    public void viewAllUsers() {
        List<User> users = getAllUsers();
        users.forEach(user -> {
            System.out.println("User: " + user.getEmail() + " - " + user.getName());
            habitRepo.findByUser(user).forEach(habit -> {
                System.out.println("  Habit: " + habit.getTitle() + " - " + habit.getFrequency());
            });
        });
    }

    public void blockUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to block: ");
        String email = scanner.nextLine();
        if (userRepo.findByEmail(email) != null) {
            User user = userRepo.findByEmail(email);
            user.setBlocked(true);
            userRepo.update(user);
            System.out.println("User " + email + " has been blocked.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void deleteUserAsAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to delete: ");
        String email = scanner.nextLine();
        User user = userRepo.findByEmail(email);
        if (user != null)
        {
            userRepo.delete(user.getId());
            System.out.println("User " + email + " has been deleted.");
        }
        else
            System.out.println("User" + email + " not found");

    }
}