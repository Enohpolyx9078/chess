package service;

import dataaccess.AlreadyTakenException;
import model.RegisterResult;
import model.UserData;

public class UserService {
    public RegisterResult register(UserData userData) throws AlreadyTakenException {
        RegisterResult result;
        //TODO call to the DAO layer to register
        return result;
    }
}
