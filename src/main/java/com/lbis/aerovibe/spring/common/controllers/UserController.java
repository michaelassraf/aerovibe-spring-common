/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.executors.UserActions;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class UserController {

    @Autowired
    UserActions userActions;

    public User signUpOrLoginFBUser(User user) throws IOException, InterruptedException {
        User expectedUser = login(user);
        if (expectedUser != null) {
            return expectedUser;
        } else {
            return signUp(user);
        }
    }

    public User signUp(User user) throws IOException, InterruptedException {
        User logInUser = userActions.get(user.getObjectKey());
        if (logInUser != null) {
            logInUser.setUserPassword(null);
            logInUser.setUserId(null);
            return logInUser;
        }
        user.setUserAQIProfileLevel(301 / 2);
        user.setUserNoNotificationsADay(1);
        user.setUserId(user.getObjectKey());
        userActions.put(user);
        Thread.sleep(1000L);
        return userActions.get(user.getObjectKey());
    }

    public User login(User user) throws IOException {
        User expectedUser = userActions.get(user.getObjectKey());
        if (expectedUser == null) {
            //no such user
            return null;
        }
        if (!user.getUserPassword().equals(expectedUser.getUserPassword())) {
            //bad password
            expectedUser.setUserPassword(null);
            return expectedUser;
        }
        return expectedUser;
    }

    public User updateUser(User user, Logger logger) {
        User mergeUser = null;
        try {
            mergeUser = mergeUsers(userActions.get(user.getObjectKey()), user);
            userActions.put(mergeUser);
            logger.info("Successfully merged user " + user.getObjectKey());
        } catch (Throwable th) {
            logger.error("Faild to merge user " + user.getObjectKey(), th);
        }
        return mergeUser;
    }

    private User mergeUsers(User orignalUser, User userToMerge) {
        orignalUser.setUserAQIProfileLevel(userToMerge.getUserAQIProfileLevel() != null ? userToMerge.getUserAQIProfileLevel() : orignalUser.getUserAQIProfileLevel());
        orignalUser.setUserBirthday(userToMerge.getUserBirthday() != null ? userToMerge.getUserBirthday() : orignalUser.getUserBirthday());
        orignalUser.setUserFirstName(userToMerge.getUserFirstName() != null ? userToMerge.getUserFirstName() : orignalUser.getUserFirstName());
        orignalUser.setUserLastName(userToMerge.getUserLastName() != null ? userToMerge.getUserLastName() : orignalUser.getUserLastName());
        orignalUser.setUserNoNotificationsADay(userToMerge.getUserNoNotificationsADay() != null ? userToMerge.getUserNoNotificationsADay() : orignalUser.getUserNoNotificationsADay());
        orignalUser.setUserPassword(userToMerge.getUserPassword() != null ? userToMerge.getUserPassword() : orignalUser.getUserPassword());
        orignalUser.setUserPicture(userToMerge.getUserPicture() != null ? userToMerge.getUserPicture() : orignalUser.getUserPicture());
        orignalUser.setUserSex(userToMerge.getUserSex() != null ? userToMerge.getUserSex() : orignalUser.getUserSex());
        return orignalUser;
    }
}
