package org.magnum.mobilecloud.video.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.magnum.mobilecloud.video.core.AuthInfo;
import org.magnum.mobilecloud.video.repository.UserDetailsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by cong on 11/14/14.
 */
public class UserDetailsManager implements UserDetailsService {
    protected final Log logger = LogFactory.getLog(getClass());
    UserDetailsRepository userDetailsRepository;

    public UserDetailsManager(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("load user");
        if(username.equals("guest")) {
           return new User("guest","pass",new String[]{"USER"});
        }
        AuthInfo user = userDetailsRepository.findOne(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        logger.debug("load user success");
        return new User(user.getUsername(),user.getPassword(),user.getAuthorities());
    }
}
