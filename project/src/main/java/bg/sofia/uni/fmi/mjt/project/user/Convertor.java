package bg.sofia.uni.fmi.mjt.project.user;

import bg.sofia.uni.fmi.mjt.project.user.models.UserModel;
import bg.sofia.uni.fmi.mjt.project.user.models.UserOutput;
import bg.sofia.uni.fmi.mjt.project.user.models.UserService;

public final class Convertor {

    private Convertor() { }

    public static UserService convertUserModelToService(UserModel model) {
        return model == null ? null : new UserService(model.id(), model.username(),
            model.firstName() + " " + model.lastName(), model.currencyPreference());
    }

    public static UserOutput convertUserServiceToOutput(UserService service) {
        return service == null ? null : new UserOutput(service.id(), service.username(), service.fullName());
    }

}
