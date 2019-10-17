package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(final String username, final String email, final UserEntity userEntity) throws SignUpRestrictedException {

        UserEntity user1 = userDao.getUsername(username);

        if (user1 == null) {
            UserEntity user2 = userDao.getUserByEmail(email);
            if (user2 == null) {
                String password = userEntity.getPassword();

                if (password == null) {
                    password = "quora@123";
                }

                String[] encryptedText = cryptographyProvider.encrypt(password);
                userEntity.setSalt(encryptedText[0]);
                userEntity.setPassword(encryptedText[1]);

                return userDao.createUser(userEntity);
            }else{
                throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
            }
        }else{
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String username, final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUsername(username);

        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());

        if(encryptedPassword.equals(userEntity.getPassword())){

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuth = new UserAuthEntity();
            userAuth.setUser(userEntity);
            userAuth.setUuid(userEntity.getUuid());

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuth.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));

            userAuth.setLoginAt(now);
            userAuth.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuth);
            userDao.updateUser(userEntity);

            return userAuth;

        }else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }
}
