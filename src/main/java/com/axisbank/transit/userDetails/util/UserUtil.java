package com.axisbank.transit.userDetails.util;

import com.axisbank.transit.authentication.model.DAO.AuthenticationDAO;
import com.axisbank.transit.authentication.repository.AuthenticationRepository;
import com.axisbank.transit.core.shared.utils.CommonUtils;
import com.axisbank.transit.userDetails.model.DAO.DAOUser;
import com.axisbank.transit.userDetails.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private String secret;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Value("${app.jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * get logged in Auth Object
     * @return
     */
     public AuthenticationDAO getAuthObject() throws Exception {
         return authenticationRepository.findByUserNameIgnoreCaseAndIsActive(getLoggedInUserName(), true);
     }

     public String getLoggedInUserName() throws Exception{
         User loggedInUser = CommonUtils.getLoggedinUser();
         return loggedInUser.getUsername();
     }

     public String getUserIdFromToken(String token){
         String userName = null;
         try{
             Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
             userName = claims.getBody().getSubject();
         } catch (ExpiredJwtException ex){
             userName = ex.getClaims().getSubject();
         }
         if(userName==null)
             return "";
         DAOUser user = userRepository.findByAuthenticationDAO_UserName(userName);
         if(user==null)
             return "";
         return user.getUserId();
     }

    public Boolean validateWithLoggedInUserId(String userId) throws Exception{
        AuthenticationDAO authAO = getAuthObject();
        if (userId.equals(authAO.getDaoUser().getUserId())) {
            return true;
        } else {
            return false;
        }
    }
}
