package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUsername(final String username){
        try{
            return entityManager.createNamedQuery("username",
                    UserEntity.class).setParameter("username",username).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }

    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public void updateUser(final UserEntity updatedUserEntity){
        entityManager.merge(updatedUserEntity);
    }

    public void updateUserAuth(final UserAuthEntity updatedUserAuthEntity){
        entityManager.merge(updatedUserAuthEntity);
    }

    public UserAuthEntity getUserAuthToken(final String accessToken){
        try{
            UserAuthEntity userAuthEntity = entityManager.createNamedQuery("userAuthByAccessToken",
                    UserAuthEntity.class).setParameter("accessToken",accessToken).getSingleResult();
            return userAuthEntity;

        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByUuid(final String userId){
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userId).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }

    }
}
