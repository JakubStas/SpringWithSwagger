package com.jakubstas.swagger.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.EmailValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;
import com.jakubstas.swagger.model.User;

@Service
public class UserService {

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    private final Pattern userNamePattern = Pattern.compile("[^a-z0-9]");

    private final Map<String, User> users = new ConcurrentHashMap<String, User>();

    private final Resource avatarJakub = new ClassPathResource("avatars/1.png");

    private final Resource avatarPeter = new ClassPathResource("avatars/2.png");

    @PostConstruct
    public void init() throws IOException {
        final byte[] avatarJakubBytes = Files.readAllBytes(avatarJakub.getFile().toPath());
        final byte[] avatarPeterBytes = Files.readAllBytes(avatarPeter.getFile().toPath());

        final User userJakub = new User();
        userJakub.setUserName("tyler");
        userJakub.setFirstName("Tyler");
        userJakub.setSurname("Durden");
        userJakub.setEmail("tyler@fc.com");
        userJakub.setAvatar(avatarJakubBytes);
        userJakub.setLastUpdated(new Date());

        final User userPeter = new User();
        userPeter.setUserName("user2");
        userPeter.setFirstName("Peter");
        userPeter.setSurname("Jones");
        userPeter.setEmail("peter@jakubstas.com");
        userPeter.setAvatar(avatarPeterBytes);
        userPeter.setLastUpdated(new Date());

        users.put(userJakub.getUserName(), userJakub);
        users.put(userPeter.getUserName(), userPeter);
    }

    public User findByUserName(final String userName) {
        return users.get(userName);
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public User createUser(final User user) throws EntityAlreadyExistsException {
        Preconditions.checkArgument(StringUtils.hasText(user.getUserName()), "Invalid user definition! Missing username.");
        Preconditions.checkArgument(StringUtils.hasText(user.getFirstName()), "Invalid user definition! Missing first name.");
        Preconditions.checkArgument(StringUtils.hasText(user.getSurname()), "Invalid user definition! Missing surname.");
        Preconditions.checkArgument(StringUtils.hasText(user.getEmail()), "Invalid user definition! Missing email address.");

        final boolean isInvalidUserName = userNamePattern.matcher(user.getUserName()).find();
        final boolean isValidEmail = emailValidator.isValid(user.getEmail());

        Preconditions.checkArgument(!isInvalidUserName, "Invalid user definition! Username must contain only letters and numbers.");
        Preconditions.checkArgument(isValidEmail, "Invalid user definition! Invalid format of email address.");

        if (users.containsKey(user.getUserName())) {
            throw new EntityAlreadyExistsException();
        }

        user.setUserName(user.getUserName().toLowerCase());
        user.setLastUpdated(new Date());
        users.put(user.getUserName(), user);

        return user;
    }

    public User updateUser(final String userName, final String firstName, final String surname, final String email) throws EntityNotFoundException {
        final User user = users.get(userName);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        if (StringUtils.hasText(firstName)) {
            user.setFirstName(firstName);
        }

        if (StringUtils.hasText(surname)) {
            user.setFirstName(surname);
        }

        if (StringUtils.hasText(email)) {
            user.setFirstName(email);
        }

        user.setLastUpdated(new Date());

        return user;
    }

    public User updateAvatar(final String userName, final InputStream avatarIs) throws EntityNotFoundException, IOException {
        final User user = users.get(userName);

        if (user == null) {
            throw new EntityNotFoundException();
        }

        user.setAvatar(IOUtils.toByteArray(avatarIs));

        return user;
    }
}
