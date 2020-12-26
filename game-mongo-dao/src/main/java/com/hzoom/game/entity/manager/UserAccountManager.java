package com.hzoom.game.entity.manager;

import com.hzoom.game.entity.UserAccount;

public class UserAccountManager {
    private UserAccount userAccount;

    public UserAccountManager(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }
}
