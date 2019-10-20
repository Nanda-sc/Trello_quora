package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String questionId, final AnswerEntity answer, final String authToken) throws InvalidQuestionException, AuthorizationFailedException {

        QuestionEntity questionEntity = answerDao.getQuestionByUuid(questionId);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }

        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authToken);

        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        answer.setDate(now);
        answer.setQuestion(questionEntity);
        answer.setUser(questionEntity.getUser());

        AnswerEntity createdAnswer = answerDao.createAnswer(answer);

        return createdAnswer;

    }
}
