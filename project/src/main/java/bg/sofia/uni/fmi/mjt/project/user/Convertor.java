package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;
import bg.sofia.uni.fmi.mjt.project.user.models.UserEntity;

public final class Convertor {

    private Convertor() { }

//    public static UserEntity convertUserModelToService(UserEntity model) {
//        return model == null ? null : new UserEntity(model.getId(), model.getUsername(),
//            model.getFirstName() + " " + model.getLastName(), model.getCurrencyPreference());
//    }

    public static UserOutput convertUserServiceToOutput(UserEntity service) {
        return service == null ? null : new UserOutput(
            service.getId(),
            service.getUsername(),
            service.getFirstName() + " " + service.getLastName());
    }

}
