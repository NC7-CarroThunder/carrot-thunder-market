package bitcamp.carrot_thunder.secret;

import bitcamp.carrot_thunder.user.model.vo.User;
import bitcamp.carrot_thunder.user.model.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nickName) throws UsernameNotFoundException {
        User user = userDao.findByName(nickName);
        return new UserDetailsImpl(user, user.getNickName());


    }
}